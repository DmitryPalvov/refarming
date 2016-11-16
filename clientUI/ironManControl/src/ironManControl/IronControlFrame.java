package ironManControl;

import java.awt.Color;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import ironManControl.ActionQueueElement;
import ironManControl.ControlThread;
import ironManControl.IronManControl;
import ironManControl.kazachok.KazachokConst;

import javax.swing.JToggleButton;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

public class IronControlFrame extends JFrame {

	public JLabel lblCommandValue;
	public JLabel lblResponseValue;
	public JLabel lblStatusValue;
	public JLabel lblErrorValue;
	
	public JFrame fForwardVideo;
	public JFrame fTopVideo;
	
	public StatusPanel statusPanel;
	
	private HashMap<Integer,Action> keyActions;
	private ArrayList<AbstractButton> buttons;

	public ControlThread controlThread;
	
	private JPanel contentPane;

	private KeyEventDispatcher keyEventDispatcher;	
	

	
	/**
	 * Create the frame.
	 */
	public IronControlFrame(ControlThread controlThread) {
		this.controlThread = controlThread;
		
		keyActions = new HashMap<Integer, Action>();
		buttons = new ArrayList<AbstractButton>();
		
		setUndecorated(true);
		setResizable(false);
		setAlwaysOnTop(true);
		setTitle("IronControl");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 720, 1920, 360);
		contentPane = new JPanel();
		
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		/*JButton btnRearLight = new JButton("[1]RL");
		btnRearLight.setBounds(12, 13, 63, 25);
		btnRearLight.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		contentPane.add(btnRearLight);*/
		int defaultButtonHeight = 25;
		int defaultButtonWidth = 97;
		
		int zeroLineY = 0;
		int firstLineY = 5+defaultButtonHeight;
		int secondLineY = 5+defaultButtonHeight+defaultButtonHeight;
		int thirdLineY = 5+defaultButtonHeight+defaultButtonHeight+defaultButtonHeight;
		
		
		JPanel panel = new JPanel();
		panel.setBounds(12, 13, 1020, 150);
		contentPane.add(panel);
		panel.setLayout(null);
		JButton btnwGomax = new JButton("[W]GoMax");
		btnwGomax.setBounds(114, firstLineY, defaultButtonWidth, defaultButtonHeight);
		panel.add(btnwGomax);
		btnwGomax.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		buttons.add(btnwGomax);
		
		JButton btnsGomid = new JButton("[S]GoMid");
		btnsGomid.setBounds(114, secondLineY, defaultButtonWidth, defaultButtonHeight);
		panel.add(btnsGomid);
		btnsGomid.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		buttons.add(btnsGomid);
		
		JButton btnxGomin = new JButton("[X]GoMin");
		btnxGomin.setBounds(114, thirdLineY, defaultButtonWidth, defaultButtonHeight);
		panel.add(btnxGomin);
		btnxGomin.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		buttons.add(btnxGomin);
		
		JToggleButton btnqhorn = new JToggleButton("[Q]");
		btnqhorn.setBounds(5, firstLineY, defaultButtonWidth, defaultButtonHeight);
		panel.add(btnqhorn);
		//btnqhorn.setIcon(new ImageIcon(".\\resources\\horn_small.png"));
		btnqhorn.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		buttons.add(btnqhorn);
		
		JButton btnaleft = new JButton("[A]Left");
		btnaleft.setBounds(0, secondLineY, defaultButtonWidth, defaultButtonHeight);
		panel.add(btnaleft);
		btnaleft.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		buttons.add(btnaleft);
		
		JButton btndright = new JButton("[D]Right");
		btndright.setBounds(223, secondLineY, defaultButtonWidth, defaultButtonHeight);
		panel.add(btndright);
		btndright.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		buttons.add(btndright);
		
		JButton btnrgearup = new JButton("[R]GearUp");
		btnrgearup.setBounds(330, firstLineY, defaultButtonWidth, defaultButtonHeight);
		panel.add(btnrgearup);
		btnrgearup.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		buttons.add(btnrgearup);
		
