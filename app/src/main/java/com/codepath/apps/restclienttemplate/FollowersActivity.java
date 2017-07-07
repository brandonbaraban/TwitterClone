package com.codepath.apps.restclienttemplate;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.codepath.apps.restclienttemplate.fragments.FollowFragment;
import com.codepath.apps.restclienttemplate.fragments.FollowersFragment;
import com.codepath.apps.restclienttemplate.models.User;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FollowersActivity extends AppCompatActivity implements FollowFragment.FollowListener {

    User user;
    FollowersFragment followersFragment;
    MenuItem miActionProgress;

    @BindView(R.id.tbMenuFollowers)
    Toolbar tbMenuFollowers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        user = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));

        followersFragment = (FollowersFragment) FollowersFragment.newInstance(user.screenName);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContainer, followersFragment);
        ft.commit();

        ButterKnife.bind(this);

        setSupportActionBar(tbMenuFollowers);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("Followers");
        }
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

    @Override
    public void onRefresh(boolean b) {
        if (miActionProgress != null) {
            miActionProgress.setVisible(b);
        }
    }
}
