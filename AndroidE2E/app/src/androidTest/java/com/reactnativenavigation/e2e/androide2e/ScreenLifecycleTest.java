package com.reactnativenavigation.e2e.androide2e;

import android.support.test.uiautomator.By;

import org.junit.Ignore;
import org.junit.Test;

public class ScreenLifecycleTest extends BaseTest {
	@Ignore
	@Test
	public void onStartOnStop() throws Exception {
		launchTheApp();
		assertMainShown();
		elementByText("PUSH LIFECYCLE SCREEN").click();
		assertExists(By.text("onStart"));
		elementByText("PUSH TO TEST ONSTOP").click();
		assertExists(By.text("onStop"));
	}

	@Ignore
	@Test
	public void unmountIsCalledWhenPopped() throws Exception {
		launchTheApp();
		assertMainShown();
		elementByText("PUSH LIFECYCLE SCREEN").click();
		assertExists(By.text("onStart"));

		device().pressBack();

		assertExists(By.text("componentWillUnmount"));
		elementByText("OK").click();
		assertExists(By.text("onStop"));
		elementByText("OK").click();
		assertMainShown();
	}
}
