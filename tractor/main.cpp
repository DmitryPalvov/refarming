#include "mbed.h"
#include "rtos.h"
#include "UIPEthernet.h"
#include "ConfigKozachok.h"

#include "niMQTT.h"

#include "Brake.h"
#include "Throttle.h"
#include "Gear.h"
#include "Steering.h"
#include "Diff.h"
#include "Plug.h"

#include "Networker.h"

DigitalOut horn 	= DigitalOut(HORN_PIN, 1);
DigitalOut frontLight 	= DigitalOut(FRONT_LIGHT_PIN, 1);
DigitalOut engineFan 	= DigitalOut(ENGINE_FAN_PIN, 1);
DigitalOut ignition 	= DigitalOut(IGNITION_PIN, 1);

DigitalOut onMass 	= DigitalOut(ON_MASS_PIN, 1);
DigitalOut haltEngine 	= DigitalOut(HALT_ENGINE_PIN, 1);

DigitalOut cam0 	= DigitalOut(CAMERA_0_PIN, 1);
DigitalOut cam1 	= DigitalOut(CAMERA_1_PIN, 1);
DigitalOut router 	= DigitalOut(ROUTER_PIN, 1);

InterruptIn userButton(USER_BUTTON_PIN);


Thread *threadNetworker;
Thread *threadWorker;
Thread *threadStatusCollector;
Thread *threadLogger;

bool isPaused = false;
bool isHalted = false;

void pauseKozachok() { // no commands for small time
  // reset throttle
  // brake
  // restart network
  if (!isPaused) {
	  isPaused = true;

	  ik::throttle.processCommand(0);
	  ik::brake.processCommand(1);
//	  ik::udpRestart();
//todo retry router connect
	  wait(2);
	  ik::brake.processCommand(0);
  }
}

void haltKozachok() { // no commands for long time
  if (!isHalted) {
	  isHalted = true;  
	  pauseKozachok();
	  haltEngine = 0;
	  wait(3);
	  onMass = 0;
	  wait(5);
	  haltEngine = 1;

 // pause+
 // halt
 // offMass
  }
}


void testLoop() {
	using namespace ik;
	LOG("Test loop start\n");	

	float p = 0.2;
	
	onMass = 0; //disable 

	wait(p);
	steering.processCommand(1);
	wait(p);
	steering.processCommand(9);
	wait(p);
	steering.processCommand(0);
	wait(p);

	wait(p);
	brake.processCommand(1);
	wait(p);
	brake.processCommand(2);
	wait(p);
	brake.processCommand(0);
	wait(p);

	wait(p);
	gear.processCommand(1);
	wait(p);
	gear.processCommand(2);
	wait(p);
	gear.processCommand(0);
	wait(p);

	wait(p);
	diff.processCommand(1);
	wait(p);
	diff.processCommand(2);
	wait(p);
	diff.processCommand(0);
	wait(p);

	wait(p);
	plug.processCommand(1);
	wait(p);
	plug.processCommand(2);
	wait(p);
	plug.processCommand(0);
	wait(p);


	wait(p);
	throttle.processCommand(1);
	wait(p);
	throttle.processCommand(2);
	wait(p);
	throttle.processCommand(0);
	wait(p);
	

	wait(p);
	haltEngine = 0;
	wait(p);
	haltEngine = 1;
	wait(p);

	wait(p);
	horn 	= 0;
	wait(p);
	horn 	= 1;
	wait(p);

	wait(p);
	frontLight	= 0;
	wait(p);
	frontLight	= 1;
	wait(p);

	wait(p);
	engineFan	= 0;
	wait(p);
	ignition	= 1;
	wait(p);


	wait(p);
	router	= 0;
	wait(p);
	router	= 1;
	wait(p);

	wait(p);
	cam0	= 0;
	wait(p);
	cam0	= 1;
	wait(p);

	wait(p);
	cam1	= 0;
	wait(p);
	cam1	= 1;
	wait(p);


	onMass = 1;
	LOG("Test loop end\n");	
}

void trigger() {
     printf("User button interrupt\n");
     testLoop();
}


