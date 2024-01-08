import java.awt.*;
import java.util.Random;
import java.util.Scanner;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.JDialog;

/**
 *	Wordle.java
 *
 *	Provide a description here.
 *
 *	@author	Scott DeRuiter and David Greenstein and Prathik Kumar
 *	@version	1.0
 *	@since		October 12, 2023
 */
public class Wordle
{
	/**	This is a complete list of fields for the game */

	/**	A String to store the word that the player is trying to find. */
	private String word;

	/**	An array of String to store the guesses that have been made. */
	private String [] wordGuess;

	/**	A String to store the letters in the current guess.  Can have from 0 to 5 chars*/
	private String letters;

	/**	File that contains 5-letter words to find. */
	private final String WORDS5 = "words5.txt";

	/**	File that contains 5-letter words allowed for user guesses. (bigger file) */
	private final String WORDS5_ALLOWED = "words5allowed.txt";

	/**	A variety of boolean variables to turn things on and off.  These include:
	 *	show				-	when true, will print the current word to the terminal
	 *	readyForKeyInput    -	when true, will accept keyboard input, when false,
	 *							will not accept keyboard input.
	 *	readyForMouseInput  -	when true, will accept mouse input, when false,
	 *							will not accept mouse input.
	 *	activeGame          -	when false, will only accept action on the RESET button.
	 */
	private boolean show, readyForKeyInput, readyForMouseInput, activeGame;

	/**  An array to determine how to color the keyboard at the bottom of the gameboard.
	 *   0 for not checked yet, 1 for no match, 2 for partial, 3 for exact
	 */
	private int [] keyBoardColors;

	/**
	 *	Creates a Wordle object.  A constructor.  Initializes all of the variables by
	 *	calling the method initAll.
	 *	@param testWord		if this String is found in words5allowed.txt, it will
	 *						be used to set word.
	 *	This method is complete.
	 */
	public Wordle(String showIt, String testWord)
	{
		show = false;
		if (showIt.equalsIgnoreCase("show"))
			show = true;

		initAll(testWord);
	}

	/**
	 *	Initializes all fields.  Calls openFileAndChooseWord to choose the word.
	 *	Sets all of the keyboard colors to light gray to start.
	 *	@param testWord		if this String is found in words5allowed.txt, it will
	 *						be used to set word.
	 *	This method is complete.
	 */
	public void initAll(String testWord)
	{
		wordGuess = new String[6];
		for(int i = 0; i < wordGuess.length; i++)
		{
			wordGuess[i] = new String("");
		}
		letters = "";
		readyForKeyInput = activeGame = true;
		readyForMouseInput = false;
		keyBoardColors = new int[29];
		word = openFileAndChooseWord(WORDS5, testWord);
	}

	/**
	 *	The main method, to run the program.  The constructor is called, so that
	 *	all of the fields are initialized.  The canvas is set up, and the GUI
	 *	(the game of Wordle) runs.
	 */
	public static void main(String[] args)
	{
		String testWord = new String("");
		String showIt = new String("");

		// Determines if args[0] and args[1] are set
		// args[0] is "show" which means to show the word chosen
		// args[1] is a word which is used as the chosen word

		if (args.length > 0) {
			showIt = args[0];
		}

		if (args.length > 1) {
			testWord = args[1].toUpperCase();
		}
		Wordle run = new Wordle(showIt, testWord);
		run.setUpCanvas();
		run.playGame();
	}

	/**
	 *	Sets up the canvas.  Enables double buffering so that the gameboard is drawn
	 *	offscreen first, then drawn to the gameboard when everything is ready (with
	 *	the show method).
	 *	This method is complete.
	 */
	public void setUpCanvas ( )
	{
		StdDraw.setCanvasSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
		StdDraw.setXscale(0, Constants.SCREEN_WIDTH);
		StdDraw.setYscale(0, Constants.SCREEN_HEIGHT);

		StdDraw.enableDoubleBuffering();
	}

	/**
	 *	Runs the game.  An endless loop is created, constantly cycling and looking
	 *	for user input.
	 *	This method is complete.
	 */
	public void playGame ( )
	{
		boolean keepGoing = true;
		while(keepGoing)
		{
			if(activeGame)
			{
				drawPanel();
			}
			update();
		}
	}

