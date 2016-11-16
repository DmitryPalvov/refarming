//#ifndef BRAKE_CLASS
//#define BRAKE_CLASS

#include "Arduino.h"
#include "Brake.h"
#include "ConfigKozachok.h"

#define BRAKE_DEFAULT_RELAY_STATE HIGH
#define BRAKE_ACTION_RELAY_STATE LOW

void Brake::setup() {
    //relay
    pinMode(BRAKE_DOWN_PIN, OUTPUT);
    digitalWrite(BRAKE_DOWN_PIN, BRAKE_DEFAULT_RELAY_STATE);
    pinMode(BRAKE_RELEASE_PIN, OUTPUT);
    digitalWrite(BRAKE_RELEASE_PIN, BRAKE_DEFAULT_RELAY_STATE);
    //todo
    /*pinMode(BRAKE_POSITION_PIN, INPUT_PULLUP);
    digitalWrite(BRAKE_POSITION_PIN, HIGH);*/
    return;
}

void Brake::calibrate() {
  return;
}

void Brake::processCommand(int command) {
  switch(command) {
    case 1: // pressing brake         
      digitalWrite(BRAKE_DOWN_PIN, BRAKE_ACTION_RELAY_STATE);
      digitalWrite(BRAKE_RELEASE_PIN, BRAKE_DEFAULT_RELAY_STATE);
      break;
    case 2: // releasing brake
      digitalWrite(BRAKE_RELEASE_PIN, BRAKE_ACTION_RELAY_STATE);
      digitalWrite(BRAKE_DOWN_PIN, BRAKE_DEFAULT_RELAY_STATE);
      break;
    case 0:          
    default: //doing nothing
      digitalWrite(BRAKE_RELEASE_PIN, BRAKE_DEFAULT_RELAY_STATE);
      digitalWrite(BRAKE_DOWN_PIN, BRAKE_DEFAULT_RELAY_STATE);
      break;
  }
  return;
}

void Brake::applyCommand() {  
}

Brake brake;
