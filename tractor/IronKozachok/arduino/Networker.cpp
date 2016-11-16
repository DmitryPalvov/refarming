
#include "Arduino.h"
#include "Networker.h"
#include "ConfigKozachok.h"
#include "Gear.h"
#include <SPI.h>
#include <Base64.h>
#include <Ethernet.h>
#include <CRC16.h>

byte mac[] = {
  0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED
};
IPAddress ip(192, 168, 0, 177);

EthernetUDP udp;

const char verify[3] = {'c','o', '\0'};
const char verifyInit[3] = {'i','n', '\0'};
char command[18] = {
                    '5','0','0','0','0',
                    '0','0','0','0','0',
                    '0','0','0','0','0',
                    '1','0','\0'};
char defaultCommand[18] = {
                    '5','0','0','0','0',
                    '0','0','0','0','0',
                    '0','0','0','0','0',
                    '1','0','\0'};

//50000000000000010
unsigned long lastCommandTime = 0;

char packetBuffer[UDP_TX_PACKET_MAX_SIZE*2]; //buffer to hold incoming packet,
char replyBuffer[128];// = "00000000000000000000000";       // a string to send back

long currentVccCacheTime = 0;
long currentVccCache = 0;



boolean checkCrc(char* data, int num) {
  char crcString[3];
  int l = base64_decode(crcString, data+num-4, 4);
  if (l != 2) {
    return false;
  } else {
    unsigned char checkCrcVal[3];
    crc16(checkCrcVal, (unsigned char *)data, num-4);
    if (checkCrcVal[0] == (unsigned char)crcString[0] && checkCrcVal[1] == (unsigned char)crcString[1]) {
      return true;  
    } else {
      return false;
    }
  }
}

long readVcc() {
  if (currentVccCache && currentTime - currentVccCacheTime < 1000000) {
    return currentVccCache;
  }
  // Read 1.1V reference against AVcc
  // set the reference to Vcc and the measurement to the internal 1.1V reference
  #if defined(__AVR_ATmega32U4__) || defined(__AVR_ATmega1280__) || defined(__AVR_ATmega2560__)
    ADMUX = _BV(REFS0) | _BV(MUX4) | _BV(MUX3) | _BV(MUX2) | _BV(MUX1);
  #elif defined (__AVR_ATtiny24__) || defined(__AVR_ATtiny44__) || defined(__AVR_ATtiny84__)
    ADMUX = _BV(MUX5) | _BV(MUX0);
  #elif defined (__AVR_ATtiny25__) || defined(__AVR_ATtiny45__) || defined(__AVR_ATtiny85__)
    ADMUX = _BV(MUX3) | _BV(MUX2);
  #else
    ADMUX = _BV(REFS0) | _BV(MUX3) | _BV(MUX2) | _BV(MUX1);
  #endif  

  //todo !!!
  delay(2); // Wait for Vref to settle
  ADCSRA |= _BV(ADSC); // Start conversion
  while (bit_is_set(ADCSRA,ADSC)); // measuring

  uint8_t low  = ADCL; // must read ADCL first - it then locks ADCH  
  uint8_t high = ADCH; // unlocks both

  long result = (high<<8) | low;

  //result = 1125300L / result; // Calculate Vcc (in mV); 1125300 = 1.1*1023*1000
  result = 1106735L / result;
  currentVccCacheTime = currentTime;
  currentVccCache = result;
  return result; // Vcc in millivolts
}


void udpSetup() {
  // give the Ethernet shield a second to initialize:
  delay(1000);
  Serial.println("Init network...");
  // start the Ethernet and UDP:
  Ethernet.begin(mac, ip);
  int res = udp.begin(23);
  if (res) {
    Serial.println("Init network done");  
  } else {
    Serial.println("Init network failed");  
  }  
}

void udpRestart() {
  Serial.println("Reinit network...");
  // start the Ethernet and UDP:
  udp.stop();
  int res = udp.begin(23);
  int ii = 3;
  if (res) {
    Serial.println("Reinit network done");  
  } else {
    ii = 7;
    Serial.println("Reinit network failed");  
  }
  for (int i=0; i<ii;i++) {
    digitalWrite(13, LOW);
    delay(100);
    digitalWrite(13, HIGH);
    delay(100);
  }

}


