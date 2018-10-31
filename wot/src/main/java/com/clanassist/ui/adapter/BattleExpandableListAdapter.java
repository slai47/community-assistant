package com.clanassist.ui.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clanassist.CAApp;
import com.clanassist.model.clan.Battle;
import com.clanassist.model.clan.Clan;
import com.clanassist.tools.AlarmManagement;
import com.clanassist.ui.UIUtils;
import com.cp.assist.R;
import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Obsidian47 on 3/9/14.
 */
public class BattleExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Clan> clans;
    private boolean battleActivityName;

    private Battle holder;

    private boolean isLightTheme;

    public BattleExpandableListAdapter(Context context, List<Clan> result) {
        this.context = context;
        clans = result;
        isLightTheme = CAApp.isLightTheme(context);
    }

    @Override
    public View getGroupView(int groupNumber, boolean expanded, View v, ViewGroup viewGroup) {
        View view = v;
        view = LayoutInflater.from(context).inflate(R.layout.list_group_clan, viewGroup, false);

        UIUtils.setUpCard(view, R.id.list_group_clan_card);

        View bump = view.findViewById(R.id.list_group_clan_bump);
        TextView title = (TextView) view.findViewById(R.id.list_battles_title);
        ImageView expand = (ImageView) view.findViewById(R.id.list_battles_expand);
        View noBattles = view.findViewById(R.id.list_battles_no_battles);
        Clan clan = (Clan) getGroup(groupNumber);

        if (groupNumber == 0) {
            bump.setVisibility(View.GONE);
        } else {
            bump.setVisibility(View.VISIBLE);
        }

        if (!battleActivityName)
            title.setText(clan.getName());
        else
            title.setText(R.string.list_battle_title_activity);
        if (clan.getBattles() != null)
            if (!clan.getBattles().isEmpty()) {
                expand.setVisibility(View.VISIBLE);
                if (expanded) {
                    expand.setImageResource(R.drawable.expander_close_holo_dark);
                } else {
                    expand.setImageResource(R.drawable.expander_open_holo_dark);
                }
                if (isLightTheme) {
                    expand.setColorFilter(R.color.material_light_text_secondary, PorterDuff.Mode.MULTIPLY);
                }
                noBattles.setVisibility(View.GONE);
            } else {
                expand.setVisibility(View.INVISIBLE);
                noBattles.setVisibility(View.VISIBLE);
            }
        else {
            expand.setVisibility(View.INVISIBLE);
            noBattles.setVisibility(View.VISIBLE);
        }
        return view;
    }

    @Override
    public View getChildView(int groupNumber, int childNumber, boolean b, View v, ViewGroup viewGroup) {
        View view = v;
        Battle battle = (Battle) getChild(groupNumber, childNumber);
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_map, viewGroup, false);
        }
        ImageView background = (ImageView) view.findViewById(R.id.list_map_image);
        TextView mapName = (TextView) view.findViewById(R.id.list_map_map_name);
        TextView battleTime = (TextView) view.findViewById(R.id.list_map_battle_time);
        TextView engagement = (TextView) view.findViewById(R.id.list_map_engagement_type);
        ImageView action = (ImageView) view.findViewById(R.id.list_map_action);
        TextView provinceName = (TextView) view.findViewById(R.id.list_map_province_name);
        TextView tvMap = (TextView) view.findViewById(R.id.list_map_map_type);

        Picasso.with(context).load(CAApp.getImageRepo(context) + battle.getArenaName() + "_screen.jpg").error(R.drawable.ic_missing_image).noFade().into(background);

        String type = battle.getType();
        if (type.equals("for_province"))
            type = context.getString(R.string.battle_type_province);
        else if (type.equals("meeting_engagement"))
            type = context.getString(R.string.battle_type_engagement);
        else
            type = context.getString(R.string.battle_type_landing);
        if (!"1".equals(battle.getMapId())) { // Wonderful logic
            tvMap.setText(R.string.event_map);
            tvMap.setVisibility(View.VISIBLE);
        } else {
            tvMap.setVisibility(View.GONE);
        }
        engagement.setText(type);
        provinceName.setText(battle.getProvinceName());
        mapName.setText(battle.getArenaName_i18n());

        battleTime.setText(UIUtils.getTimeFormat(context, battle.getTime(), context.getString(R.string.scheduling)));

        mapName.setVisibility(View.VISIBLE);

        if (battle.getTime() != null) {
            List<String> strings = null;
            try {
                strings = getStrings(battle.getTime());
            } catch (Exception e) {
                Crashlytics.log(2, "Battle Crash", "Battle = " + battle + " testing");
            }
            if (strings.isEmpty()) {
                action.setVisibility(View.GONE);
            } else {
                action.setVisibility(View.VISIBLE);
            }
            if (AlarmManagement.hasAlarm(context, battle))
                action.setImageResource((isLightTheme ? R.drawable.ic_alarm_light : R.drawable.ic_alarm_dark));
            else
                action.setImageResource(R.drawable.ic_alarm);

            action.setTag(battle);
            action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder = (Battle) view.getTag();
                    List<String> strings = getStrings(holder.getTime());
                    if (!strings.isEmpty()) {
                        if (!AlarmManagement.hasAlarm(context, holder)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(R.string.alarm_dialog_time_title);
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, strings);
                            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String choice = getStrings(holder.getTime()).get(i);
                                    String[] s = choice.split(" ");
                                    String number = s[0];
                                    int minutesBefore = 0;
                                    try {
                                        minutesBefore = Integer.parseInt(number);
                                    } catch (NumberFormatException e) {
                                    }
                                    if (minutesBefore != 0) {
                                        AlarmManagement.createAlarm(context, holder, minutesBefore);
                                        notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            builder.setNeutralButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            builder.show();
                        } else {
                            AlarmManagement.removeAlarm(context, holder.getUuid() + "");
                            holder = null;
                        }
                        BattleExpandableListAdapter.this.notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, R.string.alarm_click_no_alarm_times, Toast.LENGTH_LONG).show();
                        view.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            action.setVisibility(View.GONE);
        }
        return view;
    }

    public List<String> getStrings(Calendar battleTime) {
        List<String> strings = new ArrayList<String>();
        String afterText = context.getString(R.string.alarm_dialog_before);
        if (timeIsBeforeBattle(battleTime, 2))
            strings.add(2 + " " + afterText);
        if (timeIsBeforeBattle(battleTime, 5))
            strings.add(5 + " " + afterText);
        if (timeIsBeforeBattle(battleTime, 10))
            strings.add(10 + " " + afterText);
        if (timeIsBeforeBattle(battleTime, 15))
            strings.add(15 + " " + afterText);
        if (timeIsBeforeBattle(battleTime, 30))
            strings.add(30 + " " + afterText);
        if (timeIsBeforeBattle(battleTime, 45))
            strings.add(45 + " " + afterText);
        return strings;
    }

    public static boolean timeIsBeforeBattle(Calendar battleTime, int offset) {
        Calendar offsetTime = Calendar.getInstance();
        offsetTime.setTimeInMillis(battleTime.getTimeInMillis() - (offset * 60 * 1000));
        return offsetTime.after(Calendar.getInstance());
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return true;
    }

    @Override
    public int getGroupCount() {
        try {
            return clans.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Object getChild(int i, int i2) {
        return clans.get(i).getBattles().get(i2);
    }

    @Override
    public Object getGroup(int i) {
        return clans.get(i);
    }

    @Override
    public int getChildrenCount(int i) {
        try {
            return clans.get(i).getBattles().size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public long getChildId(int i, int i2) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    public List<Clan> getClans() {
        return clans;
    }

    public void setClans(List<Clan> clans) {
        this.clans = clans;
    }

    public boolean isBattleActivityName() {
        return battleActivityName;
    }

    public void setBattleActivityName(boolean battleActivityName) {
        this.battleActivityName = battleActivityName;
    }
}
