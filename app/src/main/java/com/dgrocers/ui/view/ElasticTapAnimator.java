package com.dgrocers.ui.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_UP;

public class ElasticTapAnimator {

	private final WeakReference<View> view;

	private ElasticTapAnimator(@NotNull View view) {
		this.view = new WeakReference<>(view);
		this.explode();
	}

	public static void animate(@NotNull View view) {
		new ElasticTapAnimator(view);
	}

	@SuppressLint("ClickableViewAccessibility")
	private void explode() {
		if (view.get() != null)
			view.get().setOnTouchListener((v, event) -> {
				switch (event.getAction()) {
					case ACTION_DOWN:
						animate(v, 0);
						break;
					case ACTION_CANCEL:
						v.animate().cancel();
						animate(v, 1);
						return true;
					case ACTION_UP:
						v.animate().cancel();
						animate(v, 2);
						animate(v, 3);
						return false;

				}
				return false;
			});
	}

	private void animate(View view, int sequence) {
		long delay = 0;
		long duration = 0;
		float zoom_x = 0.0F;
		float zoom_y = 0.0F;
		switch (sequence) {
			case 0:
			case 2:
				zoom_x = 0.95F;
				zoom_y = 0.95F;
				duration = 75;
				delay = 0;
				break;
			case 1:
				zoom_x = 1.0F;
				zoom_y = 1.0F;
				duration = 75;
				delay = 0;
				break;
			case 3:
				zoom_x = 1.0F;
				zoom_y = 1.0F;
				duration = 75;
				delay = 50;
				break;
		}

		ObjectAnimator scale_x = ObjectAnimator.ofFloat(view, "scaleX", zoom_x);
		ObjectAnimator scale_y = ObjectAnimator.ofFloat(view, "scaleY", zoom_y);

		AnimatorSet animatorSet = new AnimatorSet();
		scale_x.setDuration(duration);
		scale_y.setDuration(duration);

		animatorSet.playTogether(scale_x, scale_y);
		animatorSet.setStartDelay(delay);
		animatorSet.start();
	}
}
