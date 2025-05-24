#include <WiFi.h>
#include <Firebase_ESP_Client.h>
#include <NewPing.h>
#include <addons/TokenHelper.h>

// WiFi Credentials
#define WIFI_SSID "Dinith"
#define WIFI_PASSWORD "55555556"

// Firebase Credentials
#define API_KEY "AIzaSyCc_kCcjaUD5fBC3gYZmL1djH76ABk7dXQ"
#define DATABASE_URL "https://nodemcu-55278-default-rtdb.asia-southeast1.firebasedatabase.app/"

// Firebase User Email/Password
#define USER_EMAIL "dinithpalihakkara01@gmail.com"
#define USER_PASSWORD "Dinith2199"

// Sensor Pins
#define IR_PIN 15
#define TRIG_PIN 5
#define ECHO_PIN 18
#define MAX_DISTANCE 200

// Initialize ultrasonic sensor
NewPing sonar(TRIG_PIN, ECHO_PIN, MAX_DISTANCE);

// Firebase objects
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

void setup() {
  Serial.begin(115200);
  pinMode(IR_PIN, INPUT);

  // Connect to WiFi
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to WiFi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi connected");

  // Firebase Config
  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;

  // Assign token status callback
  config.token_status_callback = tokenStatusCallback;

  // Begin Firebase
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  while (!Firebase.ready()) {
    delay(100);
    Serial.print("-");
  }
  Serial.println("\nFirebase ready");
}

void loop() {
  int irValue = digitalRead(IR_PIN);
  unsigned int distance = sonar.ping_cm();

  // Prepare JSON
  FirebaseJson json;
  json.set("ir", irValue);
  json.set("distance", distance);
  json.set("timestamp/.sv", "timestamp");

  // Update sensor data
  if (Firebase.RTDB.setJSON(&fbdo, "/sensor_data/latest", &json)) {
    Serial.println("Sensor data updated successfully");
  } else {
    Serial.printf("Error: %s\n", fbdo.errorReason().c_str());
  }

  delay(1000); Update every 1 seconds
}
