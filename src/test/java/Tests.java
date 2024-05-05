import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class Tests {

    @BeforeEach
    public void deleteSavedData() {
        PrintWriter writer;
        try {
            writer = new PrintWriter("players.txt");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        writer.print("");
        writer.close();
    }

    @AfterEach
    public void revPlayers() {
        Players.remPlayers();
        GameDriver.cryptogram = null;
        GameDriver.playerMapping = null;
        GameDriver.player = null;

    }

    @Test
    public void TestCreatePlayer() {
        Player a = new Player("Bob");
        Assertions.assertNotNull(a);
    }

    @Test
    public void TestPlayerName() {
        Player a = new Player("Bob");
        assertEquals("Bob", a.getUsername());
    }

    @Test
    public void TestTotalGuesses() {
        Player a = new Player("Bob");
        a.incrementGuesses();
        assertEquals(1, a.getTotalGuesses());
    }

    @Test
    public void TestPlayerAccuracy() {
        Player a = new Player("Bob");
        a.incrementGuesses();
        a.incrementGuesses();
        a.incrementGuesses();
        a.incrementCorrectGuesses();
        assertEquals(33, a.getAccuracy());
    }

    @Test
    public void TestCryptoCompleted() {
        Player a = new Player("Bob");
        a.incrementCryptogramsPlayed();
        assertEquals(1, a.getCryptogramsPlayed());
    }

    @Test
    public void TestCryptoAccuracy() {
        Player a = new Player("Bob");
        a.incrementCryptogramsPlayed();
        a.incrementCryptogramsPlayed();
        a.incrementCryptogramsPlayed();
        a.incrementCryptogramsCompleted();
        assertEquals(33, a.getCompletionRate());
    }


    // CRYPTOGRAM TESTS
    @Test
    public void CryptoLetterCryptogram() {
        GameDriver.onStart();
        GameDriver.generateCryptogram(1);
        assertSame("Letter", GameDriver.cryptogram.getType());
    }

    @Test
    public void CryptoNumberCryptogram() {
        GameDriver.onStart();
        GameDriver.generateCryptogram(2);
        assertEquals(GameDriver.cryptogram.getType(), "Number");
    }


    @Test
    public void CryptoNoPhrases() {
        GameDriver.phrases = null;
        try {
            GameDriver.generateCryptogram(1);
            fail("expected exception was not occured.");
        } catch (NullPointerException ignored) {

        }
    }

    //Test works on it's on but goes on forever when ran with all other tests
//    @Test
//    public void CryptogramGuess() {
//        Player a = new Player("Bob");
//        new GameDriver(a);
//        GameDriver.playerMapping = new HashMap<>();
//        GameDriver.phrases = new ArrayList<>();
//        GameDriver.phrases.add("abcdefghijklmnopqrstuvwxyz");
//        GameDriver.generateCryptogram(1);
//        GameDriver.enterGuess("g", 'a');
//        assertEquals(1, GameDriver.playerMapping.size());
//    }

    @Test
    public void CryptoGuessNotInCrypto() {
        Player a = new Player("Bob");
        new GameDriver(a);
        GameDriver.playerMapping = new HashMap<>();
        GameDriver.phrases = new ArrayList<>();
        GameDriver.phrases.add("z");
        GameDriver.generateCryptogram(1);
        GameDriver.enterGuess("z", 'b');
        assertEquals(0, GameDriver.playerMapping.size());
    }

    @Test
    public void CryptoGuessAlreadyMapped() {
        Player a = new Player("Bob");
        new GameDriver(a);
        GameDriver.playerMapping = new HashMap<>();
        GameDriver.phrases = new ArrayList<>();
        GameDriver.phrases.add("abcdefghijklmnopqrstuvwxyz");
        GameDriver.generateCryptogram(1);
        GameDriver.enterGuess("a", 'b');
        GameDriver.enterGuess("a", 'b');
        assertEquals(1, GameDriver.playerMapping.size());

    }

    @Test
    public void CryptoRemGuess() {
        Player a = new Player("Bob");
        new GameDriver(a);
        GameDriver.playerMapping = new HashMap<>();
        GameDriver.phrases = new ArrayList<>();
        GameDriver.phrases.add("abcdefghijklmnopqrstuvwxyz");
        GameDriver.generateCryptogram(1);
        GameDriver.enterGuess("a", 'b');
        GameDriver.removeGuess("a");
        assertEquals(0, GameDriver.playerMapping.size());
    }


    @Test
    public void CryptoRemNull() {
        Player a = new Player("Bob");
        new GameDriver(a);
        GameDriver.playerMapping = new HashMap<>();
        GameDriver.phrases = new ArrayList<>();
        GameDriver.phrases.add("abcdefghijklmnopqrstuvwxyz");
        GameDriver.generateCryptogram(1);
        GameDriver.removeGuess("d ");
        assertEquals(0, GameDriver.playerMapping.size());
    }

    @Test
    public void finishCrypto() {
        Player a = new Player("Bob");
        new GameDriver(a);
        GameDriver.playerMapping = new HashMap<>();
        GameDriver.phrases = new ArrayList<>();
        GameDriver.phrases.add("z");
        GameDriver.generateCryptogram(1);
        String guess = GameDriver.cryptogram.getEncryptedPhrase();
        GameDriver.enterGuess(guess, 'z');
        assertEquals(1, a.getCryptogramsCompleted());
    }

    @Test
    public void wronglyFinishCrypto() {
        Player a = new Player("Bob");
        new GameDriver(a);
        GameDriver.playerMapping = new HashMap<>();
        GameDriver.phrases = new ArrayList<>();
        GameDriver.phrases.add("z");
        GameDriver.generateCryptogram(1);
        String guess = GameDriver.cryptogram.getEncryptedPhrase();
        GameDriver.enterGuess(guess, 'i');
        assertEquals(0, a.getCryptogramsCompleted());
    }

    @Test
    public void saveGame() {
        new GameDriver();
        GameDriver.createPlayer("Bob");
        GameDriver.playerMapping = new HashMap<>();

        GameDriver.saveGame();
        assertEquals(GameDriver.player, Players.findPlayer("Bob"));
    }

    @Test
    public void loadPlayer() {
        new GameDriver();
        GameDriver.createPlayer("Bob");

        new GameDriver();
        GameDriver.player = null;

        GameDriver.loadPlayer("Bob");
        assertEquals(Players.findPlayer("Bob"), GameDriver.player);
    }

    @Test
    public void loadNullPlayer() {
        new GameDriver();

        assertNull(Players.findPlayer("Bob"));
        assertNull(GameDriver.player);
    }

    @Test
    public void saveCryptogram() {
        new GameDriver();
        GameDriver.createPlayer("Bob");
        GameDriver.playerMapping = new HashMap<>();
        GameDriver.phrases = new ArrayList<>();
        GameDriver.phrases.add("abcdefghijklmnopqrstuvwxyz");
        GameDriver.generateCryptogram(1);
        String guess = toString().valueOf(GameDriver.cryptogram.getEncryptedPhrase().charAt(0));
        GameDriver.enterGuess(guess, 'a');
        GameDriver.saveGame();

        assertEquals(GameDriver.cryptogram, Objects.requireNonNull(GameDriver.player).getCryptogram());
        assertEquals(GameDriver.playerMapping, Objects.requireNonNull(GameDriver.player).getPlayerMapping());
    }

    @Test
    public void saveCryptogramOverwrite() {
        new GameDriver();
        GameDriver.createPlayer("Bob");
        GameDriver.playerMapping = new HashMap<>();
        GameDriver.phrases = new ArrayList<>();
        GameDriver.phrases.add("abcdefghijklmnopqrstuvwxyz");
        GameDriver.generateCryptogram(1);
        GameDriver.saveGame();

        String guess = toString().valueOf(GameDriver.cryptogram.getEncryptedPhrase().charAt(0));
        GameDriver.enterGuess(guess, 'a');

        ByteArrayInputStream in = new ByteArrayInputStream("Y".getBytes());
        System.setIn(in);

        GameDriver.saveGame();

        assertEquals(GameDriver.cryptogram, Objects.requireNonNull(GameDriver.player).getCryptogram());
        assertEquals(GameDriver.playerMapping, Objects.requireNonNull(GameDriver.player).getPlayerMapping());
    }

    @Test
    public void saveCryptogramNotOverwrite() {
        new GameDriver();
        GameDriver.createPlayer("Bob");
        GameDriver.playerMapping = new HashMap<>();
        GameDriver.phrases = new ArrayList<>();
        GameDriver.phrases.add("abcdefghijklmnopqrstuvwxyz");
        GameDriver.generateCryptogram(1);
        GameDriver.saveGame();

        String guess = toString().valueOf(GameDriver.cryptogram.getEncryptedPhrase().charAt(0));
        GameDriver.enterGuess(guess, 'a');

        ByteArrayInputStream in = new ByteArrayInputStream("N".getBytes());
        System.setIn(in);

        GameDriver.saveGame();

        assertNotEquals(GameDriver.playerMapping, Objects.requireNonNull(GameDriver.player).getPlayerMapping());
    }


    @Test
    public void loadCryptogram() {
        new GameDriver();
        GameDriver.createPlayer("Bob");
        GameDriver.playerMapping = new HashMap<>();
        GameDriver.phrases = new ArrayList<>();
        GameDriver.phrases.add("abcdefghijklmnopqrstuvwxyz");
        GameDriver.generateCryptogram(1);
        String guess = toString().valueOf(GameDriver.cryptogram.getEncryptedPhrase().charAt(0));
        GameDriver.enterGuess(guess, 'a');
        GameDriver.saveGame();

        new GameDriver();
        GameDriver.loadPlayer("Bob");
        GameDriver.loadCryptogram();
        assertEquals(GameDriver.cryptogram, Objects.requireNonNull(Players.findPlayer("Bob")).getCryptogram());
        assertEquals(GameDriver.playerMapping, Objects.requireNonNull(Players.findPlayer("Bob")).getPlayerMapping());
    }

    @Test
    public void loadNullCryptogram() {
        Player a = new Player("Bob");
        new GameDriver(a);
        GameDriver.loadCryptogram();

        assertNull(GameDriver.playerMapping);
        assertNull(GameDriver.cryptogram);

    }

    @Test
    public void saveAndLoadFile() {
        new GameDriver();
        GameDriver.createPlayer("Bob");
        GameDriver.playerMapping = new HashMap<>();

        new GameDriver();
        Players.loadPlayers();
        assertNotNull(Players.findPlayer("Bob"));
    }

    //Can't test saving on exit because exiting stops the program completely including any testing
//    @Test
//    public void saveOnExit() {
//        new GameDriver();
//        GameDriver.createPlayer("Bob");
//        GameDriver.playerMapping = new HashMap<>();
//        GameDriver.phrases = new ArrayList<>();
//        GameDriver.phrases.add("abcdefghijklmnopqrstuvwxyz");
//        GameDriver.generateCryptogram(1);
//        String guess = toString().valueOf(GameDriver.cryptogram.getEncryptedPhrase().charAt(0));
//        GameDriver.enterGuess(guess, 'a');
//        GameDriver.exitGame();
//
//        new GameDriver();
//        GameDriver.loadPlayer("Bob");
//
//        assertNotNull(GameDriver.player.getPlayerMapping());
//    }

    @Test
    public void leaderboardNoPlayers() {
        new GameDriver();
        assertFalse(GameDriver.displayLeaderboard());
    }

    @Test
    public void leaderboardPlayers() {
        new GameDriver();
        GameDriver.createPlayer("bob");
        assertTrue(GameDriver.displayLeaderboard());
    }

    @Test
    public void playersSorted() {
        new GameDriver();

        GameDriver.createPlayer("bob");
        GameDriver.player.incrementCryptogramsCompleted();

        GameDriver.createPlayer("jack");
        for (int i = 0; i<3; i++) {
            GameDriver.player.incrementCryptogramsCompleted();
        }

        GameDriver.createPlayer("john");

        GameDriver.createPlayer("kevin");
        for (int i = 0; i<5; i++) {
            GameDriver.player.incrementCryptogramsCompleted();
        }

        GameDriver.createPlayer("jason");
        for (int i = 0; i<2; i++) {
            GameDriver.player.incrementCryptogramsCompleted();
        }

        boolean isSorted = true;
        ArrayList<Player> players = Players.getPlayers();

        Collections.sort(players);

        //loops through players, returning false if any of the players cryptogramsCompletes values are not out of order
        //(they should be in descending order)
        for (int i = 0; i < players.size()-1; i++) {
            if (!(players.get(i).getCryptogramsCompleted() >= players.get(i + 1).getCryptogramsCompleted())) {
                isSorted = false;
                break;
            }
        }

        assertTrue(isSorted);
    }

    @Test
    public void testHintNoCryptogram() {
        GameDriver.createPlayer("bob");
        assertFalse(GameDriver.getHint());
    }

    @Test
    public void testHintLetter() {
        GameDriver.onStart();
        GameDriver.createPlayer("bob");
        GameDriver.generateCryptogram(1);

        assertTrue(GameDriver.getHint());
        assertEquals(1, GameDriver.playerMapping.size());
        assertTrue(GameDriver.getHint());
        assertEquals(2, GameDriver.playerMapping.size());
        assertTrue(GameDriver.getHint());
        assertEquals(3, GameDriver.playerMapping.size());

        // test the correctness of the hint mappings
        for (Map.Entry<Object, Character> entry : GameDriver.playerMapping.entrySet()) {
            assertEquals(((LetterCryptogram) GameDriver.cryptogram).getEncryptedHashMap().get(entry.getKey()), entry.getValue());
        }
    }

    @Test
    public void testHintNumber() {
        GameDriver.onStart();
        GameDriver.createPlayer("bob");
        GameDriver.generateCryptogram(2);

        assertTrue(GameDriver.getHint());
        assertEquals(1, GameDriver.playerMapping.size());
        assertTrue(GameDriver.getHint());
        assertEquals(2, GameDriver.playerMapping.size());
        assertTrue(GameDriver.getHint());
        assertEquals(3, GameDriver.playerMapping.size());

        // test the correctness of the hint mappings
        for (Map.Entry<Object, Character> entry : GameDriver.playerMapping.entrySet()) {
            assertEquals(((NumberCryptogram) GameDriver.cryptogram).getEncryptedHashMap().get(entry.getKey()), entry.getValue());
        }
    }

    @Test
    public void testHintRemoveIncorrectMappings() {
        GameDriver.onStart();
        GameDriver.createPlayer("bob");
        GameDriver.generateCryptogram(1);

        assertTrue(GameDriver.getHint());
        assertEquals(1, GameDriver.playerMapping.size());

        Map.Entry<Object, Character> hintEntry = GameDriver.playerMapping.entrySet().iterator().next();

        // remove the hint mapping
        GameDriver.removeGuess(hintEntry.getKey().toString());

        // add an incorrect mapping for the hinted character
        // the loop is necessary to guarantee an incorrect mapping is created
        for (Character c : "abcdefghijklmnopqrstuvwxyz".toCharArray()) {
            if (c != hintEntry.getValue()) {
                GameDriver.enterGuess(hintEntry.getKey().toString(), c);
                ByteArrayInputStream in = new ByteArrayInputStream("Y".getBytes());
                System.setIn(in);
            }
        }
        ByteArrayInputStream in = new ByteArrayInputStream("Y".getBytes());
        System.setIn(in);

        // re-add the hint mapping
        assertTrue(GameDriver.getHint());

        // if getHint has removed the incorrect mapping, playerMapping.size() will be 1
        assertEquals(1, GameDriver.playerMapping.size());

    }

    @Test
    public void testHintOutOfHints() {
        GameDriver.onStart();
        GameDriver.createPlayer("bob");
        GameDriver.generateCryptogram(1);

        GameDriver.getHint();
        GameDriver.getHint();
        GameDriver.getHint();

        assertFalse(GameDriver.getHint());
    }
    @Test
    public void testFrequenciesHasNoSpaces () {
        GameDriver.createPlayer("bob");
        GameDriver.playerMapping = new HashMap<>();
        GameDriver.phrases = new ArrayList<>();
        GameDriver.phrases.add("           a");
        GameDriver.generateCryptogram(1);

        for (Object key : GameDriver.cryptogram.getFrequencies().keySet()) {
            assertEquals(GameDriver.cryptogram.getFrequencies().get(key), 100);
        }

        //NumberCryptogram:
        Players.remPlayers();
        GameDriver.cryptogram = null;
        GameDriver.playerMapping = null;
        GameDriver.player = null;

        GameDriver.createPlayer("bob");
        GameDriver.playerMapping = new HashMap<>();
        GameDriver.phrases = new ArrayList<>();
        GameDriver.phrases.add("           a");
        GameDriver.generateCryptogram(2);
        for (Object key : GameDriver.cryptogram.getFrequencies().keySet()) {
            assertEquals(100, Math.round(GameDriver.cryptogram.getFrequencies().get(key)));
        }
    }

    @Test
    public void testFrequenciesPercentagies() {
        GameDriver.createPlayer("bob");
        GameDriver.playerMapping = new HashMap<>();
        GameDriver.phrases = new ArrayList<>();
        GameDriver.phrases.add("abacbc");
        GameDriver.generateCryptogram(1);

        for (Object key : GameDriver.cryptogram.getFrequencies().keySet()) {
            assertEquals(33, Math.round(GameDriver.cryptogram.getFrequencies().get(key)));
        }

        //NumbersCryptogram
        Players.remPlayers();
        GameDriver.cryptogram = null;
        GameDriver.playerMapping = null;
        GameDriver.player = null;


        GameDriver.createPlayer("bob");
        GameDriver.playerMapping = new HashMap<>();
        GameDriver.phrases = new ArrayList<>();
        GameDriver.phrases.add("abacbc");
        GameDriver.generateCryptogram(2);

        for (Object key : GameDriver.cryptogram.getFrequencies().keySet()) {
            assertEquals(33, Math.round(GameDriver.cryptogram.getFrequencies().get(key)));
        }

    }

    @Test
    public void testFrequenciesNull() {
        GameDriver.createPlayer("bob");
        GameDriver.playerMapping = new HashMap<>();
        GameDriver.phrases = new ArrayList<>();
        GameDriver.phrases.add("");
        GameDriver.generateCryptogram(1);

        for (Object key : GameDriver.cryptogram.getFrequencies().keySet()) {
            assertNull(GameDriver.cryptogram.getFrequencies().get(key));
        }

        //NumbersCryptogram
        Players.remPlayers();
        GameDriver.cryptogram = null;
        GameDriver.playerMapping = null;
        GameDriver.player = null;


        GameDriver.createPlayer("bob");
        GameDriver.playerMapping = new HashMap<>();
        GameDriver.phrases = new ArrayList<>();
        GameDriver.phrases.add("");
        GameDriver.generateCryptogram(2);

        for (Object key : GameDriver.cryptogram.getFrequencies().keySet()) {
            assertNull(GameDriver.cryptogram.getFrequencies().get(key));
        }

    }
}
