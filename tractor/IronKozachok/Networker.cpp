#include "mbed.h"
#include "rtos.h"
#include "UIPEthernet.h"

#include "crc16.h"
#include "base64.h"
#include "ConfigKozachok.h"
#include "Networker.h"
#include <string>
#include "Throttle.h"
#include "Brake.h"
#include "Gear.h"

#define REPLY_BUFFER_SIZE 128

/*
 * Simple TcpServer using the UIPEthernet library for ENC28J60 Ethernet boards.
 *
 */
UIPEthernetClass    UIPEthernet(D11, D12, D13, D10);        // mosi, miso, sck, cs

namespace ik {

// MAC number must be unique within the connected network. Modify as appropriate.
const uint8_t   mac[6] = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05 };
const uint16_t  udpPort = 10001;
EthernetUDP  server = EthernetUDP();

IPAddress ip(192, 168, 0, 177);

const unsigned char verify[3] = {'c','o', '\0'};
const unsigned char verifyInit[3] = {'i','n', '\0'};
unsigned char* command;
unsigned int commandId = 0;
unsigned int statusCode = 0;

unsigned char defaultCommand[31] = {  //+ts 10
                    '5','0','0','0','0',
                    '0','0','0','0','0',
                    '0','0','0','0','0',
                    '1','0',
		    '0','0','0','0','0','0','0','0','0','0',
'\0'};

//50000000000000010
unsigned long lastCommandTime = 0;

unsigned char packetBuffer[UIP_UDP_MAXPACKETSIZE*2]; //buffer to hold incoming packet,
unsigned char replyBuffer[REPLY_BUFFER_SIZE];// = "00000000000000000000000";       // a string to send back

long currentVccCacheTime = 0;
long currentVccCache = 0;

bool checkCrc(unsigned char* data, size_t* num) {
  unsigned char crcString[3];
  size_t n;
  mbedtls_base64_decode(crcString, sizeof(crcString), &n, (unsigned char*)((int)data+(int)num-4), (size_t)4);
  if (n != 2) {
    pc.printf("CRC length error\n");
    pc.printf("%s",crcString);
    pc.printf("\n");
    return false;
  } else {
    unsigned char checkCrcVal[3];
    crc16(checkCrcVal, data, (int)num-4);
    if (checkCrcVal[0] == crcString[0] && checkCrcVal[1] == crcString[1]) {
      return true;  
    } else {
      pc.printf("Invalid CRC\n");
      pc.printf("%s",crcString);
      pc.printf("%d_%d:",(int)crcString[0], (int)crcString[1]);
      pc.printf("%d_%d",(int)checkCrcVal[0], (int)checkCrcVal[1]);

      pc.printf("\n");
      return false;
    }
  }
}

void udpSetup() {
	command = defaultCommand;
	uint8_t revision = UIPEthernet.network.getrev();
	UIPEthernet.network.powerOff();
	UIPEthernet.network.powerOn();
	int status = UIPEthernet.network.linkStatus();
	printf("Network revision: %i Status: %i Time:%d\n", revision, status, timer.read_us());

  #if defined(DHCP)
    printf("Searching for DHCP server..\r\n");
    if(UIPEthernet.begin(mac) != 1) {
        printf("No DHCP server found.\r\n");
        printf("Exiting application.\r\n");
        return 0;
    }
    printf("DHCP server found.\r\n");
  #else
    // IP address must be unique and compatible with your network.
    UIPEthernet.begin(mac, ip);
  #endif
    IPAddress   localIP = UIPEthernet.localIP();
    printf("Local IP = ");
    for(uint8_t i = 0; i < 3; i++)
        printf("%d.", localIP[i]);
    printf("%d\r\n", localIP[3]);
    server.begin(udpPort);
}

void udpRestart() {
    udpSetup();
} 

bool isSyncedTime = false;
void syncTime(int serverTime) {
	if (!isSyncedTime) {
		set_time(serverTime);
		isSyncedTime = true;
		LOG("Sunc server time:[%d]\n",  serverTime);
	}
}

