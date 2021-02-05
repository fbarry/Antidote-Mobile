package com.example.antidote_mobile;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class HeaderTextView extends androidx.appcompat.widget.AppCompatTextView {

    static final float fontSize = 24;

    public HeaderTextView(Context context) {
        super(context);
        init();
    }

    public HeaderTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeaderTextView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.setTextSize(fontSize);
        this.setTypeface(this.getTypeface(), Typeface.BOLD);
        this.setPaintFlags(this.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

}
