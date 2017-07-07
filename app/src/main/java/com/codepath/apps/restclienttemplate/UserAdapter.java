package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.User;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bbaraban on 7/6/17.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> mUsers;
    Context context;

    // pass in the Tweets array in the constructor
    public UserAdapter(List<User> users) {
        mUsers = users;
    }

    // for each row, inflate the layout and cache references into ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View userView = inflater.inflate(R.layout.item_user, parent, false);
        ViewHolder viewHolder = new ViewHolder(userView);
        return viewHolder;
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get the data according to position
        User user = mUsers.get(position);
        // populate the views according to this data
        holder.tvUsername.setText(user.name);
        String screenName = "@" + user.screenName;
        holder.tvScreenName.setText(screenName);
        holder.tvBio.setText(user.description);
        holder.ivProfileImage.setOnClickListener(onProfileView(user));

        Glide.with(context)
                .load(user.profileImageUrl)
                .transform(new CircleTransform(context))
                .into(holder.ivProfileImage);
    }

    private View.OnClickListener onProfileView(final User user) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, UserProfileActivity.class);
                i.putExtra(User.class.getSimpleName(), Parcels.wrap(user));
                context.startActivity(i);
            }
        };
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    // create ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivProfileImage)
        ImageView ivProfileImage;
        @BindView(R.id.tvUsername)
        TextView tvUsername;
        @BindView(R.id.tvScreenName)
        TextView tvScreenName;
        @BindView(R.id.tvBio)
        TextView tvBio;

        public ViewHolder(View itemView) {
            super(itemView);

            // bind views
            ButterKnife.bind(this, itemView);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mUsers.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<User> list) {
        mUsers.addAll(list);
        notifyDataSetChanged();
    }
}
