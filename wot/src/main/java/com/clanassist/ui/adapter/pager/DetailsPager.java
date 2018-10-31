package com.clanassist.ui.adapter.pager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.clanassist.model.interfaces.IDetails;
import com.clanassist.ui.details.TankClanMembersListFragment;
import com.clanassist.ui.details.clan.ClanDetailsFragment;
import com.clanassist.ui.details.clan.ClanGraphsFragment;
import com.clanassist.ui.details.clan.ClanQueryFragment;
import com.clanassist.ui.details.player.PlayerAchievementsFragment;
import com.clanassist.ui.details.player.PlayerDetailsFragment;
import com.clanassist.ui.details.player.PlayerGraphsFragment;
import com.clanassist.ui.details.player.PlayerMoreDetailsFragment;
import com.cp.assist.R;

/**
 * Created by Harrison on 9/16/2014.
 */
public class DetailsPager extends FragmentPagerAdapter {

    private Context mCtx;
    private boolean isPlayer;
    private IDetails details;

    public DetailsPager(FragmentManager fm, Context ctx, boolean isPlayer, IDetails details) {
        super(fm);
        mCtx = ctx;
        this.isPlayer = isPlayer;
        this.details = details;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        if (!isPlayer)
            if (position == 0) {
                ClanDetailsFragment frag1 = new ClanDetailsFragment();
                frag1.setDetails(details);
                frag = frag1;
            } else if (position == 1) {
                TankClanMembersListFragment frag1 = new TankClanMembersListFragment();
                frag1.setDetails(details);
                frag = frag1;
            } else if (position == 2) {
                ClanGraphsFragment frag2 = new ClanGraphsFragment();
                frag2.setDetails(details);
                frag = frag2;
            } else {
                frag = new ClanQueryFragment();
            }
        else {
            if (position == 0) {
                PlayerDetailsFragment frag1 = new PlayerDetailsFragment();
                frag1.setDetails(details);
                frag = frag1;
            } else if (position == 1) {
                PlayerMoreDetailsFragment frag2 = new PlayerMoreDetailsFragment();
                frag2.setDetails(details);
                frag = frag2;
            } else if (position == 2) {
                PlayerGraphsFragment frag2 = new PlayerGraphsFragment();
                frag2.setDetails(details);
                frag = frag2;
            } else if (position == 3) {
                PlayerAchievementsFragment frag3 = new PlayerAchievementsFragment();
                frag3.setDetails(details);
                frag = frag3;
            } else {
                TankClanMembersListFragment frag1 = new TankClanMembersListFragment();
                frag1.setDetails(details);
                frag = frag1;
            }
        }
        return frag;
    }

    @Override
    public int getCount() {
        int count = 5;
        if(!isPlayer)
            count = 4;
        return count;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String str;
        if (!isPlayer)
            if (position == 0) {
                str = mCtx.getString(R.string.details_cap);
            } else if (position == 1) {
                str = mCtx.getString(R.string.pager_clan_members);
            } else if (position == 2) {
                str = "Graphs";
            } else {
                str = mCtx.getString(R.string.pager_clan_query);
            }
        else {
            if (position == 0) {
                str = mCtx.getString(R.string.details_cap);
            } else if (position == 1) {
                str = "MORE";
            } else if (position == 2) {
                str = "Graphs";
            } else if (position == 3) {
                str = "Achievements";
            } else {
                str = mCtx.getString(R.string.pager_tanks);
            }
        }
        return str;
    }
}
