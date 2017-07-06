package com.codepath.apps.restclienttemplate.models;

import com.codepath.apps.restclienttemplate.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by bbaraban on 6/26/17.
 */

@Table(database = MyDatabase.class)
@Parcel(analyze = {User.class})
public class User {

    @Column
    @PrimaryKey
    public long id;

    @Column
    public String name;

    public void setName(String name) {
        this.name = name;
    }

    @Column
    public String screenName;
    @Column
    public String profileImageUrl;
    @Column
    public String description;
    @Column
    public String followers;
    @Column
    public String following;

    public static User fromJSON(JSONObject jsonObject) throws JSONException {
        User user = new User();

        // extract and fill in values
        user.name = jsonObject.getString("name");
        user.id = jsonObject.getLong("id");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url").replace("_normal", "");
        user.description = jsonObject.getString("description");
        user.followers = jsonObject.getString("followers_count");
        user.following = jsonObject.getString("friends_count");

        return user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
