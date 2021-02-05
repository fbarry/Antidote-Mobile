package com.example.antidote_mobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


// It's a paint canvas, but probably shouldn't be used.
// It was gotten from following a tutorial, and contains useful code to serve as an example.
// Delete this upon project finalization / when it is no longer useful as a reference.
public class DoodleView extends View {

    private final Paint paintDoodle = new Paint();
    private final Path path = new Path();

    public DoodleView(Context context) {
        super(context);
        init();
    }

    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DoodleView,
                0, 0
        );
        try {
            float strokeyboy = a.getFloat(R.styleable.DoodleView_strokey, 1.0f);
            paintDoodle.setStrokeWidth(strokeyboy);

        } finally {
            a.recycle();
        }
    }

    public void makeThicc() {
        paintDoodle.setStrokeWidth(69);
        path.reset();
    }

    public DoodleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();

    }

    private void init() {
        paintDoodle.setColor(Color.RED);
        paintDoodle.setAntiAlias(true);
        paintDoodle.setStyle(Paint.Style.STROKE);

    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path, paintDoodle);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        invalidate();
        return true;
    }
}
