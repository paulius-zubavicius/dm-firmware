#include <QueueArray.h>

//Pin connected to ST_CP of 74HC595
int latchPin = 3;
//Pin connected to SH_CP of 74HC595
int clockPin = 2;
//Pin connected to DS of 74HC595
int dataPin = 4;

const int pin_resistor = A5;

int pause = 0;
//int skipDataSent = 10;

int motorSpeed = 1250;  // set speed of 28BYJ-48 stepper,

//max frequency is 100hz, so max speed is 1250

//(100 steps per second 8 pulses per step so 10000 divided by 8)
// 1250 equates to apx 6 seconds per rev

QueueArray<byte> buff;

void setup() {

	// initialize serial:
	Serial.begin(57600);

	//inputString.reserve(200);

	pinMode(latchPin, OUTPUT);
	pinMode(clockPin, OUTPUT);
	pinMode(dataPin, OUTPUT);

	pinMode(13, OUTPUT);
	digitalWrite(13, LOW);

	digitalWrite(latchPin, LOW);
	shiftOut(dataPin, clockPin, LSBFIRST, B00000000);
	digitalWrite(latchPin, HIGH);

}

void loop() {

	if (!buff.isEmpty()) {

		digitalWrite(13, LOW);

		digitalWrite(latchPin, LOW);
		shiftOut(dataPin, clockPin, MSBFIRST, buff.pop());
		digitalWrite(latchPin, HIGH);

		pause = map(analogRead(pin_resistor), 0, 1023, motorSpeed, 10000);

		if (pause) {
			delayMicroseconds(pause);
		}



	} else {
		digitalWrite(13, HIGH);
	}

//	if (skipDataSent) {
//		skipDataSent--;
//	} else {
//		skipDataSent = 10;
		Serial.print(buff.count());
		Serial.print("-");
//	}

}

void serialEvent() {
	while (Serial.available()) {
		buff.push(Serial.read());
	}
}

