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

import com.codepath.apps.restclienttemplate.fragments.SearchTweetsFragment;
import com.codepath.apps.restclienttemplate.fragments.TweetsListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchActivity extends AppCompatActivity implements TweetsListFragment.TimelineListener {

    MenuItem miActionProgress;

    @BindView(R.id.tbMenuSearch)
    Toolbar tbMenuSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ButterKnife.bind(this);

        String query = getIntent().getStringExtra("query");

        setSupportActionBar(tbMenuSearch);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle("Search results for \"" + query + "\"");
        }

        SearchTweetsFragment searchTweetsFragment = SearchTweetsFragment.newInstance(query);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flContainer, searchTweetsFragment);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
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

    @Override
    public void setBannerUrl(String url) {

    }
}
