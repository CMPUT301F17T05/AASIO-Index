package com.cmput301.t05.habilect;

import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Gravity;
import android.widget.Button;

import com.robotium.solo.Solo;

import static com.cmput301.t05.habilect.R.id.navigationHomeDrawer;

/**
 * @author ioltuszy
 */

public class NavigationTest extends ActivityInstrumentationTestCase2<HomeActivity> {
    private Solo solo;

    public NavigationTest() {
        super(HomeActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testNavigateActivities()
    {
        solo.waitForActivity("HomeActivity", 1000);
        solo.assertCurrentActivity("wrong activity", HomeActivity.class);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ((DrawerLayout) solo.getView(navigationHomeDrawer)).openDrawer(Gravity.START);

            }
        });
        solo.sleep(1000);
        assertTrue(((DrawerLayout) solo.getView(navigationHomeDrawer)).isDrawerVisible(Gravity.START));
        solo.clickOnView(((DrawerLayout) solo.getView(navigationHomeDrawer)).findViewById(R.id.navigateHistory));
        solo.assertCurrentActivity("wrong activity", HistoryActivity.class);
        solo.goBack();
        solo.assertCurrentActivity("wrong activity", HomeActivity.class);
        solo.clickOnView(((DrawerLayout) solo.getView(navigationHomeDrawer)).findViewById(R.id.navigateSocial));
        solo.assertCurrentActivity("wrong activity", SocialActivity.class);
        solo.assertCurrentActivity("wrong activity", SocialActivity.class);
        solo.goBack();
        solo.assertCurrentActivity("wrong activity", HomeActivity.class);
        solo.clickOnView(((DrawerLayout) solo.getView(navigationHomeDrawer)).findViewById(R.id.navigateProfile));
        solo.assertCurrentActivity("wrong activity", ProfileActivity.class);
        solo.goBack();
        solo.assertCurrentActivity("wrong activity", HomeActivity.class);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ((DrawerLayout) solo.getView(navigationHomeDrawer)).closeDrawer(Gravity.START);
            }
        });
        solo.sleep(1000);
        assertFalse(((DrawerLayout) solo.getView(navigationHomeDrawer)).isDrawerVisible(Gravity.START));
    }

    public void testNavigatePages()
    {
        solo.waitForActivity("HomeActivity", 1000);
        solo.assertCurrentActivity("wrong activity", HomeActivity.class);
        assertEquals(((ViewPager)solo.getView(R.id.homeViewPager)).getCurrentItem(),0);
        solo.scrollViewToSide(solo.getView(R.id.homeViewPager), Solo.RIGHT);
        assertEquals(((ViewPager)solo.getView(R.id.homeViewPager)).getCurrentItem(),1);
        solo.scrollViewToSide(solo.getView(R.id.homeViewPager), Solo.RIGHT);
        assertEquals(((ViewPager)solo.getView(R.id.homeViewPager)).getCurrentItem(),1);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ((DrawerLayout) solo.getView(navigationHomeDrawer)).openDrawer(Gravity.START);

            }
        });

        solo.clickOnView(((DrawerLayout) solo.getView(navigationHomeDrawer)).findViewById(R.id.navigateHistory));
        solo.assertCurrentActivity("wrong activity", HistoryActivity.class);
        assertEquals(((ViewPager)solo.getView(R.id.historyViewPager)).getCurrentItem(),0);
        solo.scrollViewToSide(solo.getView(R.id.historyViewPager), Solo.RIGHT);
        assertEquals(((ViewPager)solo.getView(R.id.historyViewPager)).getCurrentItem(),1);
        solo.scrollViewToSide(solo.getView(R.id.historyViewPager), Solo.RIGHT);
        assertEquals(((ViewPager)solo.getView(R.id.historyViewPager)).getCurrentItem(),1);
    }

    public void testNavigateDialogs() {
        solo.waitForActivity("HomeActivity", 1000);
        solo.assertCurrentActivity("wrong activity", HomeActivity.class);
        solo.waitForView(solo.getCurrentActivity().findViewById(R.layout.dialog_viewhabit));
        solo.clickOnView(solo.getView(R.id.followRequestButton));
        solo.goBack();
        solo.goBack();
        solo.clickOnView(((Button) solo.getCurrentActivity().findViewById(R.id.addHabitButton)));
        solo.goBack();

        solo.sleep(1000);
        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ((DrawerLayout) solo.getView(navigationHomeDrawer)).openDrawer(Gravity.START);

            }
        });
        solo.clickOnView(((DrawerLayout) solo.getView(navigationHomeDrawer)).findViewById(R.id.navigateHistory));
        solo.assertCurrentActivity("wrong activity", HistoryActivity.class);
        solo.sleep(1000);
        solo.waitForView(solo.getCurrentActivity().findViewById(R.layout.dialog_viewhabit));
        solo.clickOnView(solo.getView(R.id.followRequestButton));
        solo.goBack();
        solo.goBack();
        solo.goBack();
        solo.assertCurrentActivity("wrong activity", HomeActivity.class);

        solo.clickOnView(((DrawerLayout) solo.getView(navigationHomeDrawer)).findViewById(R.id.navigateSocial));
        solo.assertCurrentActivity("wrong activity", SocialActivity.class);
        solo.sleep(1000);
        solo.goBack();
        solo.assertCurrentActivity("wrong activity", HomeActivity.class);

        solo.clickOnView(((DrawerLayout) solo.getView(navigationHomeDrawer)).findViewById(R.id.navigateProfile));
        solo.assertCurrentActivity("wrong activity", ProfileActivity.class);
        solo.sleep(1000);
        solo.goBack();
        solo.assertCurrentActivity("wrong activity", HomeActivity.class);

    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
