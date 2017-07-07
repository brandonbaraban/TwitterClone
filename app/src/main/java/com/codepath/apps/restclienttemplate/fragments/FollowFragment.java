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
import com.codepath.apps.restclienttemplate.UserAdapter;
import com.codepath.apps.restclienttemplate.models.User;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bbaraban on 7/6/17.
 */

public class FollowFragment extends Fragment {
    FollowListener listener;

    UserAdapter userAdapter;
    ArrayList<User> users;
    @BindView(R.id.rvUsers)
    RecyclerView rvUsers;
    @BindView(R.id.scRefresh)
    SwipeRefreshLayout swipeContainer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_follows_list, container, false);

        // bind views
        ButterKnife.bind(this, v);
        // init the list (data source)
        users = new ArrayList<>();
        // construct the adapter from this data source
        userAdapter = new UserAdapter(users);
        // RecyclerView setup (layout manager, use adapter)
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvUsers.setLayoutManager(layoutManager);
        // set the adapter
        rvUsers.setAdapter(userAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvUsers.getContext(),
                layoutManager.getOrientation());
        rvUsers.addItemDecoration(dividerItemDecoration);

        setHasOptionsMenu(false);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FollowListener) {
            listener = (FollowListener) context;
        } else {
            //throw new ClassCastException(context.toString()
                    //+ " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    public interface FollowListener {
        public void onRefresh(boolean b);
    }

    public void processJSONArrayUsers(JSONArray response) {
        ArrayList<User> newUsers = new ArrayList<>();
        // iterate through JSONArray
        // for each entry, deserialize the JSON object
        for (int i = 0; i < response.length(); i++) {
            // convert each object to a Tweet model
            // add that Tweet model to our data source
            // notify the adapter that we've added an item
            try {
                User user = User.fromJSON(response.getJSONObject(i));
                newUsers.add(user);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        userAdapter.addAll(newUsers);
    }

    public void addUsers(List<User> newUsers, boolean clear) {
        if (clear) {
            userAdapter.clear();
        }
        userAdapter.addAll(newUsers);
    }

    public void deleteUsers() {
        userAdapter.clear();
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

    public void scrollToTop() {
        rvUsers.smoothScrollToPosition(0);
    }
}
