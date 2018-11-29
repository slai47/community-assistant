package com.half.wowsca.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;



import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.model.TwitchObj;
import com.half.wowsca.model.enums.TwitchStatus;
import com.half.wowsca.ui.UIUtils;
import com.squareup.picasso.Picasso;
import com.utilities.logging.Dlog;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by slai4 on 12/3/2015.
 */
public class TwitchAdapter extends RecyclerView.Adapter<TwitchAdapter.TwitchHolder>  {

    private List<TwitchObj> twitchObjs;

    private Context ctx;

    @Override
    public TwitchHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_twitch, parent, false);

        TwitchHolder holder = new TwitchHolder(convertView);

        return holder;
    }

    @Override
    public void onBindViewHolder(TwitchHolder holder, int position) {
        TwitchObj obj = twitchObjs.get(position);
        UIUtils.setUpCard(holder.view, R.id.twitch_area);
        holder.title.setText(obj.getName());
        holder.url = obj.getUrl();
        holder.name = obj.getName();

        if(obj.isLive() == TwitchStatus.LIVE){
            holder.alert.setText(ctx.getString(R.string.live));
        } else if(obj.isLive() == TwitchStatus.OFFLINE){
            holder.alert.setText(ctx.getString(R.string.offline));
        } else {
            holder.alert.setText("");
        }

        if(!TextUtils.isEmpty(obj.getStreamName()))
            holder.status.setText(obj.getStreamName());

        if(!TextUtils.isEmpty(obj.getLogo())){
            Picasso.get().load(obj.getLogo()).resize(800, 600).centerInside().error(R.drawable.ic_missing_image).into(holder.logo);
        }

        if(!TextUtils.isEmpty(obj.getThumbnail())){
            Picasso.get().load(obj.getThumbnail()).error(R.drawable.ic_missing_image).into(holder.background);
        }
        //check for youtube only
        if(obj.getName().equals("Jammin411")){
            holder.logo.setImageResource(R.drawable.ic_twitch_wowsreplay_icon);
            holder.background.setImageResource(R.drawable.ic_twitch_wowsreplay);
            holder.url = "http://wowreplays.com/";
            holder.status.setText("");
        } else if (obj.getName().equals("crysantos") || obj.getName().equals("kamisamurai")){
            holder.youtube.setVisibility(View.GONE);
        } else if (obj.getName().equals("notser")){
            holder.twitter.setVisibility(View.GONE);
        } else {
            holder.youtube.setVisibility(View.VISIBLE);
            holder.twitter.setVisibility(View.VISIBLE);
        }

    }


    @Override
    public int getItemCount() {
        try {
            return twitchObjs.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public static class TwitchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View view;

        TextView title;
        ImageView logo;

        ImageView background;

        TextView status;

        View youtube;
        View twitter;

        String name;
        String url;

        TextView alert;

        public TwitchHolder(View itemView) {
            super(itemView);
            view = itemView;
            View click = view.findViewById(R.id.twitch_view);
            click.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.twitch_title);
            logo = (ImageView) itemView.findViewById(R.id.twitch_logo);
            background = (ImageView) itemView.findViewById(R.id.twitch_background);
            status = (TextView) itemView.findViewById(R.id.twitch_status);
            alert = (TextView) itemView.findViewById(R.id.twitch_live);
            youtube = itemView.findViewById(R.id.twitch_youtube);
            youtube.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("https://www.youtube.com/");
                    switch (name) {
                        case "iChaseGaming":
                            sb.append("user/ichasegaming");
                            break;
                        case "Mejash":
                            sb.append("channel/UCAZ25zYeNWAR-LLXVbq4WTg");
                            break;
                        case "dontrevivemebro":
                            sb.append("user/ZoupGaming");
                            break;
                        case "Aerroon":
                            sb.append("channel/UCLOQoJ6G4D04d05fxjfPHPQ");
                            break;
                        case "BaronVonGamez":
                            sb.append("user/BaronVonGamez");
                            break;
                        case "wda_punisher":
                            sb.append("user/WDAxodus");
                            break;
                        case "wargaming":
                            sb.append("user/worldofwarshipsCOM");
                            break;
                        case "iEarlGrey":
                            sb.append("channel/UCtMGV3SHfVfiAt_w8lnmI8g");
                            break;
                        case "Flamuu":
                            sb.append("user/cheesec4t");
                            break;
                        case "clydethamonkey":
                            sb.append("user/SillyScandinavians");
                            break;
                        case "Jammin411":
                            sb.append("c/Jammin411");
                            break;
                        case "notser":
                            sb.append("user/MrNotser");
                            break;
                    }
                    CAApp.getEventBus().post(sb.toString());
                }
            });
            twitter = itemView.findViewById(R.id.twitch_twitter);
            twitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("https://twitter.com/");
                    switch (name) {
                        case "iChaseGaming":
                            sb.append("ichasegaming");
                            break;
                        case "Mejash":
                            sb.append("mejashtv");
                            break;
                        case "dontrevivemebro":
                            sb.append("ZoupGaming");
                            break;
                        case "Aerroon":
                            sb.append("Aerroon");
                            break;
                        case "BaronVonGamez":
                            sb.append("BaronVonGamez");
                            break;
                        case "wda_punisher":
                            sb.append("WDA_Punisher");
                            break;
                        case "wargaming":
                            sb.append("WorldofWarships");
                            break;
                        case "iEarlGrey":
                            sb.append("iearlgreytv");
                            break;
                        case "Flamuu":
                            sb.append("flamuchz");
                            break;
                        case "clydethamonkey":
                            sb.append("Clydeypoo");
                            break;
                        case "Jammin411":
                            sb.append("Jammin411");
                            break;
                        case "crysantos":
                            sb.append("CrysantosTV");
                            break;
                        case "kamisamurai":
                            sb.append("KamiSamuraiTV");
                            break;
                    }
                    CAApp.getEventBus().post(sb.toString());
                }
            });

        }

        @Override
        public void onClick(View v) {
            Dlog.d("Twitch", "url = " + url);
            CAApp.getEventBus().post(url);
        }
    }

    public void sort(){
        Collections.sort(getTwitchObjs(), new Comparator<TwitchObj>() {
            @Override
            public int compare(TwitchObj lhs, TwitchObj rhs) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        });
        Collections.sort(getTwitchObjs(), new Comparator<TwitchObj>() {
            @Override
            public int compare(TwitchObj lhs, TwitchObj rhs) {
                return lhs.isLive().getOrder() - rhs.isLive().getOrder();
            }
        });
        notifyDataSetChanged();
    }

    public List<TwitchObj> getTwitchObjs() {
        return twitchObjs;
    }

    public void setTwitchObjs(List<TwitchObj> twitchObjs) {
        this.twitchObjs = twitchObjs;
    }

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }
}
