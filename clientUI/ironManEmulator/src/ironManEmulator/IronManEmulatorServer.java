package ironManEmulator;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
/*
 * Copyright 2003-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//package org.apache.commons.net.telnet;
import java.net.ServerSocket;
import java.net.Socket;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;


public class IronManEmulatorServer {

	public static void main(String[] args) {
		try {
			TelnetTestSimpleServer server = new TelnetTestSimpleServer(1983);
			server.run();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}


/***
 * Simple TCP server.
 * Waits for connections on a TCP port in a separate thread.
 * 


 * @author Bruno D'Avanzo
 ***/
class TelnetTestSimpleServer implements Runnable
{
    ServerSocket serverSocket = null;
    //Socket clientSocket = null;
    Thread listener = null;
    DatagramSocket clientSocket = null;

    /***
     * test of client-driven subnegotiation.
     * 


     * @param port - server port on which to listen.
     ***/
    public TelnetTestSimpleServer(int port) throws IOException
    {
		clientSocket = new DatagramSocket(10001, InetAddress.getByAddress(new byte[]{(byte)127,(byte)0,0,(byte)1}));

    	//serverSocket = new ServerSocket(port);

        //listener = new Thread (this);

        //listener.start();
    }
    
    byte[] receiveData = new byte[1024];
    public void run() {
    	try {
    		//DatagramSocket clientSocket = new DatagramSocket(60003, InetAddress.getByAddress(new byte[]{(byte)127,(byte)0,0,(byte)1}));
    		//clientSocket.setSoTimeout(5000);
    		int i = 0;
    		while(true) {
    			i++;
    			System.out.println("here"+i);
    			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        		clientSocket.receive(receivePacket);
    			String response = new String(receivePacket.getData());
    			System.out.println(response);
    			String command = "OK:230:12:6";
	        	byte[] crc = CRC16.crc16(command.getBytes()); 
	        	crc = java.util.Base64.getEncoder().encode(crc);
	    		for (byte val : crc) {
	    			command += (char)val;
	    		}
	    		command += '\n';
	    		byte[] sendData = command.getBytes();
	    		
    			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
    			clientSocket.send(sendPacket);
    		}
			//clientSocket.close();
    	} catch (IOException ioe) {
            System.err.println("Exception in open, "+ ioe.getMessage());
            ioe.printStackTrace();

    	}
    	
    	
    }

    /***
     * Run for the thread. Waits for new connections
     ***/
    public void run1()
    {
        boolean bError = false;
        while(!bError)
        {
        	try
            {
        		Thread.sleep(20);
            	
                if (clientSocket == null) {
                	clientSocket = serverSocket.accept();
                }
                synchronized (clientSocket)
                {
                    try
                    {
                    	BufferedReader in =
        			        new BufferedReader(
        			        		new InputStreamReader(clientSocket.getInputStream()));
        			    String request;
        			    if ((request = in.readLine()) != null) {    
        			    	System.out.println(request);
        			        if (this.parseResponse(request)) {
        			        	PrintWriter pw = new PrintWriter(clientSocket.getOutputStream());
        			        	String command = "OK:230:12:6";
        			        	byte[] crc = CRC16.crc16(command.getBytes()); 
        			        	crc = java.util.Base64.getEncoder().encode(crc);
        			    		for (byte val : crc) {
        			    			command += (char)val;
        			    		}
        			    		command += '\n';
        			    		
                               	pw.print(command);
                               	pw.flush();                            		
        			        } else {
        			        	PrintWriter pw = new PrintWriter(clientSocket.getOutputStream());
        			        	String command = "ERROR";
        			        	byte[] crc = CRC16.crc16(command.getBytes()); 
        			    		crc = java.util.Base64.getEncoder().encode(crc);
        			    		for (byte val : crc) {
        			    			command += (char)val;
        			    		}
        			    		command += '\n';
        			    		
                               	pw.print(command);
                               	pw.flush();
        			        }
        			    }
                    	//clientSocket.wait();
                    }
                    catch (Exception e)
                    {
                        System.err.println("Exception in wait, "+ e.getMessage());
                        e.printStackTrace();
                    }
                    try
                    {
                        //clientSocket.close();
                    }
                    catch (Exception e)
                    {
                        System.err.println("Exception in close, "+ e.getMessage());
                    }
                }
            }
            catch (Exception e)
            {
                bError = true;
            }
        	
        }

        try
        {
            serverSocket.close();
        }
        catch (Exception e)
        {
            System.err.println("Exception in close, "+ e.getMessage());
        }
    }

    private boolean parseResponse(String response) {
    	if (response.startsWith("co")) {
    		byte[] r = response.getBytes();
    		byte[] crcB64 = java.util.Arrays.copyOfRange(r, r.length-4, r.length);
    		byte[] crc = java.util.Base64.getDecoder().decode(crcB64);    		
    		r = java.util.Arrays.copyOf(r, r.length-4);
    		byte[] o = CRC16.crc16(r);
    		
    		if (java.util.Arrays.equals(CRC16.crc16(r), crc)) {
    			return true;
    		} else {
    			System.out.println("Bad CRC");
    		}
    	} else {
    		System.err.println("Bad start");
    	}
    	return false;
    }
    
    /***
     * Disconnects the client socket
     ***/
    public void disconnect()
    {
        synchronized (clientSocket)
        {
            try
            {
                clientSocket.notify();
            }
            catch (Exception e)
            {
                System.err.println("Exception in notify, "+ e.getMessage());
            }
        }
    }

    /***
     * Stop the listener thread
     ***/
    public void stop()
    {
        listener.interrupt();
        try
        {
            serverSocket.close();
        }
        catch (Exception e)
        {
            System.err.println("Exception in close, "+ e.getMessage());
        }
    }

    /***
     * Gets the input stream for the client socket
     ***/
    public InputStream getInputStream() throws IOException
    {
        if(clientSocket != null)
        {
            return(clientSocket.getInputStream());
        }
        else
        {
            return(null);
        }
    }

    /***
     * Gets the output stream for the client socket
     ***/
    public OutputStream getOutputStream() throws IOException
    {
        if(clientSocket != null)
        {
            return(clientSocket.getOutputStream());
        }
        else
        {
            return(null);
        }
    }
}

 
