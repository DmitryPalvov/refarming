#ifndef STEERING_h
#define STEERING_h
#include "mbed.h"

namespace ik {

class Steering {
  private:
    DigitalOut* press;
    DigitalOut* release;
  public:
    void setup();
    void calibrate();
    void processCommand(int command);
    void applyCommand();
};

extern Steering steering;

} // namespace ik

#endif
