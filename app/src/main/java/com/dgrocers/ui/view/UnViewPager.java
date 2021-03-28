package com.dgrocers.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class UnViewPager extends ViewPager {

	public UnViewPager(Context context) {
		super(context);
	}

	public UnViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}

}