package com.example.antidote_mobile;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class SubheaderTextView extends androidx.appcompat.widget.AppCompatTextView {

    static final float fontSize = 20;

    public SubheaderTextView(Context context) {
        super(context);
        init();
    }

    public SubheaderTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SubheaderTextView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        this.setTextSize(fontSize);
        this.setTypeface(this.getTypeface(), Typeface.BOLD);
    }

}
