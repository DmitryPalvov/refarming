package ironManControl;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class JControlViewButton extends JToggleButton {

	public JControlViewButton(String text) {
		super(text);
		JControlViewButton b = this;
		addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent e) {
				if(b.isSelected()) {
					b.setWarning();
				} else {
					b.setBorder(BorderFactory.createLineBorder(Color.white, 3));
				}
				
			}
		});
	}
	
	public void setOK() {
		if (this.isSelected()) {
			this.setBorder(BorderFactory.createLineBorder(Color.green, 3));	
		}		
	}
	
	public void setWarning() {
		if (this.isSelected()) {
			this.setBorder(BorderFactory.createLineBorder(Color.yellow, 3));	
		}		
	}
	
}
