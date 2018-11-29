package com.clanassist.backend.Tasks;

import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;

import com.clanassist.CAApp;
import com.clanassist.model.enums.Server;
import com.clanassist.model.events.details.PlayerWN8Event;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.utilities.logging.Dlog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Created by Harrison on 6/5/2015.
 */
public class Get60DayTask extends AsyncTask<String, Void, PlayerWN8Event> {

    private Context ctx;

    public Get60DayTask(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected PlayerWN8Event doInBackground(String... params) {
        String name = params[0];
        Dlog.d("Geo60Day", name);
        return getPlayerWN8(name);
    }

    @NonNull
    public PlayerWN8Event getPlayerWN8(String name) {
        int pastDay = 0;
        int pastWeek = 0;
        int pastMonth = 0;
        int pastTwoMonths = 0;

        try {
            Server s = CAApp.getServerType(ctx);
            Dlog.d("Get60DayURL", "http://wotlabs.net/" + s.getServerName() + "/player/" + name);
            URL url = new URL("http://wotlabs.net/" + s.getServerName() + "/player/" + name);
            OkHttpClient client = new OkHttpClient();
            client.setConnectTimeout(5, TimeUnit.SECONDS);
            client.setReadTimeout(8, TimeUnit.SECONDS);
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();
            String body = response.body().string();

//            Dlog.d("Get60day", "website = " + body);
            Document doc = Jsoup.parse(body);
            for (Element table : doc.getElementsByClass("generalStats")) {
                for (Element row : table.select("tr")) {
                    Elements tds = row.select("td");
                    String text = tds.text();
                    if (text.contains("WN8")) {
                        try {
                            Dlog.d("RowInfo", text);
                            String[] splitText = text.split(" ");
                            pastDay = Integer.parseInt(splitText[2]);
                            pastWeek = Integer.parseInt(splitText[3]);
                            pastMonth = Integer.parseInt(splitText[4]);
                            int length = splitText[5].length();
                            int splitLength = length / 2;
                            String pastTwoMonthsText = splitText[5];
                            String first = pastTwoMonthsText.substring(0, splitLength);
                            pastTwoMonths = Integer.parseInt(first);
                        } catch (Exception e) {
                        }
                        break;
                    }
                }
                if (pastDay != 0)
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        PlayerWN8Event event = new PlayerWN8Event();
        event.setName(name);
        event.setPastDay(pastDay);
        event.setPastMonth(pastMonth);
        event.setPastTwoMonths(pastTwoMonths);
        event.setPastWeek(pastWeek);
        return event;
    }

    @Override
    protected void onPostExecute(PlayerWN8Event playerWN8Event) {
        super.onPostExecute(playerWN8Event);
        if(playerWN8Event != null)
            CAApp.getEventBus().post(playerWN8Event);
    }
}
