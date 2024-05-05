import java.io.*;
import java.util.ArrayList;

public class Players {
    static private ArrayList<Player> allPlayers = new ArrayList<>();
    static private File playersFile = new File("players.txt");

    //getters
    static public Player findPlayer(String name) {
        for (Player player : allPlayers) {
            if (player.getUsername().equals(name)) {
                return player;
            }
        }
        return null;
    }

    static public ArrayList<Player> getPlayers() {
        return allPlayers;
    }

    //setters


    //other
    static public void addPlayer(Player p) {
        allPlayers.add(p);
    }

    static public void savePlayers() {
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(playersFile));
            oos.writeObject(allPlayers);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static public void loadPlayers() {
        ObjectInputStream ois;

        try {
            ois = new ObjectInputStream(new FileInputStream(playersFile));
            allPlayers = (ArrayList<Player>) ois.readObject();
            ois.close();
        } catch (IOException e) {
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    static public void remPlayers(){
        allPlayers.clear();
    }
}
