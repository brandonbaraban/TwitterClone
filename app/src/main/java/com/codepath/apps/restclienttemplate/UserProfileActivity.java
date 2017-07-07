package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.fragments.TweetsListFragment;
import com.codepath.apps.restclienttemplate.fragments.UserTimelineFragment;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserProfileActivity extends AppCompatActivity implements TweetsListFragment.TimelineListener {

    private final int REQUEST_CODE_COMPOSE = 20;
    private final int REQUEST_CODE_DETAILS = 10;

    User user;
    UserTimelineFragment userTimelineFragment;
    String bannerUrl;

    @BindView(R.id.ivUserBackgroundImage)
    ImageView ivUserBackgroundImage;
    @BindView(R.id.ivUserProfileImage)
    ImageView ivUserProfileImage;
    @BindView(R.id.tvUsername)
    TextView tvUsername;
    @BindView(R.id.tvScreenName)
    TextView tvScreenName;
    @BindView(R.id.tvBio)
    TextView tvBio;
    @BindView(R.id.tvFollowers)
    TextView tvFollowers;
    @BindView(R.id.tvFollowing)
    TextView tvFollowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        user = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));

        userTimelineFragment = UserTimelineFragment.newInstance(user.screenName);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContainer, userTimelineFragment);
        ft.commit();

        ButterKnife.bind(this);

        if (bannerUrl != null) {
            Glide.with(this)
                    .load(bannerUrl)
                    .into(ivUserBackgroundImage);
        }
        Glide.with(this)
                .load(user.profileImageUrl)
                .transform(new CircleTransform(this))
                .into(ivUserProfileImage);
        tvUsername.setText(user.name);
        String text = "@" + user.screenName;
        tvScreenName.setText(text);
        if (user.description == null || user.description.equals("")) {
            tvBio.setText("[Empty description]");
        } else {
            tvBio.setText(user.description);
        }
        tvFollowing.setText(user.following);
        tvFollowers.setText(user.followers);
    }

    @Override
    public void onRefresh(boolean b) {

    }

    @Override
    public void setBannerUrl(String url) {
        if (bannerUrl == null && url != null) {
            bannerUrl = url;
            Glide.with(this)
                    .load(bannerUrl)
                    .into(ivUserBackgroundImage);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_COMPOSE) {
            // Extract name value from result extras
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            userTimelineFragment.returnFromCompose(tweet);
        } else if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_DETAILS) {
            // Extract name value from result extras
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra(Tweet.class.getSimpleName()));
            int position = data.getIntExtra("position", -1);
            userTimelineFragment.returnFromDetails(tweet, position);
        }
    }

    public void onFollowing(View view) {
        Intent i = new Intent(this, FollowingActivity.class);
        i.putExtra(User.class.getSimpleName(), Parcels.wrap(user));
        startActivity(i);
    }
}
