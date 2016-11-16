#include "mbed.h"
#include "ConfigKozachok.h"

Serial  pc(USBTX, USBRX);

namespace ik {

StoreStruct storage = {
  CONFIG_VERSION,
  0,
  1023,
  511,
  0,
  1023,
  0,
  1023,
  0,
  1023,
  400
};

unsigned long previousTime = 0;
unsigned long currentTime = 0;
unsigned long deltaTime = 0;
Timer timer;

int counter = 0;
bool isModeCalibrate = false;

short emptyPinCount = 30;
short emptyPins[] = {3, 4, 5, 6, 7, 8, 9, 10, 11, 12, //some 10
  14, 15, 16, 17, 18, 19, 20, 21, //rx,tx 8
  30, 32, 34, 36, // 4
  46, 47, 48, 49, // 4
  50, 51, 52, 53 // 4
};

//TODO

/*
void saveConfig() {
  Serial.println("Putting config to EEPROM...");
  for (unsigned int t=0; t<sizeof(storage); t++)
    EEPROM.write(CONFIG_START + t, *((char*)&storage + t));
  Serial.println("Putting done");
}
*/

/*
void loadConfig() {
  // To make sure there are settings, and they are YOURS!
  // If nothing is found it will use the default settings.
  Serial.println("Init config...");
  if (EEPROM.read(CONFIG_START + 0) == CONFIG_VERSION[0] &&
      EEPROM.read(CONFIG_START + 1) == CONFIG_VERSION[1] &&
      EEPROM.read(CONFIG_START + 2) == CONFIG_VERSION[2]) {
    Serial.println("Reading from EEPROM ...");
    for (unsigned int t=0; t<sizeof(storage); t++)
      *((char*)&storage + t) = EEPROM.read(CONFIG_START + t);
    Serial.println("Reading from EEPROM done");
  } else {
    Serial.println("Invalid config version"); 
    saveConfig();
  }
}
*/
AnalogIn steeringPosition(STEERING_POSITION_ANALOG_PIN);
AnalogIn batteryMain(BATTERY_MAIN_ANALOG_PIN);
AnalogIn batteryExtra(BATTERY_EXTRA_ANALOG_PIN);

AnalogIn currentMeter(CURRENT_METER_0_ANALOG_PIN);


/*    int printf(const char* format, ...) {
        std::va_list arg;
	bool a = true;
	if (a) {
		return pc.printf(format, arg);
	} else {
		return 0;
	}
    }*/

} // namespace ik