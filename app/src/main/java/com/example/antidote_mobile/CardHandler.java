package com.example.antidote_mobile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class CardHandler extends View {

    private final Context context;
    private static final Paint blackText = new Paint();

    private static final long TAP_WINDOW = 500;
    private static final int millisPerFrame = 2, maxDx = 150;
    private int minCardX = 50, maxCardX = 1000, cardY = 1000;

    ValueChangeListener valueChangeListener;

    Card touching;
    Card lifted;
    int xOffset, yOffset;
    boolean draggable = false, selectable = true;


    ArrayList<Card> cards;
    long animationFrames = 0, fingerDownTime = 0;
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
            draggable = a.getBoolean(R.styleable.CardHandler_draggable, false);
        } finally {
            a.recycle();
        }

        // Set up cards array
        cards = new ArrayList<>();

        // Put some cards in the array for testing purposes.
        // Intended is to get these from the db
        for (int i = 0; i < 6; i++) {
            cards.add(new Card(i * 10, 100));
            cards.get(i).forceSetPosition(cards.get(i).x, cardY);
            cards.get(i).setCardData(CardType.SYRINGE);
        }

        // Setup the timer and set the cards to fix themselves (while animating)
        invalidateTimer = new Timer();
        fixCards();
        animateFor(5000);
    }

    public void setValueChangeListener(ValueChangeListener valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
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

    public int getSelectedIndex() {
        if (lifted == null) return -1;
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i) == lifted) return i;
        }
        return -1;
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
            c.draw(canvas, getResources());
        }

    }

    public void deselect() {
        if (lifted != null) {
            lifted.setTarget(lifted.x, cardY);
            animateFor(2000);
            lifted = null;
        }
        if (valueChangeListener != null) valueChangeListener.valueChanged();
    }

    public void forceSelect(int idx) {
        deselect();
        if (idx >= cards.size() || idx < 0) return;
        lifted = cards.get(idx);
        lifted.setTarget(lifted.x, cardY - 50);
        animateFor(2000);
        if (valueChangeListener != null) valueChangeListener.valueChanged();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchX = (int) event.getX();
        int touchY = (int) event.getY();

        if (!selectable) return true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                fingerDownTime = System.currentTimeMillis();
                for (int i = cards.size() - 1; i >= 0; i--) {
                    Card c = cards.get(i);
                    if (c.pointInside(touchX, touchY)) {
                        touching = c;
                        xOffset = touchX - c.x;
                        yOffset = touchY - c.y;
                        break;
                    }
                }
                if (lifted != null && lifted != touching) {
                    lifted.setTarget(lifted.x, cardY);
                }
            case MotionEvent.ACTION_MOVE:
                if (touching != null && draggable) {
                    fixCards();
                    touching.forceSetPosition(touchX - xOffset, touchY - yOffset);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (touching != null) {
                    if (System.currentTimeMillis() - fingerDownTime < TAP_WINDOW && touching != lifted && selectable) {
                        touching.setTarget(touchX - xOffset, cardY - 50);
                        lifted = touching;
                    } else {
                        touching.setTarget(touchX - xOffset, cardY);
                        lifted = null;
                    }
                    fixCards();
                    touching = null;
                    if (valueChangeListener != null) valueChangeListener.valueChanged();
                }
                break;
        }
        return true;
    }

    public ArrayList<String> getCardData() {
        ArrayList<String> ans = new ArrayList<>();
        for (Card c : cards) {
            ans.add(c.getCardData());
        }
        return ans;
    }

    public void forceAll() {
        for (Card c : cards) c.forceMove();
    }

    public void setCards(List<String> cardData) {
        this.setCards(cardData, false);
    }

    public void setCards(List<String> cardData, boolean force) {
        cards.clear();
        System.out.println("Set Cards: " + cardData);
        int cidx = 0;
        for (String data : cardData) {
            Card c = new Card(minCardX + cidx++, cardY);
            String[] dats = data.split("\\.");
            if (dats.length == 1) {
                c.setCardData(CardType.fromString(dats[0]));
            } else if (dats.length == 2) {
                c.setCardData(CardType.fromString(dats[0]), Toxin.fromString(dats[1]));
            } else if (dats.length == 3) {
                c.setCardData(CardType.fromString(dats[0]), Toxin.fromString(dats[1]), Integer.parseInt(dats[2]));
            }
            cards.add(c);
        }
        fixCards();
        if (force) {
            for (Card c : cards) {
                c.forceMove();
            }
        }
        animateFor(5000);
    }

}
