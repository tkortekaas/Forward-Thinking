package classes;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;

public class Field {
	
	// Initialize variables
	private JButton button;
	private int id;
	private boolean active = true;

	public Field(int id) {
		// Create the button and set its properties
		this.id = id;
		button = new JButton();
		button.putClientProperty("id", id);
		button.setPreferredSize(new Dimension(75, 75));
		button.setBackground(Color.BLUE);
	}


	JButton getButton() {
		return this.button;
	}

	int getId() {
		return this.id;
	}

	boolean getActive() {
		return this.active;
	}

	void setActive(boolean active) {
		this.active = active;
		if (active) {
			// Make button appear active
			button.setBackground(Color.BLUE);
			return;
		}
		// Make button appear inactive
		button.setBackground(Color.RED);
	}
}
