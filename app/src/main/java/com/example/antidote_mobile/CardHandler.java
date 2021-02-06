package com.example.antidote_mobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class CardHandler extends View {

    private final Context context;
    private static final Paint blackText = new Paint();

    private static final int millisPerFrame = 2, maxDx = 150;
    private static int minCardX = 50, maxCardX = 1000, cardY = 1000;

    Card touching;
    int xOffset, yOffset;

    ArrayList<Card> cards;
    long animationFrames = 0;
    Timer invalidateTimer;

    // Number of times onDraw has been called (for debug)
    @SuppressWarnings("unused")
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

    private void init(@Nullable AttributeSet attrs) {
        blackText.setTextSize(50);
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
            cards.get(i).setTarget(cards.get(i).x, cardY);
        }

        // Setup the timer and set the cards to fix themselves (while animating)
        invalidateTimer = new Timer();
        fixCards();
        animateFor(1000);
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
        animateFor(600);

        if (cards.size() == 1) {
            cards.get(0).setTarget((maxCardX + minCardX) / 2 - Card.cardWidth / 2, cardY);
            return;
        }


        double dx = (maxCardX - minCardX - Card.cardWidth) / (double) (cards.size() - 1);

        // Ensure that if we  don't have enough cards to fill the play area, they
        // don't spread out too much, and they're centered.
        int offset = 0;
        if (dx > maxDx)
            offset = (maxCardX - minCardX) / 2 - maxDx * cards.size() / 2 - maxDx / 4;
        dx = Math.min(dx, maxDx);

        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).setTarget((int) (offset + minCardX + dx * i), cards.get(i).yEnd);
            cards.get(i).setCardData(CardType.SYRINGE);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void drawPlayMat(Canvas canvas) {
        Paint paint = new Paint();
        int borderB = 40, borderW = 20;
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        // This cards border is offset by a few pixels, I'm not sure why so this fix will work for now
        // Is there a better solution? Probably!
        canvas.drawRoundRect(minCardX - borderB - 7, cardY - borderB - 8, maxCardX + borderB, cardY + Card.cardHeight + borderB, 35, 35, paint);
        paint.setStrokeWidth(0);
        paint.setColor(Color.WHITE);
        canvas.drawRoundRect(minCardX - borderW - 7, cardY - borderW - 8, maxCardX + borderW, cardY + Card.cardHeight + borderW, 35, 35, paint);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawText("" + draws++, 400, 430, black);
//        canvas.drawText("" + minCardX + "," + maxCardX + "," + cardY, 400, 300, black);
        drawPlayMat(canvas);
        for (Card c : cards) {
            c.draw(canvas);
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
                    fixCards();
                    touching.forceSetPosition(touchX - xOffset, touchY - yOffset);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (touching != null) {
                    touching.setTarget(touchX - xOffset, cardY);
                    fixCards();
                    touching = null;
                }
                break;
        }
        return true;
    }


    @SuppressWarnings("unused")
    class Card implements Comparable<Card> {

        private static final int cardHeight = 367, cardWidth = 200;
        private static final double trackingDivisor = 6.5, snapRadius = 7.5;

        int xEnd, yEnd, x, y;

        CardType type = CardType.NONE;
        Toxin toxin = Toxin.NONE;
        int number = -1;

        public Card(int x, int y) {
            this.x = xEnd = x;
            this.y = yEnd = y;
        }

        public void setCardData(CardType type) {
            this.type = type;
        }

        public void setCardData(CardType type, Toxin toxin) {
            this.type = type;
            this.toxin = toxin;
        }

        public void setCardData(CardType type, Toxin toxin, int number) {
            this.type = type;
            this.toxin = toxin;
            this.number = number;
        }

        public void clearCardData() {
            type = CardType.NONE;
            toxin = Toxin.NONE;
            number = -1;
        }

        // Set this card to animate towards (tx, ty) over time
        // Ensure that you set enough animation frames for this to complete
        public void setTarget(int tx, int ty) {
            xEnd = tx;
            yEnd = ty;
        }

        // Forcibly set the card's position, without animation
        public void forceSetPosition(int x, int y) {
            this.x = xEnd = x;
            this.y = yEnd = y;
        }

        // Compute and set this card's x to where it should be on it's animation
        private void update() {
            if (Math.abs(xEnd - x) < snapRadius && Math.abs(yEnd - y) < snapRadius) {
                x = xEnd;
                y = yEnd;
                return;
            }

            double dx = (xEnd - x) / trackingDivisor;
            double dy = (yEnd - y) / trackingDivisor;

            x += dx;
            y += dy;
        }

        // Draw this card on the canvas
        public void draw(Canvas canvas) {
            update();
            Rect bounds = new Rect();
            bounds.top = y;
            bounds.left = x;
            bounds.bottom = bounds.top + cardHeight;
            bounds.right = bounds.left + cardWidth;
            Drawable img = ResourcesCompat.getDrawable(getResources(), R.drawable.blank_card, null);
            assert img != null;
            img.setBounds(bounds);
            img.draw(canvas);

            switch (type) {
                case SYRINGE:
                    canvas.drawText("S", x + 20, y + 40, blackText);
                    break;
                case TOXIN:
                    canvas.drawText("X", x + 20, y + 40, blackText);
                    break;
                case ANTIDOTE:
                    canvas.drawText("A", x + 20, y + 40, blackText);
                    break;
                case NONE:
                default:
                    break;
            }
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