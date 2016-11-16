#include "mbed.h"
#include "Brake.h"
#include "ConfigKozachok.h"

#define BRAKE_DEFAULT_RELAY_STATE 1
#define BRAKE_ACTION_RELAY_STATE 0

namespace ik {

void Brake::fall() {

    if (press->read()==BRAKE_ACTION_RELAY_STATE) {

	this->state=2;
    }else if (release->read()==BRAKE_ACTION_RELAY_STATE) {
	this->state=1;
    }
}

void Brake::rise() {

    if (press->read()==BRAKE_ACTION_RELAY_STATE || release->read()==BRAKE_ACTION_RELAY_STATE) {
	this->state=0;
    }
}


void Brake::setup() {
    state = 0;
    press = new DigitalOut(BRAKE_PRESS_PIN, BRAKE_DEFAULT_RELAY_STATE);
    release = new DigitalOut(BRAKE_RELEASE_PIN, BRAKE_DEFAULT_RELAY_STATE);

    interrupt = new InterruptIn(BRAKE_STATE_PIN);
//    interrupt->disable_irq();
    interrupt->mode(PullUp);
    interrupt->rise(this,&Brake::rise);
    interrupt->fall(this,&Brake::fall);

    return;
}

void Brake::calibrate() {
  return;
}

void Brake::processCommand(int command) {
  switch(command) {
    case 1: // pressing brake         
      release->write(BRAKE_DEFAULT_RELAY_STATE);
      press->write(BRAKE_ACTION_RELAY_STATE);
      break;
    case 2: // releasing brake
      press->write(BRAKE_DEFAULT_RELAY_STATE);
      release->write(BRAKE_ACTION_RELAY_STATE);
      break;
    case 0:          
    default: //doing nothing
      press->write(BRAKE_DEFAULT_RELAY_STATE);
      release->write(BRAKE_DEFAULT_RELAY_STATE);
      break;
  }
  return;
}

void Brake::applyCommand() {  
}

int Brake::getState() {  
	return state;
}

Brake brake;

} // namespace ik