#ifndef DIFF_h
#define DIFF_h
#include "mbed.h"

namespace ik {

class Diff {
  private:
    DigitalOut* press;
    DigitalOut* release;
  public:
    void setup();
    void calibrate();
    void processCommand(int command);
    void applyCommand();
};

extern Diff diff;

} // namespace ik

#endif