		JButton btnfgeardown = new JButton("[F]GearDown");
		btnfgeardown.setBounds(332, secondLineY, 109, 25);
		panel.add(btnfgeardown);
		btnfgeardown.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		buttons.add(btnfgeardown);
		
		JButton btntBraker = new JButton("[T]Brake-");
		btntBraker.setBounds(454, firstLineY, defaultButtonWidth, defaultButtonHeight);
		panel.add(btntBraker);
		btntBraker.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		buttons.add(btntBraker);
		
		JButton btngbrakepush = new JButton("[G]Brake+");
		btngbrakepush.setBounds(453, secondLineY, defaultButtonWidth, defaultButtonHeight);
		panel.add(btngbrakepush);
		btngbrakepush.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		buttons.add(btngbrakepush);
		
		JButton btnfl = new JButton("[2]FL");
		btnfl.setBounds(74, zeroLineY, 63, defaultButtonHeight);
		panel.add(btnfl);
		btnfl.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		buttons.add(btnfl);
		
		JButton btnfan = new JButton("[3]Fan");
		btnfan.setBounds(148, zeroLineY, 71, defaultButtonHeight);
		panel.add(btnfan);
		btnfan.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		buttons.add(btnfan);
		
		JButton btnstart = new JButton("[5]Start");
		btnstart.setBounds(312, zeroLineY, 84, defaultButtonHeight);
		panel.add(btnstart);
		btnstart.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		buttons.add(btnstart);
		
		JButton btnonmass = new JButton("[7]OnMass");
		btnonmass.setBounds(488, zeroLineY, 109, defaultButtonHeight);
		panel.add(btnonmass);
		btnonmass.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		buttons.add(btnonmass);
		
		/*JButton btnfuelcut = new JButton("[8]FuelCut");
		btnfuelcut.setBounds(621, 13, defaultButtonWidth, defaultButtonHeight);
		contentPane.add(btnfuelcut);*/
		
		JButton btnspaceBlockDiff = new JButton("[Space] Block Diff");
		btnspaceBlockDiff.setBounds(113, thirdLineY + defaultButtonHeight + 5, 561, defaultButtonHeight);
		panel.add(btnspaceBlockDiff);
		btnspaceBlockDiff.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		buttons.add(btnspaceBlockDiff);
		
		JButton btnnunblockdiff = new JButton("[N]UnblockDiff");
		btnnunblockdiff.setBounds(565, thirdLineY, defaultButtonWidth, defaultButtonHeight);
		panel.add(btnnunblockdiff);
		btnnunblockdiff.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		buttons.add(btnnunblockdiff);
		
		/*JButton btnposos = new JButton("[4]Posos-");
		btnposos.setBounds(235, zeroLineY, defaultButtonWidth, defaultButtonHeight);
		contentPane.add(btnposos);*/
		
		/*JButton btnpodsos = new JButton("[6]Podsos+");
		btnpodsos.setBounds(402, zeroLineY, defaultButtonWidth, defaultButtonHeight);
		contentPane.add(btnpodsos);*/
		
		JButton btnhalt = new JButton("[0]Halt");
		btnhalt.setBounds(890, zeroLineY, defaultButtonWidth, defaultButtonHeight);
		panel.add(btnhalt);
		btnhalt.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		buttons.add(btnhalt);
		
		JToggleButton tglbtniCam = new JToggleButton("[I]Cam1");
		tglbtniCam.setBounds(743, firstLineY, defaultButtonWidth, defaultButtonHeight);
		panel.add(tglbtniCam);
		tglbtniCam.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		buttons.add(tglbtniCam);
		
		/*JToggleButton tglbtnorouter = new JToggleButton("[O]Router");
		tglbtnorouter.setBounds(849, 67, 89, defaultButtonHeight);
		tglbtnorouter.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		contentPane.add(tglbtnorouter);*/
		
		JButton btnpcam = new JButton("[P]Cam0");
		btnpcam.setBounds(937, firstLineY, defaultButtonWidth, defaultButtonHeight);
		panel.add(btnpcam);
		btnpcam.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		buttons.add(btnpcam);

