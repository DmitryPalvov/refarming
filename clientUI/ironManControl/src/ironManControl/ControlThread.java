/**
 * 
 */
package ironManControl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Level;

import ironManControl.kazachok.KazachokConst;

/**
 * @author Dmitry
 *
 */
//public class ControlThread extends Thread {
public class ControlThread extends javax.swing.SwingWorker<String, Object> {
	
	public static boolean isSkipUserCheck = true;
	public static boolean isSkipVideoCheck = true;
	public static volatile boolean enableNet = false;
	
	public static HashMap<String, String> stateHashMap = new HashMap<String, String>();
	
	public static int readNetworkTimeout = 30;
	
	public static long videoTimeWarningThreshold = 500;
	public static long videoTimeStopThreshold = 2000;
	public static long controllerTimeWarningThreshold = 500;
	public static long controllerTimeStopThreshold = 2500;
	
	public static long userTimeThreshold = 5000;
	
	
	private JControlViewButton callerButton;
	
	Socket socket = null;
	String host = "192.168.0.177";
	byte[] hostBytes = new byte[]{(byte)192,(byte)168,0,(byte)177};
	//byte[] hostBytesLocal = new byte[]{(byte)192,(byte)168,0,(byte)88}; //lan
	//byte[] hostBytesLocal = new byte[]{(byte)192,(byte)168,1,(byte)131};
	byte[] hostBytesLocal = new byte[]{(byte)192,(byte)168,0,(byte)100};  //wifi
	/*local test*/
	//byte[] hostBytes = new byte[]{(byte)127,(byte)0,0,(byte)1};
	//byte[] hostBytesLocal = new byte[]{(byte)127,(byte)0,0,(byte)1};
	int port = 10001;
	int portLocal = 10002;
	
	
	private char[] stopCommand = new char[27];
	
	private char[] currentCommand = new char[27]; // 17 + timstamp 10
	private boolean isCommandChanged = false;
	
	public static boolean isSending = false;
	
	private volatile long lastVideoFrameTime;
	private volatile long lastControllerResponseTime;
	
	public volatile long lastUserKeepaliveClickTime;
	
	public ActionQueue actionQueue = new ActionQueue();
	
	public void initStateHashMap() {
		this.stopCommand = KazachokConst.DEFAULT_COMMAND.toCharArray();
		this.currentCommand = KazachokConst.DEFAULT_COMMAND.toCharArray();
		ControlThread.stateHashMap.put("controller_lag", "0");
		ControlThread.stateHashMap.put("controller_network", "0");
		ControlThread.stateHashMap.put("controller_system", "0");
		//ControlThread.stateHashMap.put("controller_selfcheck", "0");
		ControlThread.stateHashMap.put("camera_network", "0");		
	}

	private boolean isUserOK() {
		if (ControlThread.isSkipUserCheck) return true;
		if (ControlThread.userTimeThreshold < (System.currentTimeMillis() - this.lastUserKeepaliveClickTime)) {
			IronManControl.logger.log(Level.WARNING, "WARNING User does nothing STOP");
			IronManControl.guiFrame.ironControlFrame.lblErrorValue.setText("WARNING User does nothing STOP");
			currentCommand = java.util.Arrays.copyOf(stopCommand, stopCommand.length);
			ControlThread.stateHashMap.put("user_keepalive", "0");
			return false;
		}
		ControlThread.stateHashMap.put("user_keepalive", "1");
		return true;
	}
	
	private boolean isVideoOK() {
		if (ControlThread.isSkipVideoCheck) return true;
		if (ControlThread.videoTimeStopThreshold < System.currentTimeMillis() - this.lastVideoFrameTime) {
			//todo
			IronManControl.logger.log(Level.WARNING, "WARNING Video stalled Sending STOP");
			IronManControl.guiFrame.ironControlFrame.lblErrorValue.setText("WARNING Video stalled Sending STOP");
			currentCommand = java.util.Arrays.copyOf(stopCommand, stopCommand.length);
			ControlThread.stateHashMap.put("camera_network", "0");
			return false;
		}
		
		if (ControlThread.videoTimeWarningThreshold < System.currentTimeMillis() - this.lastVideoFrameTime) {
			IronManControl.logger.log(Level.WARNING, "WARNING Video is lagging");
			IronManControl.guiFrame.ironControlFrame.lblErrorValue.setText("WARNING Video is lagging");
		}
		ControlThread.stateHashMap.put("camera_network", "1");
		return true;
	}
	
