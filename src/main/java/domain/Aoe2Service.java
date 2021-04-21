package domain;

import domain.model.MatchId;
import domain.model.ProfileId;
import infra.WindowsService;

public class Aoe2Service {

    /** Opens the game, if already not open, and joins the given lobby. */
    public static boolean joinLobby(long lobbyId) {
        return WindowsService.runWindowsCommand("aoe2de://0/" + lobbyId);
    }

    public static boolean spectateGame(MatchId matchId) {
        return WindowsService.runWindowsCommand("aoe2de://1/" + matchId.id());
    }

    public static void downloadGame(MatchId matchId, ProfileId profileId) {
        WindowsService.runWindowsCommand(downloadLink(matchId, profileId));
    }

    public static String downloadLink(MatchId matchId, ProfileId profileId) {
        return "https://aoe.ms/replay/?gameId=" + matchId.id() + "&profileId=" + profileId.id();
    }
}
