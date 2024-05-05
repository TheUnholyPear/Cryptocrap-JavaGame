import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.*;

import static java.lang.Integer.parseInt;

// game driver should call cryptogram, and it generates a new one from the phrases in the file
public class GameDriver {
    static Player player;
    static Cryptogram cryptogram;
    static ArrayList<String> phrases;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    //key: crypto; value: guess
    static HashMap<Object, Character> playerMapping;
    public GameDriver(Player person){
        player = person;
    }
    public GameDriver(){
    }

    //This is where a player is created or loaded and what cryptogram should be created or loaded:
    public static void main(String[] args){
        onStart();

        player = null;

        Scanner input = new Scanner(System.in);

        //The player can either create a new player profile or load an existing one
        do {
            System.out.print("'new <NAME>': Create a new player profile\n'load <NAME>': Load an existing player profile\n'leaderboard': Show current leaderboard\n>>> ");
            String[] tokens = input.nextLine().split(" ");
            switch (tokens[0]){
                case "new" -> {
                    if (tokens.length == 1){
                        //Error occurs if player enters 'new' with no name
                        System.out.println("Invalid input, please enter a name");
                    } else {
                        createPlayer(tokens[1]);
                    }
                }
                case "load" -> loadPlayer(tokens[1]);
                case "leaderboard" -> displayLeaderboard();
                default -> System.out.println("Invalid input '" + tokens[0] + "'. Please try again.");
            }
        } while (player == null);

        System.out.println("Welcome, " + player.getUsername() + "!");

        //Input for type selection
        int userInput = 0;

        do {
            //Allows the user to choose what type to play
            System.out.print("Enter 1 (Letter) or 2 (Number) or 3 (Load)\n>>> ");
            try {
                userInput = Integer.parseInt(input.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Error: input is not an integer value");
            }

            if (userInput == 3){
                loadCryptogram();
                if (cryptogram == null){
                    userInput = 0;
                }
            } else {
                if (userInput != 1 && userInput != 2){
                    System.out.println("Invalid input. Please try again.");
                }
            }
        } while (userInput != 1 && userInput != 2 && userInput != 3);

        generateCryptogram(userInput);
        player.incrementCryptogramsPlayed();

        UserInput();
        input.close();
    }

    //This method is the main way the player interacts with the program:
    public static void UserInput(){
        //This is for testing:
        //System.out.println("\nTarget phrase (here for testing): \n" + cryptogram.phrase);

        //This shows the player the encrypted cryptogram:
        System.out.println("Here is your cryptogram!!");
        displayEncryptedPhrase();
        System.out.println();

        //This is for testing:
        //displayCryptogramMapping();

        //This shows the player the instructions he can input and what happens for each instruction:
        Scanner commandInput = new Scanner(System.in);
        String command;

        System.out.print(
                """
                         - Enter 'guess' followed by the letter/number in the cryptogram and its corresponding guess (separated by a space)
                         - Enter 'remove' followed by the letter/number in the cryptogram to remove a guess
                         - Enter 'guesses' to view guesses
                         - Enter 'hint' to reveal a mapping (if you are stuck!)
                         - Enter 'freq' to reveal object frequencies
                         - Enter 'solve' to show the answer (if you are stuck!)
                         - Enter 'stats' to review your statistics
                         - Enter 'leaderboard' to view the top 10 players
                         - Enter 'save' to save your cryptogram
                         - Enter 'exit' to save and exit the game
                        """);

        do {
            System.out.print(">>> ");
            command = commandInput.nextLine();
            String[] parts = command.split(" ");

            switch (parts[0]) {
                case "remove" -> {
                    if (parts.length < 2) {
                        System.out.println("Error: You have not supplied a character to remove");
                    } else {
                        String crypto = parts[1];
                        removeGuess(crypto);
                    }
                }
                case "guess" -> {
                    if (parts.length < 3) {
                        System.out.println("Error: Not enough arguments. Give the character in the cryptogram and its corresponding guess (separated by a space)");
                    } else {
                        String crypto = parts[1];
                        char guess = parts[2].charAt(0);
                        enterGuess(crypto, guess);
                    }
                }
                case "guesses" -> displayGuesses();
                case "stats" -> displayStats();
                case "save" -> saveGame();
                case "exit" -> exitGame();
                case "freq" -> displayEncryptedPhraseFrequency();
                case "solve" -> solveGame();
                case "hint" -> getHint();
                case "leaderboard" -> displayLeaderboard();
                default -> System.out.println("Invalid input. Please try again.");
            }
        } while (!command.equals("exit"));
    }

    //This method Displays the Frequency of each object in a cryptogram as a percentage to 2 decimal places@
    private static void displayEncryptedPhraseFrequency() {
        if(cryptogram.getFrequencies() == null){
            System.out.println("Error: Your Cryptogram is empty");
        } else {
            System.out.println("Here are the frequencies:\n");
            for (Object key : cryptogram.getFrequencies().keySet()) {
                System.out.println(key + " = " + df.format(cryptogram.getFrequencies().get(key)) + "%");
            }
            System.out.println("\n\nHere are the most common letter frequencies in the English language:\n");
            System.out.println(
                    """
                            e = 12.02%
                            t = 9.10%
                            a = 8.12%
                            o = 7.68%
                            i = 7.31%
                            n = 6.95%
                            s = 6.28%
                            r = 6.02%
                            h = 5.92%
                            """);
        }
    }

    // This method will give the player a correct answer, can only be done a maximum of 3 times per cryptogram.
    // Return true if no errors, and false otherwise
    public static boolean getHint() {
        // return false if no cryptogram is being played
        if (cryptogram == null) {
            System.out.println("Error: No cryptogram is being played.");
            return false;
        }

        // return false if no hints left
        if (cryptogram.getRemainingHints() == 0) {
            System.out.println("Sorry you don't have any remaining guesses");
            return false;
        }

        // Loop through the cryptogram mapping; if there is a discrepancy between the mapping and the player mapping,
        // the player receives a hint for that letter/number.
        // we need two variations of this loop to account for LetterCryptogram and NumberCryptogram

        if (cryptogram.getType().equals("Letter")) {
            for (Map.Entry<Character, Character> entry : ((LetterCryptogram) cryptogram).getEncryptedHashMap().entrySet()) {

                if (entry.getValue() != playerMapping.get(entry.getKey())) {
                    if (playerMapping.get(entry.getKey()) != null){
                        System.out.println("Removing incorrect mapping: " + entry.getKey() + " => " + playerMapping.get(entry.getKey()));
                        playerMapping.remove(entry.getKey());
                    }
                    System.out.println("Hint:\n" + entry.getKey() + " => " + entry.getValue()+"\nYou have " + (cryptogram.getRemainingHints() -1) + " left");
                    playerMapping.put(entry.getKey(), entry.getValue());

                    checkComplete();
                    cryptogram.lowerRemainingHints();
                    return true;
                }
            }
        }
        else if (cryptogram.getType().equals("Number")) {
            for (Map.Entry<Integer, Character> entry : ((NumberCryptogram) cryptogram).getEncryptedHashMap().entrySet()) {
                if (entry.getValue() != playerMapping.get(entry.getKey())) {
                    if (playerMapping.get(entry.getKey()) != null){
                        System.out.println("Removing incorrect mapping: " + entry.getKey() + " => " + playerMapping.get(entry.getKey()));
                        playerMapping.remove(entry.getKey());
                    }
                    System.out.println("Hint:\n" + entry.getKey() + " => " + entry.getValue()+"\nYou have " + (cryptogram.getRemainingHints() -1) + " left");
                    playerMapping.put(entry.getKey(), entry.getValue());

                    checkComplete();
                    cryptogram.lowerRemainingHints();
                    return true;
                }
            }
        }

        // if this line is reached the cryptogram has already been solved
        System.out.println("Error: The cryptogram has already been solved!");
        return false;
    }

    //This method will finish the game for player and exit:
    private static void solveGame() {
        System.out.println();
        System.out.println("Here is the solution to the cryptogram...");
        System.out.println();
        System.out.println(cryptogram.phrase +"\n");
        exitGame();
    }

    //This is used to save and exit the game:
    public static void exitGame() {
        Players.savePlayers();
        System.out.println("Saved Player Data... \nNow exiting...");
        System.exit(0);
    }

    //This method loads in phrases from a "phrases.txt"
    public static void onStart() {
        playerMapping = new HashMap<>();
        phrases = new ArrayList<>();

        Players.loadPlayers();


        // Read phrases from file
        try {
            Scanner scanner = new Scanner(new File("phrases.txt"));

            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                phrases.add(data);
            }
        } catch (FileNotFoundException e) {

            System.out.println("Error: File not found");
            System.exit(1);
        }
    }

