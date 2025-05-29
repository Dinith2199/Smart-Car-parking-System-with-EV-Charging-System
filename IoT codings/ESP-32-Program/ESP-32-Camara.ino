#include <WiFi.h>
#include <Firebase_ESP_Client.h>
#include <NewPing.h>
#include <ESP32Servo.h>
#include <addons/TokenHelper.h>
#include <time.h>
#include <Wire.h>
#include <LiquidCrystal_I2C.h>

// WiFi Credentials
#define WIFI_SSID "AndroidAP_202"
#define WIFI_PASSWORD "11111111"

// Firebase Credentials
#define API_KEY "AIzaSyCc_kCcjaUD5fBC3gYZmL1djH76ABk7dXQ"
#define DATABASE_URL "https://nodemcu-55278-default-rtdb.asia-southeast1.firebasedatabase.app/"
#define USER_EMAIL "dinithpalihakkara01@gmail.com"
#define USER_PASSWORD "Dinith2199"

// Sensor Pins
#define IR1_PIN 35
#define IR2_PIN 34
#define IR3_PIN 33
#define IR4_PIN 32
#define TRIG_PIN 5
#define ECHO_PIN 18
#define SERVO_PIN 13
#define MAX_DISTANCE 200
#define LDR_PIN 36  
#define LIGHT_PIN 19 

// LED Pins for Parking Slots
const int LED_PINS[4] = {14, 15, 2, 4}; // Parked LEDs: Slot 1-4
const int FREE_LED_PINS[4] = {12, 16, 17, 25}; // Free LEDs: Slot 1-4

// I2C LCD Pins
#define SDA_PIN 21
#define SCL_PIN 22

// LDR Thresholds with Hysteresis
#define LDR_THRESHOLD_ON 3500   // Turn light ON when LDR value > 3500 
#define LDR_THRESHOLD_OFF 2500  // Turn light OFF when LDR value < 2500 

// Timezone settings for Sri Lanka (GMT +5:30)
#define TIMEZONE_OFFSET 19800  
const char* ntpServer = "pool.ntp.org";

// Initialize components
NewPing sonar(TRIG_PIN, ECHO_PIN, MAX_DISTANCE);
Servo gateServo;
LiquidCrystal_I2C lcd(0x27, 16, 2); // Address 0x27, 16x2 LCD

// Firebase objects
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

// Timer variables
unsigned long lastSendTime = 0;
const unsigned long sendInterval = 1000; // Update every second
bool gateOpen = false;
unsigned long welcomeStartTime = 10000;
bool showWelcome = false;
bool lightOn = false;  // Track LDR-controlled light status
bool slotLedStatus[4] = {false, false, false, false}; // Track parked LED status
bool slotFreeLedStatus[4] = {false, false, false, false}; // Track free LED status
unsigned long gateOpenStartTime = 0;
const unsigned long gateOpenDuration = 10000; // Keep gate open for 10 seconds 

// Parking tracking
struct ParkingRecord {
  time_t startTime;
  time_t endTime;
  bool isParked;
  String plate; // Store number plate
};
ParkingRecord parkingSlots[4];