	private boolean isControllerOK() {
		if (!ControlThread.enableNet) return true;
		if (ControlThread.controllerTimeStopThreshold < System.currentTimeMillis() - this.lastControllerResponseTime) {
			IronManControl.logger.log(Level.WARNING, "WARNING Controller stalled Sending STOP");
			IronManControl.guiFrame.ironControlFrame.lblErrorValue.setText("WARNING Controller stalled Sending STOP");
			currentCommand = java.util.Arrays.copyOf(stopCommand, stopCommand.length);
			ControlThread.stateHashMap.put("controller_network", "0");
			IronManControl.guiFrame.ironControlFrame.setNetworkStatusError();
			callerButton.setWarning();
			return false;
		}
		
		ControlThread.stateHashMap.put("controller_network", "1");
		callerButton.setOK();
		return true;
	}
	
    @Override
    public String doInBackground() {
    	IronManControl.logger.log(Level.INFO, "RC started");
    	this.initStateHashMap();
    	ActionQueueElement action;
    	//this.parseResponse("");
    	boolean isOn = true;
    	while (isOn) {
    		try {
    			if (!ControlThread.enableNet) {
    				continue;
    			}
    			if(isControllerOK()) {
    				if(isUserOK()) {
    					isVideoOK();
    				}
    			}	
    			IronManControl.guiFrame.ironControlFrame.lblStatusValue.setText(ControlThread.stateHashMap.toString());
    			IronManControl.guiFrame.ironControlFrame.lblCommandValue.setText(new String(this.currentCommand));
    			
    			IronManControl.guiFrame.ironControlFrame.statusPanel.taSignalStrength.setText("Signal: "+IronManControl.getWifiSignalStrength()+"%");
				
    			boolean isSent = this.sendCommand();
				if (!isSent) { //retry
					this.sendCommand();
				}
				this.receiveUDP();
				for (int i = 0; i < 20; i++) {
					/*Polling delayed actions*/
					while ((action = (ActionQueueElement)this.actionQueue.poll()) != null) {
						IronManControl.logger.log(Level.INFO, "Delayed command");
						this.setCommandValue(action.actionId, action.actionValue);
						isCommandChanged = true;
					}
					if (isCommandChanged) {
						isCommandChanged = false;
						break;
					}
					this.receiveUDP();
				}				
			} catch(Exception e){
				e.printStackTrace();
				IronManControl.logger.log(Level.WARNING, e.getMessage(), e);
			}
		}
    	return "";
	}

    @Override
    protected void done() {
        try {
        	IronManControl.logger.log(Level.INFO, "Closed");
        } catch (Exception ignore) {
        	IronManControl.logger.log(Level.WARNING, ignore.getMessage(), ignore);
        }
    }
	
	public synchronized void accelerate(boolean forwards){
		if (forwards) {
			this.currentCommand[2] = '1';
			this.currentCommand[3] = '0';
		} else {
			this.currentCommand[3] = '1';
			this.currentCommand[2] = '0';
		}
		isCommandChanged = true;		
	}

	public synchronized void stopAccelerate(){
		this.currentCommand[2] = '0';
		this.currentCommand[3] = '0';
		isCommandChanged = true;		
	}
	
	public synchronized void setBrakeStatus(int value) {
		char val = '0';
		switch (value) {
			case 1:
				val = '1';
				break;
			case 2:
				val = '2';
				break;				
		}
		this.currentCommand[KazachokConst.BRAKE_ID] = val;
		isCommandChanged = true;		
	}
	
	public synchronized void setGearStatus(int value) {
		char val = '0';
		switch (value) {
			case 1:
				val = '1';
				break;
			case 2:
				val = '2';
				break;				
		}
		this.currentCommand[KazachokConst.GEAR_ID] = val;
		isCommandChanged = true;		
	}
	
