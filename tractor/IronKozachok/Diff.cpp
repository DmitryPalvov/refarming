#include "mbed.h"
#include "Diff.h"
#include "ConfigKozachok.h"

#define DIFF_DEFAULT_RELAY_STATE 1
#define DIFF_ACTION_RELAY_STATE 0

namespace ik {

void Diff::setup() {

    press = new DigitalOut(DIFF_PRESS_PIN, DIFF_DEFAULT_RELAY_STATE);
    release = new DigitalOut(DIFF_RELEASE_PIN, DIFF_DEFAULT_RELAY_STATE);

    return;
}

void Diff::calibrate() {
  return;
}

void Diff::processCommand(int command) {
  switch(command) {
    case 1: // pressing diff
      release->write(DIFF_DEFAULT_RELAY_STATE);
      press->write(DIFF_ACTION_RELAY_STATE);
      break;
    case 2: // releasing diff
      press->write(DIFF_DEFAULT_RELAY_STATE);
      release->write(DIFF_ACTION_RELAY_STATE);
      break;
    case 0:          
    default: //doing nothing
      press->write(DIFF_DEFAULT_RELAY_STATE);
      release->write(DIFF_DEFAULT_RELAY_STATE);
      break;
  }
  return;
}

void Diff::applyCommand() {  
}

Diff diff;

} // namespace ik