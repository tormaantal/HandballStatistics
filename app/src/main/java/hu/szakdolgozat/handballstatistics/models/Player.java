package hu.szakdolgozat.handballstatistics.models;

import androidx.annotation.NonNull;

public class Player {
    private final long playerId;
    private final String name;
    private final String team;


    public Player(long playerId, String name, String team) {
        this.playerId = playerId;
        this.name = name;
        this.team = team;
    }

    public long getId() {
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
        return name + " (" + team + " - " + playerId + ")";
    }

    public String getFileName() {
        return name.trim().replaceAll(" ", "_").toLowerCase() + "_" +
                team.trim().replaceAll(" ", "_").toLowerCase() + "_" +
                playerId + "_";

    }
}
