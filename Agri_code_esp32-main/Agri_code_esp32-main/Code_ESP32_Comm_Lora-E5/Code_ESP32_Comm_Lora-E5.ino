#include <SPI.h>
#include <Wire.h>
#include <LoRa.h>
#include <ArduinoJson.h>
#include <ArduinoJson.hpp>
#include <WiFi.h>
#include <PubSubClient.h>
#define LORA_BAND    868
#ifndef CONFIG_RADIO_OUTPUT_POWER
#define CONFIG_RADIO_OUTPUT_POWER   14
#endif
#ifndef CONFIG_RADIO_BW
#define CONFIG_RADIO_BW             125.0
#endif
#include <HTTPClient.h>  // For HTTP GET

//---- SPI config ------////
#define SCK     5    // GPIO5  -- SX1278's SCK
#define MISO    19   // GPIO19 -- SX1278's MISO
#define MOSI    27   // GPIO27 -- SX1278's MOSI
#define SS      18   // GPIO18 -- SX1278's CS
#define RST     14   // GPIO14 -- SX1278's RESET
#define DI0     26   // GPIO26 -- SX1278's IRQ(Interrupt Request)


const char* ssid = "Eya 18";
const char* password = "11112222";
const char* mqtt_server = "broker.hivemq.com";   //adresse ip LAN

WiFiClient espClient;
PubSubClient client(espClient);

volatile bool receivedFLoRa = false;

//StaticJsonDocument<80> doc;
char output[80];
void callback(char* topic, byte* payload, unsigned int length);
void reconnect();



void setup() {
  Serial.begin(115200);
  while (!Serial);
  // We start by connecting to a WiFi network
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print("-");
  }

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());

  client.setServer(mqtt_server, 1883);
  client.setCallback(callback);

  //LoRa Configuration
  Serial.println();
  Serial.println("LoRa Receiver");

  // Configure the LoRA radio
  SPI.begin(SCK, MISO, MOSI, SS);
  LoRa.setPins(SS, RST, DI0);  //Set the pins for the LoRa module
  if (!LoRa.begin(LORA_BAND * 1E6)) {
    Serial.println("Starting LoRa failed!");
    while (1);
  }
  Serial.println("init ok");
  LoRa.setTxPower(CONFIG_RADIO_OUTPUT_POWER);
  LoRa.setSignalBandwidth(CONFIG_RADIO_BW * 1000);
  LoRa.setSpreadingFactor(7);
  LoRa.setCodingRate4(5);

 // Configurer l'interruption de réception
  //LoRa.onReceive(onReceive);

  // Set the radio into receive mode
  LoRa.receive();
  delay(1500);
}


void loop() {
  if (!client.connected()) {
    reconnect();
  }
  //if the gateway receives data by LoRa, it will be send by mqtt to a "data" topic
  int packetSize = LoRa.parsePacket();
  if (packetSize) {
    Serial.print("Données reçues (taille = ");
    Serial.print(packetSize);
    Serial.println(" octets) :");
    // Lit les données reçues
    // Lire la chaîne de caractères reçue
  String receivedText = "";
  while (LoRa.available()) {
    receivedText += (char)LoRa.read();
  }

  Serial.print("Message LoRa reçu : ");
  Serial.println(receivedText);

  // Publier directement la chaîne sur MQTT
  client.publish("data", receivedText.c_str()); ////------publishing to topic "data" what is recieved from LORA -------///// 
  Serial.println("Données envoyées au broker");
  }
   client.loop(); // permettre au client MQTT de traiter les messages entrants.
  // fetchWeatherAndDecideRoof();
   delay(10);
  }


void reconnect() {
  // Loop until we're reconnected
  while (!client.connected()) {
    Serial.println("Attempting MQTT connection...");
    // Create a random client ID
    String clientId = "ESPClient-";
    clientId += String(random(0xffff), HEX);
    // Attempt to connect
    if (client.connect(clientId.c_str())) {
      Serial.println("connected");
      if (client.subscribe("data1")) {//////--------- topic "data1" to read from  the broker -------///////
        Serial.println("Subscribed to topic: data1");
      } else {
        Serial.println("Failed to subscribe to topic: data1");
      }
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      delay(5000);
    }
  }
}


//----------------------------------------//

//Interrupt  callback of LoRa //----- if recienved data on the subscribed topic we sending it to LORA -------//// 
void onReceive(int packetSize) {  
  receivedFLoRa = true; // Marquer qu'un paquet a été reçu
}

//MQTT Interrupt
void callback(char* topic, byte* payload, unsigned int length) {
  // Convertir le payload en une chaîne de caractères
  String message = "";
  for (unsigned int i = 0; i < length; i++) {
    message += (char)payload[i];
  }

  Serial.print("Message reçu depuis MQTT : ");
  Serial.println(message);

  // Envoyer via LoRa
  Serial.println("Sending data to LoRa-E5 begin");
  LoRa.beginPacket();
  LoRa.print(message);  // envoyer la chaîne complète
  LoRa.endPacket();
  Serial.println("Données envoyées via LoRa");

  LoRa.receive(); // Revenir en mode réception
}