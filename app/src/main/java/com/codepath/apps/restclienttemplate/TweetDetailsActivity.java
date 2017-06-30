package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class TweetDetailsActivity extends AppCompatActivity {

    private final int REQUEST_CODE = 20;

    private TwitterClient client;

    Tweet tweet;
    int position;

    @BindView(R.id.tbMenuDetails)
    Toolbar tbMenuDetails;
    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;
    @BindView(R.id.tvUsername)
    TextView tvUsername;
    @BindView(R.id.tvScreenName) TextView tvScreenName;
    @BindView(R.id.tvBody) TextView tvBody;
    @BindView(R.id.tvDateTime) TextView tvDateTime;
    @BindView(R.id.ivMedia) ImageView ivMedia;
    @BindView(R.id.ibtnReply)
    ImageButton ibtnReply;
    @BindView(R.id.ibtnRetweet) ImageButton ibtnRetweet;
    @BindView(R.id.ibtnFavorite) ImageButton ibtnFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        ButterKnife.bind(this);

        setSupportActionBar(tbMenuDetails);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("Tweet");
        }

        Intent i = getIntent();
        tweet = Parcels.unwrap(i.getParcelableExtra(Tweet.class.getSimpleName()));
        position = i.getIntExtra("position", -1);
        tvUsername.setText(tweet.user.name);
        String screenName = "@" + tweet.user.screenName;
        tvScreenName.setText(screenName);
        tvBody.setText(tweet.body);
        tvDateTime.setText(tweet.dateTime);

        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .into(ivProfileImage);

        if (tweet.mediaUrl != null) {
            Glide.with(this)
                    .load(tweet.mediaUrl)
                    .into(ivMedia);
        } else {
            ivMedia.setImageDrawable(null);
        }

        if (tweet.retweeted) {
            ibtnRetweet.setImageDrawable(ContextCompat.getDrawable(TweetDetailsActivity.this, R.drawable.ic_vector_retweet));
        } else {
            ibtnRetweet.setImageDrawable(ContextCompat.getDrawable(TweetDetailsActivity.this, R.drawable.ic_vector_retweet_stroke));
        }
        if (tweet.favorited) {
            ibtnFavorite.setImageDrawable(ContextCompat.getDrawable(TweetDetailsActivity.this, R.drawable.ic_vector_heart));
        } else {
            ibtnFavorite.setImageDrawable(ContextCompat.getDrawable(TweetDetailsActivity.this, R.drawable.ic_vector_heart_stroke));
        }

        client = TwitterApplication.getRestClient();
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
        intent.putExtra("position", position);
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick(R.id.ibtnReply)
    public void onReply() {
        Intent i = new Intent(TweetDetailsActivity.this, ComposeActivity.class);
        i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
        startActivityForResult(i, REQUEST_CODE);
    }

    @OnClick(R.id.ibtnRetweet)
    public void onRetweet() {
        if (tweet.retweeted) {
            client.unretweet(tweet.id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Toast.makeText(TweetDetailsActivity.this, "unretweet successful", Toast.LENGTH_SHORT).show();
                    ibtnRetweet.setImageDrawable(ContextCompat.getDrawable(TweetDetailsActivity.this, R.drawable.ic_vector_retweet_stroke));
                    tweet.retweeted = false;
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
        } else {
            client.retweet(tweet.id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Toast.makeText(TweetDetailsActivity.this, "retweet successful", Toast.LENGTH_SHORT).show();
                    ibtnRetweet.setImageDrawable(ContextCompat.getDrawable(TweetDetailsActivity.this, R.drawable.ic_vector_retweet));
                    tweet.retweeted = true;
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

    @OnClick(R.id.ibtnFavorite)
    public void onFavorite() {
        if (tweet.favorited) {
            client.unfavorite(tweet.id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Toast.makeText(TweetDetailsActivity.this, "unfavorite successful", Toast.LENGTH_SHORT).show();
                    ibtnFavorite.setImageDrawable(ContextCompat.getDrawable(TweetDetailsActivity.this, R.drawable.ic_vector_heart_stroke));
                    tweet.favorited = false;
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
        } else {
            client.favorite(tweet.id, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Toast.makeText(TweetDetailsActivity.this, "favorite successful", Toast.LENGTH_SHORT).show();
                    ibtnFavorite.setImageDrawable(ContextCompat.getDrawable(TweetDetailsActivity.this, R.drawable.ic_vector_heart));
                    tweet.favorited = true;
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
}
