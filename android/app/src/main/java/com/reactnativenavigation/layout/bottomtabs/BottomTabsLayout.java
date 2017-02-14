package com.reactnativenavigation.layout.bottomtabs;

import android.app.Activity;
import android.view.View;
import android.widget.RelativeLayout;

import com.reactnativenavigation.layout.StackLayout;

import java.util.ArrayList;
import java.util.List;

public class BottomTabsLayout extends RelativeLayout implements BottomTabs.BottomTabsSelectionListener, StackLayout {

    private List<StackLayout> tabsContent;
    private BottomTabs bottomTabs;
    private int currentTab;

    public BottomTabsLayout(Activity activity, BottomTabs bottomTabs) {
        super(activity);
        initBottomTabs(bottomTabs);
    }

    public void addTabContent(String label, View tabContent) {
        if (tabsContent.size() == 5) {
            throw new TooManyTabsException();
        }
        bottomTabs.add(label);
        attachTabContent(tabContent);
        tabsContent.add((StackLayout) tabContent);

        if (tabsContent.size() > 1) {
            tabContent.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTabSelected(int index) {
        hideTab(currentTab);
        currentTab = index;
        showTab(currentTab);
    }

    private void initBottomTabs(BottomTabs bottomTabs) {
        this.bottomTabs = bottomTabs;
        this.bottomTabs.attach(this);
        this.bottomTabs.setSelectionListener(this);

        tabsContent = new ArrayList<>();
    }

    private void attachTabContent(View tabContent) {
        LayoutParams tabParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        tabParams.addRule(ABOVE, bottomTabs.getViewId());
        addView(tabContent, tabParams);
    }

    private void showTab(int tabId) {
        tabsContent.get(tabId).asView().setVisibility(View.VISIBLE);
    }

    private void hideTab(int tabId) {
        tabsContent.get(tabId).asView().setVisibility(View.GONE);
    }

    @Override
    public void push(View view) {
        tabsContent.get(currentTab).push(view);
    }

    @Override
    public void pop() {
        tabsContent.get(currentTab).pop();
    }

    @Override
    public View asView() {
        return this;
    }
}
