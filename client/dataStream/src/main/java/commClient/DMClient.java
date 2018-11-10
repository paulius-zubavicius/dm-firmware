package commClient;

import static jssc.SerialPort.BAUDRATE_57600;
import static jssc.SerialPort.BAUDRATE_9600;
import static jssc.SerialPort.DATABITS_8;
import static jssc.SerialPort.FLOWCONTROL_RTSCTS_IN;
import static jssc.SerialPort.FLOWCONTROL_RTSCTS_OUT;
import static jssc.SerialPort.MASK_RXCHAR;
import static jssc.SerialPort.PARITY_NONE;
import static jssc.SerialPort.STOPBITS_1;

import java.util.Random;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class DMClient implements SerialPortEventListener {

	private SerialPort serialPort;
	private String buff = "";
	
	private Random rnd = new Random();

	private byte[] values0 = new byte[] { 
			(byte) 0b10000000, 
			(byte) 0b11000000, 
			(byte) 0b01100000,
			(byte) 0b00110000, 
			(byte) 0b00011000, 
			(byte) 0b00001100, 
			(byte) 0b00000110, 
			(byte) 0b00000011,
			(byte) 0b00000001, 
			(byte) 0b00000000 };

	private byte[] values1 = new byte[] { 
			(byte) 0b10000001, 
			(byte) 0b11000011, 
			(byte) 0b11100111,
			(byte) 0b11111111, 
			(byte) 0b01111110, 
			(byte) 0b00111100, 
			(byte) 0b00011000 };

	private byte[] values2 = new byte[] { 
			(byte) 0b10000000, 
			(byte) 0b11000000, 
			(byte) 0b01000000,
			(byte) 0b01100000, 
			(byte) 0b00100000, 
			(byte) 0b00110000, 
			(byte) 0b00010000,
			(byte) 0b00011000, 
			(byte) 0b00001000, 
			(byte) 0b00001100,
			(byte) 0b00000100, 
			(byte) 0b00000110, 
			(byte) 0b00000010, 
			(byte) 0b00000011,
			(byte) 0b00000001, 
			(byte) 0b00000011, 
			(byte) 0b00000010,
			(byte) 0b00000110, 
			(byte) 0b00000100, 
			(byte) 0b00001100, 
			(byte) 0b00001000,
			(byte) 0b00011000, 
			(byte) 0b00010000, 
			(byte) 0b00110000,
			(byte) 0b00100000, 
			(byte) 0b01100000, 
			(byte) 0b01000000, 
			(byte) 0b11000000
	};
	private byte[] values3 = new byte[] { 
			(byte) 0b1000_0001, 
			(byte) 0b1100_0011, 
			(byte) 0b0100_0010,
			(byte) 0b0110_0110, 
			(byte) 0b0010_0100, 
			(byte) 0b0011_1100, 
			(byte) 0b0001_1000,
			(byte) 0b1001_1001
			
			
	};
	
	public static void main(String[] ds) throws SerialPortException, InterruptedException {
		for (String string : SerialPortList.getPortNames()) {
			System.out.println(string);
		}
		DMClient client = new DMClient();
		client.streamingToArduino();
	}

	private void streamingToArduino() throws InterruptedException, SerialPortException {
		serialPort = new SerialPort("/dev/ttyACM0");
		serialPort.openPort();
		serialPort.setParams(BAUDRATE_57600, DATABITS_8, STOPBITS_1, PARITY_NONE);
		serialPort.setFlowControlMode(FLOWCONTROL_RTSCTS_IN | FLOWCONTROL_RTSCTS_OUT);
		serialPort.addEventListener(this, MASK_RXCHAR);

		int size = 0;
		while (true) {

			if (size < 50 && size > -1) {
				serialPort.writeBytes(getDataStream());
				//serialPort.sendBreak(2);
			}

			if (size > 0) {
				Thread.sleep(size);
			}
			
			synchronized (buff) {
				size = maxSizeFromRespose();
			}
			
			if (size > 0) {
				System.out.println("ARD buff size: " + size);
			}
		}
	}

	private byte[] getDataStream() {
		
		
		return values3;
		
//		int key = rnd.nextInt(3);
//		
//		switch (key) {
//		case 0:
//			return values0;
//		case 1:
//			return values1;
//		case 2:
//			return values2;
//		}
//		
//		throw new RuntimeException();
	}

	private int maxSizeFromRespose() {

		if (buff.isEmpty() || !buff.contains("-")) {
			return -1;
		}

		String[] strs = buff.split("-");

		int strs_lenght = strs.length;

		if (!buff.endsWith("-")) {
			strs_lenght--;
			buff = strs[strs.length - 1];
		} else {
			buff = "";
		}
		return Integer.parseInt(strs[strs_lenght - 1]);
	}

	@Override
	public void serialEvent(SerialPortEvent event) {
		if (event.isRXCHAR() && event.getEventValue() > 0) {
			try {
				byte[] bbuff = serialPort.readBytes(event.getEventValue());

				synchronized (buff) {
					for (byte b : bbuff) {
						buff += (char) b;
					}
				}

			} catch (SerialPortException ex) {
				System.out.println("Error in receiving string from COM-port: " + ex);
			}

		}
	}

}