package com.example.antidote_mobile;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

@SuppressWarnings("unused")
class Card implements Comparable<Card> {

    public static final int cardHeight = 367, cardWidth = 200;
    private static final double trackingDivisor = 6.5, snapRadius = 7.5;
    private static final Paint blackText = new Paint();


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
    public void draw(Canvas canvas, Resources resources) {
        blackText.setTextSize(30);
        update();
        Rect bounds = new Rect();
        bounds.top = y;
        bounds.left = x;
        bounds.bottom = bounds.top + cardHeight;
        bounds.right = bounds.left + cardWidth;
        Drawable img = ResourcesCompat.getDrawable(resources, R.drawable.blank_card, null);
        assert img != null;
        img.setBounds(bounds);
        img.draw(canvas);

        switch (type) {
            case SYRINGE:
                canvas.drawText("S", x + 20, y + 40, blackText);
                break;
            case TOXIN:
                canvas.drawText("X"+toxin.getText().charAt(0), x + 20, y + 40, blackText);
                break;
            case ANTIDOTE:
                canvas.drawText("A"+toxin.getText().charAt(0)+number, x + 20, y + 40, blackText);
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