package com.half.wowsca.interfaces;

import android.content.Context;

import com.half.wowsca.model.Captain;
import com.half.wowsca.model.Ship;
import com.half.wowsca.model.events.ShipClickedEvent;

/**
 * Created by slai4 on 9/19/2015.
 */
public interface ICaptain {

    Captain getCaptain(Context ctx);

}
