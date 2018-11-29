package com.clanassist.ui.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.clanassist.CAApp;
import com.clanassist.model.TwitchObj;
import com.cp.assist.R;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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

        holder.title.setText(obj.getName());
        holder.url = obj.getUrl();
        holder.name = obj.getName();

        if(holder.name.equals("hyf1re") || holder.name.equals("timhamlett")){
            holder.youtube.setVisibility(View.INVISIBLE);
        } else {
            holder.youtube.setVisibility(View.VISIBLE);
        }
        holder.alert.setText(obj.isLive() ? ctx.getString(R.string.online) : ctx.getString(R.string.offline));

        if(!TextUtils.isEmpty(obj.getStreamName()))
            holder.status.setText(obj.getStreamName());

        if(!TextUtils.isEmpty(obj.getLogo())){
            Picasso.with(ctx).load(obj.getLogo()).resize(800, 600).centerInside().error(R.drawable.ic_missing_image).into(holder.logo);
        }

        if(!TextUtils.isEmpty(obj.getThumbnail())){
            Picasso.with(ctx).load(obj.getThumbnail()).error(R.drawable.ic_missing_image).into(holder.background);
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
            itemView.setOnClickListener(this);
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
                    Answers.getInstance().logCustom(new CustomEvent("Youtube").putCustomAttribute("tuber", name));
                    switch (name) {
                        case "Trobsmonkey":
                            sb.append("channel/UCUbkHogEfvsElIVwVpmaAEw");
                            break;
                        case "anfield_us":
                            sb.append("user/itsabattleship");
                            break;
                        case "SirHavoc09":
                            sb.append("user/SirHavocTV");
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
                    Answers.getInstance().logCustom(new CustomEvent("Twitter").putCustomAttribute("user", name));
                    switch (name) {
                        case "hyf1re":
                            sb.append("Hyf1re");
                            break;
                        case "timhamlett":
                            sb.append("tim_hamlett");
                            break;
                        case "Trobsmonkey":
                            sb.append("trobsmonkey");
                            break;
                        case "SirHavoc09":
                            sb.append("SirHavocTv");
                            break;

                    }
                    CAApp.getEventBus().post(sb.toString());
                }
            });

        }

        @Override
        public void onClick(View v) {
            Answers.getInstance().logCustom(new CustomEvent("Twitch").putCustomAttribute("Streamer", name));
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
                int lhsLive = lhs.isLive() ? -1 : 1;
                int rhsLive = rhs.isLive() ? -1 : 1;
                return lhsLive - rhsLive;
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
