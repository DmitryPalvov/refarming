#ifndef DIFFERENTIAL_CLASS
#define DIFFERENTIAL_CLASS


#include "Arduino.h"
#include "ConfigKozachok.h"

class Differential /*implements IActuator*/{
  void setup() {
       //uln2009 + relay
      pinMode(DIFFERENTIAL_BLOCK_PIN, OUTPUT);
      digitalWrite(DIFFERENTIAL_BLOCK_PIN, LOW);
      pinMode(DIFFERENTIAL_UNBLOCK_PIN, OUTPUT);
      digitalWrite(DIFFERENTIAL_UNBLOCK_PIN, LOW);
      pinMode(DIFFERENTIAL_POSITION_PIN, INPUT_PULLUP);
      digitalWrite(DIFFERENTIAL_POSITION_PIN, HIGH);


      return;
  }

  void calibrate() {
    return;
  }

  void processCommand(int command) {
    if (command==1) {          
      digitalWrite(DIFFERENTIAL_BLOCK_PIN, HIGH);
    } else {
      digitalWrite(DIFFERENTIAL_BLOCK_PIN, LOW);
    }
    //todo if needed reverse
    
    return;
  }
};

#endif