    //This method loads a cryptogram if the player has one and will output an error if he doesn't:
    public static void loadCryptogram() {
        if (player == null) {
            System.out.println("Error: There is no account to load from");
        } else if ((player.getCryptogram() == null)){
            System.out.println("Error: There no Cryptogram to load");
        } else {
            GameDriver.playerMapping = player.getPlayerMapping();
            GameDriver.cryptogram = player.getCryptogram();
            System.out.println("Cryptogram loaded");
        }
    }

    //This method saves a cryptogram for a player and asks if the player would want save override if there is already a save.
    //If there is no player it will return with an error message:
    public static void saveCryptogram(HashMap<Object, Character> playerMapping, Cryptogram cryptogram) {
        if (player == null) {
            System.out.println("Error: You are not able to save without an account");
        } else {
            if (player.getCryptogram() != null) {
                String answer;
                do {
                    System.out.println(
                            "You already have a cryptogram game saved. Do you want to overwrite your saved game? (Y/N)");

                    Scanner scanner = new Scanner(System.in);
                    answer = scanner.nextLine();

                    if (answer.equalsIgnoreCase("N")) {
                        return;
                    }
                } while (!answer.equalsIgnoreCase("Y") && !answer.equalsIgnoreCase("N"));
            }

            player.saveCryptogram(playerMapping, cryptogram);
            System.out.println("Game Saved");
        }
    }

