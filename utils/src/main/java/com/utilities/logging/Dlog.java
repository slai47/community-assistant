package com.utilities.logging;

import android.util.Log;

public class Dlog {

	public static boolean LOGGING_MODE; 
	
	public static void d(String type, String message){
		if(LOGGING_MODE)
            try {
                Log.d(type, message);
            } catch (Exception e) {
            }
    }
	
	public static void e(String type, String message){
		if(LOGGING_MODE)
            try {
                Log.e(type, message);
            } catch (Exception e) {
            }
    }

    public static void wtf(String type, String message){
        if(LOGGING_MODE)
            try {
                Log.wtf(type, message);
            } catch (Exception e) {
            }
    }
}