void setup() {
  Serial.begin(115200);

  // Initialize I2C and LCD
  Wire.begin(SDA_PIN, SCL_PIN);
  lcd.begin(16, 2);
  lcd.backlight();
  lcd.setCursor(0, 0);
  lcd.print("Welcome to");
  lcd.setCursor(0, 1);
  lcd.print("DriveIn Parking");
  delay(2000);
  lcd.clear();

  // Initialize sensor, servo, light, and LED pins
  pinMode(IR1_PIN, INPUT_PULLUP); 
  pinMode(IR2_PIN, INPUT_PULLUP);
  pinMode(IR3_PIN, INPUT_PULLUP);
  pinMode(IR4_PIN, INPUT_PULLUP);
  pinMode(LIGHT_PIN, OUTPUT);  
  digitalWrite(LIGHT_PIN, LOW);   
  for (int i = 0; i < 4; i++) {
    pinMode(LED_PINS[i], OUTPUT);
    pinMode(FREE_LED_PINS[i], OUTPUT);
    digitalWrite(LED_PINS[i], LOW);
    digitalWrite(FREE_LED_PINS[i], LOW);
  }
  gateServo.attach(SERVO_PIN);
  gateServo.write(0); // Gate closed
  Serial.println("Components initialized");

  // Connect to WiFi
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to WiFi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nWiFi connected");

  // Configure time with Sri Lanka timezone
  configTime(TIMEZONE_OFFSET, 0, ntpServer);
  Serial.println("Waiting for time sync");
  while (time(nullptr) < 100000) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("\nTime synchronized");
  printLocalTime();

  // Firebase Config
  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;
  auth.user.email = USER_EMAIL;
  auth.user.password = USER_PASSWORD;
  config.token_status_callback = tokenStatusCallback;

  // Begin Firebase
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  while (!Firebase.ready()) {
    delay(100);
    Serial.print("-");
  }
  Serial.println("\nFirebase ready");

  // Initialize parking slots and set free LEDs ON (all slots empty)
  for (int i = 0; i < 4; i++) {
    parkingSlots[i] = {0, 0, false, ""};
    slotFreeLedStatus[i] = true;
    digitalWrite(FREE_LED_PINS[i], HIGH);
  }
}

void printLocalTime() {
  struct tm timeinfo;
  if (!getLocalTime(&timeinfo)) {
    Serial.println("Failed to obtain time");
    return;
  }
  Serial.println(&timeinfo, "Sri Lanka Time: %A, %B %d %Y %H:%M:%S");
}

String formatTime(time_t timestamp) {
  struct tm *timeinfo;
  timeinfo = localtime(&timestamp);
  char buffer[20];
  strftime(buffer, sizeof(buffer), "%H:%M:%S", timeinfo);
  return String(buffer);
}

String formatDateTime(time_t timestamp) {
  struct tm *timeinfo;
  timeinfo = localtime(&timestamp);
  char buffer[30];
  strftime(buffer, sizeof(buffer), "%Y-%m-%d %H:%M:%S", timeinfo);
  return String(buffer);
}

String formatDuration(time_t duration) {
  int hours = duration / 3600;
  int minutes = (duration % 3600) / 60;
  int seconds = duration % 60;
  return String(hours) + "h " + String(minutes) + "m " + String(seconds) + "s";
}

void updateCurrentParkingStatus() {
  for (int i = 0; i < 4; i++) {
    String slotPath = "/current_status/slot" + String(i+1);
    
    if (parkingSlots[i].isParked) {
      time_t duration = time(nullptr) - parkingSlots[i].startTime;
      Firebase.RTDB.setString(&fbdo, slotPath + "/status", "parked");
      Firebase.RTDB.setString(&fbdo, slotPath + "/start_time", formatTime(parkingSlots[i].startTime));
      Firebase.RTDB.setString(&fbdo, slotPath + "/duration", formatDuration(duration));
      Firebase.RTDB.setString(&fbdo, slotPath + "/number_plate", parkingSlots[i].plate);
      Firebase.RTDB.setBool(&fbdo, slotPath + "/led_status", slotLedStatus[i]);
      Firebase.RTDB.setBool(&fbdo, slotPath + "/free_led_status", slotFreeLedStatus[i]);
    } else {
      Firebase.RTDB.setString(&fbdo, slotPath + "/status", "empty");
      Firebase.RTDB.setString(&fbdo, slotPath + "/start_time", "00:00:00");
      Firebase.RTDB.setString(&fbdo, slotPath + "/duration", "0h 0m 0s");
      Firebase.RTDB.setString(&fbdo, slotPath + "/number_plate", "");
      Firebase.RTDB.setBool(&fbdo, slotPath + "/led_status", slotLedStatus[i]);
      Firebase.RTDB.setBool(&fbdo, slotPath + "/free_led_status", slotFreeLedStatus[i]);
    }
  }
}

