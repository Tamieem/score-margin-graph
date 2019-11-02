package com.basketballstatistics.totalpointsgraph.services;

import java.util.List;

public class ScoreboardService {
    private List<PlayByPlayService> games;
    private PlayByPlayService game;
    private String gameID;

    public List<PlayByPlayService> getGames() {
        return games;
    }

    public void setGames(List<PlayByPlayService> games) {
        this.games = games;
    }

    public PlayByPlayService getGame() {
        return game;
    }

    public void setGame(PlayByPlayService game) {
        this.game = game;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }
}
