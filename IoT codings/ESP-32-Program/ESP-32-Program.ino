#include <Arduino.h>
#include <WiFi.h>
#include <WiFiClientSecure.h>
#include <FirebaseClient.h>

// Ultrasonic sensor pins
#define TRIG_PIN 4 // GPIO4
#define ECHO_PIN 2 // GPIO2

// Network and Firebase credentials
#define WIFI_SSID "Dinith"
#define WIFI_PASSWORD "55555555"
#define WEB_API_KEY "AIzaSyCc_kCcjaUD5fBC3gYZmL1djH76ABk7dXQ"
#define DATABASE_URL "https://nodemcu-55278-default-rtdb.asia-southeast1.firebasedatabase.app/"
#define USER_EMAIL "dinithpalihakkara01@gmail.com"
#define USER_PASS "Dinith2199"

// Firebase objects
UserAuth user_auth(WEB_API_KEY, USER_EMAIL, USER_PASS);
FirebaseApp app;
WiFiClientSecure ssl_client;
using AsyncClient = AsyncClientClass;
AsyncClient aClient(ssl_client);
RealtimeDatabase Database; // Added declaration

// Timer variables
unsigned long lastSendTime = 0;
const unsigned long sendInterval = 1000; // Send every 1 second

// Forward declaration of callback
void processData(AsyncResult &aResult);

void setup() {
  Serial.begin(115200);

  // Ultrasonic sensor setup
  pinMode(TRIG_PIN, OUTPUT);
  pinMode(ECHO_PIN, INPUT);

  // Connect to Wi-Fi
  Serial.print("Connecting to Wi-Fi");
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(300);
  }
  Serial.println("\nWi-Fi Connected!");

  // Configure SSL client
  ssl_client.setInsecure(); // Skip certificate validation
  ssl_client.setTimeout(1000); // Set connection timeout

  // Initialize Firebase
  initializeApp(aClient, app, getAuth(user_auth), processData, "authTask");
  app.getApp<RealtimeDatabase>(Database);
  Database.url(DATABASE_URL);
}

void loop() {
  // Maintain authentication and async tasks
  app.loop();

  // Check if authenticated and ready
  if (!app.ready()) return;

  // Periodic data sending every 1 second
  unsigned long currentTime = millis();
  if (currentTime - lastSendTime >= sendInterval) {
    // Update last send time
    lastSendTime = currentTime;

    // Measure distance
    digitalWrite(TRIG_PIN, LOW);
    delayMicroseconds(2);
    digitalWrite(TRIG_PIN, HIGH);
    delayMicroseconds(10);
    digitalWrite(TRIG_PIN, LOW);

    long duration = pulseIn(ECHO_PIN, HIGH, 30000);
    float distance = duration * 0.034 / 2.0;

    if (distance > 0 && distance < 400) {
      Serial.printf("Distance: %.2f cm\n", distance);
      Database.set<float>(aClient, "/sensor/distance", distance, processData, "distance");
    } else {
      Serial.println("Distance out of range or timeout.");
    }
  }
}

void processData(AsyncResult &aResult) {
  if (!aResult.isResult()) return;

  if (aResult.isError()) {
    Serial.printf("[Error] %s: %s (code %d)\n",
                  aResult.uid().c_str(),
                  aResult.error().message().c_str(),
                  aResult.error().code());
  } else if (aResult.available()) {
    Serial.printf("[OK] %s: %s\n",
                  aResult.uid().c_str(),
                  aResult.c_str());
  }
}