#include "Arduino.h"
#include "Throttle.h"
#include "ConfigKozachok.h"

#define THROTTLE_DEFAULT_RELAY_STATE HIGH
#define THROTTLE_ACTION_RELAY_STATE LOW


Throttle::Throttle() {
  this->throttleSpeed = 190;
  this->throttleCommandPosition = 0;
}

void Throttle::setup() {
    //relay
    pinMode(THROTTLE_UP_PIN, OUTPUT);
    digitalWrite(THROTTLE_UP_PIN, THROTTLE_DEFAULT_RELAY_STATE);
    pinMode(THROTTLE_DOWN_PIN, OUTPUT);
    digitalWrite(THROTTLE_DOWN_PIN, THROTTLE_DEFAULT_RELAY_STATE);
    return;
}


/*void Throttle::setup() { 
    //motor driver
    pinMode(THROTTLE_SPEED_PIN, OUTPUT);
    analogWrite(THROTTLE_SPEED_PIN, 0);  
    pinMode(THROTTLE_UP_PIN, OUTPUT);
    digitalWrite(THROTTLE_UP_PIN, HIGH);
    pinMode(THROTTLE_DOWN_PIN, OUTPUT);
    digitalWrite(THROTTLE_DOWN_PIN, HIGH);
    return;
}*/

void Throttle::calibrate() {
  /*
  Serial.println("Calibrate throttle ...");

  Serial.println("Finding min position ...");
  digitalWrite(THROTTLE_UP_PIN, LOW);
  digitalWrite(THROTTLE_DOWN_PIN, HIGH);
  analogWrite(THROTTLE_SPEED_PIN, this->throttleSpeed); 
  delay(200);

  bool isChanging = true;
  int i = 0;
  int maxLoop = 100;
  double prevVal = 0;
  double tp = 0;
  while (isChanging && i<maxLoop) {
    tp = analogRead(THROTTLE_POSITION_ANALOG_PIN);
    if (tp == prevVal) {
      storage.throttleMinValue = tp;
      isChanging = false;
      Serial.print("Throttle min: ");
      Serial.println(storage.throttleMinValue);
    } else {
      Serial.print(i);
      Serial.print(" ");
      Serial.println(tp);
      prevVal = tp;
      i++;
      delay(5);
    }
  }
  digitalWrite(THROTTLE_UP_PIN, LOW);
  digitalWrite(THROTTLE_DOWN_PIN, LOW);
  analogWrite(THROTTLE_SPEED_PIN, 0); 
  
  if (isChanging) {
      Serial.print("Signal is too noisy. Last value is: ");
      Serial.println(prevVal);
      Serial.print("Aborting calibration");
      return;
  }
  
  Serial.println("Finding max position ...");
  analogWrite(THROTTLE_SPEED_PIN, this->throttleSpeed);     
  digitalWrite(THROTTLE_UP_PIN, HIGH);
  digitalWrite(THROTTLE_DOWN_PIN, LOW);
  delay(200);
  isChanging = true;
  i = 0;
  maxLoop = 100;
  prevVal = 0;
  tp = 0;
  while (isChanging && i<maxLoop) {
    tp = analogRead(THROTTLE_POSITION_ANALOG_PIN);
    if (tp == prevVal) {
      storage.throttleMaxValue = tp;
      isChanging = false;
      Serial.print("Throttle max: ");
      Serial.println(storage.throttleMaxValue);
    } else {
      Serial.print(i);
      Serial.print(" ");
      Serial.println(tp);
      prevVal = tp;
      i++;      
      delay(5);
    }
  }
  digitalWrite(THROTTLE_UP_PIN, LOW);
  digitalWrite(THROTTLE_DOWN_PIN, LOW);
  analogWrite(THROTTLE_SPEED_PIN, 0); 
  
  if (isChanging) {
      Serial.print("Signal is too noisy. Last value is: ");
      Serial.println(prevVal);
      Serial.print("Aborting calibration");
      return;
  }

  Serial.println("Calibrate throttle done");

*/
  return;
}

void Throttle::processCommand(int command) {
  switch(command) {
    case 1: // pressing throttle         
      digitalWrite(THROTTLE_UP_PIN, THROTTLE_ACTION_RELAY_STATE);
      digitalWrite(THROTTLE_DOWN_PIN, THROTTLE_DEFAULT_RELAY_STATE);
      break;
    case 2: // releasing throttle
      digitalWrite(THROTTLE_DOWN_PIN, THROTTLE_ACTION_RELAY_STATE);
      digitalWrite(THROTTLE_UP_PIN, THROTTLE_DEFAULT_RELAY_STATE);
      break;
    case 0:          
    default: //doing nothing
      digitalWrite(THROTTLE_DOWN_PIN, THROTTLE_DEFAULT_RELAY_STATE);
      digitalWrite(THROTTLE_UP_PIN, THROTTLE_DEFAULT_RELAY_STATE);
      break;
  }
  return;

/*  this->throttleCommandPosition = command;        
  return;*/
}

void Throttle::applyCommand() {
  /*double tp = analogRead(THROTTLE_POSITION_ANALOG_PIN);
  int throttlePosition = map(tp, storage.throttleMinValue, storage.throttleMaxValue, 0, 9);
  double throttleCommandPositionMap = map(this->throttleCommandPosition, 0, 9, storage.throttleMinValue, storage.throttleMaxValue);
  
  if (LOGLEVEL >= LOG_LEVEL_DEBUG && counter%100 == 0) {
    Serial.write("Throttle pos");
    Serial.print(tp);
    Serial.write(" ");
    Serial.print(throttlePosition);
    Serial.write(" ");
    Serial.print(throttleCommandPositionMap);
    Serial.write(" ");
    Serial.print(this->throttleCommandPosition);
    Serial.println();        
  }
  
  if (abs(tp - throttleCommandPositionMap) < 10) { //todo think about pid
    digitalWrite(THROTTLE_UP_PIN, LOW);    
    analogWrite(THROTTLE_SPEED_PIN, 0); 
    //TODO make keep speed
  } else if (throttleCommandPositionMap > tp) {
    digitalWrite(THROTTLE_UP_PIN, HIGH);    
    analogWrite(THROTTLE_SPEED_PIN, this->throttleSpeed);
  } else {
    digitalWrite(THROTTLE_UP_PIN, LOW);    
    analogWrite(THROTTLE_SPEED_PIN, 0);     
    // TODO use THROTTLE_DOWN_PIN if needed
  }*/

}

Throttle throttle;
