package domain;

import java.io.IOException;

public class Aoe2Service {

    /** Opens the game, if already not open, and joins the given lobby. */
    public static boolean joinLobby(long lobbyId) {
        return runWindowsCommand("aoe2de://0/" + lobbyId);
    }

    public static boolean spectateGame(long matchId) {
        return runWindowsCommand("aoe2de://1/" + matchId);
    }

    private static boolean runWindowsCommand(String command) {
        try {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + command);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
