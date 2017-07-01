package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity {

    private TwitterClient client;
    private EndlessRecyclerViewScrollListener scrollListener;
    private final int REQUEST_CODE_COMPOSE = 20;
    private final int REQUEST_CODE_DETAILS = 10;

    private MenuItem miActionProgress;

    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    @BindView(R.id.rvTweet)
    RecyclerView rvTweets;
    @BindView(R.id.tbMenuTimeline)
    Toolbar tbMenuTimeline;
    @BindView(R.id.scRefresh)
    SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApplication.getRestClient();

        // bind views
        ButterKnife.bind(this);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        // set the toolbar to act as the action bar for this activity
        setSupportActionBar(tbMenuTimeline);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("");
        }
        // init the list (data source)
        tweets = new ArrayList<>();
        // construct the adapter from this data source
        tweetAdapter = new TweetAdapter(tweets);
        // RecyclerView setup (layout manager, use adapter)
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(layoutManager);
        // set the adapter
        rvTweets.setAdapter(tweetAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvTweets.getContext(),
                layoutManager.getOrientation());
        rvTweets.addItemDecoration(dividerItemDecoration);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                fetchTimelineAsync(true);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvTweets.addOnScrollListener(scrollListener);

        fetchTimelineAsync(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
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

    public void showProgressBar() {
        // Show progress item
        miActionProgress.setVisible(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        miActionProgress.setVisible(false);
    }

    public void onComposeAction(MenuItem mi) {
        Intent i = new Intent(TimelineActivity.this, ComposeActivity.class);
        startActivityForResult(i, REQUEST_CODE_COMPOSE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_COMPOSE) {
            // Extract name value from result extras
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            tweets.add(0, tweet);
            tweetAdapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_DETAILS) {
            // Extract name value from result extras
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            int position = data.getIntExtra("position", -1);
            tweets.remove(position);
            tweets.add(position, tweet);
            tweetAdapter.notifyItemChanged(position);
        }
    }

    private void fetchTimelineAsync(final boolean loadMore) {
        if (miActionProgress != null) {
            showProgressBar();
        }
        RequestParams params = new RequestParams();
        if (loadMore) {
            params.put("max_id", tweets.get(tweets.size() - 1).id - 1);
        }
        params.put("since_id", 1);
        client.getHomeTimeline(params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("TwitterClient", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Log.d("TwitterClient", response.toString());
                // Remember to CLEAR OUT old items before appending in the new ones
                if (!loadMore) {
                    tweetAdapter.clear();
                    for (Tweet tweet : tweets) {
                        tweet.delete();
                    }
                }
                // ...the data has come back, add new items to your adapter...
                List<Tweet> list = processJSONArrayTweets(response);
                tweetAdapter.addAll(list);

                if(!loadMore) {
                    for (Tweet tweet : list) {
                        tweet.save();
                    }
                }

                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
                if (miActionProgress != null) {
                    hideProgressBar();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                // Log.d("TwitterClient", responseString);
                if (!loadMore) {
                    tweets.clear();
                    tweetAdapter.addAll(SQLite.select().
                            from(Tweet.class).
                            queryList());
                }
                swipeContainer.setRefreshing(false);
                if (miActionProgress != null) {
                    hideProgressBar();
                }
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                // Log.d("TwitterClient", errorResponse.toString());
                if (!loadMore) {
                    tweets.clear();
                    tweetAdapter.addAll(SQLite.select().
                            from(Tweet.class).
                            queryList());
                }
                swipeContainer.setRefreshing(false);
                if (miActionProgress != null) {
                    hideProgressBar();
                }
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                // Log.d("TwitterClient", errorResponse.toString());
                if (!loadMore) {
                    tweets.clear();
                    tweetAdapter.addAll(SQLite.select().
                            from(Tweet.class).
                            queryList());
                }
                swipeContainer.setRefreshing(false);
                if (miActionProgress != null) {
                    hideProgressBar();
                }
                throwable.printStackTrace();
            }
        });
    }

    private List<Tweet> processJSONArrayTweets(JSONArray response) {
        ArrayList<Tweet> newTweets = new ArrayList<>();
        // iterate through JSONArray
        // for each entry, deserialize the JSON object
        for (int i = 0; i < response.length(); i++) {
            // convert each object to a Tweet model
            // add that Tweet model to our data source
            // notify the adapter that we've added an item
            try {
                Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                newTweets.add(tweet);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return newTweets;
    }
}