	/**
	 *	If the testWord is valid, it is used as the "goal word".  If it is not, then
	 *	the text file is opened, and a word is chosen at random from the list to be
	 *	the "goal word".  If the field variable show is true, it will print the
	 *	chosen word to the terminal window. Be sure to close file when you are done.
	 *	@param inFileName		this file is to be opened, and a random word is to be
	 *							chosen from it.
	 *	@param testWord			if this String is found in words5allowed.txt, it
	 *							will be used to set word.
	 *	@return					the word chosen as the "goal word".
	 */
	public String openFileAndChooseWord(String inFileName, String testWord) {
		if (inAllowedWordFile(testWord)) {
			System.out.println(testWord);
			return testWord.toUpperCase();
		} else {
			String[] allWords = new String[getWordCount(inFileName)];
			Scanner fileChooser = FileUtils.openToRead(inFileName);

			int wordCount = 0;
			while (fileChooser.hasNext()) {
				allWords[wordCount++] = fileChooser.next();
			}
			fileChooser.close();

			if (wordCount == 0) {
				return testWord.toUpperCase();
			} else {
				Random random = new Random();
				int randomIndex = random.nextInt(wordCount);
				String result = allWords[randomIndex].toUpperCase();
				if (show) {
					System.out.println(result);
				}
				return result;
			}
		}
	}
	/**
	 * Counts the number of words in the specified file.
	 * @param inFileName				The name of the input file to read.
	 * @return							The total count of words in the file.
	 */
	public int getWordCount(String inFileName) {
		int count = 0;
		Scanner fileSelected = FileUtils.openToRead(inFileName);
		while (fileSelected.hasNext()) {
			fileSelected.next();
			count++;
		}
		fileSelected.close();
		return count;
	}

	/**
	 *	Checks to see if the word in the parameter list is found in the text file
	 *	words5allowed.txt
	 *	Returns true if the word is in the file, false otherwise.
	 *	@param possibleWord       the word to looked for in words5allowed.txt
	 *	@return                   true if the word is in the text file, false otherwise
	 */
	public boolean inAllowedWordFile(String possibleWord)
	{
		Scanner fileAllowed = FileUtils.openToRead(WORDS5_ALLOWED);

		boolean foundWord = false;
		while(fileAllowed.hasNext()) {
			String eachWord = fileAllowed.next();
			if (eachWord.toUpperCase().equals(possibleWord)) {
				foundWord = true;
			}
		}
		fileAllowed.close();
		return foundWord;
	}

	/**
	 *	Processes the guess made by the user.  This method will only be called if
	 *	the field variable letters has length 5.  The guess in letters will need
	 *	to be checked against the words in words5allowed.txt. The method
	 *	inAllowedWordFile will be called for this task.  If the guess in letters
	 *	does not exist in the text file, a message is displayed to the user in the
	 *	form of a JOptionPane with JDialog.
	 */
	public void processGuess() {
		letters = letters.toUpperCase();
		if (inAllowedWordFile(letters)) {
			// If the guess is in words5allowed.txt, put it into the guess list.
			int guessNumber = findEmptyGuessSlot();
			wordGuess[guessNumber] = letters;
			letters = "";
		} else {
			// If the guess is not in words5allowed.txt, display an error dialog.
			showInvalidInputDialog(letters + " is not in the word list.");
		}
	}

	/**
	 * Finds the first empty guess slot.
	 *
	 * @return The index of the empty slot, or -1 if none are found.
	 */
	private int findEmptyGuessSlot() {
		for (int i = 0; i < wordGuess.length; i++) {
			if (wordGuess[i].isEmpty()) {
				return i;
			}
		}
		return -1; // No empty slots found (you may want to handle this case)
	}

	/**
	 * Displays an error dialog with the given message.
	 *
	 * @param message The message to display in the dialog.
	 */
	private void showInvalidInputDialog(String message) {
		JOptionPane.showMessageDialog(null, message, "INVALID INPUT", JOptionPane.ERROR_MESSAGE);
	}


