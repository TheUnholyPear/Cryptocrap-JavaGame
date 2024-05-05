import javax.swing.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.IntStream;

import static java.lang.Integer.parseInt;

public class NumberCryptogram extends Cryptogram {
    private HashMap<Integer, Character> cryptogramMapping; // key: encrypted char; value: unencrypted char
    private HashMap<Object, Float> frequencies; // key: encrypted char; value: frequency

    // constructor
    public NumberCryptogram(String phrase_) {
        super(phrase_);
        type = "Number";
        cryptogramMapping = new HashMap<>();
        frequencies = new HashMap<>();

        int[] numbers = IntStream.rangeClosed(1, 26).toArray();

        // creates set of characters that are used in the phrase and converts to
        // ArrayList
        Set<Character> remainingCharsSet = new HashSet<>();
        for (char c : phrase.toCharArray()) {
            remainingCharsSet.add(c);
        }
        remainingCharsSet.remove(' ');
        ArrayList<Character> remainingChars = new ArrayList<>();
        remainingChars.addAll(remainingCharsSet);

        // creates ArrayList of numbers range 1-26 (characters from phrase
        // will be mapped to these)
        ArrayList<Integer> remainingCrypto = new ArrayList<>();
        for (Integer n : numbers) {
            remainingCrypto.add(n);
        }

        Random rand = new Random();
        int remainingCharsInitialSize = remainingChars.size();

        // maps characters from phrase to random characters in the alphabet
        for (int i = 0; i < remainingCharsInitialSize; i++) {
            Integer randChar = remainingCrypto.get(rand.nextInt(remainingCrypto.size()));

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
                for (Entry<Integer, Character> entry : cryptogramMapping.entrySet()) {
                    if (entry.getValue() == c) {
                        encryptedPhraseBuilder.append(entry.getKey());
                        encryptedPhraseBuilder.append('-');
                    }
                }
            }
        }
        encryptedPhrase = encryptedPhraseBuilder.toString();

        // calculate frequencies for each number as a proportion of the cryptogram
        ArrayList<Integer> numberValues = new ArrayList<>();
        for (String numberString : encryptedPhrase.split("(-)|( )")) {
            if (!numberString.equals("")) {
                numberValues.add(Integer.parseInt(numberString));
            }
        }
        for (Entry<Integer, Character> entry : cryptogramMapping.entrySet()) {
            frequencies.put(entry.getKey(), numberValues.stream().filter(number -> number.equals(entry.getKey())).count()*100 / (float) numberValues.size());
        }
    }


    // getters
    public char getPlainLetter(Object cryptoLetter) {
        return cryptogramMapping.get(cryptoLetter);
    }

    public HashMap<Integer, Character> getEncryptedHashMap() {
        return cryptogramMapping;
    }

    public int getSize() {
        return cryptogramMapping.size();
    }

    public HashMap<Object, Float> getFrequencies() {
        return frequencies;
    }

    // setters


    // other
    public boolean containsKey(String keyString) {
        try {
            int key = parseInt(keyString);
            return cryptogramMapping.containsKey(key);
        } catch (NumberFormatException e) {
            System.out.println("Error: input is not an integer value");
        }
        return false;
    }
}
