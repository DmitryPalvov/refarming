package ironManControl;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class StatusPanel extends JPanel {

	public JLabel taBattery1;
	public JLabel taBattery2;
	public JLabel taDirection;
	public JLabel taSpeed;
	public JLabel taThrottlePosition;
	public JLabel taGear;
	public JLabel taCommand;
	public JLabel taBrakeStatus;
	public JLabel taSignalStrength;
	//public javax.swing.
	
	public StatusPanel() {
		taBrakeStatus = new JLabel("Brake:\tX");
		//taBrakeStatus.setVBackground(Color.RED);
		//taBrakeStatus.setForeground(Color.RED);
		taBattery1 = new JLabel("0V");
		//taBattery1.setDragEnabled(true);
		taBattery2 = new JLabel("0V");
		
		taDirection = new JLabel("____|____");
		taDirection.setVisible(false);
		taCommand = new JLabel();
		taSpeed = new JLabel("0 km/h");
		taSpeed.setVisible(false);
		taThrottlePosition = new JLabel("Throttle:X");
		taGear = new JLabel("N");
		taSignalStrength = new JLabel("Signal:0%");
		
		setLayout(new GridLayout(6, 1));
		setBackground(Color.YELLOW);
		
		add(taBattery1);
		add(taBattery2);
		add(taDirection);
		add(taBrakeStatus);
		add(taCommand);
		this.setDirection(1, 1);
		add(taGear);
		add(taSpeed);
		add(taThrottlePosition);
		add(taSignalStrength);
	}
	
	public void setBrakeStatus(int status) {
		String text = "Brake:\tX";
		switch(status) {
		case 1:
			text = "Brake:\tUNPRESSED";
			break;
		case 2:
			text = "Brake:\tPRESSED";
			break;
			default:
				
		}
		taBrakeStatus.setText(text);
		
	}
	
	public void setBattery1Voltage(double voltage) {
		taBattery1.setText(String.valueOf((((double)Math.round(voltage*100))/100))+ "V");
	}
	
	public void setBattery2Voltage(double voltage) {
		taBattery2.setText(String.valueOf((((double)Math.round(voltage*100))/100))+ "V");
	}

	public void setDirection(int direction, int wantedDirection) {
		boolean isError = false;
		if (direction < 1) {
			direction = 1;		
			isError = true;
		}
		if (direction > 9) {
			direction = 9;
			isError = true;
		}
		if (wantedDirection < 1) {
			wantedDirection = 1;
			isError = true;
		}
		if (wantedDirection > 9) {
			wantedDirection = 9;
			isError = true;
		}
		char[] directionView = new char[] {'_', '_','_','_','|', '_','_','_','_'};
		char c = 'T';
		if (direction == wantedDirection) {
			
		} else if (direction < wantedDirection) {
			c = '>';
			directionView[wantedDirection-1] = 'V';
		} else {
			c = '<';
			directionView[wantedDirection-1] = 'V';
		}
		directionView[direction-1] = c;
		taDirection.setText(new String(directionView));
		if (isError) {
			taDirection.setBackground(Color.RED);
		} else {
			taDirection.setBackground(Color.GRAY);
		}
	}
	
	public void setGear(String gear) {
		switch (gear) {
			case "15" :
				taGear.setText("3");
				break;
			case "23" :
				taGear.setText("2");
				break;
			case "27" :
				taGear.setText("1");
				break;
			case "29" :
				taGear.setText("N");
				break;
			case "30" :
				taGear.setText("R");
				break;
			case "31" :
			default:
				taGear.setText("X");
				break;
		}
	}
	
	void setThrottlePosition(String tPos) {
		//int t = Integer.parseInt(tPos);
		taThrottlePosition.setText("Throttle: "+tPos);
	}
}

