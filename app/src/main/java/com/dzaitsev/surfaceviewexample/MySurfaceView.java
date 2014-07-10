package com.dzaitsev.surfaceviewexample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by d.zaitsev on 09.07.2014.
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
	public static final long CLICK_DELAY = 100L;
	private DrawerThread mDrawerThread;
	private List<Sprite> mSprites;
	private long mLastClick;

	public MySurfaceView(final Context context) {
		this(context, null);
	}

	public MySurfaceView(final Context context, final AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MySurfaceView(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		mDrawerThread = new DrawerThread(this);
		getHolder().addCallback(this);
	}

	@Override
	public final boolean onTouchEvent(final MotionEvent event) {
		if (System.currentTimeMillis() - mLastClick > CLICK_DELAY) {
			mLastClick = System.currentTimeMillis();
			synchronized (getHolder()) {
				for (int i = mSprites.size() - 1; i >= 0; i--) {
					final Sprite sprite = mSprites.get(i);

					if (sprite.isCollision(event.getX(), event.getY())) {
						mSprites.remove(sprite);
						break;
					}
				}
			}
			return super.onTouchEvent(event);
		}

		return false;
	}

	@Override
	public final void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {}

	@Override
	public void draw(final Canvas canvas) {
		if (canvas != null && mSprites != null) {
			canvas.drawColor(Color.WHITE);
			for (final Sprite sprite : mSprites) {
				sprite.draw(canvas);
			}
		}
	}

	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		createSprites();

		if (mDrawerThread.getState() == Thread.State.NEW) {
			mDrawerThread.setRunning(true);
			mDrawerThread.start();
		} else if (mDrawerThread.getState() == Thread.State.TERMINATED) {
			mDrawerThread.interrupt();
			mDrawerThread = new DrawerThread(this);
			mDrawerThread.setRunning(true);
			mDrawerThread.start();
		}
	}

	@Override
	public void surfaceDestroyed(final SurfaceHolder holder) {
		mDrawerThread.setRunning(false);

		boolean retry = true;
		while (retry) {
			try {
				mDrawerThread.join();
				retry = false;
			} catch (final InterruptedException ignored) {}
		}
	}

	private void createSprites() {
		mSprites = new LinkedList<Sprite>();

		mSprites.add(Sprite.valueOf(this, R.drawable.sprite01));
		mSprites.add(Sprite.valueOf(this, R.drawable.sprite02));
		mSprites.add(Sprite.valueOf(this, R.drawable.sprite03));
		mSprites.add(Sprite.valueOf(this, R.drawable.sprite04));
		mSprites.add(Sprite.valueOf(this, R.drawable.sprite05));
		mSprites.add(Sprite.valueOf(this, R.drawable.sprite06));
	}
}
