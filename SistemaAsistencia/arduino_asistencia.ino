#include <Adafruit_Fingerprint.h>
#include <SoftwareSerial.h>

SoftwareSerial mySerial(2, 3);
Adafruit_Fingerprint finger = Adafruit_Fingerprint(&mySerial);

void setup() {
  Serial.begin(9600);
  while (!Serial);
  delay(100);
  finger.begin(57600);
  if (finger.verifyPassword()) {
    Serial.println("READY");
  } else {
    Serial.println("ERROR_SENSOR");
    while (1) { delay(1); }
  }
}

void loop() {
  if (Serial.available()) {
    char command = Serial.read();
    if (command == 'E') { // Enroll mode
      enrollFingerprint();
    } else if (command == 'V') { // Verification mode (Attendance)
      verifyFingerprint();
    }
  }
  delay(50);
}

void enrollFingerprint() {
  // Wait for ID
  while (!Serial.available());
  int id = Serial.parseInt();
  if (id == 0) return;

  int p = -1;
  Serial.println("WAIT_FINGER");
  while (p != FINGERPRINT_OK) {
    p = finger.getImage();
  }

  p = finger.image2Tz(1);
  if (p != FINGERPRINT_OK) {
    Serial.println("ERR_CONV");
    return;
  }

  Serial.println("REMOVE_FINGER");
  delay(2000);
  p = 0;
  while (p != FINGERPRINT_NOFINGER) {
    p = finger.getImage();
  }

  p = -1;
  Serial.println("REPLACE_FINGER");
  while (p != FINGERPRINT_OK) {
    p = finger.getImage();
  }

  p = finger.image2Tz(2);
  if (p != FINGERPRINT_OK) {
    Serial.println("ERR_CONV");
    return;
  }

  p = finger.createModel();
  if (p != FINGERPRINT_OK) {
    Serial.println("ERR_MODEL");
    return;
  }

  p = finger.storeModel(id);
  if (p == FINGERPRINT_OK) {
    Serial.print("SAVED:");
    Serial.println(id);
  } else {
    Serial.println("ERR_SAVE");
  }
}

void verifyFingerprint() {
  Serial.println("SCANNING");
  int p = -1;
  
  // Bucle de espera infinita hasta detectar un dedo o recibir comando de parada
  while (true) {
    p = finger.getImage();
    if (p == FINGERPRINT_OK) break;
    
    // Si hay un comando entrante, abortamos para atenderlo (ej: detener verificación)
    if (Serial.available()) {
      return; 
    }
    delay(100);
  }

  // Una vez capturada la imagen, procesamos
  p = finger.image2Tz();
  if (p != FINGERPRINT_OK) {
    Serial.println("ERR_CONV");
    return;
  }

  p = finger.fingerFastSearch();
  if (p == FINGERPRINT_OK) {
    Serial.print("FOUND:");
    Serial.print(finger.fingerID);
    Serial.print(",");
    Serial.println(finger.confidence);
  } else if (p == FINGERPRINT_NOTFOUND) {
    Serial.println("NOT_FOUND");
  } else {
    Serial.println("ERR_SEARCH");
  }
}
