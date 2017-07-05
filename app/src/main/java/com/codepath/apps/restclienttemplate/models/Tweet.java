package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;

import com.codepath.apps.restclienttemplate.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by bbaraban on 6/26/17.
 */

@Table(database = MyDatabase.class)
@Parcel(analyze={Tweet.class})
public class Tweet extends BaseModel {

    @Column
    @PrimaryKey
    public long id;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    public User user;

    public void setUser(User user) {
        this.user = user;
    }

    // list out the attributes
    @Column
    public String body;
    @Column
    public String createdAt;
    @Column
    public String relativeTimestamp;
    @Column
    public String dateTime;
    @Column
    public boolean retweeted;
    @Column
    public boolean favorited;
    @Column
    public String mediaUrl;

    public Tweet() {
    }

    // deserialize the JSON
    public static Tweet fromJSON(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();

        // extract the values from JSON
        tweet.body = jsonObject.getString("text");
        tweet.id = jsonObject.getLong("id");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.getRelativeTimeAgo(tweet.createdAt);
        tweet.retweeted = jsonObject.getBoolean("retweeted");
        tweet.favorited = jsonObject.getBoolean("favorited");
        tweet.user = User.fromJSON(jsonObject.getJSONObject("user"));

        JSONObject entities = jsonObject.getJSONObject("entities");
        JSONArray media = null;
        try {
            media = entities.getJSONArray("media");
        } catch (JSONException e) {
            // e.printStackTrace();
        }
        if (media != null) {
            tweet.mediaUrl = media.getJSONObject(0).getString("media_url");
        }

        return tweet;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getRelativeTimestamp() {
        return relativeTimestamp;
    }

    public void setRelativeTimestamp(String relativeTimestamp) {
        this.relativeTimestamp = relativeTimestamp;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public boolean isRetweeted() {
        return retweeted;
    }

    public void setRetweeted(boolean retweeted) {
        this.retweeted = retweeted;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public void getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (relativeDate.contains("second")) {
            relativeDate = relativeDate.substring(0, 1) + "s";
        } else if (relativeDate.contains("seconds")) {
            relativeDate = relativeDate.substring(0, 1) + "s";
        } else if (relativeDate.contains("minute")) {
            relativeDate = relativeDate.substring(0, 1) + "m";
        } else if (relativeDate.contains("minutes")) {
            relativeDate = relativeDate.substring(0, 1) + "m";
        } else if (relativeDate.contains("hour")) {
            relativeDate = relativeDate.substring(0, 1) + "h";
        } else if (relativeDate.contains("day")) {
            relativeDate = relativeDate.substring(0, 1) + "d";
        } else if (relativeDate.contains("days")) {
            relativeDate = relativeDate.substring(0, 1) + "d";
        }
        dateTime = rawJsonDate.substring(11, 16) + " . " + rawJsonDate.substring(8, 10) + " " + rawJsonDate.substring(4, 7) + " " + rawJsonDate.substring(rawJsonDate.length() - 2, rawJsonDate.length());
        relativeTimestamp = relativeDate;
    }
}