	/**
	 *	Draws the entire game panel.  This includes the guessed words, the current
	 *	word being guessed, and all of the letters in the "keyboard" at the bottom
	 *	of the gameboard.  The correct colors will need to be chosen for every letter.
	 *  Completed
	 */
	public void drawPanel() {
		StdDraw.clear(StdDraw.WHITE);

		// Determine color of guessed letters and draw backgrounds
		// 0 for not checked yet, 1 for no match, 2 for partial, 3 for exact
		// draw guessed letter backgrounds
		int wordIndex;

		for (int i = 0; i < 6; i++) {
			//This array is used to keep track of the status of each letter in the current word guess.
			// The values 0, 1, 2, and 3 represent different states: 0 for not checked yet,
			// 1 for no match, 2 for partial match, and 3 for an exact match.

			int[] letterStatus = new int[5];
			String currentWordGuess = wordGuess[i];
			if (currentWordGuess.length() == 5) {
				int[] exactMatch = {0,0,0,0,0}; //Default 0 and when match is there 1
				int[] partialMatch = {0,0,0,0,0}; //Default 0 and when match is there 1
				// Check for exact matches
				for (wordIndex = 0; wordIndex < 5; wordIndex++) {
					char guessedLetter = currentWordGuess.charAt(wordIndex);
					char actualLetter = word.charAt(wordIndex);
					if (guessedLetter == actualLetter) {
						letterStatus[wordIndex] = 3;
						exactMatch[wordIndex] = 1;
						partialMatch[wordIndex] = 1;
					}
				}
				processWordMatch(i, letterStatus, exactMatch,  partialMatch, currentWordGuess);
			}

			//Completed part for displaying word in frame
			drawWordFrames(currentWordGuess, letterStatus, i);
		}

		// draw Wordle board
		Font font = new Font("Arial", 1, 12);
		StdDraw.setFont(font);
		StdDraw.picture(350.0, 720.0, "wordle.png");
		StdDraw.setPenColor(StdDraw.LIGHT_GRAY);

		// draw keyboard with appropriate colors in the bottom frame
		drawKeyBoardFrame();
		// draw guesses
		drawAllLettersGuessed();
		StdDraw.show();
		StdDraw.pause(20);

		//Check Won or Lost
		checkIfWonOrLost();
	}

	/**
	 * Process the matching of letters in the current word guess against the target word.
	 *
	 * @param index            The index of the current word guess.
	 * @param letterStatus     letterStatus tracker for match
	 * @param exactMatch       int value passed and set for exact match (0 & 1)
	 * @param partialMatch     int value passed and set for partial match (0 & 1)
	 * @param currentWordGuess The current word guess to match against the target word.
	 */
	public void processWordMatch(int index, int[] letterStatus, int[] exactMatch, int[] partialMatch,
								 String currentWordGuess) {
		int wordIndex = 0;
		boolean breakOut = false;

		while (true && !breakOut) {
			if (wordIndex >= 5) {
				// Check if there is no match and assign status
				noMatchCheck(letterStatus);
				int letterIndex = 0;

				while (true && !breakOut) {
					if (letterIndex >= 5) {
						breakOut = true;
					}

					if (!breakOut) {
						updateKeyBoardColors(letterIndex, letterStatus, index);
						letterIndex++;
					}
				}
			}

			if (!breakOut) {
				int letterIndex = currentWordGuess.charAt(wordIndex);
				matchCheck(exactMatch, partialMatch, wordIndex, letterIndex, letterStatus);
			}
			wordIndex++;
		}
	}
	/**
	 * Update KeyBoard Colors to be used for display later.
	 * @param letterIndex				access the individual letters at that position
	 * @param letterStatus				letterStatus tracker for match
	 * @param currIndex					current index word tracker
	 */
	private void updateKeyBoardColors(int letterIndex, int []letterStatus, int currIndex) {
		char charFound = wordGuess[currIndex].charAt(letterIndex);
		for(int keyIndex = 0; keyIndex < Constants.KEYBOARD.length; keyIndex++) {
			if (keyBoardColors[keyIndex] < letterStatus[letterIndex] && Constants.KEYBOARD[keyIndex].charAt(0) == charFound) {
				keyBoardColors[keyIndex] = letterStatus[letterIndex];
			}
		}
	}

