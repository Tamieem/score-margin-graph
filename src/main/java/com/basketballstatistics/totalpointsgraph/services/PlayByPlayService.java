package com.basketballstatistics.totalpointsgraph.services;

import org.springframework.stereotype.Component;

@Component
public class PlayByPlayService {
    private String gameID;
    private String gameName;

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public String getGameID() {
        return gameID;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
