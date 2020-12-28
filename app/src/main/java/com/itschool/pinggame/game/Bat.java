package com.itschool.pinggame.game;

import android.graphics.RectF;

public class Bat {

    private RectF mRect;
    private float mLength;
    private float mHeight;
    private float mXCoord;
    private float mYCoord;
    private float mBatSpeed;
    public final int STOPPED = 0;
    public final int LEFT = 1;
    public final int RIGHT = 2;
    private int mBatMoving = STOPPED;


    private int mScreenX;
    private int mScreenY;


    public Bat(int x, int y) {
        mScreenY = y;
        mScreenX = x;


        mLength = mScreenX >> 3;

        mHeight = mScreenY / 25;

        mXCoord = mScreenX >> 1;
        mYCoord = mScreenY - 20;

        mRect = new RectF(mXCoord, mYCoord,
                mXCoord + mLength,
                mYCoord + mHeight);

        mBatSpeed = mScreenX;
    }

    public RectF getRect() {
        return mRect;
    }

    public void setMovementState(int state) {
        mBatMoving = state;
    }

    public void update(long fps) {

        if (mBatMoving == LEFT) {
            mXCoord = mXCoord - mBatSpeed / fps;
        }

        if (mBatMoving == RIGHT) {
            mXCoord = mXCoord + mBatSpeed / fps;
        }


        if (mRect.left < 0) {
            mXCoord = 0;
        }
        if (mRect.right > mScreenX) {
            mXCoord = mScreenX -
                    (mRect.right - mRect.left);
        }

        mRect.left = mXCoord;
        mRect.right = mXCoord + mLength;
    }



}
