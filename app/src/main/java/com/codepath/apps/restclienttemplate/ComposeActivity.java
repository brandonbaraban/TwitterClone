package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    private TwitterClient client;

    private Tweet mTweet;

    private MenuItem miActionProgress;

    @BindView(R.id.etStatus) EditText etStatus;
    @BindView(R.id.tbMenuCompose)
    Toolbar tbMenuCompose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        mTweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));

        client = TwitterApplication.getRestClient();
        // bind views
        ButterKnife.bind(this);

        setSupportActionBar(tbMenuCompose);
        ActionBar ab = getSupportActionBar();
        if (mTweet != null) {
            String reply = "@" + mTweet.user.screenName + " ";
            etStatus.setText(reply);
            etStatus.setSelection(reply.length());
            if (ab != null) {
                ab.setTitle("Reply to " + reply);
            }
        } else {
            if (ab != null) {
                ab.setTitle("Compose tweet");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose, menu);
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

    @OnClick(R.id.btnTweet)
    public void sendTweet() {
        if (miActionProgress != null) {
            showProgressBar();
        }
        RequestParams params = new RequestParams();
        params.put("status", etStatus.getText().toString());
        if (mTweet != null) {
            params.put("in_reply_to_status_id", mTweet.id);
        }
        client.sendTweet(params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Tweet tweet = Tweet.fromJSON(response);
                    Intent i = new Intent();
                    i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    setResult(RESULT_OK, i);
                    if (miActionProgress != null) {
                        hideProgressBar();
                    }
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (miActionProgress != null) {
                        hideProgressBar();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
                if (miActionProgress != null) {
                    hideProgressBar();
                }
            }
        });
    }
}
