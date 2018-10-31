package com.clanassist.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.cp.assist.R;

/**
 * Created by Harrison on 6/20/2015.
 */
public class OutlinedTextView extends TextView {

    private Paint stroke;
    private Paint fill;

    private float textSize;

    public OutlinedTextView(Context context) {
        super(context);
        init();
    }

    public OutlinedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        textSize = getTextSize();

        stroke = new Paint();
        stroke.setColor(getResources().getColor(R.color.black_stroke));
        stroke.setStrokeWidth(1.2f);
        stroke.setStyle(Paint.Style.STROKE);
        stroke.setTextSize(textSize - 1.0f);
        stroke.setAntiAlias(true);
        stroke.setTypeface(Typeface.DEFAULT_BOLD);

        setTextColor(getResources().getColor(R.color.transparent));

        fill = new Paint();
        fill.setColor(getResources().getColor(R.color.white));
        fill.setStyle(Paint.Style.FILL);
        fill.setTextSize(textSize - 1.0f);
        fill.setAntiAlias(true);
        fill.setTypeface(Typeface.DEFAULT_BOLD);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(getText().toString(), (getWidth() - stroke.measureText(getText().toString())), getBaseline(), fill);
        canvas.drawText(getText().toString(), (getWidth() - stroke.measureText(getText().toString())), getBaseline(), stroke);
    }
}
