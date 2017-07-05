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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.fragments.TweetsListFragment;
import com.codepath.apps.restclienttemplate.fragments.TweetsPagerAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements TweetsListFragment.TimelineListener {

    private final int REQUEST_CODE_COMPOSE = 20;
    private final int REQUEST_CODE_DETAILS = 10;

    MenuItem miActionProgress;
    User user;
    TwitterClient client;
    TweetsPagerAdapter tweetsPagerAdapter;

    @BindView(R.id.tbMenuTimeline)
    Toolbar tbMenuTimeline;
    @BindView(R.id.ivUserProfileImage)
    ImageView ivUserProfileImage;
    @BindView(R.id.viewpager) ViewPager vpPager;
    @BindView(R.id.sliding_tabs) TabLayout tabLayout;
    @BindView(R.id.tvTitle)
    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        client = TwitterApplication.getRestClient();

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

        getUser();
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
            vpPager.setCurrentItem(0, true);
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

    public void onProfileView(View v) {
        Intent i = new Intent(MainActivity.this, UserProfileActivity.class);
        i.putExtra(User.class.getSimpleName(), Parcels.wrap(user));
        startActivity(i);
    }

    public void getUser() {
        client.getUser(new RequestParams(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    user = User.fromJSON(response);
                    Glide.with(MainActivity.this)
                            .load(user.profileImageUrl)
                            .transform(new CircleTransform(MainActivity.this))
                            .into(ivUserProfileImage);

                    String text = "@" + user.screenName;
                    tvTitle.setText(text);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
            }
        });
    }
}
