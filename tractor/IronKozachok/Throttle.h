#ifndef THROTTLE_h
#define THROTTLE_h
#include "mbed.h"

namespace ik {

    static const double arrayPos[10][10] = {
{0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00}, //0
{0.11,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00}, //1
{0.15,0.15,0.00,0.00,0.00,0.00,0.00,0.00,0.00,0.00}, //2
{0.16,0.16,0.16,0.16,0.00,0.00,0.00,0.00,0.00,0.00}, //3
{0.19,0.19,0.19,0.19,0.19,0.00,0.00,0.00,0.00,0.00}, //4
{0.20,0.20,0.20,0.20,0.20,0.20,0.00,0.00,0.00,0.00}, //5
{0.24,0.24,0.24,0.24,0.24,0.24,0.24,0.00,0.00,0.00}, //6
{0.25,0.25,0.25,0.25,0.25,0.25,0.25,0.25,0.00,0.00}, //7
{0.29,0.29,0.29,0.29,0.29,0.29,0.29,0.29,0.29,0.00}, //8
{0.32,0.32,0.32,0.32,0.32,0.32,0.32,0.32,0.32,0.32}  //9
};
    static const double arrayPos1[10] = 
//{0.00,0.25,0.30,0.31,0.33,0.35, 0.38, 0.41, 0.45, 0.49};
{0.00,0.50,0.64,0.70,0.78,0.90, 0.94, 1.00, 1.00, 1.00};

class Throttle {
  private:
/*    DigitalOut* press;
    DigitalOut* release;*/

    PwmOut* servo;
    AnalogIn* position;
    int wantedPosition;
    int prevPosition;
    int minPosition;
    int maxPosition;

void superCalibrate();
  public:

    void setup();
    void calibrate();
    void processCommand(int command);
    void applyCommand();
    double computeDuty();
    int getPosition();
};

extern Throttle throttle;

} //namespace

#endif
