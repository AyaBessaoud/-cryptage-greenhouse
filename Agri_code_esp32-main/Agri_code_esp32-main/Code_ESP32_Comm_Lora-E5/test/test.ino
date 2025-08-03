#include <WiFi.h>
#include <HTTPClient.h>
#include <ArduinoJson.h>

// --- WiFi credentials ---
const char* ssid = "Ooredoo68E864";
const char* password = "D2AZHG94?3&3#";
const char* apiKey = "de06f6099f10d6b5d2286d77daa5bbb6";  // OpenWeatherMap API key

// --- User input ---
String city = "";
String country = "";
bool locationEntered = false;

// Dummy indoor sensor data (replace with actual readings from STM32 later)
float indoorTemp = 36.5;
float indoorHumidity = 85.0;
float indoorSoilMoisture = 950;
float indoorLight = 45;

float outdoorTemp, outdoorHumidity, windSpeed, rain;
String weatherMain;
bool isRoofOpen = false;

void setup() {
  Serial.begin(115200);
  WiFi.begin(ssid, password);

  Serial.print("Connecting to WiFi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nConnected!");

  Serial.println("\n📍 Enter your city name (e.g., Tunis):");
  while (city == "") {
    if (Serial.available()) {
      city = Serial.readStringUntil('\n');
      city.trim();
      Serial.print("City set to: ");
      Serial.println(city);
    }
    delay(100);
  }

  Serial.println("🌍 Enter your 2-letter country code (e.g., TN):");
  while (country == "") {
    if (Serial.available()) {
      country = Serial.readStringUntil('\n');
      country.trim();
      Serial.print("Country code set to: ");
      Serial.println(country);
    }
    delay(100);
  }

  locationEntered = true;
}

void loop() {
  if (!locationEntered) return;

  String serverName = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "," + country + "&appid=" + String(apiKey) + "&units=metric";

  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    http.begin(serverName);
    int httpResponseCode = http.GET();

    if (httpResponseCode == 200) {
      String payload = http.getString();
      Serial.println("\n🌤️ Weather API Response:");
      Serial.println(payload);

      DynamicJsonDocument doc(1024);
      deserializeJson(doc, payload);

      outdoorTemp = doc["main"]["temp"];
      outdoorHumidity = doc["main"]["humidity"];
      windSpeed = doc["wind"]["speed"];
      weatherMain = doc["weather"][0]["main"].as<String>();
      rain = doc["rain"]["1h"] | 0.0;

      // --- Print formatted weather ---
      Serial.println("\n📊 OUTDOOR WEATHER REPORT:");
      Serial.printf("  🌡️  Temp: %.2f °C\n", outdoorTemp);
      Serial.printf("  💧  Humidity: %.2f %%\n", outdoorHumidity);
      Serial.printf("  💨  Wind Speed: %.2f m/s\n", windSpeed);
      Serial.printf("  🌧️  Rain (last 1h): %.2f mm\n", rain);
      Serial.printf("  🌤️  Condition: %s\n", weatherMain.c_str());

      // --- Print indoor values ---
      Serial.println("\n🌱 INDOOR GREENHOUSE READINGS:");
      Serial.printf("  🌡️  Temp: %.2f °C\n", indoorTemp);
      Serial.printf("  💧  Humidity: %.2f %%\n", indoorHumidity);
      Serial.printf("  🌾  Soil Moisture: %.2f\n", indoorSoilMoisture);
      Serial.printf("  💡  Light: %.2f lux\n", indoorLight);

      // --- Roof control logic ---
      bool shouldOpen = false;

      if (indoorSoilMoisture < 1000 && rain > 0.2)
        shouldOpen = true;
      else if (indoorHumidity > 80 && outdoorHumidity < 60)
        shouldOpen = true;
      else if (indoorTemp > 35 && outdoorTemp < 30)
        shouldOpen = true;
      else if (indoorLight < 50 && weatherMain == "Clear")
        shouldOpen = true;

      if (rain > 1.0 || windSpeed > 8.0 || weatherMain == "Thunderstorm")
        shouldOpen = false;

      if (shouldOpen && !isRoofOpen) {
        Serial.println("\n✅ DECISION: OPEN the roof 🌿");
        isRoofOpen = true;
      } else if (!shouldOpen && isRoofOpen) {
        Serial.println("\n🛑 DECISION: CLOSE the roof 🔒");
        isRoofOpen = false;
      } else {
        Serial.printf("\n🔁 DECISION: Keep roof %s\n", isRoofOpen ? "OPEN" : "CLOSED");
      }

    } else {
      Serial.print("HTTP GET failed, error: ");
      Serial.println(httpResponseCode);
    }

    http.end();
  } else {
    Serial.println("WiFi Disconnected");
  }

  delay(15000);
}
