#include "mbed.h"
//#include "ConfigKozachok.h"


//using namespace ik;
DigitalOut dd3 	= DigitalOut(D3, 1);
DigitalOut dd4 	= DigitalOut(D4, 1);
DigitalOut dd5 	= DigitalOut(D5, 1);

DigitalOut dd6 	= DigitalOut(D6, 1);
DigitalOut dd7 	= DigitalOut(D7, 1);
DigitalOut dd8 	= DigitalOut(D8, 1);
DigitalOut dd9 	= DigitalOut(D9, 1);

int main() {
double p = 0.3;
dd8 = 0;
wait(p);
dd8 = 1;
wait(p);
dd9 = 0;
wait(p);
dd9 = 1;
wait(p);

DigitalOut pb7(PB_7, 1);
pb7 =0;
wait(p);
pb7 =1;

DigitalOut pc14(PC_14, 1);
pc14 =0;
wait(p);
pc14 =1;

DigitalOut pc15(PC_15, 1);
pc15 =0;
wait(p);
pc15 =1;


/*	wait(0.1);
	dd3 = 0;
	wait(0.1);
	dd4 = 0;
	wait(0.1);
	dd5 = 0;
	wait(0.1);
	dd6 = 0;

	wait(0.1);
	dd3 = 1;
	wait(0.1);
	dd4 = 1;
	wait(0.1);
	dd5 = 1;
	wait(0.1);
	dd6 = 1;*/

	
}

