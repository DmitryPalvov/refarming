#ifndef PODSOS_CLASS
#define PODSOS_CLASS


#include "Arduino.h"
#include "ConfigKozachok.h"

double podsosCommandPosition = 0;
int podsosCurrentStep = 0;

class Podsos /*implements IActuator*/{
  public:
  void setup() {
    //relay
    pinMode(PODSOS_UP_PIN, OUTPUT);
    digitalWrite(PODSOS_UP_PIN, HIGH);
    pinMode(PODSOS_DOWN_PIN, OUTPUT);
    digitalWrite(PODSOS_DOWN_PIN, HIGH);
    return;
  }

  void calibrate() {
    Serial.println("Calibrate podsos ...");

    Serial.println("Finding min position ...");
    digitalWrite(PODSOS_UP_PIN, HIGH);
    digitalWrite(PODSOS_DOWN_PIN, LOW);
    delay(200);
  
    bool isChanging = true;
    int i = 0;
    int maxLoop = 100;
    double prevVal = 0;
    double tp = 0;
    while (isChanging && i<maxLoop) {
      double tp = analogRead(PODSOS_POSITION_ANALOG_PIN);
      if (tp == prevVal) {
        storage.podsosMinValue = tp;
        isChanging = false;
        Serial.print("Podsos min: ");
        Serial.println(storage.podsosMinValue);
      } else {
        Serial.print(i);
        Serial.print(" ");
        Serial.println(tp);
        prevVal = tp;
        i++;
        delay(5);
      }
    }
    digitalWrite(PODSOS_UP_PIN, HIGH);
    digitalWrite(PODSOS_DOWN_PIN, HIGH);
    
    if (isChanging) {
        Serial.print("Signal is too noisy. Last value is: ");
        Serial.println(prevVal);
        Serial.print("Aborting calibration");
        return;
    }
    
    Serial.println("Finding max position ...");
    digitalWrite(PODSOS_UP_PIN, LOW);
    digitalWrite(PODSOS_DOWN_PIN, HIGH);
    delay(200);
    isChanging = true;
    i = 0;
    maxLoop = 100;
    prevVal = 0;
    tp = 0;
    while (isChanging && i<maxLoop) {
      double tp = analogRead(PODSOS_POSITION_ANALOG_PIN);
      if (tp == prevVal) {
        storage.podsosMaxValue = tp;
        isChanging = false;
        Serial.print("Podsos max: ");
        Serial.println(storage.podsosMaxValue);
      } else {
        Serial.print(i);
        Serial.print(" ");
        Serial.println(tp);
        prevVal = tp;
        i++;      
        delay(5);
      }
    }
    digitalWrite(PODSOS_UP_PIN, HIGH);
    digitalWrite(PODSOS_DOWN_PIN, HIGH);
    
    if (isChanging) {
        Serial.print("Signal is too noisy. Last value is: ");
        Serial.println(prevVal);
        Serial.print("Aborting calibration");
        return;
    }
  
    Serial.println("Calibrate podsos done");

    return;
  }

  void processCommand(int command) {
    podsosCommandPosition = command;
    return;
  }

  void applyCommand() {
    double pp = analogRead(PODSOS_POSITION_ANALOG_PIN);
    int podsosPosition = map(pp, storage.podsosMinValue, storage.podsosMaxValue, 0, 9);
    double podsosCommandPositionMap = map(podsosCommandPosition, 0, 9, storage.podsosMinValue, storage.podsosMaxValue);
    
    if (LOGLEVEL >= LOG_LEVEL_DEBUG && counter%100 == 0) {
      Serial.write("Podsos pos");
      Serial.print(pp);
      Serial.write(" ");
      Serial.print(podsosPosition);
      Serial.write(" ");
      Serial.print(podsosCommandPositionMap);
      Serial.write(" ");
      Serial.print(podsosCommandPosition);
      Serial.println();        
    }
    
    if (abs(pp - podsosCommandPositionMap) < 10) {
      digitalWrite(PODSOS_UP_PIN, HIGH);    
      digitalWrite(PODSOS_DOWN_PIN, HIGH);
    } else if (pp > podsosCommandPositionMap) { // up
      digitalWrite(PODSOS_UP_PIN, LOW);
      digitalWrite(PODSOS_DOWN_PIN, HIGH);
    } else if (pp < podsosCommandPositionMap) { //down
      digitalWrite(PODSOS_UP_PIN, HIGH);
      digitalWrite(PODSOS_DOWN_PIN, LOW);
    } else { //ok
      
    }

  }

  void homePosition() {
    Serial.println("Podsos home start ...");
  
    int i;
    int pause = 500;
    int maxSteps = 300;
    digitalWrite(PODSOS_DIRECTION_PIN, HIGH); // Set the direction.
    delay(3000);
    for (i = 0; i<maxSteps; i++) // Iterate for 4000 microsteps.
    {
      digitalWrite(PODSOS_STEP_PIN, LOW); // This LOW to HIGH change is what creates the
      delayMicroseconds(pause);
      digitalWrite(PODSOS_STEP_PIN, HIGH);
      delayMicroseconds(pause); // This delay time is close to top speed for this
    } // particular motor. Any faster the motor stalls.
    Serial.println("Podsos home done");
  }
};

#endif
