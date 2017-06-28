package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cz.msebera.android.httpclient.Header;

public class ComposeActivity extends AppCompatActivity {

    private TwitterClient client;

    private MenuItem miActionProgress;

    @BindView(R.id.etStatus) EditText etStatus;
    @BindView(R.id.tvCharCount)
    TextView tvCharCount;
    @BindView(R.id.tbMenuCompose)
    Toolbar tbMenuCompose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApplication.getRestClient();
        // bind views
        ButterKnife.bind(this);

        setSupportActionBar(tbMenuCompose);
        getSupportActionBar().setTitle("Compose tweet");
        tvCharCount.setText("140");
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

    @OnTextChanged(value = R.id.etStatus,
            callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void charCountUpdate() {
        String status = etStatus.getText().toString();
        Integer statusLength = status.length();
        tvCharCount.setText(String.valueOf(140 - statusLength));
    }

    @OnClick(R.id.btnTweet)
    public void sendTweet() {
        if (miActionProgress != null) {
            showProgressBar();
        }
        client.sendTweet(etStatus.getText().toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Tweet tweet = Tweet.fromJSON(response);
                    Intent i = new Intent();
                    i.putExtra("tweet", Parcels.wrap(tweet));
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
