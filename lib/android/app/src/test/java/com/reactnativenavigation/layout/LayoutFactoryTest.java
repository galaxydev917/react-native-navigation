package com.reactnativenavigation.layout;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.reactnativenavigation.BaseTest;
import com.reactnativenavigation.layout.bottomtabs.BottomTabsLayout;
import com.reactnativenavigation.react.ReactRootViewCreator;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.robolectric.Robolectric;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LayoutFactoryTest extends BaseTest {

	private final static String NODE_ID = "myUniqueId";
	private final static String REACT_ROOT_VIEW_KEY = "myName";

	private final static String OTHER_NODE_ID = "anotherUniqueId";
	private final static String OTHER_REACT_ROOT_VIEW_KEY = "anotherName";

	private Activity activity;
	private View mockView;
	private View otherMockView;
	private ReactRootViewCreator reactRootViewCreator;

	@Before
	public void setUp() {
		activity = Robolectric.buildActivity(AppCompatActivity.class).get();
		mockView = new View(activity);
		otherMockView = new View(activity);
		reactRootViewCreator = mock(ReactRootViewCreator.class);
	}

	@Test
	public void returnsContainerThatHoldsTheRootView() throws Exception {
		when(reactRootViewCreator.create(any(Activity.class), eq(NODE_ID), eq(REACT_ROOT_VIEW_KEY))).thenReturn(mockView);
		final LayoutNode node = createContainerNode();

		final ViewGroup result = (ViewGroup) createLayoutFactory().create(node);

		assertThat(result).isInstanceOf(Container.class);
		TestUtils.assertViewChildren(result, mockView);
	}

	@Test
	public void returnsContainerStack() throws Exception {
		when(reactRootViewCreator.create(any(Activity.class), eq(NODE_ID), eq(REACT_ROOT_VIEW_KEY))).thenReturn(mockView);
		final LayoutNode containerNode = createContainerNode();
		final LayoutNode stackNode = createContainerStackNode(containerNode);

		final ViewGroup result = (ViewGroup) createLayoutFactory().create(stackNode);

		assertThat(result).isInstanceOf(ContainerStackLayout.class);
		ViewGroup container = (ViewGroup) TestUtils.assertViewChildrenCount(result, 1).get(0);
		TestUtils.assertViewChildren(container, mockView);
	}

	@Test
	public void returnsContainerStackWithMultipleViews() throws Exception {
		final View mockView1 = mock(View.class);
		final View mockView2 = mock(View.class);
		when(reactRootViewCreator.create(any(Activity.class), eq(NODE_ID), eq(REACT_ROOT_VIEW_KEY))).thenReturn(mockView1);
		when(reactRootViewCreator.create(any(Activity.class), eq(OTHER_NODE_ID), eq(OTHER_REACT_ROOT_VIEW_KEY))).thenReturn(mockView2);

		final LayoutNode containerNode1 = createContainerNode(NODE_ID, REACT_ROOT_VIEW_KEY);
		final LayoutNode containerNode2 = createContainerNode(OTHER_NODE_ID, OTHER_REACT_ROOT_VIEW_KEY);
		final LayoutNode stackNode = createContainerStackNode(containerNode1, containerNode2);

		final ViewGroup result = (ViewGroup) createLayoutFactory().create(stackNode);

		assertThat(result).isInstanceOf(ContainerStackLayout.class);
		List<View> containers = TestUtils.assertViewChildrenCount(result, 2);
		ViewGroup container1 = (ViewGroup) containers.get(0);
		ViewGroup container2 = (ViewGroup) containers.get(1);
		TestUtils.assertViewChildren(container1, mockView1);
		TestUtils.assertViewChildren(container2, mockView2);
	}

	@Test
	public void returnsSideMenuRoot() throws Exception {
		when(reactRootViewCreator.create(any(Activity.class), eq(NODE_ID), eq(REACT_ROOT_VIEW_KEY))).thenReturn(mockView);
		final LayoutNode containerNode = createSideMenuContainerNode(Arrays.asList(createContainerNode()));
		final ViewGroup result = (ViewGroup) createLayoutFactory().create(containerNode);
		assertThat(result).isInstanceOf(SideMenuLayout.class);
	}

	@Test
	public void hasContentContainer() throws Exception {
		when(reactRootViewCreator.create(any(Activity.class), eq(NODE_ID), eq(REACT_ROOT_VIEW_KEY))).thenReturn(mockView);
		LayoutNode contentContainer = createContainerNode();
		final LayoutNode sideMenu = createSideMenuContainerNode(Arrays.asList(contentContainer));
		final ViewGroup result = (ViewGroup) createLayoutFactory().create(sideMenu);
		assertThat(result.getChildAt(0)).isInstanceOf(Container.class);
	}

	@Test
	public void hasLeftMenu() throws Exception {
		when(reactRootViewCreator.create(any(Activity.class), eq(NODE_ID), eq(REACT_ROOT_VIEW_KEY))).thenReturn(mockView);
		LayoutNode sideMenuLeft = createSideMenuLeftNode();
		final LayoutNode sideMenu = createSideMenuContainerNode(Arrays.asList(sideMenuLeft));
		final ViewGroup result = (ViewGroup) createLayoutFactory().create(sideMenu);
		assertThat(result.getChildAt(0)).isInstanceOf(Container.class);
	}

	@Test
	public void hasRightMenu() throws Exception {
		when(reactRootViewCreator.create(any(Activity.class), eq(NODE_ID), eq(REACT_ROOT_VIEW_KEY))).thenReturn(mockView);
		LayoutNode sideMenuRight = createSideMenuRightNode();
		final LayoutNode sideMenu = createSideMenuContainerNode(Arrays.asList(sideMenuRight));
		final ViewGroup result = (ViewGroup) createLayoutFactory().create(sideMenu);
		assertThat(result.getChildAt(0)).isInstanceOf(Container.class);
	}

	@Test
	public void pushScreenToScreenStackLayout() throws Exception {
		when(reactRootViewCreator.create(any(Activity.class), eq(NODE_ID), eq(REACT_ROOT_VIEW_KEY))).thenReturn(mockView);
		final LayoutNode container = createContainerNode();
		final LayoutNode stackNode = createContainerStackNode(container);
		final ContainerStackLayout containerStackLayout = (ContainerStackLayout) createLayoutFactory().create(stackNode);

		when(reactRootViewCreator.create(any(Activity.class), eq(OTHER_NODE_ID), eq(OTHER_REACT_ROOT_VIEW_KEY))).thenReturn(otherMockView);
		final LayoutNode pushedContainer = createContainerNode(OTHER_NODE_ID, OTHER_REACT_ROOT_VIEW_KEY);
		containerStackLayout.push(createLayoutFactory().create(pushedContainer));

		ViewGroup result = (ViewGroup) TestUtils.assertViewChildrenCount(containerStackLayout, 1).get(0);
		assertThat(result.getChildAt(0)).isEqualTo(otherMockView);
	}

	@Test
	public void pushTwoScreensToStackLayout() throws Exception {
		when(reactRootViewCreator.create(any(Activity.class), eq(NODE_ID), eq(REACT_ROOT_VIEW_KEY))).thenReturn(mockView);
		final LayoutNode container = createContainerNode();
		final LayoutNode stackNode = createContainerStackNode(container);
		final ContainerStackLayout containerStackLayout = (ContainerStackLayout) createLayoutFactory().create(stackNode);

		View first = new View(activity);
		pushContainer(containerStackLayout, OTHER_NODE_ID, OTHER_REACT_ROOT_VIEW_KEY, first);

		View second = new View(activity);
		pushContainer(containerStackLayout, "secondPushedScreenId", "secondPushedScreenKey", second);

		ViewGroup result = (ViewGroup) TestUtils.assertViewChildrenCount(containerStackLayout, 1).get(0);
		assertThat(result.getChildAt(0)).isEqualTo(second);
	}

	@Test
	public void popTwoScreensFromStackLayout() throws Exception {
		when(reactRootViewCreator.create(any(Activity.class), eq(NODE_ID), eq(REACT_ROOT_VIEW_KEY))).thenReturn(mockView);
		final LayoutNode container = createContainerNode();
		final LayoutNode stackNode = createContainerStackNode(container);
		final ContainerStackLayout containerStackLayout = (ContainerStackLayout) createLayoutFactory().create(stackNode);

		pushContainer(containerStackLayout, OTHER_NODE_ID, OTHER_REACT_ROOT_VIEW_KEY, new View(activity));
		pushContainer(containerStackLayout, "secondPushedScreenId", "secondPushedScreenKey", new View(activity));

		containerStackLayout.pop();
		containerStackLayout.pop();

		ViewGroup result = (ViewGroup) TestUtils.assertViewChildrenCount(containerStackLayout, 1).get(0);
		assertThat(result.getChildAt(0)).isEqualTo(mockView);
	}

	private void pushContainer(ContainerStackLayout containerStackLayout, String screenId, String reactRootViewKey, View rootView) throws Exception {
		when(reactRootViewCreator.create(any(Activity.class), eq(screenId), eq(reactRootViewKey))).thenReturn(rootView);
		View pushedContainer = createLayoutFactory().create(createContainerNode(screenId, reactRootViewKey));
		containerStackLayout.push(pushedContainer);
	}

	private void pushContainer(BottomTabsLayout containerStackLayout, String screenId, String reactRootViewKey, View rootView) throws Exception {
		when(reactRootViewCreator.create(any(Activity.class), eq(screenId), eq(reactRootViewKey))).thenReturn(rootView);
		View pushedContainer = createLayoutFactory().create(createContainerNode(screenId, reactRootViewKey));
		containerStackLayout.push(pushedContainer);
	}

	@Test
	public void popScreenFromScreenStackLayout() throws Exception {
		when(reactRootViewCreator.create(any(Activity.class), eq(NODE_ID), eq(REACT_ROOT_VIEW_KEY))).thenReturn(mockView);
		final LayoutNode container = createContainerNode();
		final LayoutNode stackNode = createContainerStackNode(container);
		final ContainerStackLayout containerStackLayout = (ContainerStackLayout) createLayoutFactory().create(stackNode);

		when(reactRootViewCreator.create(any(Activity.class), eq(OTHER_NODE_ID), eq(OTHER_REACT_ROOT_VIEW_KEY))).thenReturn(otherMockView);
		final LayoutNode pushedContainer = createContainerNode(OTHER_NODE_ID, OTHER_REACT_ROOT_VIEW_KEY);
		containerStackLayout.push(createLayoutFactory().create(pushedContainer));

		containerStackLayout.pop();
		ViewGroup result = (ViewGroup) TestUtils.assertViewChildrenCount(containerStackLayout, 1).get(0);
		assertThat(result.getChildAt(0)).isEqualTo(mockView);
	}

	@Test(expected = IllegalArgumentException.class)
	@Ignore
	public void throwsExceptionForUnknownType() throws Exception {
		when(reactRootViewCreator.create(any(Activity.class), eq(NODE_ID), eq(REACT_ROOT_VIEW_KEY))).thenReturn(mockView);
		final LayoutNode node = new LayoutNode(NODE_ID, null, new JSONObject(), Collections.<LayoutNode>emptyList());

		createLayoutFactory().create(node);
	}

	private LayoutFactory createLayoutFactory() {
		return new LayoutFactory(activity, reactRootViewCreator);
	}

	private LayoutNode createContainerNode() throws Exception {
		return createContainerNode(NODE_ID, REACT_ROOT_VIEW_KEY);
	}

	private LayoutNode createSideMenuLeftNode() throws Exception {
		List<LayoutNode> children = Arrays.asList(createContainerNode());
		return new LayoutNode("SideMenuLeft", LayoutNode.Type.SideMenuLeft, null, children);
	}

	private LayoutNode createSideMenuRightNode() throws Exception {
		List<LayoutNode> children = Arrays.asList(createContainerNode());
		return new LayoutNode("SideMenuRight", LayoutNode.Type.SideMenuRight, null, children);
	}

	private LayoutNode createContainerNode(final String id, final String name) throws JSONException {
		return new LayoutNode(id, LayoutNode.Type.Container, new JSONObject().put("name", name), null);
	}

	private LayoutNode createSideMenuContainerNode(List<LayoutNode> children) {
		return new LayoutNode("SideMenuRoot", LayoutNode.Type.SideMenuRoot, null, children);
	}

	private LayoutNode createContainerStackNode(LayoutNode... children) {
		return new LayoutNode("ContainerStack", LayoutNode.Type.ContainerStack, null, Arrays.asList(children));
	}

	private LayoutNode createBottomTabNode(LayoutNode... children) {
		return new LayoutNode("BottomTabs", LayoutNode.Type.BottomTabs, null, Arrays.asList(children));
	}
}