    //This is the method called to save the cryptogram
    public static void saveGame() {
        saveCryptogram(playerMapping,cryptogram);
        Players.savePlayers();
    }

    //This method loads a player from Players and if it cannot find said player it will output an error message and create a new one:
    public static void loadPlayer(String name) {
        Player target = Players.findPlayer(name);
        if (target != null) {
            player = target;
        } else {
            System.out.print("Error: Player does not exist. Creating new player.\n");
            createPlayer(name);
        }
    }

    //This method is the method called to create a new player:
    public static void createPlayer(String name) {
        player = new Player(name);

        Players.addPlayer(player);
        Players.savePlayers();
    }

    //This takes in the users guess and 'sends' it to cryptogram to be guessed
    public static void enterGuess(String crypto, char guess) {
        //return if key doesn't exist in cryptogram
        if (!cryptogram.containsKey(crypto)) {
            System.out.println("The cryptogram value entered is not used in the cryptogram!");
            return;
        }

        // return if guess character already used
        for (Map.Entry<Object, Character> entry :playerMapping.entrySet()){
            if (entry.getValue() == guess) {
                System.out.println("You have already mapped " + entry.getKey() + " to " + guess);
                return;
            }
        }

        //converts crypto string to the required type used in the cryptogram hashmap depending on cryptogram type
        if (cryptogram.getType().equals("Letter")) {
            if (!checkIfOverwrite(crypto.charAt(0))) {
                return;
            }

            playerMapping.put(crypto.charAt(0), guess);
            if (playerMapping.get(crypto.charAt(0)) == cryptogram.getPlainLetter(crypto.charAt(0))) {
                player.incrementCorrectGuesses();
            }
        } else {
            if (!checkIfOverwrite(parseInt(crypto))) {
                return;
            }

            playerMapping.put(parseInt(crypto), guess);
            if (playerMapping.get(parseInt(crypto)) == cryptogram.getPlainLetter(parseInt(crypto))) {
                player.incrementCorrectGuesses();
            }
        }

        System.out.println("Guessing " + crypto + " is " + guess);

        player.incrementGuesses();
        checkComplete();
    }

    //This method checks if a player's guess overrides a previous guess, and it gives the player the option if they would like too or not:
    private static boolean checkIfOverwrite(Object crypto) {
        //check crypto value has not already been mapped
        if (playerMapping.get(crypto) != null) {

            String answer;
            do {
                System.out.println(
                        "This cryptogram value has already been mapped. Do you want to overwrite the mapping? (Y/N)");

                Scanner scanner = new Scanner(System.in);
                answer = scanner.nextLine();

                if (answer.equalsIgnoreCase("N")) {
                    return false;
                }
            } while (!answer.equalsIgnoreCase("Y") && !answer.equalsIgnoreCase("N"));
        }
        return true;
    }

    //This resets the guess to a null or blank value
    public static void removeGuess(String crypto) {
        if (cryptogram.getType().equals("Letter")) {
            if (playerMapping.containsKey(crypto.charAt(0))) {
                char mappedLetter = playerMapping.remove(crypto.charAt(0));
                System.out.println("Removed mapping: " + crypto + " => " + mappedLetter);
                return;
            }
        } else {
            if (playerMapping.containsKey(parseInt(crypto))) {
                char mappedLetter = playerMapping.remove(parseInt(crypto));
                System.out.println("Removed mapping: " + crypto + " => " + mappedLetter);
                return;
            }
        }
        System.out.println("Error: Letter " + crypto + " has not been mapped.");
    }

