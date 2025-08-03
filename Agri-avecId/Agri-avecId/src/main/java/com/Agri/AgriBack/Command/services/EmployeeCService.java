package com.Agri.AgriBack.Command.services;

import com.Agri.AgriBack.Command.entity.Farm;
import com.Agri.AgriBack.Command.entity.GreenHouse;
import com.Agri.AgriBack.Command.producer.EmployeeProducer;
import com.Agri.AgriBack.Command.entity.Employee;
import com.Agri.AgriBack.Command.repository.EmployeeCRepo;
import com.Agri.AgriBack.Command.repository.FarmCRepo;
import com.Agri.AgriBack.Command.repository.GreenHouseCRepo;
import com.Agri.AgriBack.DTO.EmployeeDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Data
@AllArgsConstructor
public class EmployeeCService implements UserDetailsService {
    @Autowired
    private EmployeeCRepo empRepo;
    @Autowired
    private GreenHouseCRepo greenHouseRepo;
    @Autowired
    private FarmCRepo farmRepo;

    @Autowired
    private EmployeeProducer empProducer;

    public Employee createEmployee(Employee employee) {
        Employee savedEmployee = empRepo.save(employee);
        EmployeeDTO dto = mapper(savedEmployee);
        empProducer.createEmployeeEvent(dto);
        return savedEmployee;
    }

    public Employee updateEmployee(Employee employee){
        if (employee.getFerme() != null) {
            List<Farm> fermes = employee.getFerme().stream()
                    .map(f -> farmRepo.findById(f.getId())
                            .orElseThrow(() -> new RuntimeException("Farm doesn't exist: ID = " + f.getId())))
                    .collect(Collectors.toList());
            employee.setFerme(fermes);
        }

        if (employee.getSerre() != null) {
            List<GreenHouse> serres = employee.getSerre().stream()
                    .map(s -> greenHouseRepo.findById(s.getId())
                            .orElseThrow(() -> new RuntimeException("Greenhouse doesn't exist: ID = " + s.getId())))
                    .collect(Collectors.toList());
            employee.setSerre(serres);
        }

        Employee savedEmployee = empRepo.save(employee);
        EmployeeDTO dto = mapper(savedEmployee);
        empProducer.updateEmployeeEvent(dto);
        return savedEmployee;
    }

    public void deleteEmployee (Long id){
        Optional<Employee> empOpt = empRepo.findById(id);
        if(empOpt.isPresent()){
            empRepo.deleteById(id);
            empProducer.deleteEmployeeEvent(id);
        }
    }

    //pour l'authentification selon email et mdp
    public Employee authenticateUser(String email, String password) {
        Optional<Employee> user = empRepo.findByEmailAndPassword(email, password);
        return user.orElse(null);
    }

    //pour la generation d'un mot de passe random
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int PASSWORD_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();

    public String generatePassword() {
        return IntStream.range(0, PASSWORD_LENGTH)
                .map(i -> RANDOM.nextInt(CHARS.length()))
                .mapToObj(CHARS::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }

    //pour l'envoi d'un email
    @Autowired
    private JavaMailSender mailSender;
    @Async
    public void sendSimpleEmail(String toEmail, String subject, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sarra.labidi.395@gmail.com");
        message.setTo(toEmail);

        // Utilisation de HTML dans le corps du message
        String htmlBody = "Votre mot de passe pour l'authentification  " + code + "";

        message.setText(htmlBody); // Utilisez setText pour le contenu du message
        message.setSubject(subject);

        mailSender.send(message); // Envoi du message

        System.out.println("Mail Send...");
    }

    private EmployeeDTO mapper(Employee emp){
        EmployeeDTO dto = new EmployeeDTO();
        dto.setAddress(emp.getAddress());
        dto.setEmail(emp.getEmail());
        // Extraire les IDs des fermes
        dto.setFermeId(
                emp.getFerme() != null
                        ? emp.getFerme().stream().map(Farm::getId).collect(Collectors.toList())
                        : new ArrayList<>()
        );
        // Extraire les IDs des serres
        dto.setSerreId(
                emp.getSerre() != null
                        ? emp.getSerre().stream().map(GreenHouse::getId).collect(Collectors.toList())
                        : new ArrayList<>()
        );
        dto.setId(emp.getId());
        dto.setFirstName(emp.getFirstName());
        dto.setRole(emp.getRole());
        dto.setLastName(emp.getLastName());
        dto.setMobile(emp.getMobile());
        dto.setPassword(emp.getPassword());
        return dto;
    }

    //pour recuperer l'utilisateur selon son email
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee user = empRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }

    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        return loadUserByUsername(email);
    }
}
