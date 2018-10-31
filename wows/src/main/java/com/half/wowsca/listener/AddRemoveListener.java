package com.half.wowsca.listener;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;



import com.half.wowsca.CAApp;
import com.half.wowsca.R;
import com.half.wowsca.managers.CaptainManager;
import com.half.wowsca.model.Captain;
import com.half.wowsca.model.events.AddRemoveEvent;

import java.util.Map;

/**
 * Created by slai4 on 9/19/2015.
 */
public class AddRemoveListener implements View.OnClickListener {

    private Captain captain;
    private Context ctx;
    private CheckBox box;

    public AddRemoveListener(Captain captain, Context ctx, CheckBox box) {
        this.captain = captain;
        this.ctx = ctx;
        this.box = box;
    }

    @Override
    public void onClick(View view) {
        Map<String, Captain> captains = CaptainManager.getCaptains(ctx);
        AddRemoveEvent event = new AddRemoveEvent();
        event.setCaptain(captain);
        if (captains.get(CaptainManager.getCapIdStr(captain)) == null) {
            event.setRemove(false);
            box.setChecked(true);
            Toast.makeText(ctx, captain.getName() + " " + ctx.getString(R.string.list_clan_added_message), Toast.LENGTH_SHORT).show();
        } else {
            event.setRemove(true);
            box.setChecked(false);
            Toast.makeText(ctx, captain.getName() + " " + ctx.getString(R.string.list_clan_removed_message), Toast.LENGTH_SHORT).show();
        }
        CAApp.getEventBus().post(event);
    }
}