void addParkingHistory(int slotNumber, time_t startTime, time_t endTime, String plate) {
  String historyPath = "/parking_history/slot" + String(slotNumber) + "/" + String(startTime);
  
  FirebaseJson historyRecord;
  historyRecord.set("start_time", formatDateTime(startTime));
  historyRecord.set("end_time", formatDateTime(endTime));
  historyRecord.set("number_plate", plate);
  
  time_t duration = endTime - startTime;
  historyRecord.set("duration", formatDuration(duration));
  
  if (Firebase.RTDB.set(&fbdo, historyPath, &historyRecord)) {
    Serial.printf("Parking history added for slot %d: Plate %s\n", slotNumber, plate.c_str());
  } else {
    Serial.println("Failed to add parking history: " + fbdo.errorReason());
  }
}

void updateDisplay() {
  int availableSlots = 0;
  for (int i = 0; i < 4; i++) {
    if (!parkingSlots[i].isParked) availableSlots++;
  }

  lcd.clear();
  if (gateOpen && showWelcome && (millis() - welcomeStartTime < 1000)) {
    lcd.setCursor(0, 0);
    lcd.print("Welcome!");
  } else {
    showWelcome = false;
    lcd.setCursor(0, 0);
    if (gateOpen) {
      if (availableSlots == 0) {
        lcd.print("Slots Full");
      } else {
        lcd.print("Available: ");
        lcd.print(availableSlots);
      }
    } else {
      lcd.print("Free Slots: ");
      lcd.print(availableSlots);
    }
  }
}

