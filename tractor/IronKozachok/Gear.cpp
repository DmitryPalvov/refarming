#include "mbed.h"
#include "Gear.h"
#include "ConfigKozachok.h"

#define GEAR_DEFAULT_RELAY_STATE 1
#define GEAR_ACTION_RELAY_STATE 0

namespace ik {

void Gear::setup() {

    press = new DigitalOut(GEAR_PRESS_PIN, GEAR_DEFAULT_RELAY_STATE);
    release = new DigitalOut(GEAR_RELEASE_PIN, GEAR_DEFAULT_RELAY_STATE);

    currentGear = new BusIn(
	GEAR_POSITION_REAR_PIN, 
	GEAR_POSITION_NEUTRAL_PIN,
	GEAR_POSITION_1_PIN, 
	GEAR_POSITION_2_PIN,
	GEAR_POSITION_3_PIN);
    currentGear->mode(PullUp);
    return;
}
//DigitalOut ind(LED4);


int Gear::getCurrentGear() {
//         int pins = currentGear->read();
/*         if(pins) {
             ind = 1;
         } else {
             ind = 0;
         }*/
// printf("Pos: %i\n", pins);
    return currentGear->read();
}


void Gear::calibrate() {
  return;
}

void Gear::processCommand(int command) {
  switch(command) {
    case 1: // pressing brake         
      release->write(GEAR_DEFAULT_RELAY_STATE);
      press->write(GEAR_ACTION_RELAY_STATE);
      break;
    case 2: // releasing brake
      press->write(GEAR_DEFAULT_RELAY_STATE);
      release->write(GEAR_ACTION_RELAY_STATE);
      break;
    case 0:          
    default: //doing nothing
      press->write(GEAR_DEFAULT_RELAY_STATE);
      release->write(GEAR_DEFAULT_RELAY_STATE);
      break;
  }
  return;
}

void Gear::applyCommand() {  
}

Gear gear;

} // namespace ik