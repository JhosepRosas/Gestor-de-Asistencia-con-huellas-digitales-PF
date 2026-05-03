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
  
  while (true) {
    int p = finger.getImage();
    
    // Si no hay dedo, simplemente seguimos esperando en el bucle
    if (p == FINGERPRINT_NOFINGER) {
      if (Serial.available()) return; // Abortar si llega un nuevo comando
      delay(100);
      continue;
    }

    // Si hay un dedo, intentamos procesarlo
    if (p == FINGERPRINT_OK) {
      p = finger.image2Tz();
      if (p != FINGERPRINT_OK) {
        // Si la imagen fue mala, no enviamos error a Java, 
        // simplemente esperamos a que el usuario acomode mejor el dedo
        continue; 
      }

      p = finger.fingerFastSearch();
      if (p == FINGERPRINT_OK) {
        Serial.print("FOUND:");
        Serial.print(finger.fingerID);
        Serial.print(",");
        Serial.println(finger.confidence);
        
        // Esperar a que quite el dedo para no spamear la misma lectura
        while (finger.getImage() != FINGERPRINT_NOFINGER) { delay(100); }
        return; // Salir y esperar nueva orden 'V' de Java
      } else if (p == FINGERPRINT_NOTFOUND) {
        Serial.println("NOT_FOUND");
        
        // Esperar a que quite el dedo
        while (finger.getImage() != FINGERPRINT_NOFINGER) { delay(100); }
        return; // Salir y esperar nueva orden 'V' de Java
      }
    }
    delay(100);
  }
}