bool readCommand(unsigned char* temp, size_t* len) {
  if (!checkCrc(temp, (size_t*)((unsigned int)len-1))) return false;
  int vlen = sizeof (verify);
  for(int i = 0; i < vlen-1; i++) {
    if (i==1) { 
	commandId = ((int)temp[i]) - 0x30;
	break; //todo
    }
    if (temp[i] != verify[i]) {
      return false;
    }
  }

  command = temp+vlen-1;
//parse time
  char t[11] = "0000000000";
  memcpy(t, (void*)command+17, 10);
  int serverTime = std::atol (t);

  LOG("%s\n", command);

  if (!isSyncedTime) {
	syncTime(serverTime);
  }
  //is command timed out
  int ct = time(NULL);
  pc.printf("com:%s st:%d lct:%d td:%d\n", command, serverTime, lastCommandTime, serverTime - ct);

  if (serverTime - ct < -1) { // 1 second max timeout
//todo reinit time if (+1)
	//timeout
	LOG("Command is timed out [%d] diff\n", serverTime - ct);
	statusCode = statusCode & 0b1011;
	return false;
  }
  lastCommandTime = timer.read_us();

  return true;  
} // readCommand

void softReset() {
  //todo
}

void connectionAbortStop() {
  if (counter%10000 == 0) {
    pc.printf("No heartbeat - STOP\n");
    udpRestart();  
  }
  command = defaultCommand;
  //todo use command      
}

void statusCollect(bool isOK) {
  for (int i = 0; i<REPLY_BUFFER_SIZE;i++) {
     replyBuffer[i]=0;
  }
  std::sprintf(
	(char*)replyBuffer, 
	"%s:%i:%i:%i:%i:%i:%s:%s:%i:%s:%s:%s:%s:%s:%s:%s:%s:%i:%i\n", 
	isOK?"OK":"ER", 
	steeringPosition.read_u16()>>8, 
	batteryMain.read_u16()>>8,
	batteryExtra.read_u16()>>8,
	throttle.getPosition(),
	gear.getCurrentGear(),
	"NA", // gear pos
	"NA", // podsos pos
	brake.getState(), // brake pos
	"NA", // differential pos
	"NA", // gas level
	"NA", // temp1 level
	"NA", // temp2 level
	"NA", // temp3 level
	"NA", // speed level
	"NA", // rpm level
	"NA", // ram
	commandId,  // last command Id
	statusCode //status code
	);
  int l = 0;
  for (int i = 0; i<REPLY_BUFFER_SIZE;i++) {
	if (replyBuffer[i] == '\n') {
		l = i;
		break;
	}
  }
  LOG("%s\n->\n", replyBuffer);
// steering, batt1, batt2, throttle, gear, gearPos, podsos, break, block_diff, gas_level, temp1, temp2, temp3, speed, rpm, 

  unsigned char checkCrcVal[3];
  crc16(checkCrcVal, replyBuffer, l);
  size_t olen;
  mbedtls_base64_encode((unsigned char *)((int)&replyBuffer + l), 5, &olen, checkCrcVal, 2);
#ifdef NETWORKER_DEBUG
  pc.printf("%s\n",replyBuffer);
  pc.printf("%s\n",replyBuffer + l);
  pc.printf("%s\n",checkCrcVal);
//  pc.printf("%s\n",based64);
  pc.printf("%i\n",(int)olen);

  pc.printf("\n");
#endif
}

void parseRequest(int packetSize) {
  // if there's data available, read a packet
//    pc.printf("START %i\n", timer.read_us());
    IPAddress remote = server.remoteIP();

#ifdef NETWORKER_DEBUG
    pc.printf("From ");
    for (int i = 0; i < 4; i++) {
      pc.printf("%i",remote[i]);
      if (i < 3) {
        pc.printf(".");
      }
    }
    pc.printf(", port ");
    pc.printf("%i", server.remotePort());
    pc.printf("\n");
#endif

    // read the packet into packetBufffer
    server.read(packetBuffer, UIP_UDP_MAXPACKETSIZE*2);
    server.flush();

#ifdef NETWORKER_DEBUG
    pc.printf("Contents:\n");
    pc.printf("%s\n",packetBuffer);
#endif

    bool res = readCommand(packetBuffer, (size_t*)packetSize);
    if (!res) { 
	statusCode = statusCode & 0b1111;
    } else {
	statusCode = statusCode & 0b1110;
    }
//    pc.printf("END %i\n", timer.read_us());
}

void sendResponse() {
    server.beginPacket(server.remoteIP(), server.remotePort());
    // send a reply, to the IP address and port that sent us the packet we received    
    server.write((uint8_t*)replyBuffer, sizeof(replyBuffer));
    int res = server.endPacket();
    if(!res) { 
	statusCode = statusCode & 0b1111;
    } else {
	statusCode = statusCode & 0b1101;
    }

#ifdef NETWORKER_DEBUG
    if (!res) {
        pc.printf("Send error\n");
    } else {
        pc.printf("Send OK\n");
    }
#endif
}

} // namespace ik