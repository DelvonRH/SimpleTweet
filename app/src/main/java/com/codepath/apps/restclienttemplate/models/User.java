package com.codepath.apps.restclienttemplate.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class User
{
    public String name, screenName, profileImageUrl;

    // Empty constructor
    public User(){}

    public static User fromJson(JSONObject jsonObject) throws JSONException
    {
        User user = new User();
        user.name = jsonObject.getString("name");
        user.screenName = jsonObject.getString("screen_name");
        user.profileImageUrl = jsonObject.getString("profile_image_url_https");
        Log.d("Profile", "profilePath " + user.profileImageUrl);
        return user;
    }
}