boolean readCommand(char* temp, int len) {
  if (!checkCrc(temp, len-1)) return false;
  int vlen = sizeof (verify);
  for(int i = 0; i < vlen-1; i++) {
    if (i==1) break; //todo
    if (temp[i] != verify[i]) {
      return false;
    }
  }
  memcpy(command, temp+vlen-1, sizeof(command)-1);
  Serial.println(command);
  lastCommandTime = micros();
  return true;  
} // function
  
void softReset() {
  //todo
  Serial.println("Reset");
  digitalWrite(12, LOW);
  delay(200);
}

void connectionAbortStop() {
  if (counter%100 == 0) {
    Serial.println("No heartbeat - STOP");
    udpRestart();  
    /*if (counter%1000 == 0){
      softReset();
    }*/
  }
  memcpy(command, defaultCommand, sizeof(defaultCommand)-1);
  //todo use command      
}




void sendResponse(boolean isError) {
  
  String response;
  if (isError) {
    response = "OK";
  } else {
    response = "ER";
  }
  
  response += ":";
  response += analogRead(STEERING_POSITION_ANALOG_PIN); // steering
  response += ":";
  response += analogRead(BATTERY_MAIN_ANALOG_PIN); // batt1
  response += ":";
  //response += analogRead(BATTERY_EXTRA_ANALOG_PIN); // batt2
  response += readVcc(); // batt2
  response += ":";
  response += analogRead(THROTTLE_POSITION_ANALOG_PIN);
  response += ":";
  response += gear.getCurrentGear(); // gear
  response += ":";
  response += analogRead(GEAR_POSITION_ANALOG_PIN); // gear pos
  response += ":";
  response += analogRead(PODSOS_POSITION_ANALOG_PIN); // gear pos
  response += ":";
  response += digitalRead(BRAKE_POSITION_PIN); // break pos
  response += ":";
  response += digitalRead(DIFFERENTIAL_POSITION_PIN); // differential pos
  response += ":";
  response += "NA"; // gas level
  response += ":";
  response += "NA"; // temp1 level
  response += ":";
  response += "NA"; // temp2 level
  response += ":";
  response += "NA"; // temp3 level
  response += ":";
  response += "NA"; // speed level
  response += ":";
  response += "NA"; // rpm level
  //response += ":";
  
  //response += freeRam(); // ram
// steering, batt1, batt2, throttle, gear, gearPos, podsos, break, block_diff, gas_level, temp1, temp2, temp3, speed, rpm, 

  
  int l = response.length();
  response.toCharArray(replyBuffer, l+1);
  unsigned char checkCrcVal[3];
  crc16(checkCrcVal, (unsigned char *)replyBuffer, l);
  base64_encode(replyBuffer+l, (char *)checkCrcVal, 2);
  Serial.println(replyBuffer);
}

void udpLoop() {
  // if there's data available, read a packet
  int packetSize = udp.parsePacket();
  if (packetSize) {
    /*Serial.print("Received packet of size ");
    Serial.println(packetSize);*/
    Serial.print("From ");
    IPAddress remote = udp.remoteIP();
    for (int i = 0; i < 4; i++) {
      Serial.print(remote[i], DEC);
      if (i < 3) {
        Serial.print(".");
      }
    }
    Serial.print(", port ");
    Serial.println(udp.remotePort());

    // read the packet into packetBufffer
    udp.read(packetBuffer, UDP_TX_PACKET_MAX_SIZE*2);
    Serial.println("Contents:");
    Serial.println(packetBuffer);
    boolean res = readCommand(packetBuffer, packetSize);
    udp.beginPacket(udp.remoteIP(), udp.remotePort());
    sendResponse(res);
    // send a reply, to the IP address and port that sent us the packet we received    
    udp.write(replyBuffer);
    udp.endPacket();
  }
  
}


