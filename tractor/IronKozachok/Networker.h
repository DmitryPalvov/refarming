#ifndef NETWORKER_h
#define NETWORKER_h

namespace ik {

extern unsigned char* command;
extern unsigned long lastCommandTime;
extern EthernetUDP server;

void udpSetup();
void udpRestart();

bool checkCrc(unsigned char* data, size_t* num);
bool readCommand(unsigned char* temp, size_t* len);
void connectionAbortStop();
void statusCollect(bool isOK);

void parseRequest(int packetSize);
void sendResponse();

} //namespace ik

#endif

