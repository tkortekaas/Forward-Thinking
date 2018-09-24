package classes;

import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.*;

@SuppressWarnings("serial")
public class Main extends JPanel {
	// Initialize variables
	static JFrame frame = new JFrame("Forward Thinking"); // Frame of the application
	static JPanel board = new Board(); // Frame of the Game Board
	static int phase = 0; // Determines step in the tutorial
	static boolean recentlySaved = false; // For assuring that the user saves its game before quitting
	static int[] vars = new int[50]; // First 49 are the board state, last one is the amount of open fields
	static URL first = Main.class.getResource("images/first.gif"), second = Main.class.getResource("images/second.gif"),
			third = Main.class.getResource("images/third.gif"), fourth = Main.class.getResource("images/fourth.gif"); // All tutorial images
	static Icon icon; // Icon for tutorial
	static JButton button; // Button for tutorial

	public static void main(String[] args) {
		// Create ActionListener for static class
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (e.getActionCommand()) {
				case "NEWGAME":
					newGame();
					break;
				case "LOADGAME":
					fileOptions(); // For choosing what file to load
					break;
				case "SAVEGAME":
					saveGame();
					break;
				case "EXIT":
					exitGame();
					break;
				case "TUTORIAL":
					showTutorial();
					break;
				case "ABOUT":
					// Redirect player to my site for more information
					try {
						Desktop.getDesktop().browse(new URL("http://www.jelter.net").toURI());
					} catch (Exception exception) {
						exception.printStackTrace();
					}
					break;
				default:
					break;
				}
			}
		};

		// Menu Bar
		JMenuBar bar = new JMenuBar();

		// File
		JMenu file = new JMenu("File");
		JMenuItem newGame = new JMenuItem("New Game");
		JMenuItem loadGame = new JMenuItem("Load Game");
		JMenuItem saveGame = new JMenuItem("Save Game");
		JMenuItem exit = new JMenuItem("Exit");
		// Help
		JMenu help = new JMenu("Help");
		JMenuItem tutorial = new JMenuItem("Tutorial");
		JMenuItem about = new JMenuItem("About");

		// Add ActionListeners and set ActionCommands
		newGame.addActionListener(actionListener);
		newGame.setActionCommand("NEWGAME");
		loadGame.addActionListener(actionListener);
		loadGame.setActionCommand("LOADGAME");
		saveGame.addActionListener(actionListener);
		saveGame.setActionCommand("SAVEGAME");
		exit.addActionListener(actionListener);
		exit.setActionCommand("EXIT");
		tutorial.addActionListener(actionListener);
		tutorial.setActionCommand("TUTORIAL");
		about.addActionListener(actionListener);
		about.setActionCommand("ABOUT");

		// Add MenuItems to their JMenu
		file.add(newGame);
		file.add(loadGame);
		file.add(saveGame);
		file.addSeparator();
		file.add(exit);
		help.add(tutorial);
		help.add(about);

		// Add Jmenu's to JMenuBar
		bar.add(file);
		bar.add(help);

		// Add everything to the frame
		frame.setJMenuBar(bar);
		frame.add(board);

		// Set frame properties
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				exitGame();
			}
		});
		frame.pack();
		frame.setVisible(true);

	}

	public static void showTutorial() {
		// Create the base for the dialog
		JDialog dialog = new JDialog(frame, "Tutorial", true);

		// Create GIF in a button and label for the text
		JLabel label = new JLabel(
				"<html>The goal of this game is to have just one tile left on the board by jumping over other tiles.</html>");
		icon = new ImageIcon(fourth);
		button = new JButton(icon);
		
		// Create the loop of the tutorial
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (phase) {
				case 0:
					icon = new ImageIcon(first);
					label.setText(
							"<html>At the start of the game you may choose one tile that can be removed from the field.<br>This will then turn RED.<br>In this example we will remove tile in the middle of the board</html>");
					break;
				case 1:
					icon = new ImageIcon(second);
					label.setText(
							"<html>When you select a tile the tile you pressed will turn PINK.<br>The tiles that you can jump over will turn YELLOW.<br>Press the YELLOW tile to jump.</html>");
					break;
				case 2:
					icon = new ImageIcon(second);
					label.setText(
							"<html>After this another tile may 'jump' over another tile and on to the empty field.<br>This will lead to the tile that is jumped over to be removed.<br>Now you have 2 RED tiles!</html>");
					break;
				case 3:
					icon = new ImageIcon(third);
					label.setText("<html>You may jump over tiles HORIZONTALLY, VERTICALLY or DIAGONALLY.</html>");
					break;
				case 4:
					icon = new ImageIcon(fourth);
					label.setText(
							"<html>The game is over when:<br>All but one tile remains<br>OR<br>When you can't jump anymore</html>");
					break;
				case 5:
					// Return to the board
					dialog.dispose();
					phase = 0;
					break;
				}
				System.out.println(phase);
				new JButton();
				button.setIcon(icon);
				phase++;
			}
		});

		// Create FlowLayout for holding the button and label
		JPanel tutPanel = new JPanel();
		tutPanel.setLayout(new FlowLayout());
		tutPanel.add(button);
		tutPanel.add(label);

		// Put the panel in the dialog and set the dialog's properties
		dialog.add(tutPanel);
		dialog.setSize(545, 400);
		dialog.setLocation(frame.getWidth() / 2 - dialog.getWidth() / 2, frame.getHeight() / 2 + frame.getHeight() / 2);
		dialog.setLocationRelativeTo(frame);
		dialog.setUndecorated(true);
		dialog.setVisible(true);
	}

	static void newGame() {
		// Reset the board if the user doesn't want to save its game first
		if (Board.openFields < 49 && !recentlySaved) { // Determine if it is worth saving the game
			int dialogButton = JOptionPane.YES_NO_OPTION;
			int dialogResult = JOptionPane.showConfirmDialog(null,
					"Do you start a new game without saving your current one?", "Warning", dialogButton);
			if (dialogResult == JOptionPane.YES_OPTION)
				Board.resetBoard();
			return;
		}
		Board.resetBoard();
	}

	static void saveGame() {
		try {
			// Check if save folder exists : if not create one
			File dir = new File("savefiles");
			if (!dir.exists())
				dir.mkdir();

			// Ask for the file name and check for files with the same name
			boolean validName = false;
			String fileName = "save";
			while (!validName) {
				fileName = JOptionPane.showInputDialog(frame, "Enter a name for your save file (No special characters)",
						"Save Game", JOptionPane.QUESTION_MESSAGE);
				File file = new File("savefiles/" + fileName + ".ser");
				if (file.exists()) {
					int selected = JOptionPane.showConfirmDialog(frame, "Do you want to override your save file?",
							"Existing file found!", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (selected == JOptionPane.YES_OPTION) {
						validName = true; // Override previous save
					} else if (selected == JOptionPane.CANCEL_OPTION) {
						return; // Don't override previous save
					}
				} else {
					if (fileName.matches("[0-9a-zA-Z]+") && fileName.length() <= 100) // Make sure the file has valid
																						// characters/length
						validName = true; // No file with the same name found
				}

			}
			// Save board state in to array
			for (int i = 0; i < 49; i++) {
				vars[i] = Board.fields[i].getActive() ? 1 : 0;
			}
			vars[49] = Board.openFields;

			// Save board state in to file
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("savefiles/" + fileName + ".ser"));
			out.writeObject(vars);
			out.flush();
			out.close();

			recentlySaved = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static void loadGame(String dir) {
		try {
			// Get all variables from the save file
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(dir));
			vars = (int[]) in.readObject();
			in.close();

			// Reset board state to the one from the save file
			Board.resetBoard(vars);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static void exitGame() {
		// If the user doesn't want to save or if it is not worth saving, quit the game
		if (Board.openFields < 49 && !recentlySaved) {
			if (JOptionPane.showConfirmDialog(null, "Do you want to quit without saving?", "Warning",
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				System.exit(0);
			return;
		}
		System.exit(0);
	}

	static void fileOptions() {
		// Check if there are save files
		File folder = new File("savefiles");
		if (!folder.exists())
			return;
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles.length <= 0)
			return;

		// Add all existing files to an ArrayList
		ArrayList<String> files = new ArrayList<String>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains(".ser")) {
				files.add(listOfFiles[i].getName().replaceAll(".ser$", ""));
			}
		}

		// Ask the player what game he/she wants to load
		int option = JOptionPane.showOptionDialog(frame, "What game do you want to load?", "Save File", 0,
				JOptionPane.QUESTION_MESSAGE, null, files.toArray(), files.get(0));

		// And then load this file if the player chose a file
		if (option != -1)
			loadGame("savefiles/" + files.get(option) + ".ser");
	}

}
