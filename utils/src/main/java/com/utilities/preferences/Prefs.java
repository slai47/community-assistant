package com.utilities.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Prefs {

	private SharedPreferences settings;
	
	public Prefs(Context ctx) {
		settings = PreferenceManager.getDefaultSharedPreferences(ctx);
	}

    public void remove(String prefsName){
        Editor edit = settings.edit();
        edit.remove(prefsName);
        edit.apply();
    }

	public boolean getBoolean(String prefsName, boolean defaultValue){
		return settings.getBoolean(prefsName, defaultValue);
	}
	
	public void setBoolean(String prefsName, boolean value){
		Editor edit = settings.edit();
		edit.putBoolean(prefsName, value);
		edit.apply();
	}

	public String getString(String prefsName, String defaultValue){
		return settings.getString(prefsName, defaultValue);
	}
	
	public void setString(String prefsName, String value){
		Editor edit = settings.edit();
		edit.putString(prefsName, value);
		edit.apply();
	}
	
	public int getInt(String prefsName, int defaultValue){
		return settings.getInt(prefsName, defaultValue);
	}
	
	public void setInt(String prefsName, int defaultValue){
		Editor edit = settings.edit();
		edit.putInt(prefsName, defaultValue);
		edit.apply();
	}

    public long getLong(String prefName, long defaultValue){
        return settings.getLong(prefName, defaultValue);
    }

    public void setLong(String prefName, long value){
        Editor edit = settings.edit();
        edit.putLong(prefName, value);
        edit.apply();
    }
}
