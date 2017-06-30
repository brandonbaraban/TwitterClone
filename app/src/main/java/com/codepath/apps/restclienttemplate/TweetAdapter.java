package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

/**
 * Created by bbaraban on 6/26/17.
 */

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {

    private final int REQUEST_CODE_COMPOSE = 20;
    private final int REQUEST_CODE_DETAILS = 10;

    TwitterClient client;

    private List<Tweet> mTweets;
    Context context;

    // pass in the Tweets array in the constructor
    public TweetAdapter(List<Tweet> tweets) {
        mTweets = tweets;
        client = TwitterApplication.getRestClient();
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_tweet, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get the data according to position
        Tweet tweet = mTweets.get(position);
        // populate the views according to this data
        holder.tvUsername.setText(tweet.user.name);
        String screenName = "@" + tweet.user.screenName;
        holder.tvScreenName.setText(screenName);
        holder.tvTimestamp.setText(tweet.relativeTimestamp);
        holder.tvBody.setText(tweet.body);
        if (tweet.retweeted) {
            holder.ibtnRetweet.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_retweet));
        } else {
            holder.ibtnRetweet.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_retweet_stroke));
        }
        if (tweet.favorited) {
            holder.ibtnFavorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_heart));
        } else {
            holder.ibtnFavorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_heart_stroke));
        }
        holder.ibtnReply.setOnClickListener(onReply(tweet));
        holder.ibtnRetweet.setOnClickListener(onRetweet(holder, tweet, position));
        holder.ibtnFavorite.setOnClickListener(onFavorite(holder, tweet, position));

        Glide.with(context)
                .load(tweet.user.profileImageUrl)
                .into(holder.ivProfileImage);

        holder.itemView.setOnClickListener(onDetails(tweet, position));
    }

    private View.OnClickListener onFavorite(final ViewHolder holder, final Tweet tweet, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweet.favorited) {
                    mTweets.get(position).favorited = false;
                    client.unfavorite(tweet.uid, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Toast.makeText(context, "unfavorite successful", Toast.LENGTH_SHORT).show();
                            holder.ibtnFavorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_heart_stroke));
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
                    mTweets.get(position).favorited = true;
                    client.favorite(tweet.uid, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Toast.makeText(context, "favorite successful", Toast.LENGTH_SHORT).show();
                            holder.ibtnFavorite.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_heart));
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
        };
    }

    private View.OnClickListener onRetweet(final ViewHolder holder, final Tweet tweet, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tweet.retweeted) {
                    mTweets.get(position).retweeted = false;
                    client.unretweet(tweet.uid, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Toast.makeText(context, "unretweet successful", Toast.LENGTH_SHORT).show();
                            holder.ibtnRetweet.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_retweet_stroke));
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
                    mTweets.get(position).retweeted = true;
                    client.retweet(tweet.uid, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            Toast.makeText(context, "retweet successful", Toast.LENGTH_SHORT).show();
                            holder.ibtnRetweet.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_vector_retweet));
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
        };
    }

    private View.OnClickListener onReply(final Tweet tweet) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ComposeActivity.class);
                i.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                ((AppCompatActivity) context).startActivityForResult(i, REQUEST_CODE_COMPOSE);
            }
        };
    }

    private View.OnClickListener onDetails(final Tweet tweet, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // make sure the position is valid, i.e. actually exists in the view
                if (position != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, TweetDetailsActivity.class);
                    intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                    intent.putExtra("position", position);
                    ((AppCompatActivity) context).startActivityForResult(intent, REQUEST_CODE_DETAILS);
                }
            }
        };
    }

    @Override
    public int getItemCount() {
        return mTweets.size();
    }

    // create ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivProfileImage)
        ImageView ivProfileImage;
        @BindView(R.id.tvUsername)
        TextView tvUsername;
        @BindView(R.id.tvScreenName)
        TextView tvScreenName;
        @BindView(R.id.tvTimestamp)
        TextView tvTimestamp;
        @BindView(R.id.tvBody)
        TextView tvBody;
        @BindView(R.id.ibtnReply)
        ImageButton ibtnReply;
        @BindView(R.id.ibtnRetweet)
        ImageButton ibtnRetweet;
        @BindView(R.id.ibtnFavorite)
        ImageButton ibtnFavorite;

        public ViewHolder(View itemView) {
            super(itemView);

            // bind views
            ButterKnife.bind(this, itemView);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mTweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        mTweets.addAll(list);
        notifyDataSetChanged();
    }
}
