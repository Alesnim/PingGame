package com.itschool.pinggame;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.itschool.pinggame.game.Ball;
import com.itschool.pinggame.game.Bat;

import java.io.IOException;

public class PongView extends SurfaceView implements Runnable {

    Thread mGameThread = null;
    SurfaceHolder mHolder;
    volatile boolean playingStatus;
    boolean paused = true;

    Canvas canvas;
    Paint paint;
    long fps;
    int mScreenX;
    int mScreenY;

    // The players mBat
    Bat mBat;

    // A mBall
    Ball mBall;

    // For sound FX
    SoundPool sp;
    int beep1ID = -1;
    int beep2ID = -1;
    int beep3ID = -1;
    int loseLifeID = -1;

    // The mScore
    int mScore = 0;

    // Lives
    int mLives = 3;


    public PongView(Context context, int x, int y) {
        super(context);

        mScreenX = x;
        mScreenY = y;
        mHolder = getHolder();
        paint = new Paint();

        mBat = new Bat(mScreenX, mScreenY);
        mBall = new Ball(mScreenX, mScreenY);

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        sp = new SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build();

        try {
            // Create objects of the 2 required classes
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor = null;

            // Load our fx in memory ready for use
            try {
                descriptor = assetManager.openFd("beep1.ogg");
            } catch (IOException e) {
                e.printStackTrace();
            }
            beep1ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("beep2.ogg");
            beep2ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("beep3.ogg");
            beep3ID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("lose_life.ogg");
            loseLifeID = sp.load(descriptor, 0);

            descriptor = assetManager.openFd("explode.ogg");
            // explodeID = sp.load(descriptor, 0);

        } catch (IOException e) {
            // Print an error message to the console
            Log.e("error", "failed to load sound files");
        }


        setupRestart();
    }

    private void setupRestart() {
        mBall.reset(mScreenX, mScreenY);

        if (mLives == 0) {
            mScore = 0;
            mLives = 3;
        }

    }


    @Override
    public void run() {
        while (playingStatus) {
            long startFrameTime = System.currentTimeMillis();

            if (!paused) {
                update();
            }

            draw(canvas);

            long timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }
        }
    }

    private void update() {
        mBat.update(fps);
        mBall.update(fps);
        if (mBat.getRect().intersect(mBall.getmRect())) {
            mBall.setRandomXVelocity();
            mBall.reverseYVelocity();
            mBall.clearY(mBat.getRect().top - 2);
            mScore++;
            mBall.increaseVelocity();
        }

        if (mBall.getmRect().top < 0) {
            mBall.reverseYVelocity();
            mBall.clearY(12);
        }

        if (mBall.getmRect().left < 0) {
            mBall.reverseXVelocity();
            mBall.clearX(2);

        }

        if (mBall.getmRect().right > mScreenX) {
            mBall.reverseXVelocity();
            mBall.clearX(mScreenX - 22);

        }
    }

    @Override
    public void draw(Canvas canv) {
        super.draw(canv);
        if (mHolder.getSurface().isValid()) {

            canvas = mHolder.lockCanvas();

            canvas.drawColor(Color.argb(255, 120, 197, 87));

            // Choose the brush color for drawing
            paint.setColor(Color.argb(255, 255, 255, 255));

            // Draw the mBat
            canvas.drawRect(mBat.getRect(), paint);

            // Draw the mBall
            canvas.drawRect(mBall.getmRect(), paint);


            // Change the drawing color to white
            paint.setColor(Color.argb(255, 255, 255, 255));

            // Draw the mScore
            paint.setTextSize(40);
            canvas.drawText("Score: " + mScore
                    + "   Lives: " + mLives, 10, 50, paint);

            // Draw everything to the screen
            mHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause() {
        playingStatus = false;
        try {
            mGameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }

    public void resume() {
        playingStatus = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            // Player has touched the screen
            case MotionEvent.ACTION_DOWN:

                paused = false;

                // Is the touch on the right or left?
                if (event.getX() > mScreenX >> 1) {
                    mBat.setMovementState(mBat.RIGHT);
                } else {
                    mBat.setMovementState(mBat.LEFT);
                }
                break;
            // Player has removed finger from screen
            case MotionEvent.ACTION_UP:
                mBat.setMovementState(mBat.STOPPED);
                break;
        }
        return true;
    }
}
