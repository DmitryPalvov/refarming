#ifndef PLUG_h
#define PLUG_h
#include "mbed.h"

namespace ik {

class Plug {
  private:
    DigitalOut* press;
    DigitalOut* release;
  public:
    void setup();
    void calibrate();
    void processCommand(int command);
    void applyCommand();
};

extern Plug plug;

} // namespace ik

#endif
