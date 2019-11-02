package com.basketballstatistics.totalpointsgraph.services;

import org.springframework.stereotype.Component;

@Component
public class PlayByPlayService {
    private String gameID;
    private String gameName;
    private String date;

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
