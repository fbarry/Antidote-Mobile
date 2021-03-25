package com.example.antidote_mobile;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

import java.util.HashSet;

@SuppressWarnings("unused")
class Card implements Comparable<Card> {

    public static final int cardHeight = 367, cardWidth = 240;
    private static final double trackingDivisor = 6.5, snapRadius = 8.5;
    private static final Paint blackText = new Paint();


    int xStart, yStart, xEnd, yEnd, x, y;

    boolean animateSize = false;
    CardType type = CardType.NONE;
    Toxin toxin = Toxin.NONE;
    int number = -1;
    double size = 1.0;

    public Card(int x, int y) {
        this.x = xEnd = x;
        this.y = yEnd = y;
    }

    public static CardType getCardType(String card) {
        return CardType.fromString(card.split("\\.")[0]);
    }

    public static Toxin getToxin(String card) {
        return Toxin.fromString(card.split("\\.")[1]);
    }

    public static int getNumber(String card) {
        return Integer.parseInt(card.split("\\.")[2]);
    }

    public String getCardData() {
        String ret = type.getText();
        if (toxin != Toxin.NONE) ret += "." + toxin.getText();
        if (number != -1) ret += "." + number;
        return ret;
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
        xStart = x;
        yStart = y;
        xEnd = tx;
        yEnd = ty;
    }

    public void setAnimateSize(boolean toAnimateSize) {
        animateSize = toAnimateSize;
    }

    // Forcibly set the card's position, without animation
    public void forceSetPosition(int x, int y) {
        this.x = xStart = xEnd = x;
        this.y = yStart = yEnd = y;
    }

    public void forceMove() {
        this.x = xStart = xEnd;
        this.y = yStart = yEnd;
    }

    double sign(double x) {
        return x < 0 ? -1 : 1;
    }

    // Compute and set this card's x to where it should be on it's animation
    private void update() {
        if (Math.abs(xEnd - x) < snapRadius && Math.abs(yEnd - y) < snapRadius) {
            x = xEnd;
            y = yEnd;
            return;
        }

        if (animateSize) {
            double startDist = Utilities.dist(xStart, yStart, x, y);
            double endDist = Utilities.dist(x, y, xEnd, yEnd);
            double totalDist = Utilities.dist(xStart, yStart, xEnd, yEnd);
            double t = startDist / totalDist;
            size = Math.min(t, 1 - t) * 2;
        }

        double dx = (xEnd - x) / trackingDivisor;
        double dy = (yEnd - y) / trackingDivisor;

        x += dx;
        y += dy;
    }

    // Draw this card on the canvas
    public void draw(Canvas canvas, Resources resources) {
        draw(canvas, resources, true, new HashSet<>());
    }

    public void draw(Canvas canvas, Resources resources, boolean isWorkstation, HashSet<Toxin> crossToxins) {
        blackText.setTextSize(30);
        update();
        Rect bounds = new Rect();
        bounds.top = y;
        bounds.left = x;
        bounds.bottom = (int) (bounds.top + cardHeight * size);
        bounds.right = (int) (bounds.left + cardWidth * size);
        Drawable img = ResourcesCompat.getDrawable(resources, R.drawable.blank_card, null);
        if(type == CardType.TOXIN && isWorkstation) img = ResourcesCompat.getDrawable(resources, R.drawable.cardback, null);
        assert img != null;
        img.setBounds(bounds);
        img.draw(canvas);

        Drawable centerImage = null;
        Drawable thumbnailImage = null;
        Drawable numberImage = null;
        Drawable crossImage = null;

        switch (type) {
            case SYRINGE:
                thumbnailImage = ResourcesCompat.getDrawable(resources, R.drawable.syringethumbnail, null);
                centerImage = ResourcesCompat.getDrawable(resources, R.drawable.syringe, null);
                break;
            case TOXIN:
                if (isWorkstation) break;
                centerImage = ResourcesCompat.getDrawable(resources, toxin.getResX(), null);
                thumbnailImage = ResourcesCompat.getDrawable(resources, toxin.getThumbres(), null);
                numberImage = ResourcesCompat.getDrawable(resources, R.drawable.numberx, null);
                break;
            case ANTIDOTE:
                centerImage = ResourcesCompat.getDrawable(resources, toxin.getRes(), null);
                thumbnailImage = ResourcesCompat.getDrawable(resources, toxin.getThumbres(), null);
                numberImage = ResourcesCompat.getDrawable(resources, Utilities.getNumberResource(number), null);
                if(crossToxins.contains(toxin)) crossImage = ResourcesCompat.getDrawable(resources, R.drawable.cardcross, null);
                break;
            case NONE:
            default:
                break;
        }

        if (centerImage != null) {
            double scaleFactor = .7;
            Rect bounds2 = new Rect();
            bounds2.top = (int) (y - 5 * size + cardHeight * size * ((1 - scaleFactor) / 2));
            bounds2.left = (int) (x + cardWidth * size * ((1 - scaleFactor) / 2));
            bounds2.bottom = (int) (bounds2.top + cardHeight * size * scaleFactor);
            bounds2.right = (int) (bounds2.left + cardWidth * size * scaleFactor);
            centerImage.setBounds(bounds2);
            centerImage.draw(canvas);
        }
        if (thumbnailImage != null) {
            double scaleFactor = .135;
            Rect bounds2 = new Rect();
            bounds2.top = (int) (y + 1 * size);
            bounds2.left = (int) (x + 5 * size);
            bounds2.bottom = (int) (bounds2.top + cardHeight * size * scaleFactor);
            bounds2.right = (int) (bounds2.left + cardWidth * size * scaleFactor);
            thumbnailImage.setBounds(bounds2);
            thumbnailImage.draw(canvas);
        }
        if (numberImage != null) {
            double scaleFactor = .135;
            if (type == CardType.ANTIDOTE) scaleFactor *= 1.35;

            Rect bounds2 = new Rect();
            bounds2.top = (int) (y + (type == CardType.TOXIN ? 1 : -8) * size);
            bounds2.left = (int) (x + (type == CardType.TOXIN ? 19 : 12) * size);
            bounds2.bottom = (int) (bounds2.top + cardHeight * size * scaleFactor);
            bounds2.right = (int) (bounds2.left + cardHeight * size * scaleFactor);
            numberImage.setBounds(bounds2);
            Utilities.setDrawableColor(toxin.getColorString(), numberImage);
            numberImage.draw(canvas);
        }
        if(crossImage != null){
            crossImage.setBounds(bounds);
            crossImage.draw(canvas);
        }
    }

    // Returns whether (x,y) is inside this card)
    public boolean pointInside(int x, int y) {
        return x >= this.x && y >= this.y && x <= this.x + cardWidth && y <= this.y + cardHeight;
    }

    public String getStringValue() {
        String out = "NONE";
        if (type != CardType.NONE) out = type.getText();
        if (toxin != Toxin.NONE) out += "." + toxin.getText();
        if (number != -1) out += "." + number;
        return out;
    }

    @Override
    public int compareTo(Card o) {
        return Integer.compare(xEnd, o.xEnd);
    }
}