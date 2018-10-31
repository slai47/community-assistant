package com.clanassist.model.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.clanassist.ui.views.OutlinedTextView;
import com.cp.assist.R;

/**
 * Created by Harrison on 6/27/2015.
 */
public class ClanTankHolder {

    public int accountId;
    public String name;

    public ImageView ivTank;
    public TextView tvName;
    public TextView tvRole;

    public View aWN8;
    public View aOverallWn8;
    public View aClanWn8;
    public View aSHWn8;
    public OutlinedTextView tvWn8;
    public OutlinedTextView tvClanWn8;
    public OutlinedTextView tvSHWn8;
    public TextView tvMastery;

    public View aAdvancedSection;
    public TextView tvBattles;
    public TextView tvWinRate;
    public TextView tvKD;
    public TextView tvExp;
    public TextView tvDamage;
    public TextView tvHitPer;

    public View aLastBattle;
    public TextView tvLastBattle;

    public View tvRecentTitle;
    public View aRecents;
    public View aRecentsDay;
    public View aRecentsWeek;
    public View aRecentsMonth;
    public View aRecentsTwoMonth;
    public OutlinedTextView tvDayWn8;
    public OutlinedTextView tvWeekWn8;
    public OutlinedTextView tvMonthWn8;
    public OutlinedTextView tvTwoMonthWn8;

    public static ClanTankHolder getHolder(View view, String name, int id) {
        ClanTankHolder holder = new ClanTankHolder();
        holder.accountId = id;
        holder.name = name;

        holder.ivTank = (ImageView) view.findViewById(R.id.list_tank_icon);
        holder.tvName = (TextView) view.findViewById(R.id.list_member_name);
        holder.tvRole = (TextView) view.findViewById(R.id.list_member_role);

        holder.aWN8 = view.findViewById(R.id.list_member_wn8_area);
        holder.aOverallWn8 = view.findViewById(R.id.list_member_wn8_overall_area);
        holder.aClanWn8 = view.findViewById(R.id.list_member_wn8_clan_area);
        holder.aSHWn8 = view.findViewById(R.id.list_member_wn8_sh_area);
        holder.tvWn8 = (OutlinedTextView) view.findViewById(R.id.list_member_wn8);
        holder.tvClanWn8 = (OutlinedTextView) view.findViewById(R.id.list_member_wn8_clan);
        holder.tvSHWn8 = (OutlinedTextView) view.findViewById(R.id.list_member_wn8_sh);
        holder.tvMastery = (TextView) view.findViewById(R.id.list_member_mastery);

        holder.aAdvancedSection = view.findViewById(R.id.list_member_advanced_area);
        holder.tvBattles = (TextView) view.findViewById(R.id.list_member_advanced_battles);
        holder.tvWinRate = (TextView) view.findViewById(R.id.list_member_advanced_win_rate);
        holder.tvDamage = (TextView) view.findViewById(R.id.list_member_advanced_dam);
        holder.tvKD = (TextView) view.findViewById(R.id.list_member_advanced_kills);
        holder.tvExp = (TextView) view.findViewById(R.id.list_member_advanced_exp);
        holder.tvHitPer = (TextView) view.findViewById(R.id.list_member_advanced_hits);

        holder.aLastBattle = view.findViewById(R.id.list_member_advanced_last_battle_area);
        holder.tvLastBattle = (TextView) view.findViewById(R.id.list_member_advanced_last_battle);

        holder.aRecents = view.findViewById(R.id.list_member_recents);
        holder.aRecentsDay = view.findViewById(R.id.list_member_recents_day_area);
        holder.aRecentsWeek = view.findViewById(R.id.list_member_recents_week_area);
        holder.aRecentsMonth = view.findViewById(R.id.list_member_recents_month_area);
        holder.aRecentsTwoMonth = view.findViewById(R.id.list_member_recents_two_month_area);

        holder.tvRecentTitle = view.findViewById(R.id.list_member_recents_tv);

        holder.tvDayWn8 = (OutlinedTextView) view.findViewById(R.id.list_member_recents_day);
        holder.tvWeekWn8 = (OutlinedTextView) view.findViewById(R.id.list_member_recents_week);
        holder.tvMonthWn8 = (OutlinedTextView) view.findViewById(R.id.list_member_recents_month);
        holder.tvTwoMonthWn8 = (OutlinedTextView) view.findViewById(R.id.list_member_recents_two_month);

        return holder;
    }

}