	/**
	 * Checks for matches for letters and assigns status accordingly.
	 * @param exactMatch					int value passed and set for exact match (0 & 1)
	 * @param partialMatch					int value passed and set for partial match (0 & 1)
	 * @param wordIndex						check the letters in the guessed word
	 * @param letterIndex					access the individual letters at that position
	 * @param letterStatus					letterStatus tracker for match
	 */
	private void matchCheck(int[] exactMatch, int partialMatch[],
							int wordIndex, int letterIndex, int letterStatus[]) {
		for (int a = 0; a < 5; ++a) {
			int currentCharIndex = this.word.charAt(a);
			if (exactMatch[wordIndex] !=1 && partialMatch[a]!=1
					&& letterIndex == currentCharIndex) {
				letterStatus[wordIndex] = 2;
				exactMatch[wordIndex] = 1;
				partialMatch[a] = 1;
			}
		}
	}
	/**
	 * Update status when there is no match.
	 * @param letterStatus		letterStatus tracker for match
	 */
	private void noMatchCheck(int[] letterStatus) {
		for(int wordIndex = 0; wordIndex < 5; wordIndex++) {
			if (letterStatus[wordIndex] == 0) {
				letterStatus[wordIndex] = 1;
			}
		}

	}

	/**
	 * Draw frames for word letters based on their status.
	 *
	 * @param currentWordGuess The current word guess.
	 * @param letterStatus     An array representing the status of each letter (0 for not checked yet, 1 for no match, 2 for partial match, and 3 for exact match).
	 * @param rowIndex         The row index where the frames should be drawn.
	 */
	private void drawWordFrames(String currentWordGuess, int[] letterStatus, int rowIndex) {
		for (int displayIndex = 0; displayIndex < 5; displayIndex++) {
			String imageFileName = "letterFrame.png";
			switch (letterStatus[displayIndex]) {
				case 1:
					imageFileName = "letterFrameDarkGray.png";
					break;
				case 2:
					imageFileName = "letterFrameYellow.png";
					break;
				case 3:
					imageFileName = "letterFrameGreen.png";
					break;
			}
			StdDraw.picture(209 + displayIndex * 68, 650 - rowIndex * 68, imageFileName);
		}
	}

	/**
	 *	This method is called by drawPanel, and draws all of the letters in the
	 *	guesses made by the user.
	 *	This method is complete.
	 */
	public void drawAllLettersGuessed ( )
	{
		// Draw guessed letters
		Font font = new Font("Arial", Font.BOLD, 34);
		StdDraw.setFont(font);
		int guessNumber = 0;
		for(int i = 0; i < wordGuess.length; i++)
		{
			if(wordGuess[i].length() > 0)
			{
				for(int j = 0; j < wordGuess[i].length(); j++)
				{
					StdDraw.text(209 + j * 68, 644 - i * 68, "" + wordGuess[i].charAt(j));
				}
			}
			if(wordGuess[i].length() == 5)
			{
				guessNumber = i + 1;
			}
		}
		for(int i = 0; i < letters.length(); i++)
		{
			StdDraw.text(209 + i * 68, 644 - guessNumber * 68, "" + letters.substring(i, i+1));
		}
	}

	/**
	 * Draws the keyboard with appropriate colors based on the given key states.
	 *
	 */
	public void drawKeyBoardFrame() {
		int keyboardIndex = 0;
		int[][] keyPlacement = Constants.KEYPLACEMENT;

		for (int placementIndex = 0; placementIndex < keyPlacement.length; placementIndex++) {
			int[] placement = keyPlacement[placementIndex];

			if (keyboardIndex != 19 && keyboardIndex != 27 && keyboardIndex != 28) {
				if (keyBoardColors[keyboardIndex] == 0) {
					StdDraw.picture((double)placement[0], (double)placement[1], "keyBackground.png");
				} else if (keyBoardColors[keyboardIndex] == 1) {
					StdDraw.picture((double)placement[0], (double)placement[1], "keyBackgroundDarkGray.png");
				} else if (keyBoardColors[keyboardIndex] == 2) {
					StdDraw.picture((double)placement[0], (double)placement[1], "keyBackgroundYellow.png");
				} else if (keyBoardColors[keyboardIndex] == 3) {
					StdDraw.picture((double)placement[0], (double)placement[1], "keyBackgroundGreen.png");
				}
			} else {
				StdDraw.picture((double)placement[0], (double)placement[1], "keyBackgroundBig.png");
			}

			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.text((double)placement[0], (double)placement[1], Constants.KEYBOARD[keyboardIndex]);
			++keyboardIndex;
		}
	}

