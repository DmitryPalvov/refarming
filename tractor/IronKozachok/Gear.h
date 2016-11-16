#ifndef GEAR_h
#define GEAR_h
#include "mbed.h"

namespace ik {

class Gear {
  private:
    DigitalOut* press;
    DigitalOut* release;
    BusIn* currentGear;
  public:
    void setup();
    void calibrate();
    void processCommand(int command);
    void applyCommand();
    int getCurrentGear();
};

extern Gear gear;

} // namespace ik

#endif
