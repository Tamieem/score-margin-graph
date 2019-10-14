package com.basketballstatistics.totalpointsgraph.services;

import java.util.Dictionary;
import java.util.Hashtable;

public class ScoreboardService {
    private Dictionary scores = new Hashtable();

    public Dictionary getScores() {
        return scores;
    }

    public void setScores(Dictionary scores) {
        this.scores = scores;
    }
}
