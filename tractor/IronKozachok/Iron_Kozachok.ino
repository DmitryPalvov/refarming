/*
 * Kozachok
 */

#include <EEPROM.h>
#include <SPI.h>
#include <Base64.h>
#include <Ethernet.h>
#include <CRC16.h>


#include "ConfigKozachok.h"
#include "Brake.h"
#include "Gear.h"
#include "Steering.h"
#include "Throttle.h"
#include "Networker.h"

int ledState = LOW;
void setup() {

  
  Serial.begin(9600);  
  Serial.println("Init pins...");

  /*TODO
   * for(int i = 0; i<emptyPinCount; i++) {
    pinMode(emptyPins[i], OUTPUT);
    digitalWrite(emptyPins[i], LOW);
  }*/

  brake.setup();
  gear.setup();
  steering.setup();
  throttle.setup();
  //differential.setup();
  //podsos.setup();

  pinMode(13, OUTPUT);
  digitalWrite(13, LOW);  

  //relay
  pinMode(HORN_PIN, OUTPUT);
  digitalWrite(HORN_PIN, HIGH);
  pinMode(FRONT_LIGHT_PIN, OUTPUT);
  digitalWrite(FRONT_LIGHT_PIN, HIGH);
  pinMode(REAR_LIGHT_PIN, OUTPUT);
  digitalWrite(REAR_LIGHT_PIN, HIGH);
  pinMode(STOP_LIGHT_PIN, OUTPUT);
  digitalWrite(STOP_LIGHT_PIN, HIGH);  
  pinMode(HALT_ENGINE_PIN, OUTPUT);
  digitalWrite(HALT_ENGINE_PIN, HIGH);
  pinMode(ENGINE_FAN_PIN, OUTPUT);
  digitalWrite(ENGINE_FAN_PIN, HIGH);
  pinMode(STARTER_PIN, OUTPUT);
  digitalWrite(STARTER_PIN, HIGH);
  pinMode(IGNITION_MASS_PIN, OUTPUT);
  digitalWrite(IGNITION_MASS_PIN, HIGH);
  //pinMode(FUEL_CUT_PIN, OUTPUT);
  
  //relay
  pinMode(RASPBERRY_PIN, OUTPUT);
  digitalWrite(RASPBERRY_PIN, HIGH);  
  pinMode(ROUTER_PIN, OUTPUT);
  digitalWrite(ROUTER_PIN, HIGH);
  pinMode(CAMERA_1_PIN, OUTPUT);
  digitalWrite(CAMERA_1_PIN, HIGH);
  
  Serial.println("Init done");
  
  loadConfig();
  //char d[24] = "ER:377:343:313:1443z9M=";

  udpSetup();
 // calibrateLoop();
  //loopTest();
}

void loop(){
  //loopTest();
  loopFull();
}

void loopTest(){
  digitalWrite(BRAKE_DOWN_PIN, LOW);
  delay(500);
  digitalWrite(BRAKE_DOWN_PIN, HIGH);
  delay(500);
  digitalWrite(BRAKE_RELEASE_PIN, LOW);
  delay(500);
  digitalWrite(BRAKE_RELEASE_PIN, HIGH);
  delay(500);

  digitalWrite(THROTTLE_UP_PIN, LOW);
  delay(500);
  digitalWrite(THROTTLE_UP_PIN, HIGH);
  delay(500);
  digitalWrite(THROTTLE_DOWN_PIN, LOW);
  delay(500);
  digitalWrite(THROTTLE_DOWN_PIN, HIGH);
  delay(500);

  digitalWrite(GEAR_UP_PIN, LOW);
  delay(500);
  digitalWrite(GEAR_UP_PIN, HIGH);
  delay(500);
  digitalWrite(GEAR_DOWN_PIN, LOW);
  delay(500);
  digitalWrite(GEAR_DOWN_PIN, HIGH);
  delay(500);

  digitalWrite(STEERING_LEFT_PIN, LOW);
  delay(500);
  digitalWrite(STEERING_LEFT_PIN, HIGH);
  delay(500);
  digitalWrite(STEERING_RIGHT_PIN, LOW);
  delay(500);
  digitalWrite(STEERING_RIGHT_PIN, HIGH);
  delay(500);


}
void loopFull() {
  if (counter%100 ==0) {
    if (ledState == HIGH) {
      digitalWrite(13, LOW);
      ledState = LOW;
    } else {
      digitalWrite(13, HIGH);
      ledState = HIGH;
    }
  }
  currentTime = micros();
  deltaTime = currentTime - previousTime;
  previousTime = currentTime;
  udpLoop();  
  if (micros() - lastCommandTime > 2500000) {
    connectionAbortStop();
  }
  processCommand();
  applyCommand();
  counter++;
  //todo 
  delay(10);
}


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

