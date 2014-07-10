package com.dzaitsev.surfaceviewexample;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by d.zaitsev on 09.07.2014.
 */
public class DrawerThread extends Thread {
	private final MySurfaceView mMySurfaceView;
	private boolean mRunning;
	private static final long FPS = 60;

	public DrawerThread(final MySurfaceView view) {
		mMySurfaceView = view;
	}

	public void setRunning(final boolean running) {
		mRunning = running;
	}

	@Override
	public void run() {
		final long ticksPs = 1000 / FPS;

		while (mRunning && !interrupted()) {
			final long startTime = System.currentTimeMillis();
			final SurfaceHolder surfaceHolder = mMySurfaceView.getHolder();

			Canvas canvas = null;
			try {
				canvas = surfaceHolder.lockCanvas();
				synchronized (surfaceHolder) {
					mMySurfaceView.draw(canvas);
				}
			} finally {
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}

			final long sleepTime = ticksPs - (System.currentTimeMillis() - startTime);

			try {
				if (sleepTime > 0) {
					sleep(sleepTime);
				}
			} catch (final InterruptedException ignored) {
			}
		}
	}
}