		/*JButton btnlanreset = new JButton("[{]LanReset");
		btnlanreset.setBounds(960, 67, 113, 25);
		btnlanreset.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		contentPane.add(btnlanreset);*/

		JButton btnbParkingBrake = new JButton("[B]");
		btnbParkingBrake.setBounds(475, thirdLineY, defaultButtonWidth, defaultButtonHeight);
		panel.add(btnbParkingBrake);
		//btnbParkingBrake.setIcon(new ImageIcon(".\\resources\\paring_brake.png"));
		btnbParkingBrake.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		buttons.add(btnbParkingBrake);
		
		JButton btnyplug = new JButton("[Y]Plug-");
		btnyplug.setBounds(560, firstLineY, defaultButtonWidth, defaultButtonHeight);
		panel.add(btnyplug);
		btnyplug.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		buttons.add(btnyplug);
		
		JButton btnhplug = new JButton("[H]Plug+");
		btnhplug.setBounds(562, secondLineY, defaultButtonWidth, defaultButtonHeight);
		panel.add(btnhplug);
		btnhplug.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		buttons.add(btnhplug);

		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(null);
		controlPanel.setBounds(1280, 10, 140, 150);
		contentPane.add(controlPanel);
		
		JControlViewButton btnRc = new JControlViewButton ("RemoteControl");
		controlThread.setCallerButton(btnRc);
		//btnRc.addL
		btnRc.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (btnRc.isSelected()) {
					controlThread.startNetworking();
					enableKeyDispatcher();
				} else {
					controlThread.stopNetworking();
					setNetworkStatusError();
				}
			}
		});
		btnRc.setBounds(0, 0, 137, defaultButtonHeight);
		btnRc.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		controlPanel.add(btnRc);
		
		JControlViewButton tglbtnAudio = new JControlViewButton("Audio");
		tglbtnAudio.setBounds(0, 90, 137, defaultButtonHeight);
		tglbtnAudio.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		tglbtnAudio.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (tglbtnAudio.isSelected()) {
					IronManControl.videoAudioManager.runInputAudio();
				} else {
					IronManControl.videoAudioManager.stopInputAudio();
				}
			}
		});
		controlPanel.add(tglbtnAudio);
		
		JControlViewButton tglbtnTopvideo = new JControlViewButton("TopVideo");
		tglbtnTopvideo.setBounds(0, 30, 137, defaultButtonHeight);
		tglbtnTopvideo.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		tglbtnTopvideo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (tglbtnTopvideo.isSelected()) {
					IronManControl.videoAudioManager.runTopVideo(tglbtnTopvideo);
				} else {
					IronManControl.videoAudioManager.stopTopVideo();
				}
			}
		});
		controlPanel.add(tglbtnTopvideo);
		
		JControlViewButton tglbtnForwardvideo = new JControlViewButton("ForwardVideo");
		tglbtnForwardvideo.setBounds(0, 60, 137, defaultButtonHeight);
		tglbtnForwardvideo.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		tglbtnForwardvideo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (tglbtnForwardvideo.isSelected()) {
					IronManControl.videoAudioManager.runForwardVideo(tglbtnForwardvideo);
				} else {
					IronManControl.videoAudioManager.stopForwardVideo();;
				}
			}
		});
		controlPanel.add(tglbtnForwardvideo);
		
		JPanel statusTextPanel = new JPanel();
		statusTextPanel.setLayout(null);
		statusTextPanel.setBounds(0, 170, 1280, 150);
		contentPane.add(statusTextPanel);
		
		
		JLabel lblCommand_1 = new JLabel("Command:");
		lblCommand_1.setBounds(5, 0, 63, 16);
		statusTextPanel.add(lblCommand_1);
		
		lblCommandValue = new JLabel("command");
		lblCommandValue.setBounds(86, 0, 162, 16);
		statusTextPanel.add(lblCommandValue);
		
		JLabel lblResponse = new JLabel("Response:");
		lblResponse.setBounds(5, 25, 63, 16);
		statusTextPanel.add(lblResponse);

		lblResponseValue = new JLabel("state");
		lblResponseValue.setBounds(86, 25, 347, 16);
		statusTextPanel.add(lblResponseValue);
		
		JLabel lblStatus = new JLabel("Status:");
		lblStatus.setBounds(5, 50, 56, 16);
		statusTextPanel.add(lblStatus);
		
		lblStatusValue = new JLabel("status");
		lblStatusValue.setBounds(86, 50, 523, 16);
		statusTextPanel.add(lblStatusValue);
		
		JLabel lblError = new JLabel("Error:");
		lblError.setBounds(5, 75, 56, 16);
		statusTextPanel.add(lblError);
		
		lblErrorValue = new JLabel("OK");
		lblErrorValue.setBounds(86, 75, 561, 16);
		statusTextPanel.add(lblErrorValue);
	
		statusPanel = new StatusPanel();
		statusPanel.setBounds(1280+150, 0, 239, 177);
		contentPane.add(statusPanel);
		
		
		/* # 0 - steering position
		 * # 1 - accelerate position 
		 * # 2 - brake position
		 * # 3 - gear position
		 * # 4 - differential position
		 * # 5 - podsos position
		 * # 6 - horn
		 * # 7 - forw light
		 * # 8 - rear light
		 * # 9 - engine fan
		 * # 10 - starter
		 * # 11 - ignition (mass)
		 * # 12 - fuel cut position
		 * # 13 - halt engine
		 	*/
		// command = "63010200000000000"
		keyActions.put(KeyEvent.VK_Q, new AbstractAction() { // horn
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					btnqhorn.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));
					controlThread.setCommandValue(KazachokConst.HORN_ID, '1');
				} else if(ae.getID() == KeyEvent.KEY_RELEASED) {
					btnqhorn.setBorder(BorderFactory.createLineBorder(Color.white, 3));
					controlThread.setCommandValue(KazachokConst.HORN_ID, '0');
				}
			}
		});
		
		keyActions.put(KeyEvent.VK_0, new AbstractAction() { // halt
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					btnhalt.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));
					controlThread.setCommandValue(KazachokConst.HALT_ENGINE_ID, '1');
				} else if(ae.getID() == KeyEvent.KEY_RELEASED) {
					btnhalt.setBorder(BorderFactory.createLineBorder(Color.white, 3));
					controlThread.setCommandValue(KazachokConst.HALT_ENGINE_ID, '0');
				}
			}
		});
		
		keyActions.put(KeyEvent.VK_W, new AbstractAction() { // throttle
			public void actionPerformed(ActionEvent ae) {
				IronManControl.logger.log(Level.INFO, "Push");
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					btnwGomax.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));
					controlThread.actionQueue.clearActions(KazachokConst.THROTTLE_ID);
					controlThread.setCommandValue(KazachokConst.THROTTLE_ID, '9');
				} else if(ae.getID() == KeyEvent.KEY_RELEASED) {
					btnwGomax.setBorder(BorderFactory.createLineBorder(Color.white, 3));
					controlThread.setCommandValue(KazachokConst.THROTTLE_ID, '0');
				}
			}
		});
		keyActions.put(KeyEvent.VK_S, new AbstractAction() { // throttle
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					btnsGomid.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));
					controlThread.setCommandValue(KazachokConst.THROTTLE_ID, '3');
				} else if(ae.getID() == KeyEvent.KEY_RELEASED) {
					btnsGomid.setBorder(BorderFactory.createLineBorder(Color.white, 3));
					controlThread.setCommandValue(KazachokConst.THROTTLE_ID, '0');
				}
			}
		});
		keyActions.put(KeyEvent.VK_X, new AbstractAction() { // throttle
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					btnxGomin.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));
					controlThread.setCommandValue(KazachokConst.THROTTLE_ID, '1');
				} else if(ae.getID() == KeyEvent.KEY_RELEASED) {
					btnxGomin.setBorder(BorderFactory.createLineBorder(Color.white, 3));
					controlThread.setCommandValue(KazachokConst.THROTTLE_ID, '0');
				}
			}
		});
		
		keyActions.put(KeyEvent.VK_T, new AbstractAction() { // brake
			public void actionPerformed(ActionEvent ae) {
				btnbParkingBrake.setBorder(BorderFactory.createLineBorder(Color.white, 3));
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					btntBraker.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));
					controlThread.actionQueue.clearActions(KazachokConst.BRAKE_ID);
					controlThread.setCommandValue(KazachokConst.BRAKE_ID, '1');					
				} else if(ae.getID() == KeyEvent.KEY_RELEASED) {
					btntBraker.setBorder(BorderFactory.createLineBorder(Color.white, 3));
					//controlThread.setCommandValue(KazachokConst.BRAKE_ID, '0');
					controlThread.actionQueue.clearActions(KazachokConst.BRAKE_ID);
					controlThread.actionQueue.add(new ActionQueueElement(100, KazachokConst.BRAKE_ID, '2'));
					controlThread.actionQueue.add(new ActionQueueElement(2000, KazachokConst.BRAKE_ID, '0'));
				}
			}
		});
		keyActions.put(KeyEvent.VK_G, new AbstractAction() { // brake
			public void actionPerformed(ActionEvent ae) {
				btnbParkingBrake.setBorder(BorderFactory.createLineBorder(Color.white, 3));
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					btngbrakepush.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));
					controlThread.actionQueue.clearActions(KazachokConst.BRAKE_ID);
					controlThread.setCommandValue(KazachokConst.BRAKE_ID, '2');
				} else if(ae.getID() == KeyEvent.KEY_RELEASED) {
					btngbrakepush.setBorder(BorderFactory.createLineBorder(Color.white, 3));
					controlThread.actionQueue.clearActions(KazachokConst.BRAKE_ID);
					controlThread.setCommandValue(KazachokConst.BRAKE_ID, '0');
				}
			}
		});
		keyActions.put(KeyEvent.VK_B, new AbstractAction() { // parking brake 
			public void actionPerformed(ActionEvent ae) {				
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					btnbParkingBrake.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));
					controlThread.actionQueue.clearActions(KazachokConst.BRAKE_ID);
					controlThread.setCommandValue(KazachokConst.BRAKE_ID, '1');
					controlThread.actionQueue.add(new ActionQueueElement(1000, KazachokConst.BRAKE_ID, '0'));
					
				}
			}
		});
		
		keyActions.put(KeyEvent.VK_A, new AbstractAction() { // turn left
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					btnaleft.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));
					controlThread.setCommandValue(KazachokConst.STEERENG_ID, '1');
					//todo increment
				} else if(ae.getID() == KeyEvent.KEY_RELEASED) {
					btnaleft.setBorder(BorderFactory.createLineBorder(Color.white, 3));
					controlThread.setCommandValue(KazachokConst.STEERENG_ID, '5');
					//todo isKeepSteereng
				}
			}
		}); 
		keyActions.put(KeyEvent.VK_D, new AbstractAction() { // turn right
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					btndright.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));
					controlThread.setCommandValue(KazachokConst.STEERENG_ID, '9');
					//todo increment
				} else if(ae.getID() == KeyEvent.KEY_RELEASED) {
					btndright.setBorder(BorderFactory.createLineBorder(Color.white, 3));
					controlThread.setCommandValue(KazachokConst.STEERENG_ID, '5');
					//todo isKeepSteereng
				}
			}
		}); 
		keyActions.put(KeyEvent.VK_R, new AbstractAction() { // gear up
			public void actionPerformed(ActionEvent ae) {
				if (true) { // non automated
					if (ae.getID() == KeyEvent.KEY_PRESSED) {
						btnrgearup.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));
						controlThread.setCommandValue(KazachokConst.GEAR_ID, '2');
					} else if(ae.getID() == KeyEvent.KEY_RELEASED) {
						btnrgearup.setBorder(BorderFactory.createLineBorder(Color.white, 3));
						controlThread.setCommandValue(KazachokConst.GEAR_ID, '0');
					}	
				} else { // automated
					if (ae.getID() == KeyEvent.KEY_PRESSED) {
						controlThread.actionQueue.clearActions(KazachokConst.GEAR_ID);
						controlThread.setCommandValue(KazachokConst.GEAR_ID, '2');
						//todo measure time
						controlThread.actionQueue.add(new ActionQueueElement(1500, KazachokConst.GEAR_ID, '1'));
						controlThread.actionQueue.add(new ActionQueueElement(2000, KazachokConst.GEAR_ID, '0'));
					}
				}
				
			}
		});
		keyActions.put(KeyEvent.VK_F, new AbstractAction() { // gear down
			public void actionPerformed(ActionEvent ae) {
				if (true) { // non automated
					if (ae.getID() == KeyEvent.KEY_PRESSED) {
						btnfgeardown.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));
						controlThread.setCommandValue(KazachokConst.GEAR_ID, '1');					
					} else if(ae.getID() == KeyEvent.KEY_RELEASED) {
						btnfgeardown.setBorder(BorderFactory.createLineBorder(Color.white, 3));
						controlThread.setCommandValue(KazachokConst.GEAR_ID, '0');
					}
				} else {
					if (ae.getID() == KeyEvent.KEY_PRESSED) {
						controlThread.actionQueue.clearActions(KazachokConst.GEAR_ID);
						controlThread.setCommandValue(KazachokConst.GEAR_ID, '1');
						//todo measure time
						controlThread.actionQueue.add(new ActionQueueElement(1500, KazachokConst.GEAR_ID, '2'));
						controlThread.actionQueue.add(new ActionQueueElement(2000, KazachokConst.GEAR_ID, '0'));
					}
				}
			}
		}); 

		keyActions.put(KeyEvent.VK_Y, new AbstractAction() { // plug up
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					btnyplug.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));
					controlThread.setCommandValue(KazachokConst.PLUG_ID, '2');
				} else if(ae.getID() == KeyEvent.KEY_RELEASED) {
					btnyplug.setBorder(BorderFactory.createLineBorder(Color.white, 3));
					controlThread.setCommandValue(KazachokConst.PLUG_ID, '0');
				}	
				
			}
		});
		keyActions.put(KeyEvent.VK_H, new AbstractAction() { // plug down
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					btnhplug.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));
					controlThread.setCommandValue(KazachokConst.PLUG_ID, '1');					
				} else if(ae.getID() == KeyEvent.KEY_RELEASED) {
					btnhplug.setBorder(BorderFactory.createLineBorder(Color.white, 3));
					controlThread.setCommandValue(KazachokConst.PLUG_ID, '0');
				}
			}
		}); 

		
		/*keyActions.put(KeyEvent.VK_E,  new AbstractAction() { // keep throttle up
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					isKeepThrottle = true;
					if (ae.getID() == KeyEvent.KEY_PRESSED) {
						char current = controlThread.getCommandValue(KazachokConst.THROTTLE_ID);
						if (current != '9') {
							current++;
						}
						controlThread.setCommandValue(KazachokConst.THROTTLE_ID, current);
					}
				}				
			}
		});
		keyActions.put(KeyEvent.VK_R,  new AbstractAction() { // keep throttle down
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					isKeepThrottle = true;
					if (ae.getID() == KeyEvent.KEY_PRESSED) {
						char current = controlThread.getCommandValue(KazachokConst.THROTTLE_ID);
						if (current != '0') {
							current--;
						}
						controlThread.setCommandValue(KazachokConst.THROTTLE_ID, current);
					}
				}				
			}
		}); */
		
		/*keyActions.put(KeyEvent.VK_X,  new AbstractAction() { // reset keep throttle
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					isKeepThrottle = false;					
				}				
			}
		})*/;
		
		/*keyActions.put(KeyEvent.VK_1, new AbstractAction() { // rear light
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					char currentValue = controlThread.getCommandValue(KazachokConst.REAR_LIGHT_ID);					
					boolean state = (currentValue=='0');
					btnRearLight.setBorder(BorderFactory.createLineBorder((state?Color.yellow:Color.white), 3));
					controlThread.setCommandValue(KazachokConst.REAR_LIGHT_ID, (state?'1':'0'));
				}
			}
		});*/ 
		keyActions.put(KeyEvent.VK_2, new AbstractAction() { // front light
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					char currentValue = controlThread.getCommandValue(KazachokConst.FRONT_LIGHT_ID);
					boolean state = (currentValue=='0');
					btnfl.setBorder(BorderFactory.createLineBorder((state?Color.yellow:Color.white), 3));
					controlThread.setCommandValue(KazachokConst.FRONT_LIGHT_ID, (state?'1':'0'));
				}
			}
		});
		keyActions.put(KeyEvent.VK_3, new AbstractAction() { // engine fan
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					char currentValue = controlThread.getCommandValue(KazachokConst.ENGINE_FAN_ID);
					boolean state = (currentValue=='0');
					btnfan.setBorder(BorderFactory.createLineBorder((state?Color.yellow:Color.white), 3));
					controlThread.setCommandValue(KazachokConst.ENGINE_FAN_ID, (state?'1':'0'));
				}
			}
		});
		
		keyActions.put(KeyEvent.VK_5, new AbstractAction() { // starter
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					btnstart.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));
					controlThread.setCommandValue(KazachokConst.STARTER_ID, '1');
				} else if(ae.getID() == KeyEvent.KEY_RELEASED) {
					btnstart.setBorder(BorderFactory.createLineBorder(Color.white, 3));
					controlThread.setCommandValue(KazachokConst.STARTER_ID, '0');
				}
			}
		}); 
		keyActions.put(KeyEvent.VK_7, new AbstractAction() { // on Mass
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					char currentValue = controlThread.getCommandValue(KazachokConst.IGNITION_MASS_ID);
					boolean state = (currentValue=='0');
					btnonmass.setBorder(BorderFactory.createLineBorder((state?Color.yellow:Color.white), 3));
					controlThread.setCommandValue(KazachokConst.IGNITION_MASS_ID, (state?'1':'0'));
				}
			}
		});
		/*keyActions.put(KeyEvent.VK_8, new AbstractAction() { // fuel cut
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					char currentValue = controlThread.getCommandValue(KazachokConst.FUEL_CUT_ID);
					boolean state = (currentValue=='0');
					btnfuelcut.setBorder(BorderFactory.createLineBorder((state?Color.yellow:Color.white), 3));
					controlThread.setCommandValue(KazachokConst.FUEL_CUT_ID, (currentValue=='0'?'1':'0'));
				}
			}
		});*/
		
		keyActions.put(KeyEvent.VK_SPACE, new AbstractAction() { // differential on
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					btnspaceBlockDiff.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));
					btnnunblockdiff.setBorder(BorderFactory.createLineBorder(Color.white, 3));
					controlThread.actionQueue.clearActions(KazachokConst.DIFFERENTIAL_ID);
					controlThread.setCommandValue(KazachokConst.DIFFERENTIAL_ID, '1');
					controlThread.actionQueue.add(new ActionQueueElement(500, KazachokConst.DIFFERENTIAL_ID, '0'));					
				}
			}
		}); 
		keyActions.put(KeyEvent.VK_N, new AbstractAction() { // differential off
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					btnspaceBlockDiff.setBorder(BorderFactory.createLineBorder(Color.white, 3));
					btnnunblockdiff.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));
					
					controlThread.actionQueue.clearActions(KazachokConst.DIFFERENTIAL_ID);
					controlThread.setCommandValue(KazachokConst.DIFFERENTIAL_ID, '2');
					controlThread.actionQueue.add(new ActionQueueElement(500, KazachokConst.DIFFERENTIAL_ID, '0'));
					
				}
			}
		}); 

		
		keyActions.put(KeyEvent.VK_6, new AbstractAction() { // posos +
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					char current = controlThread.getCommandValue(KazachokConst.PODSOS_ID);
					if (current != '9') {
						current++;
					}
					controlThread.setCommandValue(KazachokConst.PODSOS_ID, current);
				}
			}			
		});
		keyActions.put(KeyEvent.VK_4, new AbstractAction() { // podsos -
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					char current = controlThread.getCommandValue(KazachokConst.PODSOS_ID);
					if (current != '0') {
						current--;
					}
					controlThread.setCommandValue(KazachokConst.PODSOS_ID, current);
				}
			}			
		});
		
		keyActions.put(KeyEvent.VK_I, new AbstractAction() { // raspberry
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					char currentValue = controlThread.getCommandValue(KazachokConst.RASPBERRY_ID);
					boolean state = (currentValue=='0');
					tglbtniCam.setBorder(BorderFactory.createLineBorder((state?Color.yellow:Color.white), 3));
					controlThread.setCommandValue(KazachokConst.RASPBERRY_ID, (state?'1':'0'));
				}
			}
		});
		/*keyActions.put(KeyEvent.VK_O, new AbstractAction() { // router
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					char currentValue = controlThread.getCommandValue(KazachokConst.ROUTER_ID);
					boolean state = (currentValue=='0');
					tglbtnorouter.setBorder(BorderFactory.createLineBorder((state?Color.yellow:Color.white), 3));
					controlThread.setCommandValue(KazachokConst.ROUTER_ID, (state?'1':'0'));
				}
			}
		});*/
		keyActions.put(KeyEvent.VK_P, new AbstractAction() { // camera 1
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					char currentValue = controlThread.getCommandValue(KazachokConst.CAMERA_1_ID);
					boolean state = (currentValue=='0');
					btnpcam.setBorder(BorderFactory.createLineBorder((state?Color.yellow:Color.white), 3));
					controlThread.setCommandValue(KazachokConst.CAMERA_1_ID, (state?'1':'0'));
				}
			}
		});
		
		/*keyActions.put(KeyEvent.VK_OPEN_BRACKET, new AbstractAction() { // lan reset
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					char currentValue = controlThread.getCommandValue(KazachokConst.ROUTER_ID);
					boolean state = (currentValue=='0');
					btnlanreset.setBorder(BorderFactory.createLineBorder((state?Color.yellow:Color.white), 3));
					controlThread.setCommandValue(KazachokConst.CAMERA_1_ID, (state?'1':'0'));
				}
			}
		});*/

		keyActions.put(KeyEvent.VK_M, new AbstractAction() { // show profiler
			public void actionPerformed(ActionEvent ae) {
				if (ae.getID() == KeyEvent.KEY_PRESSED) {
					IronManControl.logger.log(Level.INFO, IronManControl.profiler.toString());
				}
			}
		});

		keyEventDispatcher = new KeyEventDispatcher() {
			@Override
			public boolean dispatchKeyEvent(KeyEvent e) {
				int key = e.getKeyCode();
				if (keyActions.containsKey(key)){
					keyActions.get(key).actionPerformed(new ActionEvent(e.getSource(), e.getID(), null ));
					return true;
				}else if (key == 0){
					return false;
				}else{
					return false;
				}
			}
		};
		
	}
	
	boolean isEnabledKeyDispatcher = false;
	public void enableKeyDispatcher() {
		if (!isEnabledKeyDispatcher) {
			KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);
			isEnabledKeyDispatcher = true;
		}
	}
	
	public void disableKeyDispatcher() {
		if (isEnabledKeyDispatcher) {
			KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(keyEventDispatcher);
			isEnabledKeyDispatcher = false;
		}
		
	}
	
	public void setNetworkStatusError() { // we don't know current position
		statusPanel.setGear("-1");
		statusPanel.setThrottlePosition("X");
		statusPanel.setBrakeStatus(0);
		statusPanel.setBattery1Voltage(0);
		statusPanel.setBattery2Voltage(0);
		
		for(AbstractButton button :buttons) {
			button.setBorder(BorderFactory.createLineBorder(Color.white, 3));
		}		
		disableKeyDispatcher();
	}
}
