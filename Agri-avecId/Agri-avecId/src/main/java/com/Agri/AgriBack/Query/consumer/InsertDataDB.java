package com.Agri.AgriBack.Query.consumer;

import com.Agri.AgriBack.Command.entity.Actuator;
import com.Agri.AgriBack.Command.entity.Employee;
import com.Agri.AgriBack.Command.entity.SensorType;
import com.Agri.AgriBack.Query.entity.*;
import com.Agri.AgriBack.Query.repository.*;
import com.Agri.AgriBack.Query.services.EndDeviceQService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class InsertDataDB {

    @Autowired
    private LogsRepo logsRepo;

    @Autowired
    private ActHistoryQRepo actHistoryQRepo;

    @Autowired
    private SensorHistoryRepo sensorHistoryRepo;

    @Autowired
    private DeviceHistoryRepo deviceHistoryRepo;

    @Autowired
    private SensorQRepo sensorQRepo;
    @Autowired
    private ActuatorQRepo actuatorQRepo;
    @Autowired
    private EndDeviceQService deviceService;
    @Autowired
    private EmployeeQRepo empRepo;
    @Autowired
    private NotifRepo notificationRepo;

    private final SimpMessagingTemplate messagingTemplate; //permet d’envoyer des messages via WebSocket aux abonnés


    @KafkaListener(topics = "data3", groupId = "Agri-group",
            containerFactory = "stringKafkaListenerContainerFactory")
    public void listen (String message) {
         message = message.replace("\"", "");
        System.out.println("Message reçu : " + message);
        try {
            String[] parts = message.split(";");
            if (parts.length < 3) return;

            String codDevice = parts[0].trim();
            String lastPart = parts[parts.length - 1].trim();

// Traiter timestamp pouvant contenir une partie décimale
            String timestampStr = lastPart;
            if (timestampStr.contains(".")) {
                timestampStr = timestampStr.substring(0, timestampStr.indexOf('.'));
            }
            long epochSeconds = Long.parseLong(timestampStr);

// Conversion en LocalDateTime
            LocalDateTime timestamp = LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(epochSeconds),
                    ZoneId.systemDefault()
            );


            if (parts.length == 3) {
                // Cas 1 : Message log d'actionneur simple (un seul actionneur)
                String middle = parts[1].trim();

                    // Cas : codDevice ; Output:etat:index ; timestamp
                    String[] actuatorParts = middle.split(":");
                    if (actuatorParts.length == 3) {
                        try {
                            Actuator.Outputs output = Actuator.Outputs.valueOf(actuatorParts[0].trim());
                            int etat = Integer.parseInt(actuatorParts[1].trim());
                            int index = Integer.parseInt(actuatorParts[2].trim());

                            ActuatorQ actuatorQ = deviceService.findActuator(index,codDevice, output);
                            ActuatorHistoryQ actuator = new ActuatorHistoryQ();
                            actuator.setCodDevice(codDevice);
                            actuator.setDate(timestamp);
                            actuator.setEtat(etat);
                            actuator.setOutput(output);
                            actuator.setIndex(index);
                            actuator.setIdU(actuatorQ != null ? actuatorQ.getIdU() : null);
                            actHistoryQRepo.save(actuator);
                            messagingTemplate.convertAndSend("/topic/logs", actuator);
                            messagingTemplate.convertAndSend("/topic/actHistoy", actuator);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            } else {
                // Cas : Capteurs et/ou actionneurs avec ou sans batterie
                int battery = -1;
                int currentIndex = 1;

                // Vérifie si la 2e valeur est un entier (batterie)
                try {
                    battery = Integer.parseInt(parts[currentIndex].trim());
                    DeviceHistory device = new DeviceHistory();
                    device.setCodDevice(codDevice);
                    device.setBattery(battery);
                    device.setDate(timestamp);
                    deviceHistoryRepo.save(device);
                    messagingTemplate.convertAndSend("/topic/logs", device);
                    currentIndex++;
                } catch (NumberFormatException ignored) {}

                // Traite les données (capteurs puis actionneurs)
                while (currentIndex < parts.length - 1) {
                    String data = parts[currentIndex].trim();
                    String[] subParts = data.split(":");

                    if (subParts.length == 4) {
                        // Sensor: type:index:value:etat
                        try { System.out.println("sensor History");
                            SensorType type = SensorType.valueOf(subParts[0].trim());
                            int index = Integer.parseInt(subParts[1].trim());
                            float value = Float.parseFloat(subParts[2].trim());
                            int etat = Integer.parseInt(subParts[3].trim());

                            SensorQ sensorQ = deviceService.findSensor( index, type,codDevice);
                            SensorHistory sensor = new SensorHistory();
                            sensor.setCodDevice(codDevice);
                            sensor.setDate(timestamp);
                            sensor.setIndex(index);
                            sensor.setEtat(etat);
                            sensor.setTypeSensor(type);
                            sensor.setValue(value);
                            sensor.setIdU(sensorQ != null ? sensorQ.getIdU() : null);
                            sensorHistoryRepo.save(sensor);
                            messagingTemplate.convertAndSend("/topic/logs", sensor);
                            if(etat == 0){
                                System.out.println("notif");
                                GreenHouseQ serre = sensorQ.getEndDevice().getSerre();
                                // Récupération des agriculteurs de la serre
                                List<EmployeeQ> agri = empRepo.findBySerre(serre);

                                // Récupération des administrateurs
                                List<EmployeeQ> admins = empRepo.findByRole(Employee.Role.Admin);

                                // Fusionner les deux listes (éviter les doublons si nécessaire)
                                Set<EmployeeQ> userSet = new HashSet<>();
                                userSet.addAll(agri);
                                userSet.addAll(admins);
                                List<EmployeeQ> users = new ArrayList<>(userSet);
                                String mesg = "Le capteur "+ sensorQ.getIdU() + " de l'appareil final "+ codDevice+ " qui appartient à "+ serre.getDescription()+
                                        " a dépassé le seuil";
                                Notification notif = new Notification();
                                notif.setUsers(users);
                                notif.setMessage(mesg);

                                // Sauvegarder dans la base
                                notificationRepo.save(notif);
                                // Envoi en temps réel via WebSocket à tous les utilisateurs abonnés à /topic/notifications
                                messagingTemplate.convertAndSend("/topic/notifications", notif);
                                for(EmployeeQ user : users){
                                    sendSimpleEmail(user.getEmail(), sensorQ.getIdU(),codDevice,serre.getDescription(), value);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (subParts.length == 3) {
                        // Actuator: Output:etat:index
                        try {
                            Actuator.Outputs output = Actuator.Outputs.valueOf(subParts[0].trim());
                            int index  = Integer.parseInt(subParts[1].trim());
                            int etat = Integer.parseInt(subParts[2].trim());

                            ActuatorQ actuatorQ = deviceService.findActuator(index,codDevice, output);
                            ActuatorHistoryQ actuator = new ActuatorHistoryQ();
                            actuator.setCodDevice(codDevice);
                            actuator.setDate(timestamp);
                            actuator.setEtat(etat);
                            actuator.setOutput(output);
                            actuator.setIndex(index);
                            actuator.setIdU(actuatorQ != null ? actuatorQ.getIdU() : null);
                            actHistoryQRepo.save(actuator);
                            messagingTemplate.convertAndSend("/topic/logs", actuator);
                            messagingTemplate.convertAndSend("/topic/actHistoy", actuator);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    currentIndex++;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //pour l'envoi d'un email
    @Autowired
    private JavaMailSender mailSender;
    @Async
    public void sendSimpleEmail(String toEmail, String capteur,String codDevice, String serre, float valeur) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sarra.labidi.395@gmail.com");
        message.setTo(toEmail);

        // Utilisation de HTML dans le corps du message
        String htmlBody = "Le capteur " + capteur +  " de l'appareil final "+ codDevice+ " qui appartient à "+ serre+ " a dépassé le seuil pour atteindre "+ valeur;

        message.setText(htmlBody); // Utilisez setText pour le contenu du message
        message.setSubject("Alerte de dépassement de seuil");

        mailSender.send(message); // Envoi du message

        System.out.println("Mail Send...");
    }
}
