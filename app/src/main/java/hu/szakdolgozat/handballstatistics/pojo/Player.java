package hu.szakdolgozat.handballstatistics.pojo;

public class Player {
    private int playerId;
    private String name;
    private String team;


    public Player(int playerId, String name, String team) {
        this.playerId = playerId;
        this.name = name;
        this.team = team;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }
}
