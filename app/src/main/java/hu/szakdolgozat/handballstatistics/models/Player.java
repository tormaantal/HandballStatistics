package hu.szakdolgozat.handballstatistics.models;

import androidx.annotation.NonNull;

public class Player {
    private final int playerId;
    private final String name;
    private final String team;


    public Player(int playerId, String name, String team) {
        this.playerId = playerId;
        this.name = name;
        this.team = team;
    }

    public int getId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public String getTeam() {
        return team;
    }

    @NonNull
    @Override
    public String toString() {
        return name + " - " + team + " (" + playerId + ")";
    }
}