void loop() {
  unsigned long currentTime = millis();
  if (currentTime - lastSendTime >= sendInterval && Firebase.ready()) {
    lastSendTime = currentTime;

    // Read sensor data - LOW means parked (sensor blocked)
    bool irStatus[4] = {
      digitalRead(IR1_PIN) == LOW,
      digitalRead(IR2_PIN) == LOW,
      digitalRead(IR3_PIN) == LOW,
      digitalRead(IR4_PIN) == LOW
    };
    
    unsigned int distance = sonar.ping_cm();

    // Read LDR sensor
    int ldrValue = analogRead(LDR_PIN);  
    Serial.print("LDR Value: ");
    Serial.println(ldrValue);  // Debug LDR value

    // Light control with hysteresis
    if (ldrValue > LDR_THRESHOLD_ON && !lightOn) {
      digitalWrite(LIGHT_PIN, HIGH);  // Turn on LDR-controlled light
      lightOn = true;
      Firebase.RTDB.setBool(&fbdo, "/light/status", true);
      Serial.println("Light turned ON (LDR covered, high value)");
    } else if (ldrValue < LDR_THRESHOLD_OFF && lightOn) {
      digitalWrite(LIGHT_PIN, LOW);   // Turn off LDR-controlled light
      lightOn = false;
      Firebase.RTDB.setBool(&fbdo, "/light/status", false);
      Serial.println("Light turned OFF (LDR uncovered, low value)");
    }
    // Update LDR value in Firebase
    Firebase.RTDB.setInt(&fbdo, "/sensor_data/ldr", ldrValue);

    // Read gate status from Firebase
    bool firebaseGateStatus = false;
    if (Firebase.RTDB.getBool(&fbdo, "/gate/status")) {
      firebaseGateStatus = fbdo.boolData();
      Serial.printf("Firebase gate status: %s\n", firebaseGateStatus ? "true" : "false");
    } else {
      Serial.println("Failed to read gate status: " + fbdo.errorReason());
    }

    // Gate control: Ultrasonic and Firebase
    bool ultrasonicOpen = (distance > 0 && distance < 10);
    if (!gateOpen && (firebaseGateStatus || ultrasonicOpen)) {
      gateOpen = true;
      gateServo.write(90); // Open gate
      gateOpenStartTime = millis();
      showWelcome = true;
      welcomeStartTime = millis();
      Firebase.RTDB.setBool(&fbdo, "/gate/status", true);
      Serial.println(ultrasonicOpen ? "Gate opened (Ultrasonic distance < 10 cm)" : "Gate opened (Firebase status: true)");
    } else if (gateOpen && !firebaseGateStatus && !ultrasonicOpen && (millis() - gateOpenStartTime >= gateOpenDuration)) {
      gateOpen = false;
      gateServo.write(0);  // Close gate
      Firebase.RTDB.setBool(&fbdo, "/gate/status", false);
      Serial.println("Gate closed (Firebase: false, Ultrasonic: >= 10 cm or invalid, duration elapsed)");
    }

    // Update parking status and control slot LEDs
    for (int i = 0; i < 4; i++) {
      if (irStatus[i] && !parkingSlots[i].isParked) {
        // Vehicle just parked
        parkingSlots[i].startTime = time(nullptr);
        parkingSlots[i].isParked = true;
        slotLedStatus[i] = true;
        slotFreeLedStatus[i] = false;
        digitalWrite(LED_PINS[i], HIGH); // Parked LED ON
        digitalWrite(FREE_LED_PINS[i], LOW); // Free LED OFF
        Serial.printf("Slot %d: Vehicle parked at %s, Parked LED ON, Free LED OFF\n", i+1, formatTime(parkingSlots[i].startTime).c_str());
        
        // Fetch number plate from Firebase
        String platePath = "/current_status/slot" + String(i+1) + "/number_plate";
        if (Firebase.RTDB.getString(&fbdo, platePath)) {
          parkingSlots[i].plate = fbdo.stringData();
          Serial.printf("Slot %d: Number plate fetched: %s\n", i+1, parkingSlots[i].plate.c_str());
        } else {
          parkingSlots[i].plate = "Unknown";
          Serial.println("Failed to fetch number plate for slot " + String(i+1) + ": " + fbdo.errorReason());
        }
      } else if (!irStatus[i] && parkingSlots[i].isParked) {
        // Vehicle just left
        parkingSlots[i].endTime = time(nullptr);
        parkingSlots[i].isParked = false;
        slotLedStatus[i] = false;
        slotFreeLedStatus[i] = true;
        digitalWrite(LED_PINS[i], LOW); // Parked LED OFF
        digitalWrite(FREE_LED_PINS[i], HIGH); // Free LED ON
        Serial.printf("Slot %d: Vehicle left at %s, Parked LED OFF, Free LED ON\n", i+1, formatTime(parkingSlots[i].endTime).c_str());
        // Add to parking history with number plate
        addParkingHistory(i+1, parkingSlots[i].startTime, parkingSlots[i].endTime, parkingSlots[i].plate);
        parkingSlots[i].plate = ""; // Clear plate after recording
      } else {
        // Maintain LED states
        if (parkingSlots[i].isParked) {
          slotLedStatus[i] = true;
          slotFreeLedStatus[i] = false;
          digitalWrite(LED_PINS[i], HIGH); // Parked LED ON
          digitalWrite(FREE_LED_PINS[i], LOW); // Free LED OFF
        } else {
          slotLedStatus[i] = false;
          slotFreeLedStatus[i] = true;
          digitalWrite(LED_PINS[i], LOW); // Parked LED OFF
          digitalWrite(FREE_LED_PINS[i], HIGH); // Free LED ON
        }
      }
    }

    // Update current status in Firebase
    updateCurrentParkingStatus();

    // Update distance
    if (distance > 0) {
      Firebase.RTDB.setInt(&fbdo, "/sensor_data/distance", distance);
    }

    // Update LCD display
    updateDisplay();

    Serial.println("Data updated successfully");
    printLocalTime(); // Optional: Print current time 
  }
}