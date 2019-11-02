package com.basketballstatistics.totalpointsgraph.controllers;


import com.basketballstatistics.totalpointsgraph.services.GameDateService;
import com.basketballstatistics.totalpointsgraph.services.PlayByPlayService;
import com.basketballstatistics.totalpointsgraph.services.ScoreboardService;
import org.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Controller
public class PlayByPlayController {
    private String date;


    @GetMapping({"/", ""})
    public String showDate(Model model){
        model.addAttribute("gamedate", new GameDateService());
        return "index";
    }

    @RequestMapping("/scoreboard")
    public String getGame(@ModelAttribute GameDateService gameDateService, Model model) {
        String temp = gameDateService.getDate();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        Date date1 = format.parse(temp, new ParsePosition(0));
        if (today.compareTo(date1) <= 0) // We can't get the graph of a game that hasn't happend yet
            return "404";
        temp = temp.replaceAll("-", "");
        date = temp;
        List<PlayByPlayService> pbps =listScores(temp);
        model.addAttribute("gamescores", pbps);
        return "scoreboard";
    }

    @RequestMapping(value="/graph", method=RequestMethod.POST)
    public String getGraph(@ModelAttribute PlayByPlayService gamescore){
        listPlayByPlay(gamescore);
        return "graph";
    }


    private List<PlayByPlayService> listScores(String date){
        List<PlayByPlayService> list = new ArrayList<>();
        String url = String.format("http://data.nba.net/json/cms/noseason/scoreboard/%s/games.json", date);
        JSONObject content = scraper(url);
        if(content == null)
            return null;
        JSONObject sportscontent = new JSONObject(content.getJSONObject("sports_content").toString());
        JSONObject games = new JSONObject(sportscontent.getJSONObject("games").toString());
        List game_list = (new JSONArray(games.getJSONArray("game").toString())).toList();
        for(int i = 0; i< game_list.size(); i++){
            HashMap temp = (HashMap) game_list.get(i);
            String gameID = (String) temp.get("id");
            String gameDate = (String) temp.get("date");
            HashMap home = (HashMap) temp.get("home");
            String home_name =  (String) home.get("nickname");
            HashMap away = (HashMap) temp.get("visitor");
            String away_name = (String) away.get("nickname");
            PlayByPlayService score = new PlayByPlayService();
            score.setGameID(gameID);
            score.setDate(gameDate);
            score.setGameName(away_name+ " @ " + home_name);
            list.add(score);
        }
        return list;
    }

    private void listPlayByPlay(PlayByPlayService pbp) {
            for(int i = 1; i<=4; i++) {
                String url = String.format("http://data.nba.net/prod/v1/%s/%s_pbp_%i.json", pbp.getDate(), pbp.getGameID(), i);
                JSONObject content = scraper(url);
            }
    }

    private JSONObject scraper(String url){
        JSONObject content = new JSONObject();
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            System.out.println("\nSending 'GET' request to URL: : " + url);
            System.out.println("Response Code: " + con.getResponseCode());
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            content = new JSONObject(response.toString());
        }catch(Exception e){ e.printStackTrace(); }
        finally {
            return content;
        }
    }

}
