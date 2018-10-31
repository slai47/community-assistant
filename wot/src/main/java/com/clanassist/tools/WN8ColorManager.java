package com.clanassist.tools;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.cp.assist.R;
import com.utilities.vaults.StringVault;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.StringTokenizer;

/**
 * Created by Harrison on 7/21/2014.
 */
public class WN8ColorManager {

    public static final int AVERAGE_WINRATE = 49;
    private static SparseArray<Integer> colors;

    public static void setDifferenceColor(TextView tv, double dif) {
        if (dif < 0) {
            tv.setTextColor(Color.RED);
        } else if (dif > 0) {
            tv.setTextColor(Color.GREEN);
        } else {
            tv.setTextColor(tv.getTextColors().getDefaultColor());
        }
    }

    public static void setDifferenceColorCustom(TextView tv, double dif) {
        if (dif < 0) {
            tv.setTextColor(tv.getResources().getColor(R.color.wn_bad));
        } else if (dif > 0) {
            tv.setTextColor(tv.getResources().getColor(R.color.wn_very_good));
        } else {
            tv.setTextColor(tv.getTextColors().getDefaultColor());
        }
    }

    public static SparseArray<Integer> getColors(Context ctx) {
        if (colors == null) {
            colors = new SparseArray<Integer>();
            BufferedReader br = null;
            String line = "";
            String cvsSplitBy = ",";
            StringTokenizer st = null;
            try {
                InputStream stream = ctx.getResources().openRawResource(R.raw.wn8_color_values);
                br = new BufferedReader(new InputStreamReader(stream, Charset.forName(StringVault.DEFAULT_ENCODING)));
                while ((line = br.readLine()) != null) {
                    st = new StringTokenizer(line, cvsSplitBy);
                    int number = Integer.parseInt(st.nextToken());
                    int color = Color.parseColor(st.nextToken());
                    colors.put(number, color);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null)
                    try {
                        br.close();
                    } catch (IOException e) {
                    }
            }
        }
        return colors;
    }

    public static void setColorForWN8(View textView,
                                      int wn8,
//                                      boolean flap,
                                      boolean isColorBlind) {
        int wn8Temp = wn8;
        int color = -1;
        SparseArray<Integer> array = getColors(textView.getContext());
        if (wn8 >= 0 && wn8 < 10000) {
            while (array.get(wn8Temp) == null) {
                wn8Temp--;
            }
            color = array.get(wn8Temp);
        } else if (wn8 > 10000) {
            color = Color.parseColor("#FF56007F");
        }
        if (isColorBlind)
            color = Color.parseColor("#FF000000");
        if (color != -1) {
//            int drawable = R.drawable.wn8_rounded_box;
//            if (flap)
//                drawable = R.drawable.wn8_flap;
//            Drawable background = textView.getContext().getResources().getDrawable(drawable);
//            background.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
//                textView.setBackground(background);
//            else
//                textView.setBackgroundDrawable(background);
//            textView.setTextColor(textView.getResources().getColor(R.color.white));
            textView.setBackgroundColor(color);
        }
    }

    public static int getBackgroundColor(Context ctx, int wn8) {
        int wn8Temp = wn8;
        int color = -1;
        SparseArray<Integer> array = getColors(ctx);
        if (wn8 >= 0 && wn8 < 10000) {
            while (array.get(wn8Temp) == null) {
                wn8Temp--;
            }
            color = array.get(wn8Temp);
        } else if (wn8 > 10000) {
            color = Color.parseColor("#FF56007F");
        }
        return color;
    }

    public static void setWinRateColor(TextView textView, double winrate) {
        int resNumber = -1;
        if (winrate >= 65.0) {
            resNumber = R.color.wn_super_unicum;
        } else if (winrate >= 60.0) {
            resNumber = R.color.wn_unicum;
        } else if (winrate >= 56.0) {
            resNumber = R.color.wn_great;
        } else if (winrate >= 54.0) {
            resNumber = R.color.wn_very_good;
        } else if (winrate >= 52.0) {
            resNumber = R.color.wn_good;
        } else if (winrate >= 49.0) {
            resNumber = R.color.wn_average;
        } else if (winrate >= 47.0) {
            resNumber = R.color.wn_below_average;
        } else {
            resNumber = R.color.wn_bad;
        }
        if (resNumber != -1)
            textView.setTextColor(textView.getResources().getColor(resNumber));
    }

    public static int getWinRateColor(Context ctx, double winrate) {
        int resNumber = -1;
        if (winrate >= 65.0) {
            resNumber = R.color.wn_super_unicum;
        } else if (winrate >= 60.0) {
            resNumber = R.color.wn_unicum;
        } else if (winrate >= 56.0) {
            resNumber = R.color.wn_great;
        } else if (winrate >= 54.0) {
            resNumber = R.color.wn_very_good;
        } else if (winrate >= 52.0) {
            resNumber = R.color.wn_good;
        } else if (winrate >= 49.0) {
            resNumber = R.color.wn_average;
        } else if (winrate >= 47.0) {
            resNumber = R.color.wn_below_average;
        } else {
            resNumber = R.color.wn_bad;
        }
        return ctx.getResources().getColor(resNumber);
    }

    public static int getBattlesColor(Context ctx, double battles) {
        int resNumber = -1;
        if (battles >= 20000) {
            resNumber = R.color.wn_super_unicum;
        } else if (battles >= 17500) {
            resNumber = R.color.wn_unicum;
        } else if (battles >= 15000) {
            resNumber = R.color.wn_great;
        } else if (battles >= 13000) {
            resNumber = R.color.wn_very_good;
        } else if (battles >= 8500) {
            resNumber = R.color.wn_good;
        } else if (battles >= 6000) {
            resNumber = R.color.wn_average;
        } else if (battles >= 3500) {
            resNumber = R.color.wn_below_average;
        } else {
            resNumber = R.color.wn_bad;
        }
        return ctx.getResources().getColor(resNumber);
    }

    public static int getKillsColor(Context ctx, double kills) {
        int resNumber = -1;
        if (kills >= 2.2) {
            resNumber = R.color.wn_super_unicum;
        } else if (kills >= 1.9) {
            resNumber = R.color.wn_unicum;
        } else if (kills >= 1.7) {
            resNumber = R.color.wn_great;
        } else if (kills >= 1.3) {
            resNumber = R.color.wn_very_good;
        } else if (kills >= 1.0) {
            resNumber = R.color.wn_good;
        } else if (kills >= 0.8) {
            resNumber = R.color.wn_average;
        } else if (kills >= 0.6) {
            resNumber = R.color.wn_below_average;
        } else {
            resNumber = R.color.wn_bad;
        }
        return ctx.getResources().getColor(resNumber);
    }
}
