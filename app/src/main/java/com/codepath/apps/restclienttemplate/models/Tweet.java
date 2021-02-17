package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.codepath.apps.restclienttemplate.TimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
@Entity(foreignKeys = @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId"))
public class Tweet
{
    @ColumnInfo
    public String body;
    @ColumnInfo
    public String createdAt;
    @ColumnInfo
    public String mediaURL;
    @ColumnInfo
    @PrimaryKey
    public Long id;

    @ColumnInfo
    public long userId;

    @Ignore
    public User user;

    // Empty constructor
    public Tweet(){}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException
    {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.id = jsonObject.getLong("id");
        User user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.userId = user.id;
        if(jsonObject.has("extended_entities"))
        {
            JSONObject extendedEntities = jsonObject.getJSONObject("extended_entities");

            if(extendedEntities.has("media"))
            {
                JSONArray media = extendedEntities.getJSONArray("media");
                tweet.mediaURL = media.getJSONObject(0).getString("media_url");
                Log.d("Media","Media Path " + tweet.mediaURL);
            }
        }
        tweet.user = user;
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException
    {
        List<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++)
        {
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }

        return tweets;
    }

    public String getFormattedTimestamp()
    {
        return TimeFormatter.getTimeDifference(createdAt);
    }
}
