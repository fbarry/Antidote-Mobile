package com.example.antidote_mobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class CardHandler extends View {

    private final Context context;
    private static final Paint blackText = new Paint();

    private static final int millisPerFrame = 2;
    private static int minCardX = 50, maxCardX = 1000, cardY = 1000;

    Card touching;
    int xOffset, yOffset;

    ArrayList<Card> cards;
    long animationFrames = 0;
    Timer invalidateTimer;

    // Number of times onDraw has been called (for debug)
    static int draws = 0;

    public CardHandler(Context context) {
        super(context);
        this.context = context;
        init(null);
    }

    public CardHandler(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public CardHandler(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    public CardHandler(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        blackText.setTextSize(70);
        blackText.setColor(Color.BLACK);

        // Pull attrs from the activity's XML
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CardHandler, 0, 0);
        try {
            minCardX = a.getInteger(R.styleable.CardHandler_deck_left, 10);
            maxCardX = a.getInteger(R.styleable.CardHandler_deck_right, 10);
            cardY = a.getInteger(R.styleable.CardHandler_deck_top, 10);
        } finally {
            a.recycle();
        }

        // Set up cards array
        cards = new ArrayList<>();

        // Put some cards in the array for testing purposes.
        // Intended is to get these from the db
        for (int i = 0; i < 6; i++) {
            cards.add(new Card(i * 10, 100));
            cards.get(i).setTarget(cards.get(i).x, cardY, 400);
        }

        // Setup the timer and set the cards to fix themselves (while animating)
        invalidateTimer = new Timer();
        animateFor(450);
        fixCards();
    }

    // Ensure that this CardHandler will continue animations for a set amount of time
    private void animateFor(int millis) {
        // If we're already animating, don't create a new timer, just update the
        // number of frames we need to do
        if (animationFrames > 0) {
            animationFrames = millis / millisPerFrame;
            return;
        }

        animationFrames = millis / millisPerFrame;

        // Schedule a periodic timer which repeatedly invalidates the view, so
        // the Activity has to call onDraw (so animations can be recalcd and shown)
        invalidateTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                postInvalidate();
                if (animationFrames-- <= 0) {
                    animationFrames = 0;
                    cancel();
                }
            }
        }, 0, millisPerFrame);
    }

    // Ensure that "cards" is in sorted order of target x, and set each card's
    // animation to go to that position.
    private void fixCards() {
        Collections.sort(cards);
        double dx = (maxCardX - minCardX - Card.cardWidth) / (double) (cards.size() - 1);

        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).setTarget((int) (minCardX + dx * i), cards.get(i).yEnd, 300);
        }
        // TODO: Center cards if we don't have enough to fill the range
    }


    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawText("" + draws++, 400, 430, black);
//        canvas.drawText("" + minCardX + "," + maxCardX + "," + cardY, 400, 300, black);

        for (Card c : cards) {
            c.draw(canvas, context);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchX = (int) event.getX();
        int touchY = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (int i = cards.size() - 1; i >= 0; i--) {
                    Card c = cards.get(i);
                    if (c.pointInside(touchX, touchY)) {
                        touching = c;
                        xOffset = touchX - c.x;
                        yOffset = touchY - c.y;
                        break;
                    }
                }
            case MotionEvent.ACTION_MOVE:
                if (touching != null) {
                    animateFor(1000);
                    fixCards();
                    touching.forceSetPosition(touchX - xOffset, touchY - yOffset);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (touching != null) {
                    touching.setTarget(touchX - xOffset, cardY);
                    animateFor(450);
                    fixCards();
                    touching = null;
                }
                break;
        }
        return true;
    }


    static class Card implements Comparable<Card> {

        private static final int cardHeight = 367, cardWidth = 200;

        int xStart, xEnd, yStart, yEnd;
        long tStart, tEnd;
        int x, y;

        public Card(int x, int y) {
            this.x = xEnd = x;
            this.y = yEnd = y;
        }

        // Set this card to animate towards (tx, ty) over 100ms
        public void setTarget(int tx, int ty) {
            setTarget(tx, ty, 100);
        }

        // Set this card to animate towards (tx, ty) over a period of time
        public void setTarget(int tx, int ty, long delay) {
            if (tx == xEnd && ty == yEnd) return;
            // If we're currently en route, just say this is our new start
            xStart = x;
            yStart = y;

            // Set our targets for interpolation later
            xEnd = tx;
            yEnd = ty;
            tStart = System.currentTimeMillis();
            tEnd = tStart + delay;
        }

        // Forcibly set the card's position, without animation
        public void forceSetPosition(int x, int y) {
            this.x = xStart = xEnd = x;
            this.y = yStart = yEnd = y;
            tEnd = tStart;
        }

        // Compute and set this card's x to where it should be on it's animation
        private void update() {
            if (tStart == tEnd) {
                x = xEnd;
                y = yEnd;
                return;
            }

            double progress = (double) (System.currentTimeMillis() - tStart) / (tEnd - tStart);
            if (progress >= 1) {
                x = xEnd;
                y = yEnd;
                tStart = tEnd;
                return;
            }

            x = linterp(xStart, xEnd, progress);
            y = linterp(yStart, yEnd, progress);
        }

        // Linear interpolation from s to e, with progress p
        private static int linterp(int s, int e, double p) {
            return (int) (s + (e - s) * p);
        }

        // Draw this card on the canvas
        public void draw(Canvas canvas, Context context) {
            update();
            Rect bounds = new Rect();
            bounds.top = y;
            bounds.left = x;
            bounds.bottom = bounds.top + cardHeight;
            bounds.right = bounds.left + cardWidth;
            Drawable img = context.getResources().getDrawable(R.drawable.blank_card);
            img.setBounds(bounds);
            img.draw(canvas);
        }

        // Returns whether (x,y) is inside this card)
        public boolean pointInside(int x, int y) {
            return x >= this.x && y >= this.y && x <= this.x + cardWidth && y <= this.y + cardHeight;
        }

        @Override
        public int compareTo(Card o) {
            return Integer.compare(xEnd, o.xEnd);
        }
    }
}
