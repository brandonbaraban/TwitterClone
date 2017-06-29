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
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

public class TweetDetailsActivity extends AppCompatActivity {

    private final int REQUEST_CODE = 20;

    private TwitterClient client;

    Tweet tweet;

    @BindView(R.id.tbMenuDetails)
    Toolbar tbMenuDetails;
    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;
    @BindView(R.id.tvUsername)
    TextView tvUsername;
    @BindView(R.id.tvScreenName) TextView tvScreenName;
    @BindView(R.id.tvBody) TextView tvBody;
    @BindView(R.id.tvDateTime) TextView tvDateTime;
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

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(Tweet.class.getSimpleName()));
        tvUsername.setText(tweet.user.name);
        String screenName = "@" + tweet.user.screenName;
        tvScreenName.setText(screenName);
        tvBody.setText(tweet.body);
        tvDateTime.setText(tweet.dateTime);

        Glide.with(this)
                .load(tweet.user.profileImageUrl)
                .into(ivProfileImage);

        client = TwitterApplication.getRestClient();
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
            client.unretweet(tweet.uid, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(TweetDetailsActivity.this, "unRetweet successful", Toast.LENGTH_SHORT).show();
                    ibtnRetweet.setImageDrawable(ContextCompat.getDrawable(TweetDetailsActivity.this, R.drawable.ic_vector_retweet_stroke));
                    tweet.retweeted = false;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(TweetDetailsActivity.this, "unRetweet unsuccessful", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            });
        } else {
            client.retweet(tweet.uid, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(TweetDetailsActivity.this, "Retweet successful", Toast.LENGTH_SHORT).show();
                    ibtnRetweet.setImageDrawable(ContextCompat.getDrawable(TweetDetailsActivity.this, R.drawable.ic_vector_retweet));
                    tweet.retweeted = true;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(TweetDetailsActivity.this, "Retweet unsuccessful", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            });
        }
    }

    @OnClick(R.id.ibtnFavorite)
    public void onFavorite() {
        if (tweet.favorited) {
            client.unfavorite(tweet.uid, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(TweetDetailsActivity.this, "unFavorite successful", Toast.LENGTH_SHORT).show();
                    ibtnFavorite.setImageDrawable(ContextCompat.getDrawable(TweetDetailsActivity.this, R.drawable.ic_vector_heart_stroke));
                    tweet.favorited = false;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(TweetDetailsActivity.this, "unFavorite unsuccessful", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            });
        } else {
            client.favorite(tweet.uid, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(TweetDetailsActivity.this, "Favorite successful", Toast.LENGTH_SHORT).show();
                    ibtnFavorite.setImageDrawable(ContextCompat.getDrawable(TweetDetailsActivity.this, R.drawable.ic_vector_heart));
                    tweet.favorited = true;
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(TweetDetailsActivity.this, "Favorite unsuccessful", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                }
            });
        }
    }
}
