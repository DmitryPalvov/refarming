#include "Arduino.h"
#include "Steering.h"
#include "ConfigKozachok.h"

#define STEERING_DEFAULT_RELAY_STATE HIGH
#define STEERING_ACTION_RELAY_STATE LOW


Steering::Steering() {
  this->steeringSpeed = 190;
  this->steeringCommandPosition = 5;
}


void Steering::setup() {
  //motor driver
  /*pinMode(STEERING_SPEED_PIN, OUTPUT);
  analogWrite(STEERING_SPEED_PIN, 0);*/
  pinMode(STEERING_LEFT_PIN, OUTPUT);
  digitalWrite(STEERING_LEFT_PIN, HIGH);
  pinMode(STEERING_RIGHT_PIN, OUTPUT);
  digitalWrite(STEERING_RIGHT_PIN, HIGH);
  //todo position
  return;
}

void Steering::calibrate() {
  Serial.println("Calibrate steering ...");

  Serial.println("Finding left position ...");
  digitalWrite(STEERING_LEFT_PIN, LOW);
  digitalWrite(STEERING_RIGHT_PIN, HIGH);
  analogWrite(STEERING_SPEED_PIN, steeringSpeed); 
  delay(200);

  bool isChanging = true;
  int i = 0;
  int maxLoop = 100;
  double prevVal = 0;
  double tp = 0;
  while (isChanging && i<maxLoop) {
    tp = analogRead(STEERING_POSITION_ANALOG_PIN);
    if (tp == prevVal) {
      storage.steeringLeftValue = tp;
      isChanging = false;
      Serial.print("Steering left: ");
      Serial.println(storage.steeringLeftValue);
    } else {
      Serial.print(i);
      Serial.print(" ");
      Serial.println(tp);
      prevVal = tp;
      i++;
      delay(5);
    }
  }
  digitalWrite(STEERING_LEFT_PIN, LOW);
  digitalWrite(STEERING_RIGHT_PIN, LOW);
  analogWrite(STEERING_SPEED_PIN, 0); 
  
  if (isChanging) {
      Serial.print("Signal is too noisy. Last value is: ");
      Serial.println(prevVal);
      Serial.print("Aborting calibration");
      return;
  }
  
  Serial.println("Finding right position ...");
  analogWrite(STEERING_SPEED_PIN, this->steeringSpeed);     
  digitalWrite(STEERING_LEFT_PIN, HIGH);
  digitalWrite(STEERING_RIGHT_PIN, LOW);
  delay(200);
  isChanging = true;
  i = 0;
  maxLoop = 100;
  prevVal = 0;
  tp = 0;
  while (isChanging && i<maxLoop) {
    double tp = analogRead(STEERING_POSITION_ANALOG_PIN);
    if (tp == prevVal) {
      storage.steeringRightValue = tp;
      isChanging = false;
      Serial.print("Steering right: ");
      Serial.println(storage.steeringRightValue);
    } else {
      Serial.print(i);
      Serial.print(" ");
      Serial.println(tp);
      prevVal = tp;
      i++;      
      delay(5);
    }
  }
  digitalWrite(STEERING_LEFT_PIN, LOW);
  digitalWrite(STEERING_RIGHT_PIN, LOW);
  analogWrite(STEERING_SPEED_PIN, 0); 
  
  if (isChanging) {
      Serial.print("Signal is too noisy. Last value is: ");
      Serial.println(prevVal);
      Serial.print("Aborting calibration");
      return;
  }

  Serial.println("Calibrate steering done");
  return;
}

void Steering::processCommand(int command) {
//  this->steeringCommandPosition = command;
  switch(command) {
    case 1: // moving left         
      digitalWrite(STEERING_LEFT_PIN, STEERING_ACTION_RELAY_STATE);
      digitalWrite(STEERING_RIGHT_PIN, STEERING_DEFAULT_RELAY_STATE);
      break;
    case 9: // moving right
      digitalWrite(STEERING_RIGHT_PIN, STEERING_ACTION_RELAY_STATE);
      digitalWrite(STEERING_LEFT_PIN, STEERING_DEFAULT_RELAY_STATE);
      break;
    case 5:          
    default: //doing nothing
      digitalWrite(STEERING_LEFT_PIN, STEERING_DEFAULT_RELAY_STATE);
      digitalWrite(STEERING_RIGHT_PIN, STEERING_DEFAULT_RELAY_STATE);
      break;
  }
  return;
}

void Steering::applyCommand() {
/*
  double sp = analogRead(STEERING_POSITION_ANALOG_PIN);
  int steeringPosition = map(sp, storage.steeringLeftValue, storage.steeringRightValue, 0, 9);
  double steeringCommandPositionMap = map(this->steeringCommandPosition, 0, 9, storage.steeringLeftValue, storage.steeringRightValue);
  
  if (LOGLEVEL >= LOG_LEVEL_DEBUG && counter%100 == 0) {
    Serial.write("Steering pos");
    Serial.print(sp);
    Serial.write(" ");
    Serial.print(steeringPosition);
    Serial.write(" ");
    Serial.print(steeringCommandPositionMap);
    Serial.write(" ");
    Serial.print(this->steeringCommandPosition);
    Serial.println();        
  }
  
  if (abs(sp - steeringCommandPositionMap) < 10) { //todo think about pid
    digitalWrite(STEERING_LEFT_PIN, LOW);    
    digitalWrite(STEERING_RIGHT_PIN, LOW);
    analogWrite(STEERING_SPEED_PIN, 0);
  } else if (sp > steeringCommandPositionMap) { // left
    digitalWrite(STEERING_RIGHT_PIN, LOW);
    digitalWrite(STEERING_LEFT_PIN, HIGH);
    analogWrite(STEERING_SPEED_PIN, this->steeringSpeed);
  } else if (sp < steeringCommandPositionMap) { //right
    digitalWrite(STEERING_RIGHT_PIN, HIGH);
    digitalWrite(STEERING_LEFT_PIN, LOW);
    analogWrite(STEERING_SPEED_PIN, this->steeringSpeed);
  } else { //ok
    
  }
*/
}

Steering steering;
