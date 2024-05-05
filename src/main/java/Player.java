import java.io.Serializable;
import java.util.HashMap;

public class Player implements Serializable, Comparable<Player>{
    private String username;
    private int totalGuesses;
    private int correctGuesses;
    private int cryptogramsPlayed;
    private int cryptogramsCompleted;

    private HashMap<Object, Character> playerMapping;
    private Cryptogram cryptogram;


    public Player(String username_) {
        username = username_;
        totalGuesses = 0;
        correctGuesses = 0;
        cryptogramsPlayed = 0;
        cryptogramsCompleted = 0;
        cryptogram = null;
        playerMapping = null;
    }


    //getters
    public String getUsername() {
        return username;
    }

    public int getAccuracy() {
        if (correctGuesses == 0) {
            return 0;
        } else {
            return ((correctGuesses*100)/totalGuesses);
        }
    }



    public int getTotalGuesses() {
        return totalGuesses;
    }

    public int getCorrectGuesses() {
        return correctGuesses;
    }

    public int getCryptogramsPlayed() {
        return cryptogramsPlayed;
    }

    public int getCryptogramsCompleted() {
        return cryptogramsCompleted;
    }

    public HashMap<Object, Character> getPlayerMapping() {
        return playerMapping;
    }

    public Cryptogram getCryptogram() {
        return cryptogram;
    }

    //setters
    public void incrementCryptogramsPlayed() {
        cryptogramsPlayed++;
    }

    public void incrementCryptogramsCompleted() {
        cryptogramsCompleted++;
    }

    public int getCompletionRate(){
        if (cryptogramsCompleted == 0) {
            return 0;
        } else {

            return ((cryptogramsCompleted*100)/cryptogramsPlayed);
        }
    }


    public void incrementGuesses() {
        totalGuesses++;
    }

    public void incrementCorrectGuesses() {
        correctGuesses++;
    }

    public void saveCryptogram(HashMap<Object, Character> currPlayerMapping, Cryptogram currCryptogram) {
        playerMapping = (HashMap<Object, Character>) currPlayerMapping.clone();
        cryptogram = currCryptogram;
    }

    @Override
    public int compareTo(Player o) {
        //If this player's score comes before the score received in descending order then don't swap objects
        if (cryptogramsCompleted > o.getCryptogramsCompleted()) {
            return -1;
            //If this player's score is equal to the score received then compare name (ascending order)
        } else if (cryptogramsCompleted == o.getCryptogramsCompleted()) {
            return 0;

            //If this player's score comes after the score received in descending order then swap objects
        } else {
            return 1;
        }
    }
}