	public synchronized void setSteeringStatus(int value) {
		char val = '5';
		switch (value) {
			case 4:
				val = '4';
				break;
			case 6:
				val = '6';
				break;				
		}
		this.currentCommand[KazachokConst.STEERENG_ID] = val;
		isCommandChanged = true;		
	}
	
	public synchronized void setThrottleStatus(int value) {
		char val = '0';
		switch (value) {
			case 1:
				val = '1';
				break;
			case 2:
				val = '2';
				break;				
		}
		this.currentCommand[KazachokConst.THROTTLE_ID] = val;
		isCommandChanged = true;		
	}
	
	public synchronized void turn(int value){
		if (value < 1) value = 1;
		if (value > 9) value = 9;
		this.currentCommand[0] = (char) ('0'+(char)value);
		isCommandChanged = true;
	}

	/*public synchronized void setSteeringSpeed(int value){
		if (value < 1) value = 1;
		if (value > 4) value = 4;
		this.currentCommand[1] = (char) ('0'+(char)value);
		isCommandChanged = true;		
	}*/
	
	//private boolean isRetry = false;
	DatagramSocket clientSocket = null;
	DatagramPacket sendPacket = null;

	private void _initUDP() throws IOException {
		if (this.clientSocket == null) {
			IronManControl.logger.log(Level.INFO, "Init socket");
			clientSocket = new DatagramSocket(new InetSocketAddress(InetAddress.getByAddress(this.hostBytesLocal), this.portLocal));
			clientSocket.setSoTimeout(100); // TODO timeout
			clientSocket.setReuseAddress(true);
		}
	}
	
