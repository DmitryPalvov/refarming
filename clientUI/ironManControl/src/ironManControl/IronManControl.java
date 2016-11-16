package ironManControl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.logging.Level;

public class IronManControl {
	
	public static java.util.logging.Logger logger = java.util.logging.Logger.getLogger("IronManControl");
	public static java.util.logging.Logger commandLogger = java.util.logging.Logger.getLogger("IronManCommand");

	//public final static String frontCameraUri = "rtsp://192.168.0.168:554/0";	
	public final static String frontCameraUri = "ffmpeg://tcp://192.168.0.101:5000";
		
	public static ProfilerMap<String, Integer> profiler = new ProfilerMap<String, Integer>();	
	
	public static GuiFrame guiFrame;
	
	public static VideoAudioManager videoAudioManager;
	
	private static String logPath = "C:/work/ironMan/";
	//private static String path = ;
	
	private static String wifiSingalStrengthCommand = "netsh wlan show interfaces";
	
    private static void initLogger() throws IOException{
    	java.util.logging.FileHandler fh;  

    	// This block configure the logger with handler and formatter  
        fh = new java.util.logging.FileHandler(logPath+"all.log");
        logger.addHandler(fh);
        java.util.logging.SimpleFormatter formatter = new java.util.logging.SimpleFormatter();
        fh.setFormatter(formatter);  
        
        fh = new java.util.logging.FileHandler(logPath+"command.log"); 
        commandLogger.addHandler(fh);
        formatter = new java.util.logging.SimpleFormatter();
        fh.setFormatter(formatter);  
        
        
        
    }

	public static void main(String[] args) {
		try {
			OsCheck.OSType ostype=OsCheck.getOperatingSystemType();
			switch (ostype) {
			    case Windows: 
			    	break;
			    case MacOS:
			    	logPath = "/Users/thestig/Documents/Refarming/Logs/";
			    	//todo add gstreamer path
			    	break;
			    case Linux: break;
			    case Other: break;
			}
			initLogger();
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}		
		try {
			logger.log(Level.INFO, "Starting...");
			
			
			IronManControl.guiFrame = new GuiFrame();
			IronManControl.guiFrame.init();
					
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.WARNING, e.getMessage(), e);
		}
		
	}
	
	public static int getWifiSignalStrength() {
		OsCheck.OSType ostype=OsCheck.getOperatingSystemType();
		switch (ostype) {
		    case Windows: 
		    	return getWifiSignalStrength_Windows();
		    case MacOS: break;
		    case Linux: break;
		    case Other: break;
		}
		return 0;
	}
	
	public static int getWifiSignalStrength_Windows() {
		String res = "0";
		try {
			Process process = Runtime.getRuntime().exec(wifiSingalStrengthCommand);
			InputStream in = process.getInputStream();
			BufferedReader reader = new BufferedReader (new InputStreamReader(in));
			String l;
			int i = 0;
			while ((l = reader.readLine())!= null) {
				if (i == 18) {
					String[] a = l.split(" ");
					res = a[a.length-1].substring(0, a[a.length-1].lastIndexOf("%"));
					break;
				}
				i++;
				
			}	
		} catch (IOException e) {
			logger.log(Level.WARNING, "Reading signal strength error");
		}
		return Integer.parseInt(res);
	}

	void testCrc() {
		byte[] crc = CRC16.crc16("co5000x".getBytes());
		logger.log(Level.INFO, (char)crc[0] + " "+ (char)crc[1]);
		logger.log(Level.INFO, crc[0] + " "+ crc[1]);
		
		crc = CRC16.crc16("co6000x".getBytes());
		logger.log(Level.INFO, (char)crc[0] + " "+ (char)crc[1]);
		logger.log(Level.INFO, crc[0] + " "+ crc[1]);
		
		logger.log(Level.INFO, new Character((char)(256-51)).toString());
		logger.log(Level.INFO, new Character((char)(256-119)).toString());
	}
	
}
class ProfilerMap<S, I> extends HashMap<String, Integer> {
	
	private static final long serialVersionUID = 1L;

	public int increment(String key) {
		
		int count = this.containsKey(key) ? (int)this.get(key) : 0;
		count++;
		this.put(key, Integer.valueOf(count));
		return count;
	}
}
