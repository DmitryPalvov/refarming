package ironManControl;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;

@SuppressWarnings("serial")
public class GuiPanel extends JPanel {
	
	/*private JButton fowardButton;
	private JButton leftButton;
	private JButton rightButton;
	private JButton backwardButton;*/
	public JLabel lblErrors;
	public JTextArea taStatus;
	
	public JTextArea taCommandOverride;
	
	private JToggleButton btnRemoteControl;
	private JToggleButton btnStartAudio;
	private JToggleButton btnStartForwardVideo;
	private JToggleButton btnStartTopVideo;
	
	public JToggleButton tbBrakePush;
	public JToggleButton tbBrakePull;
	public JToggleButton tbBrakePark;
	
	
	public GuiPanel() {
		lblErrors = new JLabel("Init");
		taStatus = new JTextArea("OLOL");
		taCommandOverride = new JTextArea();
		taStatus.setEditable(false);
		taStatus.setDragEnabled(true);
		
		btnRemoteControl = new JToggleButton("RC");
		btnStartAudio = new JToggleButton("Sound");
		btnStartForwardVideo = new JToggleButton("FV");
		btnStartTopVideo = new JToggleButton("TV");
		
		tbBrakePush = new JToggleButton("BrakePush[g]");
		tbBrakePull = new JToggleButton("BrakePull[t]");
		tbBrakePark = new JToggleButton("BrakePark[b]");
		
		
		/*fowardButton = new JButton("^");
		leftButton = new JButton("<");
		rightButton = new JButton(">");
		backwardButton = new JButton("v");*/
		
		SetupPanel();
	}
	
	private void SetupPanel(){
		//setLayout(null);
		setLayout(new GridLayout(6, 1));
		setBackground(Color.DARK_GRAY);
		
		add(lblErrors);
		
		
/*		add(btnRemoteControl);
		add(btnStartAudio);
		add(btnStartForwardVideo);
		add(btnStartTopVideo);*/
		
		btnRemoteControl.setBounds(200, 25, 89, 23);
		btnRemoteControl.setSelected(true);
		btnRemoteControl.setBackground(Color.GREEN);
		btnRemoteControl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (btnRemoteControl.isSelected()) {
					if (IronManControl.guiFrame != null && IronManControl.guiFrame.controlThread != null) {
						IronManControl.guiFrame.controlThread.startNetworking();
						btnRemoteControl.setBackground(Color.GREEN);
					}
					
				} else {
					if (IronManControl.guiFrame != null && IronManControl.guiFrame.controlThread != null) {
						IronManControl.guiFrame.controlThread.stopNetworking();
						btnRemoteControl.setBackground(Color.GRAY);
					}					
				}
			}
		});
		
		btnStartAudio.setBounds(200, 50, 83, 23);
		btnStartAudio.setSelected(false);
		btnStartAudio.setBackground(Color.GRAY);
		btnStartAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (btnStartAudio.isSelected()) {
					IronManControl.videoAudioManager.startProcess("audio");
					btnStartAudio.setText("ON");
					btnStartAudio.setBackground(Color.GREEN);
				} else {
					IronManControl.videoAudioManager.stopProcess("audio");
					btnStartAudio.setText("OFF");
					btnStartAudio.setBackground(Color.GRAY);
				}
			}
		});
		
		btnStartForwardVideo.setBounds(200, 75, 83, 23);
		btnStartForwardVideo.setSelected(true);
		btnStartForwardVideo.setBackground(Color.GREEN);
		btnStartForwardVideo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (btnStartForwardVideo.isSelected()) {
					IronManControl.videoAudioManager.startProcess("forwardVideo");
					btnStartForwardVideo.setText("FV ON");
					btnStartForwardVideo.setBackground(Color.GREEN);
				} else {
					IronManControl.videoAudioManager.stopProcess("forwardVideo");
					btnStartForwardVideo.setText("FV OFF");
					btnStartForwardVideo.setBackground(Color.GRAY);
				}
			}
		});
		
		btnStartTopVideo.setBounds(200, 100, 83, 23);
		btnStartTopVideo.setSelected(true);
		btnStartTopVideo.setBackground(Color.GREEN);
		btnStartTopVideo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (btnStartTopVideo.isSelected()) {
					IronManControl.videoAudioManager.startProcess("topVideo");
					btnStartTopVideo.setText("TV ON");
					btnStartTopVideo.setBackground(Color.GREEN);
				} else {
					IronManControl.videoAudioManager.stopProcess("topVideo");
					btnStartTopVideo.setText("TV OFF");
					btnStartTopVideo.setBackground(Color.GRAY);
				}
			}
		});
		lblErrors.setBounds(0, 0, 500, 22);
		taStatus.setBounds(0, 22, 500, 22);
		
		/*leftButton.setBounds(10, 151, 43, 23);
		fowardButton.setBounds(63, 117, 43, 23);
		backwardButton.setBounds(63, 151, 43, 23);
		rightButton.setBounds(116, 151, 43, 23);*/
		//btnConnect.setBounds(200, 25, 89, 23);
		
		taCommandOverride.setBounds(200, 150, 83, 23);
	}
}
