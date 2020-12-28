package com.itschool.pinggame.game;

import android.graphics.RectF;

import java.util.Random;

public class Ball {
    private RectF mRect;
    private float mXVelocity;
    private float mYVelocity;
    private float mBallWidth;
    private float mBallHeight;

    public Ball(int screenX, int screenY) {
        mBallWidth = screenX / 100;
        mBallHeight = mBallWidth;

        mXVelocity = screenY / 4;
        mYVelocity = mXVelocity;
        mRect = new RectF();
    }

    public RectF getmRect() {
        return mRect;
    }

    public void update(long fps) {
        mRect.left = mRect.left + (mXVelocity / fps);
        mRect.top = mRect.top + (mYVelocity / fps);
        mRect.right = mRect.left + mBallWidth;
        mRect.bottom = mRect.top - mBallHeight;
    }

    public void reverseYVelocity() {
        mYVelocity = -mYVelocity;
    }

    public void reverseXVelocity() {
        mXVelocity = -mXVelocity;
    }

    public void setRandomXVelocity() {
        Random r = new Random();
        int xRandom = r.nextInt(2);

        if (xRandom == 0) {
            reverseXVelocity();
        }
    }

    public void increaseVelocity() {
        mXVelocity = mXVelocity + (mXVelocity / 10);
        mYVelocity = mYVelocity + (mYVelocity / 10);
    }

    public void clearY(float y) {
        mRect.bottom = y;
        mRect.top = y - mBallHeight;
    }

    public void clearX(float x) {
        mRect.left = x;
        mRect.right = x + mBallWidth;
    }

    public void reset(int x, int y) {
        mRect.left = x >> 1; // x / 2
        mRect.top = y - 20;
        mRect.right = (x >> 1) + mBallWidth;
        mRect.bottom = y - 20 - mBallHeight;
    }


}
