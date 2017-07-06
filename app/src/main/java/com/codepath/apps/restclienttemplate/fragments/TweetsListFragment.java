package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TweetAdapter;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetsListFragment extends Fragment {

    private TimelineListener listener;

    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    @BindView(R.id.rvTweet)
    RecyclerView rvTweets;
    @BindView(R.id.scRefresh)
    SwipeRefreshLayout swipeContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tweets_list, container, false);

        // bind views
        ButterKnife.bind(this, v);
        // init the list (data source)
        tweets = new ArrayList<>();
        // construct the adapter from this data source
        tweetAdapter = new TweetAdapter(tweets);
        // RecyclerView setup (layout manager, use adapter)
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvTweets.setLayoutManager(layoutManager);
        // set the adapter
        rvTweets.setAdapter(tweetAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvTweets.getContext(),
                layoutManager.getOrientation());
        rvTweets.addItemDecoration(dividerItemDecoration);

        setHasOptionsMenu(false);

        return v;
    }

    public interface TimelineListener {
        public void onRefresh(boolean b);
    }

    // Store the listener (activity) that will have events fired once the fragment is attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TimelineListener) {
            listener = (TimelineListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    public void processJSONArrayTweets(JSONArray response) {
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
        tweetAdapter.addAll(newTweets);
    }

    public void addTweets(List<Tweet> newTweets, boolean clear) {
        if (clear) {
            tweetAdapter.clear();
        }
        tweetAdapter.addAll(newTweets);
    }

    public void deleteTweets() {
        tweetAdapter.clear();
        for (Tweet tweet : tweets) {
            tweet.delete();
        }
    }

    public void saveTweets() {
        for (Tweet tweet : tweets) {
            tweet.save();
        }
    }

    public long getMaxId() {
        return tweets.get(tweets.size() - 1).id - 1;
    }

    public void showProgressBar() {
        // Show progress item
        listener.onRefresh(true);
    }

    public void hideProgressBar() {
        // Hide progress item
        swipeContainer.setRefreshing(false);
        listener.onRefresh(false);
    }

    public void returnFromCompose(Tweet tweet) {
        tweets.add(0, tweet);
        tweetAdapter.notifyItemInserted(0);
        rvTweets.scrollToPosition(0);
    }

    public void returnFromDetails(Tweet tweet, int position){
        tweets.remove(position);
        tweets.add(position, tweet);
        tweetAdapter.notifyItemChanged(position);
    }

    public void scrollToTop() {
        rvTweets.smoothScrollToPosition(0);
    }
}
