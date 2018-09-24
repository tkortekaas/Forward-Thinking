package classes;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Board extends JPanel implements ActionListener {

	// Initialize variables
	static int openFields = 49; // Fields remaining to remove
	static boolean selected = false; // Did the player already select a field?
	Field firstField; // First of the fields selected in the process of removing other fields
	JPanel board = new JPanel(new GridLayout(7, 5)); // Create 7 by 5 playing field
	static Field[] fields = new Field[49]; // All 49 fields of which not all will be visible in the end
	static ArrayList<Integer> options = new ArrayList<Integer>(); // The fields that the user can jump over

	public Board() {
		// Create all the fields
		for (int i = 0; i < 49; i++) {
			fields[i] = new Field(i);
			JButton button = fields[i].getButton();
			button.addActionListener(this);
			board.add(button);
		}

		// Make empty fields invisible
		int[] emptyFields = { 0, 1, 5, 6, 7, 13, 35, 41, 42, 43, 47, 48 };
		for (int i = 0; i < 12; i++) {
			fields[emptyFields[i]].getButton().setVisible(false);
		}

		// Add the board to the frame
		add(board);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton button = (JButton) e.getSource(); // Pressed button
		int id = (Integer) button.getClientProperty("id"); // The number of the field in the array
		Field field = fields[id]; // The selected field

		// Choose what to do with the selected field
		if (openFields <= 1) {
			gameOver();
		} else if (openFields >= 49) {
			firstRemoval(field);
		} else {
			if (!field.getActive())
				return;
			if (selected) {
				if (options.contains(id)) {
					removeField(field);
					return;
				}
				resetColors();
			}
			showOptions(field);
		}
	}

	private void firstRemoval(Field field) {
		// This is the first move and this field may be removed freely
		Main.recentlySaved = false;
		field.setActive(false);
		openFields -= 1;
		selected = false;
	}

	private void resetColors() {
		// Bring all colors back to the state they were in when they were not options or
		// selected
		for (int i : options) {
			fields[i].getButton().setBackground(Color.BLUE);
		}
		firstField.getButton().setBackground(Color.BLUE);
		selected = false;
	}

	private void removeField(Field secondField) {
		Main.recentlySaved = false;
		openFields -= 1;
		resetColors();

		// First field
		firstField.setActive(false);

		// Second field
		secondField.setActive(false);

		// New field
		int newField = -firstField.getId() + secondField.getId() * 2;
		fields[newField].setActive(true);
	}

	private void showOptions(Field field) {
		// Make the field of choice known and make it pink
		firstField = field;
		field.getButton().setBackground(Color.PINK);
		int fieldId = field.getId();

		// Variables needed for determining the options
		int mod = fieldId % 7; // For vertical options
		int div = fieldId / 7; // For horizontal options

		// Delete previous options
		options.clear();

		// Determine options
		if (mod >= 2) { // Ignore the two fields on the left
			int one = fieldId - 1, two = fieldId - 8, three = fieldId + 6; // Fields directly next to the selected field

			// Left horizontally
			if (fields[one].getActive() && !fields[fieldId - 2].getActive()) { // Check whether you can jump
				fields[one].getButton().setBackground(Color.YELLOW);
				options.add(one);
			}

			// Left diagonally up
			if (div >= 2) { // Ignore the upper two fields
				if (fields[two].getActive() && !fields[fieldId - 16].getActive()) { // Check whether you can jump
					fields[two].getButton().setBackground(Color.YELLOW);
					options.add(two);
				}
			}

			// Left diagonally down
			if (div < 5) { // Ignore the bottom two fields
				if (fields[three].getActive() && !fields[fieldId + 12].getActive()) { // Check whether you can jump
					fields[three].getButton().setBackground(Color.YELLOW);
					options.add(three);
				}
			}
		}
		if (mod <= 4) { // Ignore the two fields on the right
			int one = fieldId + 1, two = fieldId - 6, three = fieldId + 8; // Fields directly next to the selected field

			// Right horizontally
			if (fields[one].getActive() && !fields[fieldId + 2].getActive()) { // Check whether you can jump
				fields[one].getButton().setBackground(Color.YELLOW);
				options.add(one);
			}

			// Right diagonally up
			if (div >= 2) { // Ignore the upper two fields
				if (fields[two].getActive() && !fields[fieldId - 12].getActive()) { // Check whether you can jump
					fields[two].getButton().setBackground(Color.YELLOW);
					options.add(two);
				}
			}

			// Right diagonally down
			if (div < 5) { // Ignore the bottom two fields
				if (fields[three].getActive() && !fields[fieldId + 16].getActive()) { // Check whether you can jump
					fields[three].getButton().setBackground(Color.YELLOW);
					options.add(three);
				}
			}
		}

		int four = fieldId + 7, five = fieldId - 7;// Fields directly next to the selected field
		// Right horizontally
		if (div < 5) { // Ignore the bottom two fields
			if (fields[four].getActive() && !fields[fieldId + 14].getActive()) { // Check whether you can jump
				fields[four].getButton().setBackground(Color.YELLOW);
				options.add(four);
			}
		}
		// Left horizontally
		if (div >= 2) { // Ignore the upper two fields
			if (fields[five].getActive() && !fields[fieldId - 14].getActive()) { // Check whether you can jump
				fields[five].getButton().setBackground(Color.YELLOW);
				options.add(five);
			}
		}
		selected = true;
	}

	private void gameOver() {
		// Display Game-Over-Message
		JOptionPane.showMessageDialog(null, "Congratulations, you won!", "WINNER!", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void resetBoard() {
		// Reset board state
		openFields = 49;
		selected = false;
		options.clear();
		for (Field field : fields)
			field.setActive(true);
	}

	public static void resetBoard(int[] vars) {
		// Return board state to the one from the save file
		openFields = vars[49];
		selected = false;
		options.clear();
		for (int i = 0; i < 49; i++)
			fields[i].setActive(vars[i] == 1);
	}

}
