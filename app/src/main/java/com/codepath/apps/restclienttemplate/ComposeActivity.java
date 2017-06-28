package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
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

    @BindView(R.id.etStatus) EditText etStatus;
    @BindView(R.id.tvCharCount)
    TextView tvCharCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApplication.getRestClient();
        // bind views
        ButterKnife.bind(this);

        tvCharCount.setText("140");
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
        client.sendTweet(etStatus.getText().toString(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Tweet tweet = Tweet.fromJSON(response);
                    Intent i = new Intent();
                    i.putExtra("tweet", Parcels.wrap(tweet));
                    setResult(RESULT_OK, i);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("TwitterClient", responseString);
                throwable.printStackTrace();
            }
        });
    }
}
