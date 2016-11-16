/*
 * Simple TcpServer using the UIPEthernet library for ENC28J60 Ethernet boards.
 *
 */

#include "mbed.h"
#include "ConfigKozachok.h"

#include "Brake.h"

void testLoop() {
	printf("Test loop start\n");	

	float p = 0.5;
	wait(p);
	ik::brake.processCommand(0);
	wait(p);
	ik::brake.processCommand(1);
	wait(p);
	ik::brake.processCommand(0);
	p = 0.2;
	wait(p);

	ik::brake.processCommand(2);
	wait(p);
	ik::brake.processCommand(0);


	printf("Test loop end\n");	

}

void setup(void) {

  printf("Iron Kozachok version [%s] is ready to serve\n", CONFIG_VERSION);

  ik::brake.setup();

}

int main(void)
{
  setup();
  testLoop();

}

