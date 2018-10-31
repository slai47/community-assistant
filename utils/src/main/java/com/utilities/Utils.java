package com.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;

import com.utilities.vaults.StringVault;

import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Utils {

    public static float interpolateValue(float vO, float vT, float i)
    {
        return ((vT - vO) * i) + vO;
    }

    public static String getInputStreamResponse(URL requested) throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(requested).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static String validateUrl(String url){
        return url.replaceAll(" ", "%20").replaceAll(",", "%2C");
    }

	public static boolean hasInternetConnection(Context ctx){
		ConnectivityManager connectivityManager 
        	= (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}

	public static DateFormat getHourMinuteFormatter(Context ctx){
		if (android.text.format.DateFormat.is24HourFormat(ctx)) {
			// 24 hour format
			return get24HourMinuteFormatter();
		}
		else
		{
			// 12 hour format
			return getHourMinuteFormatter();
		}
	}

	/**
	 * #.##
	 *  
	 * @return
	 */
	public static DecimalFormat getDefaultDecimalFormatter(){
		return new DecimalFormat(StringVault.DECIMAL_TWO_DEPTH);
	}

    /**
     * #.#
     *
     * @return
     */
    public static DecimalFormat getOneDepthDecimalFormatter(){
        return new DecimalFormat(StringVault.DECIMAL_ONE_DEPTH);
    }


	/**
	 * HH MM SS A
	 * 
	 * @return
	 */
	public static DateFormat getHourMinuteSecondFormatter(){
		return new SimpleDateFormat(StringVault.H_MM_SS_A);
	}
	/**
	 * #.#
	 * 
	 * @return
	 */
	public static DecimalFormat getSpeedDecimalFormatter(){
		return new DecimalFormat(StringVault.SPEED_DECIMAL);
	}
	/**
	 * H MM A
	 * 
	 * @return
	 */
	private static DateFormat getHourMinuteFormatter(){
		return new SimpleDateFormat(StringVault.H_MM_A);
	}

    /**
     * h MM A
     *
     * @return
     */
	private static DateFormat get24HourMinuteFormatter(){
        return new SimpleDateFormat(StringVault.TWENTYFOUR_H_MM_A);
    }
	/**
	 * MM DD YYYY
	 *
	 * @return
	 */
	public static DateFormat getDayMonthYearFormatter(Context ctx){
		String format = Settings.System.getString(ctx.getContentResolver(), Settings.System.DATE_FORMAT);
		if(TextUtils.isEmpty(format)) {
			return new SimpleDateFormat(StringVault.MM_DD_YYYY);
		} else{
			return new SimpleDateFormat(format);
		}
	}

	/**
	 * MM DD
	 * 
	 * @return
	 */
	public static DateFormat getDayMonthFormatter(){
		return new SimpleDateFormat(StringVault.MM_DD);
	}

	/**
	 * MM DD
	 *
	 * @return
	 */
	public static DateFormat getMonthDayFormatter(){
		return new SimpleDateFormat(StringVault.DD_MM);
	}


	public static boolean canUseExternalStorage(){
    	boolean mExternalStorageAvailable = false;
    	boolean mExternalStorageWriteable = false;
    	String state = Environment.getExternalStorageState();

    	if (Environment.MEDIA_MOUNTED.equals(state)) {
    	    // We can read and write the media
    	    mExternalStorageAvailable = mExternalStorageWriteable = true;
    	} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
    	    // We can only read the media
    	    mExternalStorageAvailable = true;
    	    mExternalStorageWriteable = false;
    	} else {
    	    // Something else is wrong. It may be one of many other states, but all we need
    	    //  to know is we can neither read nor write
    	    mExternalStorageAvailable = mExternalStorageWriteable = false;
    	}
    	return mExternalStorageWriteable;
    }

	public static long convertToMinutes(long time){
		return (time / 1000) / 60;
	}

	public static long convertToHours(long time){
		return convertToMinutes(time) / 60;
	}

	public static long convertToDays(long time){
		return convertToHours(time) / 24;
	}
}
