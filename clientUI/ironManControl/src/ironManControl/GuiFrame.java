package ironManControl;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JPanel;

import ironManControl.kazachok.KazachokConst;

@SuppressWarnings("serial")
public class GuiFrame {
	
	private HashMap<Integer,Action> keyActions;
	
	public GuiPanel guiPanel;
	public ControlThread controlThread;
	public IronControlFrame ironControlFrame;
	
	public GuiFrame() {
	}
	
	public void init() {
		controlThread = new ControlThread();
		
		IronManControl.logger.log(Level.INFO, "setupFrame");
		ironControlFrame = new IronControlFrame(controlThread);
		ironControlFrame.setVisible(true);
		ironControlFrame.controlThread = controlThread;

		IronManControl.logger.log(Level.INFO, "setupControlThread");
		setupControlThread();
		
		IronManControl.logger.log(Level.INFO, "setupVideo");
		setupVideo();		
	}
	
	private void setupVideo() {
		IronManControl.videoAudioManager = new VideoAudioManager();
		
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
		    @Override
		    public void run() {
		    	IronManControl.videoAudioManager.finish();			        
		    }
		});
		
		IronManControl.videoAudioManager.setup();
	}
	
		
	private void setupControlThread() {
		controlThread.execute();		
	}

}






