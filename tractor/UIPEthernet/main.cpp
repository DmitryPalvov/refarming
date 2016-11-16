/*
 * Simple TcpServer using the UIPEthernet library for ENC28J60 Ethernet boards.
 *
 */

#include "mbed.h"
#include "UIPEthernet.h"
#include "UIPServer.h"
#include "UIPClient.h"
#include "Brake.h"

Serial  pc(USBTX, USBRX);

int main(void)
{
	brake.setup();


}