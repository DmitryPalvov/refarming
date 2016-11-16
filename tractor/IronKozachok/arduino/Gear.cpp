#include "Arduino.h"
#include "ConfigKozachok.h"
#include "Gear.h"

#define GEAR_DEFAULT_RELAY_STATE HIGH
#define GEAR_ACTION_RELAY_STATE LOW

Gear::Gear() {
  this->gearCommandPosition = 0;
}

void Gear::setup() {
  this->gearCommandPosition = storage.gearNeutralValue;

  //relay
  pinMode(GEAR_UP_PIN, OUTPUT);
  digitalWrite(GEAR_UP_PIN, GEAR_DEFAULT_RELAY_STATE);
  pinMode(GEAR_DOWN_PIN, OUTPUT);
  digitalWrite(GEAR_DOWN_PIN, GEAR_DEFAULT_RELAY_STATE);

  //todo
  pinMode(GEAR_POSITION_REAR_PIN, INPUT_PULLUP);
  pinMode(GEAR_POSITION_NEUTRAL_PIN, INPUT_PULLUP);
  pinMode(GEAR_POSITION_1_PIN, INPUT_PULLUP);
  pinMode(GEAR_POSITION_2_PIN, INPUT_PULLUP);
  pinMode(GEAR_POSITION_3_PIN, INPUT_PULLUP);
  digitalWrite(GEAR_POSITION_REAR_PIN, HIGH);
  digitalWrite(GEAR_POSITION_NEUTRAL_PIN, HIGH);
  digitalWrite(GEAR_POSITION_1_PIN, HIGH);
  digitalWrite(GEAR_POSITION_2_PIN, HIGH);
  digitalWrite(GEAR_POSITION_3_PIN, HIGH);


  return;
}

void Gear::calibrate() {
  /*Serial.println("Calibrate gear ...");

  Serial.println("Finding min position ...");
  digitalWrite(GEAR_UP_PIN, HIGH);
  digitalWrite(GEAR_DOWN_PIN, LOW);
  delay(200);

  bool isChanging = true;
  int i = 0;
  int maxLoop = 100;
  double prevVal = 0;
  double tp = 0;
  while (isChanging && i<maxLoop) {
    tp = analogRead(GEAR_POSITION_ANALOG_PIN);
    if (tp == prevVal) {
      storage.gearMinValue = tp;
      isChanging = false;
      Serial.print("Podsos min: ");
      Serial.println(storage.gearMinValue);
    } else {
      Serial.print(i);
      Serial.print(" ");
      Serial.println(tp);
      prevVal = tp;
      i++;
      delay(5);
    }
  }
  digitalWrite(GEAR_UP_PIN, HIGH);
  digitalWrite(GEAR_DOWN_PIN, HIGH);
  
  if (isChanging) {
      Serial.print("Signal is too noisy. Last value is: ");
      Serial.println(prevVal);
      Serial.print("Aborting calibration");
      return;
  }
  
  Serial.println("Finding max position ...");
  digitalWrite(GEAR_UP_PIN, LOW);
  digitalWrite(GEAR_DOWN_PIN, HIGH);
  delay(200);
  isChanging = true;
  i = 0;
  maxLoop = 100;
  prevVal = 0;
  while (isChanging && i<maxLoop) {
    tp = analogRead(GEAR_POSITION_ANALOG_PIN);
    if (tp == prevVal) {
      storage.gearMaxValue = tp;
      isChanging = false;
      Serial.print("Podsos max: ");
      Serial.println(storage.gearMaxValue);
    } else {
      Serial.print(i);
      Serial.print(" ");
      Serial.println(tp);
      prevVal = tp;
      i++;      
      delay(5);
    }
  }
  digitalWrite(GEAR_UP_PIN, HIGH);
  digitalWrite(GEAR_DOWN_PIN, HIGH);
  
  if (isChanging) {
      Serial.print("Signal is too noisy. Last value is: ");
      Serial.println(prevVal);
      Serial.print("Aborting calibration");
      return;
  }

  Serial.println("Calibrate gear done");*/

  return;
}

void Gear::processCommand(int command) {
  switch(command) {
    case 1: // pressing gear up         
      digitalWrite(GEAR_UP_PIN, GEAR_ACTION_RELAY_STATE);
      digitalWrite(GEAR_DOWN_PIN, GEAR_DEFAULT_RELAY_STATE);
      break;
    case 2: // releasing gear down
      digitalWrite(GEAR_DOWN_PIN, GEAR_ACTION_RELAY_STATE);
      digitalWrite(GEAR_UP_PIN, GEAR_DEFAULT_RELAY_STATE);
      break;
    case 0:          
    default: //doing nothing
      digitalWrite(GEAR_DOWN_PIN, GEAR_DEFAULT_RELAY_STATE);
      digitalWrite(GEAR_UP_PIN, GEAR_DEFAULT_RELAY_STATE);
      break;
  }
  return;
  //todo
  /*switch(command) {
    case 1: //down
      this->gearCommandPosition = storage.gearMinValue;
      break;
    case 2: // up
      this->gearCommandPosition = storage.gearMaxValue;
      break;
    default: // do nothing
      this->gearCommandPosition = storage.gearNeutralValue;
      break;            
  } */   
  return;
}

void Gear::applyCommand() {
  /*double gp = analogRead(GEAR_POSITION_ANALOG_PIN);

  if (LOGLEVEL >= LOG_LEVEL_DEBUG && counter%100 == 0) {
    Serial.write("Gear pos: ");
    Serial.print(gp);
    Serial.write(" ");
    Serial.print(this->gearCommandPosition);
    Serial.println();        
  }

  if (abs(gp - this->gearCommandPosition) < 10) {
    digitalWrite(GEAR_UP_PIN, HIGH);    
    digitalWrite(GEAR_DOWN_PIN, HIGH);
  } else if (gp > this->gearCommandPosition) { // up
    digitalWrite(GEAR_UP_PIN, LOW);
    digitalWrite(GEAR_DOWN_PIN, HIGH);
  } else if (gp < this->gearCommandPosition) { //down
    digitalWrite(GEAR_UP_PIN, HIGH);
    digitalWrite(GEAR_DOWN_PIN, LOW);
  } */


}

int Gear::getCurrentGear() {
  int r  = (digitalRead(GEAR_POSITION_REAR_PIN)==HIGH)?0:1;
  int n  = (digitalRead(GEAR_POSITION_NEUTRAL_PIN)==HIGH)?0:1;
  int f1 = (digitalRead(GEAR_POSITION_1_PIN)==HIGH)?0:1;
  int f2 = (digitalRead(GEAR_POSITION_2_PIN)==HIGH)?0:1;
  int f3 = (digitalRead(GEAR_POSITION_3_PIN)==HIGH)?0:1;
  return (f3<<4 | f2<<3 | f1<<2 | n<<1 | r);
}

Gear gear;