	private int nn = 0;
	public boolean sendCommand() {
		String command = new String(this.currentCommand);
		String key = "c"+nn;
		nn++;
		if (nn>9) nn = 0;
		command = key + command;
		
		command+=(int)(System.currentTimeMillis()/1000);
		
		IronManControl.logger.log(Level.INFO, "Command: ["+command+"]");
		
		/*add CRC*/
		byte[] crc = CRC16.crc16(command.getBytes()); 
		//IronManControl.logger.log(Level.INFO, "CRC: ["+(int)crc[0]+"_"+(int)crc[1]+"]");
		crc = java.util.Base64.getEncoder().encode(crc);
		for (byte val : crc) {
			command += (char)val;
		}
		command += '\n';
		
		
		if (!ControlThread.enableNet) {
			String debugResponse = "OK:122:0:114:4:31:NA:NA:0:NA:NA:NA:NA:NA:NA:0:0:0:0:0:0";
			this.parseResponse(debugResponse);
			return true;
		}
		//IronManControl.logger.log(Level.INFO, "Command full: ["+command+"]");
		try {
			_initUDP();
			//callerButton.setOK();
			IronManControl.logger.log(Level.INFO, "Begin");
			InetAddress IPAddress = InetAddress.getByAddress(this.hostBytes);
			byte[] sendData = command.getBytes();
			if (sendPacket == null) {
				sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, this.port);
			} else {
				sendPacket.setData(sendData);
				sendPacket.setLength(sendData.length);
			}
			clientSocket.send(sendPacket);
			
			IronManControl.logger.log(Level.INFO, "END");
			IronManControl.commandLogger.log(Level.INFO, "SEND\tOK\t"+command+"");
			
			return true;
		} catch (IOException ioe) {
			callerButton.setWarning();
			IronManControl.logger.log(Level.WARNING, ioe.getMessage(), ioe);
			IronManControl.guiFrame.ironControlFrame.lblErrorValue.setText(ioe.getMessage());
	    	IronManControl.profiler.increment("controller_network_error");
	    	/*if (!this.isRetry) {
        		this.isRetry = true;
        		IronManControl.logger.log(Level.INFO, "Resend");
        		this.sendCommand();
        	}*/
	    	IronManControl.commandLogger.log(Level.INFO, "SEND\tFAIL\t"+command+"");
	    	return false;	    	
		}
	}

	public void receiveUDP() {
		byte[] receiveData = new byte[1024];
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		try {
			_initUDP();
			clientSocket.setSoTimeout(ControlThread.readNetworkTimeout); // TODO timeout
			clientSocket.receive(receivePacket);
			callerButton.setOK();
			
			String response = new String(receivePacket.getData());
//			ControlThread.stateHashMap.put("controller_lag", String.valueOf(System.currentTimeMillis() - t));
			IronManControl.logger.log(Level.INFO, "Got: "+response);
			IronManControl.profiler.increment("controller_packet_recieved");
			
			if ((response = this.validateResponse(response)) == "") {	        	
	        	IronManControl.profiler.increment("controller_network_error");
	        	ControlThread.stateHashMap.put("controller_system", "0");
	        } else {
	        	IronManControl.guiFrame.ironControlFrame.enableKeyDispatcher();
	        	this.parseResponse(response);
	        	this.lastControllerResponseTime = System.currentTimeMillis();
	        	ControlThread.stateHashMap.put("controller_system", "1");
	        	ControlThread.stateHashMap.put("controller_network", "1");
	        }

		} catch (IOException ioe) {
			//IronManControl.logger.log(Level.INFO, "No data (timeout)");
		}		
	}
	
	public long getLastVideoFrameTime() {
		return lastVideoFrameTime;
	}

	public void setLastVideoFrameTime(long lastVideoFrameTime) {
		this.lastVideoFrameTime = lastVideoFrameTime;
	}
	
	private String validateResponse(String response) {
		response = response.trim();
		String result = "";
		if (!response.startsWith("OK") && !response.startsWith("ER")) {
			IronManControl.logger.log(Level.WARNING, "BAD Response: ["+response+"] should start with OK or ER");			
		} else {
			try {
				byte[] r = response.getBytes();
				byte[] crcB64 = java.util.Arrays.copyOfRange(r, r.length-4, r.length);
				byte[] crc = java.util.Base64.getDecoder().decode(crcB64);
				r = java.util.Arrays.copyOf(r, r.length-4);
				byte[] o = CRC16.crc16(r);
				if (!java.util.Arrays.equals(CRC16.crc16(r), crc)) {
					System.out.println(crc[0]);
					System.out.println(crc[1]);
					System.out.println(o[0]);
					System.out.println(o[1]);
					System.out.println(String.valueOf(r));
					IronManControl.logger.log(Level.WARNING, "BAD Response: ["+response+"] CRC error NOT EQUAL");
				} else {
					//response = new String(r);
					result = new String(r);
					IronManControl.logger.log(Level.INFO, "Response: ["+result+"]");
				}				
			} catch (Exception e) {
				IronManControl.logger.log(Level.WARNING, "BAD Response: ["+response+"] CRC error EXCEPTION");
			}
			
		}
		return result;
	}
	
	int prev = -1;
	/**
	 * TODO
	 * @param response
	 * @return
	 */
	private boolean parseResponse(String response) {
		//response = "OK:122:0:114:4:31:NA:NA:NA:NA:NA:NA:NA:NA:NA:NA:NA";
		
		// steering, batt1, batt2, throttle, gear, gearPos, podsos, break, block_diff, gas_level, temp1, temp2, temp3, speed, rpm,
		//60 = 12,2;
		double batt1Multiplier = 0.21;
		double batt2Multiplier = 0.21;
		//double batt1Multiplier = 3.3/256/0.16*1.22*2;//1.32;
		//double batt2Multiplier = 3.3/256/0.16*1.22*2;//1.32;
		IronManControl.commandLogger.log(Level.INFO, "RECIEVE\tOK\t"+response+"");
    	
		String[] vals = response.split(":");
		if (vals.length < 4) {
			IronManControl.logger.log(Level.WARNING, "Not all");
			ControlThread.stateHashMap.put("car_steering", "0");
			ControlThread.stateHashMap.put("car_batt1", "0");
			ControlThread.stateHashMap.put("car_batt2", "0");
		} else {
			int i = 1;
			ControlThread.stateHashMap.put("car_steering_pos", vals[i++]);
			ControlThread.stateHashMap.put("car_batt1", vals[i++]);
			ControlThread.stateHashMap.put("car_batt2", vals[i++]);
			ControlThread.stateHashMap.put("car_throttle_pos", vals[i++]);
			ControlThread.stateHashMap.put("car_gear", vals[i++]);
			ControlThread.stateHashMap.put("car_gear_pos", vals[i++]);
			ControlThread.stateHashMap.put("car_podsos_pos", vals[i++]);
			ControlThread.stateHashMap.put("car_brake_pos", vals[i++]);
			ControlThread.stateHashMap.put("car_diff_pos", vals[i++]);
			ControlThread.stateHashMap.put("car_gas_level", vals[i++]);
			ControlThread.stateHashMap.put("car_temp1", vals[i++]);
			ControlThread.stateHashMap.put("car_temp2", vals[i++]);
			ControlThread.stateHashMap.put("car_temp3", vals[i++]);
			ControlThread.stateHashMap.put("car_speed", vals[i++]);
			ControlThread.stateHashMap.put("car_rpm", vals[i++]);
			ControlThread.stateHashMap.put("car_ram", vals[i++]);
			int ooo = Integer.parseInt(vals[i]);
			if (ooo != prev + 1) {
				IronManControl.profiler.increment("controller_network_error_1");
				IronManControl.logger.log(Level.WARNING, "ALARM " + ooo + " " +prev + " " + IronManControl.profiler.get("controller_network_error_1"));
				
			} else {
				IronManControl.logger.log(Level.INFO, "OK");				
			}
			prev = ooo;
			if (prev == 9) prev = -1;
			ControlThread.stateHashMap.put("car_command_id", vals[i++]);
			ControlThread.stateHashMap.put("car_command_code", vals[i++]);
		}

		// view response
		if (IronManControl.guiFrame == null) return true;
		
		IronManControl.guiFrame.ironControlFrame.lblResponseValue.setText(response);
		IronManControl.guiFrame.ironControlFrame.lblErrorValue.setText("");
		
		//String s = ControlThread.stateHashMap.get("car_steering");
		IronManControl.guiFrame.ironControlFrame.statusPanel.setGear(ControlThread.stateHashMap.get("car_gear"));
		IronManControl.guiFrame.ironControlFrame.statusPanel.setThrottlePosition(ControlThread.stateHashMap.get("car_throttle_pos"));
		IronManControl.guiFrame.ironControlFrame.statusPanel.setBrakeStatus(Integer.parseInt(ControlThread.stateHashMap.get("car_brake_pos")));
		//IronManControl.logger.log(Level.INFO, String.valueOf(Integer.parseInt(ControlThread.stateHashMap.get("car_batt1"))*batt1Multiplier));
		//IronManControl.logger.log(Level.INFO, String.valueOf(Integer.parseInt(ControlThread.stateHashMap.get("car_batt2"))*batt2Multiplier));
		IronManControl.guiFrame.ironControlFrame.statusPanel.setBattery1Voltage(Integer.parseInt(ControlThread.stateHashMap.get("car_batt1"))*batt1Multiplier);
		IronManControl.guiFrame.ironControlFrame.statusPanel.setBattery2Voltage(Integer.parseInt(ControlThread.stateHashMap.get("car_batt2"))*batt2Multiplier);
		
		IronManControl.guiFrame.ironControlFrame.statusPanel.taSignalStrength.setText("Signal: "+IronManControl.getWifiSignalStrength()+"%");
		
		return true;
	}
	
	public void stopNetworking() {
		IronManControl.logger.log(Level.INFO, "RC Stop networking");
		ControlThread.enableNet = false;
	}

	public void startNetworking() {
		IronManControl.logger.log(Level.INFO, "RC Start networking");
		ControlThread.enableNet = true;		
	}
	
	long map(long x, long in_min, long in_max, long out_min, long out_max) {
	  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
	
	//todo add command array
	
	public void setCommandValue(short index, char value) {
		if (this.currentCommand[index] != value) {
			this.currentCommand[index] = value;
			this.isCommandChanged = true;
		}	
		IronManControl.logger.log(Level.INFO, new String(this.currentCommand));
	}	
	
	public char getCommandValue(short index) {
		return this.currentCommand[index];
	}
	
	public void setCallerButton(JControlViewButton callerButton) {
		this.callerButton = callerButton;
	}
	
	/*private void setOKInUI(){
		callerButton.setBorder(BorderFactory.createLineBorder(Color.green, 3));
	}
	
	private void setErrorInUI() {
		callerButton.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));
	}*/
	
}
