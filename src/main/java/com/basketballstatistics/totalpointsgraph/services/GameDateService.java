package com.basketballstatistics.totalpointsgraph.services;

import org.springframework.stereotype.Component;

@Component
public class GameDateService {
    private String date;

    public String getDate(){
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
