package com.reactnativenavigation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.reactnativenavigation.viewcontrollers.Navigator;

public class NavigationActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler {
	private Navigator navigator;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app().getReactGateway().onActivityCreated(this);
		navigator = new Navigator(this);
		setContentView(navigator.getView());
	}

	@Override
	protected void onResume() {
		super.onResume();
		app().getReactGateway().onActivityResumed(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		app().getReactGateway().onActivityPaused(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		app().getReactGateway().onActivityDestroyed(this);
	}

	@Override
	public void invokeDefaultOnBackPressed() {
		onBackPressed();
	}

	@Override
	public void onBackPressed() {
		if (!navigator.handleBack()) {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onKeyUp(final int keyCode, final KeyEvent event) {
		return app().getReactGateway().onKeyUp(keyCode) || super.onKeyUp(keyCode, event);
	}

	private NavigationApplication app() {
		return (NavigationApplication) getApplication();
	}

	public Navigator getNavigator() {
		return navigator;
	}

	public void toast(final String text) {
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}
}
