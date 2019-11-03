package com.basketballstatistics.totalpointsgraph.services;

import java.util.HashMap;
import java.util.List;

public class PlayByPlayService {
    private List<HashMap> pbp;
    private String gameName;
    private String homeTeam;
    private String awayTeam;

    public PlayByPlayService() {
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public List<HashMap> getPbp() {
        return pbp;
    }

    public void setPbp(List<HashMap> pbp) {
        this.pbp = pbp;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }
}
