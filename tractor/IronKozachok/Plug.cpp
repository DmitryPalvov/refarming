#include "mbed.h"
#include "Plug.h"
#include "ConfigKozachok.h"

#define PLUG_DEFAULT_RELAY_STATE 1
#define PLUG_ACTION_RELAY_STATE 0

namespace ik {

void Plug::setup() {

    press = new DigitalOut(PLUG_PRESS_PIN, PLUG_DEFAULT_RELAY_STATE);
    release = new DigitalOut(PLUG_RELEASE_PIN, PLUG_DEFAULT_RELAY_STATE);

    //todo
    /*pinMode(PLUG_POSITION_PIN, INPUT_PULLUP);
    digitalWrite(PLUG_POSITION_PIN, HIGH);*/
    return;
}

void Plug::calibrate() {
  return;
}

void Plug::processCommand(int command) {
  switch(command) {
    case 1: // pressing brake         
      release->write(PLUG_DEFAULT_RELAY_STATE);
      press->write(PLUG_ACTION_RELAY_STATE);
      break;
    case 2: // releasing brake
      press->write(PLUG_DEFAULT_RELAY_STATE);
      release->write(PLUG_ACTION_RELAY_STATE);
      break;
    case 0:          
    default: //doing nothing
      press->write(PLUG_DEFAULT_RELAY_STATE);
      release->write(PLUG_DEFAULT_RELAY_STATE);
      break;
  }
  return;
}

void Plug::applyCommand() {  
}

Plug plug;

} // namespace ik