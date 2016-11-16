#ifndef BRAKE_h
#define BRAKE_h
#include "mbed.h"

namespace ik {

class Brake {
  private:
    int state;
    void fall();
    void rise();

    DigitalOut* press;
    DigitalOut* release;
    InterruptIn* interrupt;

  public:
    void setup();
    void calibrate();
    void processCommand(int command);
    void applyCommand();
    int getState();
};

extern Brake brake;

} // namespace ik

#endif