void processCommand(void) {
  using namespace ik;
  if (LOGLEVEL >= LOG_LEVEL_DEBUG) {
    LOG("%s\n",command);    
  }
        
  for (unsigned int commandIndex = 0; commandIndex < 17; commandIndex++) { 
    char c = command[commandIndex];
    int i = c - '0';
//printf("%i => %i\n", commandIndex, i);
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
      case 4: //differential
        diff.processCommand(i);
        break;
      /*case 5: //podsos
        //podsos.processCommand(i);
        break;*/
      case 6: //horn
        if (c=='1') {
          horn = 0;          
        } else {
          horn = 1;          
        }
        break;
      case 7: //front_light
        if (c=='1') {          
          frontLight = 0;
        } else {
          frontLight = 1;
        }
        break;
      case 8: //plug
        plug.processCommand(i);
        break;
      case 9: //engine_fan
        if (c=='1') {          
          engineFan = 0;
        } else {
          engineFan = 1;
        }        
        break;
      case 10: //starter
        if (c=='1') {          
          ignition = 0;
        } else {
          ignition = 1;
        }        
        break;
      case 11: //ignition mass
        if (c=='1') {          
          onMass = 0;
        } else {
          onMass = 1;
        }        
        break;
      case 12: //fuel cut
        //TODO
        break;
      case 13: //halt engine
        if (c=='1') {          
          haltEngine = 0;
        } else {
          haltEngine = 1;
        }
        break;
      case 14: //cam1
        if (c=='1') {          
          cam1 = 0;
        } else {
          cam1 = 1;
        }
        break;
      case 15: //router
        if (c=='1') {          
          router = 0;
        } else {
          router = 1;
//	  udpRestart();
        }
        break;
      case 16: //camera 1
        if (c=='1') {          
          cam0 = 0;
        } else {
          cam0 = 1;
        }
        break;
    } // switch    
  } // for
} // function

void applyCommand(void) {
  using namespace ik; 
  brake.applyCommand();
  gear.applyCommand();
  steering.applyCommand();
  throttle.applyCommand();
  //podsos.applyCommand();
  diff.applyCommand();  
  plug.applyCommand();  
} // function

void setup(void) {
  using namespace ik;
  LOG("Iron Kozachok version [%s] is ready to serve\n", CONFIG_VERSION);

  timer.start();

  brake.setup();
  throttle.setup();
  diff.setup();
  gear.setup();
  steering.setup();
  plug.setup();


  udpSetup();
  lastCommandTime = timer.read_us();
}

void threadFunctionStatusCollector(void const *argument) {
  using namespace ik; 

    while (true) {
//	LOG("threadStatusCollector\n");
	statusCollect(true);
        osDelay(50);
    }
}

void threadFunctionNetworker(void const *argument) {
  using namespace ik; 
    while(true) {
//        LOG("threadNetworker\n");
	int packetSize = server.parsePacket();
	if (packetSize) {
		isPaused = false;
		isHalted = false;

		parseRequest(packetSize);

		sendResponse();
	}
        osDelay(1);
//        osDelay(2000);
    }
}

void threadFunctionWorker(void const *argument) {
  using namespace ik; 
    while(true) {
//        LOG("threadWorker ready\n");
	if (!isPaused) {
		processCommand();

		applyCommand();
	}
        osDelay(10);

    }
}

void threadFunctionLogger(void const *argument) {
  using namespace ik; 

    while(true) {
//        LOG("threadFunctionLogger\n");
        osDelay(5000);
    }
}

int main() {
    setup();
    userButton.rise(&trigger);

    threadNetworker = new Thread(threadFunctionNetworker, NULL, osPriorityNormal, DEFAULT_STACK_SIZE, NULL);
    threadWorker = new Thread (threadFunctionWorker, NULL, osPriorityNormal, DEFAULT_STACK_SIZE, NULL);
    threadStatusCollector = new Thread (threadFunctionStatusCollector, NULL, osPriorityNormal, DEFAULT_STACK_SIZE, NULL);
//    threadLogger = new Thread (threadFunctionLogger, NULL, osPriorityLow, DEFAULT_STACK_SIZE, NULL);
    while (true) {
         using namespace ik; 

        if (timer.read_us() - lastCommandTime > HALT_TRACTOR_TIMEOUT) {
		haltKozachok();
        }

        if (timer.read_us() - lastCommandTime > PAUSE_TRACTOR_TIMEOUT) {
		pauseKozachok();
        }

//        LOG("I'm still alive\n");
        osDelay(1000);
    }
}

int main__() {

	unsigned char cc[34] = "c9500000000000000101476795657Qo4=";
//	setup();
  using namespace ik;
  LOG("Iron Kozachok version [%s] is ready to serve\n", CONFIG_VERSION);

  timer.start();

  udpSetup();
  lastCommandTime = timer.read_us();

  readCommand(cc, (size_t*)sizeof(cc));
}

int main___() {
  PwmOut servo(D2);
  PwmOut servo1(D3);

  servo.period_ms(1);
  servo.write(0.0f); //todo
  servo1.period_ms(3);
  servo1.write(0.5f); //todo

double i = 0.1f;
while(1) {
wait(0.3);

  servo.write(i); //todo
	i+=0.1f;
if (i > 1) i = 0.1f;
//wait();
//  servo.write(0.0f); //todo
}
}
