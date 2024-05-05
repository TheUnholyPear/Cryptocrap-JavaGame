import java.io.Serializable;
import java.util.HashMap;

public class Cryptogram implements Serializable {
    protected String phrase;
    protected String type;
    protected String encryptedPhrase;
    protected Integer remainingHints = 3;

    //constructor
    public Cryptogram(String phrase_) {
        phrase = phrase_;
    }


    //getters
    public String getEncryptedPhrase() {
        return encryptedPhrase;
    }

    public String getType() {
        return type;
    }

    public HashMap<Object, Float> getFrequencies() {
        return null;
    }

    public char getPlainLetter(Object key) {
        return ' ';
    }

    public int getSize() {
        return 0;
    }

    public Integer getRemainingHints() {
        return remainingHints;
    }

    //setters
    public void setFrequencies() {

    }

    public void setLetter(char crypto, char guess) {

    }

    public void setPhrase(String phrase_) {
        phrase = phrase_;
    }

    //other

    public boolean containsKey(String crypto) {
        return false;
    }

    public void lowerRemainingHints() {
        remainingHints--;
    }
}