#include "mbed.h"
 
 

/*int _main1() {
AnalogIn input(A0);
//    uint16_t samples[1024];
 
 
    printf("Results:\n");
    for(int i=0; i<1024; i++) {
        printf("%d, %.2f\n", i, input.read());
//        printf("%d, 0x%04X\n", i, samples[i]);
        wait(0.5f);

    }
} */

PwmOut servo(PC_2); 
//Servo servo(PC_2);
DigitalOut dir 	= DigitalOut(PB_14, 0);
AnalogIn throttlePos(PA_0);

Serial  pc1(USBTX, USBRX);
 
int main() {
printf("Hi\n");

//    servo.period_ms(20);          // servo requires a 20ms period

/*    for(float p=0; p<1.0; p += 0.1) {
        servo = p;
        wait(0.2);
    }*/
servo.period_ms(1);
    printf("Servo Calibration Controls:\n");
    printf("1,2,3 - Position Servo (full left, middle, full right)\n");
    printf("4,5 - Decrease or Increase position\n");
    printf("6,7 - Direction\n");
    printf("8 - Off\n");
 
    float position = 1.0;
//  throttle.setup();
    
    while(1) {                   
        switch(pc1.getc()) {
            case '1': position = 0.0; break;
            case '2': position = 0.2; break;
            case '3': position = 0.5; break;
            case '4': position -= 0.01; break; 
            case '5': position += 0.01; break; 
	    case '6': dir = 1;printf("forward\n");break;
	    case '7': dir = 0;printf("backward\n");break;
	    case '8': position = 0.0;printf("stop\n");break;
        }
        printf("position = %.1f\n", position);
        printf("curPos = %.2f\n", throttlePos.read());
        servo = position;
    }




}
