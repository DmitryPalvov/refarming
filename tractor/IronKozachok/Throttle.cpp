#include "mbed.h"
#include "Throttle.h"
#include "ConfigKozachok.h"

#define THROTTLE_DEFAULT_RELAY_STATE 1
#define THROTTLE_ACTION_RELAY_STATE 0

#define IS_THROTTLE_SERVO

namespace ik {

#ifndef IS_THROTTLE_SERVO

void Throttle::setup() {

    press = new DigitalOut(THROTTLE_PRESS_PIN, THROTTLE_DEFAULT_RELAY_STATE);
    release = new DigitalOut(THROTTLE_RELEASE_PIN, THROTTLE_DEFAULT_RELAY_STATE);
    return;
}

void Throttle::calibrate() {
  return;
}

void Throttle::processCommand(int command) {
  switch(command) {
    case 1: // pressing throttle         
      release->write(THROTTLE_DEFAULT_RELAY_STATE);
      press->write(THROTTLE_ACTION_RELAY_STATE);
      break;
    case 2: // releasing brake
      press->write(THROTTLE_DEFAULT_RELAY_STATE);
      release->write(THROTTLE_ACTION_RELAY_STATE);
      break;
    case 0:          
    default: //doing nothing
      press->write(THROTTLE_DEFAULT_RELAY_STATE);
      release->write(THROTTLE_DEFAULT_RELAY_STATE);
      break;
  }
  return;
}

void Throttle::applyCommand() {  
}


#else

void Throttle::setup() {
  servo = new PwmOut(THROTTLE_PWM_PIN);
  servo->write(0.0f); //todo
  servo->period_ms(1);

  position = new AnalogIn(THROTTLE_POSITION_ANALOG_PIN);
  wantedPosition = 0;
  minPosition = 128;
  maxPosition = 512;
  this->calibrate();
//this->superCalibrate();
}

void Throttle::processCommand(int command) {
  this->wantedPosition = command;

/*  switch(command) {
    case 1: // pressing brake         
      servo->write(1.0f); //todo
      break;
    case 2: // releasing brake
      servo->write(0.0f); //todo
      break;
    case 0:          
    default: //doing nothing
      break;
  }*/
  return;
}

void Throttle::superCalibrate() {
  printf("Throttle super calibrate start\n");
   int pos = getPosition();
   int p = pos;
   int p1 = pos;
   int p2 = pos;
   double a=0.0;
   for(;a<1.0;a+=0.02) {
        servo->write(0.0);
	wait(0.5);
	servo->write(a);
	wait(0.1);
	p = getPosition();
	wait(0.1);
	p1 = getPosition();
	wait(0.2);
	p2 = getPosition();

	if (p>pos+6) {
	  printf("S:%i R:%i R2:%i R3:%i U:%.2f\n", pos, p, p1, p2, a);
	}
	if (p>pos+200) break;
   }
//139, 223
  printf("S:%i R:%i U:%.2f\n", pos, p, a);
  servo->write(0.0);
/*printf("Linear\n");
  for (int i = 0; i<10;i++) {
     processCommand(i);
     applyCommand();
     wait(4);
     printf("%i %i\n", i, getPosition());

  }
printf("Stair\n");
  for (int i = 0; i<10;i++) {
     processCommand(0);
     applyCommand();
     wait(1);
     processCommand(i);
     applyCommand();
     wait(4);
     printf("%i %i\n", i, getPosition());
  }
  */
}

void Throttle::calibrate() {
  printf("Throttle calibrate start\n");
  wait(1);
  servo->write(1.0);
  wait(1);
  maxPosition = getPosition();
  servo->write(0.0);
  wait(1);
  minPosition = getPosition();
  printf("Throttle calibrate end [%i, %i]\n", minPosition, maxPosition);
//superCalibrate();
  return;
}

double Throttle::computeDuty() {
    int in = getPosition();
    prevPosition = in;
    in = (in-minPosition)*10/(maxPosition-minPosition);
    if (in < 0) in = 0;
    if (in > 9) in = 9;
    int want = wantedPosition; 
    if (want < 0) want = 0;
    if (want > 9) want = 9;

    if (in > want) {
	want = want - (in - want);
        if (want < 0) want = 0;
    }
    double res = arrayPos1[want];
//printf("In:%i To:%i V:%.2f Pr:%i R:%.2f\n",in, want, arrayPos[want][in], prevPosition, res);

/*    int batteryValue = batteryMain.read_u16();
//    if(batteryValue > 15000 && batteryValue < 40000) { //is ok
    if(batteryValue > 4000 && batteryValue < 40000) { //is ok
	//30000                                     
res = res * 2* 31000 / batteryValue;
//	res = res * 31000 / batteryValue;
    } else {
	//printf("Voltage error [%i]\n",batteryValue);
	res = 0.0;
    }*/
//printf("In:%i To:%i V:%.2f B:%i Pr:%i R:%.2f\n",in, want, arrayPos1[want], batteryValue, prevPosition, res);
    if (res > 1.0) res = 1.0;
    if (res < 0.0) res = 0.0;
    return res;
}


void Throttle::applyCommand() {  
  servo->write(this->computeDuty());
}

#endif

int Throttle::getPosition() {
    return (int)(position->read_u16()>>8);
}

Throttle throttle;

} // namespace ik