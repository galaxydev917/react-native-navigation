package com.reactnativenavigation.mocks;

import android.content.Context;
import android.view.View;

import com.reactnativenavigation.viewcontrollers.ContainerViewController;

public class TestContainerView extends View implements ContainerViewController.ContainerView {
	public TestContainerView(final Context context) {
		super(context);
	}

	@Override
	public boolean isReady() {
		return false;
	}

	@Override
	public View asView() {
		return this;
	}

	@Override
	public void destroy() {

	}

	@Override
	public void sendContainerStart() {

	}

	@Override
	public void sendContainerStop() {

	}
}
