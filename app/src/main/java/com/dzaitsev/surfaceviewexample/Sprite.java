package com.dzaitsev.surfaceviewexample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.security.SecureRandom;
import java.util.Random;


/**
 * Created by d.zaitsev on 09.07.2014.
 */
public class Sprite {
	private static final Random RANDOM = new SecureRandom();
	private static final int COLUMNS = 3;
	private static final int MAX_SPEED = 5;
	private static final int ROWS = 4;

	/**
	 * [direction : animation]:<br>
	 * [0 : 3] - up<br>
	 * [1 : 1] - left<br>
	 * [2 : 0] - down<br>
	 * [3 : 2] - right<br>
	 */
	private static final int[] DIRECTION_TO_ANIMATION_MAP = {
		3,
		1,
		0,
		2
	};

	private final Bitmap mBitmap;
	private final MySurfaceView mMySurfaceView;
	private final int mHeight;
	private final int mWidth;
	private int mCurrentFrame;
	private int mX;
	private int mXSpeed;
	private int mY;
	private int mYSpeed;

	public Sprite(final MySurfaceView mySurfaceView, final Bitmap bitmap) {
		mMySurfaceView = mySurfaceView;
		mBitmap = bitmap;
		mWidth = mBitmap.getWidth() / COLUMNS;
		mHeight = mBitmap.getHeight() / ROWS;

		final int doubleSpeed = MAX_SPEED + MAX_SPEED;

		mXSpeed = RANDOM.nextInt(doubleSpeed) - MAX_SPEED;
		mYSpeed = RANDOM.nextInt(doubleSpeed) - MAX_SPEED;

		mX = RANDOM.nextInt(mMySurfaceView.getWidth() - mWidth);
		mY = RANDOM.nextInt(mMySurfaceView.getHeight() - mHeight);
	}

	public static Sprite valueOf(final MySurfaceView mySurfaceView, final int resId) {
		final Bitmap bitmap = BitmapFactory.decodeResource(mySurfaceView.getResources(), resId);
		return valueOf(mySurfaceView, bitmap);
	}

	public static Sprite valueOf(final MySurfaceView mySurfaceView, final Bitmap bitmap) {
		return new Sprite(mySurfaceView, bitmap);
	}

	public boolean isCollision(final float x, final float y) {
		final boolean xCollision = x > mX && x < mX + mWidth;
		final boolean yCollision = y > mY && y < mY + mHeight;

		return xCollision && yCollision;
	}

	public void draw(final Canvas canvas) {
		update();
		final int srcX = mCurrentFrame * mWidth;
		final int srcY = getAnimationRow() * mHeight;
		final Rect src = new Rect(srcX, srcY, srcX + mWidth, srcY + mHeight);
		final Rect dst = new Rect(mX, mY, mX + mWidth, mY + mHeight);

		canvas.drawBitmap(mBitmap, src, dst, null);
	}

	private int getAnimationRow() {
		final double dirDouble = StrictMath.atan2(mXSpeed, mYSpeed) / (StrictMath.PI / 2) + 2;
		final int direction = (int) StrictMath.round(dirDouble) % ROWS;
		return DIRECTION_TO_ANIMATION_MAP[direction];
	}

	private void update() {
		if (mX >= mMySurfaceView.getWidth() - mWidth - mXSpeed || mX + mXSpeed <= 0) {
			mXSpeed = -mXSpeed;
		}

		mX += mXSpeed;

		if (mY >= mMySurfaceView.getHeight() - mHeight - mYSpeed || mY + mYSpeed <= 0) {
			mYSpeed = -mYSpeed;
		}

		mY += mYSpeed;
		mCurrentFrame = ++mCurrentFrame % COLUMNS;
	}
}
