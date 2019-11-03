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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        List<ScoreboardService> sbs =listScores(temp);
        model.addAttribute("gamescores", sbs);
        return "scoreboard";
    }

    @RequestMapping(value="/graph", method=RequestMethod.POST)
    public String getGraph(@ModelAttribute ScoreboardService game) throws ParseException {
        ScoreboardService test = new ScoreboardService();
        test.setGameName("Rockets @ Nets");
        test.setDate("20191101");
        test.setGameID("0021900069");
        PlayByPlayService pbp = listPlayByPlay(test);
        return "graph";
    }


    private List<ScoreboardService> listScores(String date){
        List<ScoreboardService> list = new ArrayList<>();
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
            ScoreboardService score = new ScoreboardService();
            score.setGameID(gameID);
            score.setDate(gameDate);
            score.setGameName(away_name+ " @ " + home_name);
            list.add(score);
        }
        return list;
    }

    private PlayByPlayService listPlayByPlay(ScoreboardService sbs) throws ParseException {
        PlayByPlayService pbps = new PlayByPlayService();
        JSONObject[] contents = new JSONObject[4];
        for(int i = 1; i<=4; i++) {
            String d = String.valueOf(i);
            String url = String.format("http://data.nba.net/prod/v1/%1$s/%2$s_pbp_%3$s.json", sbs.getDate(), sbs.getGameID(), d);
            contents[i-1] = scraper(url);
        }
        List<HashMap> pbp = new ArrayList<>();
        int j = 0;
        for (JSONObject content :contents) {
            j++;
            List plays = (new JSONArray(content.getJSONArray("plays").toString())).toList();
            for (int i =0; i< plays.size(); i++){
                HashMap temp = (HashMap) plays.get(i);
                if((Boolean) temp.get("isScoreChange")){
                    HashMap<String, Object> scored = new HashMap<>(); // Stripping the HashMap given to us from NBA's website to only include what we need
                    scored.put("homeTeamScore", Integer.parseInt((String) temp.get("hTeamScore")));
                    scored.put("awayTeamScore", Integer.parseInt((String) temp.get("vTeamScore")));
                    scored.put("description", (String) temp.get("description"));
                    int home = Integer.parseInt((String) temp.get("hTeamScore"));
                    int away = Integer.parseInt((String) temp.get("vTeamScore"));
                    int diff = home - away;
                    scored.put("scoreMargin", diff);
                    scored.put("quarter", j);
                    scored.put("clock", (String)temp.get("clock"));
//                    String timeLeftInQuarter = (String) temp.get("clock");
//                    SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
//                    LocalTime time = LocalTime.parse(timeLeftInQuarter);
//                    Date timeInQuarter = sdf.parse(timeLeftInQuarter);
//                    long millitime = time.getNano();
//                    long millisecondsInQuarter = timeInQuarter.getTime();
//                    // 60 seconds in a 1 minute, 6000 milliseconds in 1 second
//                    long quarter = 720000; // 720000 milliseconds in 12 minutes
//                    long timeElapsedinQuarter = quarter-millisecondsInQuarter;
//                    long realtime = quarter*(j-1) + timeElapsedinQuarter;
//                    String timeElapsed = String.format("%d:%d",
//                            TimeUnit.MILLISECONDS.toMinutes(realtime),
//                            TimeUnit.MILLISECONDS.toSeconds(realtime) -
//                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(realtime))
//                    );
//                    scored.put("timeElapsed", (String) timeElapsed);
//                    scored.put("timeElapsedinMilli", (long) realtime);
                    pbp.add(scored);
                }
            }
        }
        pbps.setPbp(pbp);
        pbps.setGameName(sbs.getGameName());
        String[] teams = sbs.getGameName().split("@");
        pbps.setAwayTeam(teams[0].strip());
        pbps.setHomeTeam(teams[1].strip());
        return pbps;
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