void processCommand() {
  if (LOGLEVEL >= LOG_LEVEL_DEBUG && counter%100 == 0) {
    Serial.println(command);    
  }
  //int pos;
        
  for (int commandIndex = 0; commandIndex < sizeof(command); commandIndex++) { 
    char c = command[commandIndex];
    int i = c - '0';
    switch(commandIndex) {
      case 0: //steereng
        steering.processCommand(i);
        break;
      case 1: //throttle
        throttle.processCommand(i);
        break;
      case 2: //brake
        brake.processCommand(i);
        break;
      case 3: //gear
        gear.processCommand(i);
        break;
      /*case 4: //differential
        //differential.processCommand(i);
        break;
      case 5: //podsos
        //podsos.processCommand(i);
        break;*/
      case 6: //horn
        if (c=='1') {          
          digitalWrite(HORN_PIN, LOW);
        } else {
          digitalWrite(HORN_PIN, HIGH);
        }
        break;
      case 7: //front_light
        if (c=='1') {          
          digitalWrite(FRONT_LIGHT_PIN, LOW);
        } else {
          digitalWrite(FRONT_LIGHT_PIN, HIGH);
        }
        break;
      case 8: //rear_light
        if (c=='1') {          
          digitalWrite(REAR_LIGHT_PIN, LOW);
        } else {
          digitalWrite(REAR_LIGHT_PIN, HIGH);
        }        
        break;
      case 9: //engine_fan
        if (c=='1') {          
          digitalWrite(ENGINE_FAN_PIN, LOW);
        } else {
          digitalWrite(ENGINE_FAN_PIN, HIGH);
        }        
        break;
      case 10: //starter
        if (c=='1') {          
          digitalWrite(STARTER_PIN, LOW);
        } else {
          digitalWrite(STARTER_PIN, HIGH);
        }        
        break;
      case 11: //ignition mass
        if (c=='1') {          
          digitalWrite(IGNITION_MASS_PIN, LOW);
        } else {
          digitalWrite(IGNITION_MASS_PIN, HIGH);
        }        
        break;
      case 12: //fuel cut
        //TODO
        break;
      case 13: //halt engine
        if (c=='1') {          
          digitalWrite(HALT_ENGINE_PIN, LOW);
        } else {
          digitalWrite(HALT_ENGINE_PIN, HIGH);
        }
        break;
      case 14: //raspberry
        if (c=='1') {          
          digitalWrite(RASPBERRY_PIN, LOW);
        } else {
          digitalWrite(RASPBERRY_PIN, HIGH);
        }
        break;
      case 15: //router
        // todo
        break;
      case 16: //camera 1
        if (c=='1') {          
          digitalWrite(CAMERA_1_PIN, LOW);
        } else {
          digitalWrite(CAMERA_1_PIN, HIGH);
        }
        break;
    } // switch    
  } // for
} // function

void applyCommand() {
  brake.applyCommand();
  gear.applyCommand();
  steering.applyCommand();
  throttle.applyCommand();
  //podsos.applyCommand();
  //differential.applyCommand();  
} // function



int freeRam () {
  extern int __heap_start, *__brkval;
  int v;
  return (int) &v - (__brkval == 0 ? (int) &__heap_start : (int) __brkval);
}

void calibratePotentiometers() {
/*throttle*/
/*steering*/
/*podsos*/
/*gear*/

}

void calibrateLoop() {
  
  Serial.println("Calibration start ...");
  brake.calibrate();  
  throttle.calibrate();
  steering.calibrate();
  gear.calibrate();
  //podsos.calibrate();
  //differential.calibrate();
  Serial.println("Calibration done");

  saveConfig();  
}

