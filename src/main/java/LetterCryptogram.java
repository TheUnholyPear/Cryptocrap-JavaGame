import java.util.*;
import java.util.Map.Entry;

public class LetterCryptogram extends Cryptogram {
    private HashMap<Character, Character> cryptogramMapping; // key: encrypted char; value: unencrypted char
    private HashMap<Object, Float> frequencies; // key: encrypted char; value: frequency


    // constructor
    public LetterCryptogram(String phrase_) {
        super(phrase_);
        type = "Letter";
        cryptogramMapping = new HashMap<>();
        frequencies = new HashMap<>();

        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();

        // creates set of characters that are used in the phrase and converts to
        // ArrayList
        Set<Character> remainingCharsSet = new HashSet<>();
        for (char c : phrase.toCharArray()) {
            remainingCharsSet.add(c);
        }
        remainingCharsSet.remove(' ');
        ArrayList<Character> remainingChars = new ArrayList<>();
        remainingChars.addAll(remainingCharsSet);

        // creates ArrayList of characters that is the whole alphabet (characters from phrase
        // will be mapped to these)
        ArrayList<Character> remainingCrypto = new ArrayList<>();
        for (Character c : alphabet) {
            remainingCrypto.add(c);
        }

        Random rand = new Random();
        int remainingCharsInitialSize = remainingChars.size();

        // maps characters from phrase to random characters in the alphabet
        for (int i = 0; i < remainingCharsInitialSize; i++) {
            Character randChar;

            do { // get random character from the ArrayList of crypto characters
                randChar = remainingCrypto.get(rand.nextInt(remainingCrypto.size()));
            } while (remainingChars.get(i) == randChar); // ensure a letter is not assigned to itself

            // create the mapping
            cryptogramMapping.put(randChar, remainingChars.get(i));

            // ensure crypto characters are not reused
            remainingCrypto.remove(randChar);
        }

        // create an encryptedPhrase string
        StringBuilder encryptedPhraseBuilder = new StringBuilder();
        for (char c : phrase.toCharArray()) {
            if (c == ' ') {
                encryptedPhraseBuilder.append(c);
            } else {
                for (Entry<Character, Character> entry : cryptogramMapping.entrySet()) {
                    if (entry.getValue() == c) {
                        encryptedPhraseBuilder.append(entry.getKey());
                    }
                }
            }
        }
        encryptedPhrase = encryptedPhraseBuilder.toString();

        // calculate the frequencies of each character as a proportion of the cryptogram
        for (Entry<Character, Character> entry : cryptogramMapping.entrySet()) {
            frequencies.put(entry.getKey(), ((Long) encryptedPhrase.chars().filter(character -> character == entry.getKey()).count()).floatValue()*100 / encryptedNoSpacesLength().length());
        }
    }

    // getters
    public char getPlainLetter(Object cryptoLetter) {
        return cryptogramMapping.get(cryptoLetter);
    }

    public HashMap<Character, Character> getEncryptedHashMap() {
        return cryptogramMapping;
    }

    public int getSize() {
        return cryptogramMapping.size();
    }

    public HashMap<Object, Float> getFrequencies() {
        return frequencies;
    }
    public String encryptedNoSpacesLength() {
        String realLength = encryptedPhrase.replaceAll("\\s+","");
        return realLength;
    }
    // setters


    // other
    public boolean containsKey(String keyString) {
        char key = keyString.charAt(0);
        return cryptogramMapping.containsKey(key);
    }
}
