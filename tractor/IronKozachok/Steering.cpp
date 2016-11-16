#include "mbed.h"
#include "Steering.h"
#include "ConfigKozachok.h"

#define STEERING_DEFAULT_RELAY_STATE 1
#define STEERING_ACTION_RELAY_STATE 0

namespace ik {

void Steering::setup() {
      //left
    press = new DigitalOut(STEERING_LEFT_PIN, STEERING_DEFAULT_RELAY_STATE);
//right
    release = new DigitalOut(STEERING_RIGHT_PIN, STEERING_DEFAULT_RELAY_STATE);

    return;
}

void Steering::calibrate() {
  return;
}

void Steering::processCommand(int command) {
  switch(command) {
    case 1: // left
      release->write(STEERING_DEFAULT_RELAY_STATE);
      press->write(STEERING_ACTION_RELAY_STATE);
      break;
    case 9: // releasing brake
      press->write(STEERING_DEFAULT_RELAY_STATE);
      release->write(STEERING_ACTION_RELAY_STATE);
      break;
    case 5:          
    default: //doing nothing
      press->write(STEERING_DEFAULT_RELAY_STATE);
      release->write(STEERING_DEFAULT_RELAY_STATE);
      break;
  }
  return;
}

void Steering::applyCommand() {  
}

Steering steering;

} // namespace ik