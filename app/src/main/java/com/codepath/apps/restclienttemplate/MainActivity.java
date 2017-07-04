package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.codepath.apps.restclienttemplate.fragments.TweetsListFragment;
import com.codepath.apps.restclienttemplate.fragments.TweetsPagerAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements TweetsListFragment.HomeTimelineListener {

    private final int REQUEST_CODE_COMPOSE = 20;
    private final int REQUEST_CODE_DETAILS = 10;

    MenuItem miActionProgress;
    TweetsPagerAdapter tweetsPagerAdapter;

    @BindView(R.id.tbMenuTimeline)
    Toolbar tbMenuTimeline;
    @BindView(R.id.viewpager) ViewPager vpPager;
    @BindView(R.id.sliding_tabs) TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        // set the toolbar to act as the action bar for this activity
        setSupportActionBar(tbMenuTimeline);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("");
        }

        tweetsPagerAdapter = new TweetsPagerAdapter(getSupportFragmentManager(), this);
        vpPager.setAdapter(tweetsPagerAdapter);
        tabLayout.setupWithViewPager(vpPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Store instance of the menu item containing progress
        miActionProgress = menu.findItem(R.id.miActionProgress);
        // Extract the action-view from the menu item
        ProgressBar v = (ProgressBar) MenuItemCompat.getActionView(miActionProgress);
        // Return to finish
        return super.onPrepareOptionsMenu(menu);
    }

    public void onComposeAction(View v) {
        Intent i = new Intent(MainActivity.this, ComposeActivity.class);
        startActivityForResult(i, REQUEST_CODE_COMPOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_COMPOSE) {
            // Extract name value from result extras
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            ((TweetsListFragment) tweetsPagerAdapter.getCurrentFragment()).returnFromCompose(tweet);
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_DETAILS) {
            // Extract name value from result extras
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            int position = data.getIntExtra("position", -1);
            ((TweetsListFragment) tweetsPagerAdapter.getCurrentFragment()).returnFromDetails(tweet, position);
        }
    }


    @Override
    public void onRefresh(boolean b) {
        if (miActionProgress != null) {
            miActionProgress.setVisible(b);
        }
    }
}
