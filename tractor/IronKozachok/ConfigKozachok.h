#ifndef CONFIG_KOZACHOK
#define CONFIG_KOZACHOK

#include "mbed.h"
#include <cstdarg>
/*
  public static final short STEERENG_ID = 0;
  public static final short THROTTLE_ID = 1;
  public static final short BRAKE_ID = 2;
  public static final short GEAR_ID = 3;
  public static final short DIFFERENTIAL_ID = 4;
  public static final short PODSOS_ID = 5;
  public static final short HORN_ID = 6;
  public static final short FRONT_LIGHT_ID = 7;
  public static final short REAR_LIGHT_ID = 8;
  public static final short ENGINE_FAN_ID = 9;
  public static final short STARTER_ID = 10;
  public static final short IGNITION_MASS_ID = 11;
  public static final short FUEL_CUT_ID = 12;
  public static final short HALT_ENGINE_ID = 13;

  public static final short RASPBERRY_ID = 14;
  public static final short ROUTER_ID = 15;
  public static final short CAMERA_1_ID = 16;

*/  

#define LOG_LEVEL_NOOUTPUT 0 
#define LOG_LEVEL_ERRORS 1
#define LOG_LEVEL_INFOS 2
#define LOG_LEVEL_DEBUG 3
#define LOG_LEVEL_VERBOSE 4

#define PAUSE_TRACTOR_TIMEOUT 2000000
#define HALT_TRACTOR_TIMEOUT  200000000 //5000000

// default loglevel if nothing is set from user
#define LOGLEVEL LOG_LEVEL_DEBUG 

#define CONFIG_VERSION "v00"
#define CONFIG_START 0

#define STEERING_LEFT_PIN D2
#define STEERING_RIGHT_PIN D3

#define STEERING_POSITION_ANALOG_PIN PA_1
#define CURRENT_METER_0_ANALOG_PIN PA_1

#define THROTTLE_PWM_PIN PC_2 
#define THROTTLE_POSITION_ANALOG_PIN PA_0

#define BRAKE_PRESS_PIN D4
#define BRAKE_RELEASE_PIN D5

#define BRAKE_STATE_PIN PD_2

#define GEAR_PRESS_PIN D6
#define GEAR_RELEASE_PIN D7


#define GEAR_POSITION_REAR_PIN PC_10
#define GEAR_POSITION_NEUTRAL_PIN PC_12
#define GEAR_POSITION_1_PIN PA_13
#define GEAR_POSITION_2_PIN PA_14
#define GEAR_POSITION_3_PIN PA_15

#define DIFF_PRESS_PIN D9
#define DIFF_RELEASE_PIN D8

//#define PODSOS_PRESS_PIN PA_14
//#define PODSOS_RELEASE_PIN PA_15

//#define PODSOS_DIRECTION_PIN 34
//#define PODSOS_STEP_PIN 36
//#define PODSOS_MOTOR_ENABLE_PIN ??

#define PLUG_PRESS_PIN PC_4
#define PLUG_RELEASE_PIN PB_13

#define HORN_PIN PB_2
#define FRONT_LIGHT_PIN PB_1 
#define ENGINE_FAN_PIN PB_15
#define IGNITION_PIN PB_14 

#define ON_MASS_PIN PC_8
#define HALT_ENGINE_PIN PC_6


#define USER_BUTTON_PIN PC_13

#define ROUTER_PIN PC_9 
#define CAMERA_0_PIN PB_8
#define CAMERA_1_PIN PB_9
//#define CAMERA_2_PIN PC_15
#define RASPBERRY_PIN CAMERA_1_PIN

#define BATTERY_MAIN_ANALOG_PIN PC_0
#define BATTERY_EXTRA_ANALOG_PIN PC_1

//#define SERIAL_LOGGER_ENABLE
#ifdef SERIAL_LOGGER_ENABLE 
	#define LOG(format, ...) \
	pc.printf(format, ##__VA_ARGS__)
#endif
#ifndef SERIAL_LOGGER_ENABLE 
	#define LOG(format, ...) \
	
#endif


extern Serial pc;

namespace ik {

struct StoreStruct {
  // This is for mere detection if they are your settings
  char version[4];
  double steeringLeftValue;
  double steeringRightValue;
  double steeringCenterValue;
  double throttleMinValue;
  double throttleMaxValue;
  double podsosMinValue;
  double podsosMaxValue;
  double gearMinValue;
  double gearMaxValue;
  double gearNeutralValue;
};
extern StoreStruct storage;

// main loop time variables
extern unsigned long previousTime;
extern unsigned long currentTime;
extern unsigned long deltaTime;

extern Timer timer;

// loop counter
extern int counter;
extern bool isModeCalibrate;
extern short emptyPinCount;
extern short emptyPins[];

extern AnalogIn steeringPosition;
extern AnalogIn batteryMain;
extern AnalogIn batteryExtra;
extern AnalogIn currentMeter0;

//int printf(const char* format, ...);
//void saveConfig();
//void loadConfig();

} //namespace ik

#endif
