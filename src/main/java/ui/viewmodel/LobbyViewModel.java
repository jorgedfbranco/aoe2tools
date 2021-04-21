package ui.viewmodel;

import domain.model.Lobby;
import domain.model.MatchId;
import infra.DateTimeUtils;
import ui.model.SlotViewModel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LobbyViewModel {
    private final Lobby lobby;
    private final List<SlotViewModel> slots = new ArrayList<>();
    private final Optional<LocalDateTime> estCreationTime;

    public LobbyViewModel(Lobby lobby, Optional<LocalDateTime> estCreationTime) {
        this.lobby = lobby;
        this.estCreationTime = estCreationTime;
        for (var slot : lobby.slots())
            slots.add(new SlotViewModel(slot));
    }

    public MatchId getId() {
        return lobby.id();
    }
    public String getTitle() { return lobby.title(); }
    public String getMapType() { return lobby.mapType(); }
    public String getMapSize() { return lobby.mapSize(); }
    public int getRating() { return lobby.averageRating(); }
    public Lobby getLobby() { return lobby; }
    public List<SlotViewModel> getPlayers() { return slots; }

    public String getCreated() {
        if (lobby.created().isPresent())
            return DateTimeUtils.humanizedDuration(lobby.created().get());
        return estCreationTime.map(localDateTime -> DateTimeUtils.humanizedDuration(localDateTime) + " (est)").orElse("");
    }
}