	/**
	 *	Checks to see if the game has been won or lost.  The game is won if the user
	 *	enters the correct word with a guess.  The game is lost when the user does
	 *	not enter the correct word with the last (6th) guess.  An appropriate message
	 *	is displayed to the user in the form of a JOptionPane with JDialog for a win or a loss.
	 */
	public void checkIfWonOrLost ( )
	{
		String lastWord = "";
		for(int i = 0; i < wordGuess.length; i++)
		{
			if(wordGuess[i].length() == 5)
			{
				lastWord = wordGuess[i];
			}
		}
		// declare the winner by matching the word
		JOptionPane pane;
		JDialog d;
		if (lastWord.equals(word) || wordGuess[wordGuess.length - 1].length() == 5) {
			activeGame = false;

			if (lastWord.equals(word)) {
				pane = new JOptionPane(lastWord + " is the word! Press RESET to begin again");
			} else {
				pane = new JOptionPane(word + " was the word. Press RESET to begin again");
			}

			d = pane.createDialog((Component) null, lastWord.equals(word) ? "CONGRATULATIONS!" : "Sorry!");
			d.setLocation(365, 250);
			d.setVisible(true);
		}
	}

	/**
	 *	This method is constantly looking for keyboard or mouse input from the user,
	 *	and reacting to this input.
	 *	This method is complete.
	 */
	public void update ( )
	{
		if(activeGame)
		{
			respondToKeys();
		}
		respondToMouse();
	}

	/**
	 *	Responds to input from the keyboard.  Will call the method processGuess
	 *	when the user has entered a word to guess.
	 *	This method is complete.
	 */
	public void respondToKeys ( )
	{
		if(readyForKeyInput && StdDraw.hasNextKeyTyped() &&
				StdDraw.isKeyPressed(KeyEvent.VK_BACK_SPACE) && letters.length() > 0)
		{
			letters = letters.substring(0, letters.length() - 1);
			readyForKeyInput = false;
		}
		else if(readyForKeyInput && StdDraw.hasNextKeyTyped() &&
				StdDraw.isKeyPressed(KeyEvent.VK_ENTER) && letters.length() == 5)
		{
			processGuess();
			readyForKeyInput = false;
		}
		else if(readyForKeyInput && StdDraw.hasNextKeyTyped() && letters.length() < 5)
		{
			String letter = "" + StdDraw.nextKeyTyped();
			letter = letter.toUpperCase();
			if(letter.charAt(0) >= 'A' && letter.charAt(0) <= 'Z')
			{
				letters += letter;
			}
			readyForKeyInput = false;
		}
		else
		{
			while(StdDraw.hasNextKeyTyped())
			{
				StdDraw.nextKeyTyped();
			}
			if(!StdDraw.hasNextKeyTyped())
			{
				readyForKeyInput = true;
			}
		}
	}

	/**
	 *	Responds to input from the mouse, simulating the typing of keys on the
	 *	"keyboard" at the bottom of the game panel. Will call the method processGuess
	 *	when the user has entered a word to guess.
	 *	This method is complete.
	 */
	public void respondToMouse ( )
	{
		if(readyForMouseInput && StdDraw.isMousePressed())
		{
			for(int i = 0; i < Constants.KEYPLACEMENT.length; i++)
			{
				if(StdDraw.mouseX() > Constants.KEYPLACEMENT[i][0] - 22 &&
						StdDraw.mouseX() < Constants.KEYPLACEMENT[i][0] + 22 &&
						StdDraw.mouseY() > Constants.KEYPLACEMENT[i][1] - 29 &&
						StdDraw.mouseY() < Constants.KEYPLACEMENT[i][1] + 29)
				{
					if(i == 28)
					{
						initAll("");
						activeGame = true;
					}
					else if(activeGame && i == 27 && letters.length() > 0)
					{
						letters = letters.substring(0, letters.length() - 1);
					}
					else if(activeGame && i == 19 && letters.length() == 5)
					{
						processGuess();
					}
					else if(activeGame && i != 19 && i != 27 && i != 28 && letters.length() < 5)
					{
						String letter = Constants.KEYBOARD[i].toUpperCase();
						letters += letter;
					}
				}
			}
			readyForMouseInput = false;
		}
		else if(!StdDraw.isMousePressed())
		{
			readyForMouseInput = true;
		}
	}
}