    //This method Displays all the guesses a player has made for this cryptogram:
    public static void displayGuesses() {
        System.out.println("\nCurrent guesses:");
        for (Map.Entry<Object, Character> entry : playerMapping.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

    //This method displays all the user stats:
    public static void displayStats(){
        int completed = player.getCryptogramsCompleted();
        int played = player.getCryptogramsPlayed();
        int compRate = player.getCompletionRate();
        int corGuesses = player.getCorrectGuesses();
        int Guesses = player.getTotalGuesses();
        int GuessAcc = player.getAccuracy();

        System.out.println("Here are " + GameDriver.player.getUsername() + " Statistics"
                + "\n Games Completed: " + completed
                + "\n Games Played: " + played
                + "\n Completion Rate: " + compRate
                + "\n"
                + "\n Total Guesses: " + Guesses
                + "\n Total Correct Guesses: " + corGuesses
                + "\n Guess Accuracy: " + GuessAcc + "%");
    }


    //This method displays all the correct mapping and is only used for testing:
    public static void displayCryptogramMapping() {
        if (cryptogram.getType().equals("Letter")) {
            HashMap<Character, Character> cryptogramMapping = ((LetterCryptogram) cryptogram).getEncryptedHashMap();

            System.out.println("\nComplete Crypto Mapping:");
            for (Map.Entry<Character, Character> entry : cryptogramMapping.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
        } else {

            HashMap<Integer, Character> cryptogramMapping = ((NumberCryptogram) cryptogram).getEncryptedHashMap();

            System.out.println("\nComplete Crypto Mapping:");
            for (Map.Entry<Integer, Character> entry : cryptogramMapping.entrySet()) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
        }
    }

    //This method displays the encrypted cryptogram that the player is trying to solve:
    public static void displayEncryptedPhrase() {
        String encryptedPhrase = cryptogram.getEncryptedPhrase();
        System.out.println(encryptedPhrase);
    }

    //This method is called to check if the player has completed the cryptogram
    private static void checkComplete() {
        if (cryptogram.getSize() != playerMapping.size()) {
            return;
        }

        if (playerMapping.containsValue(null)) {
            return;
        }

        for (Map.Entry<Object, Character> entry : playerMapping.entrySet()) {

            Character playerValue = entry.getValue();
            Character correctValue = cryptogram.getPlainLetter(entry.getKey());

            if (!Objects.equals(playerValue, correctValue)) { //if the player got any mapping wrong
                System.out.println("Your mapping is incorrect.");
                return;
            }
        }

        //if the player's mapping is correct
        player.incrementCryptogramsCompleted();
        System.out.println("Game finished! Well done! \n" + "restart game");
    }

    //This method crates a new Number or Letter cryptogram class:
    public static void generateCryptogram(int type) {
        // randomly choose a phrase from the phrases list to be given to the cryptogram
        Random rand = new Random();
        String phrase = phrases.get(rand.nextInt(phrases.size()));

        if (type == 1) {
            cryptogram = new LetterCryptogram(phrase);
        } else if (type == 2) {
            cryptogram = new NumberCryptogram(phrase);
        }
    }

    //This method displays the top 10 players
    public static boolean displayLeaderboard() {
        ArrayList<Player> players = Players.getPlayers();
        boolean leaderboardDisplayed;

        //Sorts players in descending order of successfully completed cryptograms
        Collections.sort(players);



        if (players.size() == 0) {
            System.out.println("Error: There are no players for the leaderboard to display stats.");
            leaderboardDisplayed = false;
        } else {
            System.out.println("\nThe top 10 in the leaderboard are:");
            System.out.printf("%-5s %-20s %-21s%n","","Name","Cryptograms Completed");

            //Display first 10 players (will be top 10) or less if there are less than 10
            for (int i = 0; i < 10; i++) {
                //The player's position in the top 10.
                System.out.printf("%-5s", (i+1)+".");

                //will only print existing players.
                //if there doesn't exist any more players then nothing will be printed after the position numbers,
                //leaving blank space
                if (i < players.size()) {
                    System.out.printf(" %-20s %-21s", players.get(i).getUsername(), players.get(i).getCryptogramsCompleted());
                }
                System.out.print("\n");
            }
            leaderboardDisplayed = true;
        }
        System.out.println();
        return leaderboardDisplayed;
    }
}
