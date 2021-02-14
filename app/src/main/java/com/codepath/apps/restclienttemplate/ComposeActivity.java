package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcel;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity
{
    public static final int MAX_TWEET_LENGTH = 280;
    public static final String TAG = "ComposeActivity";

    EditText etCompose;
    Button btnTweet;
    ImageView ivProfilePic3;
    TwitterClient client;
    User user;
    Context context;
    ProgressBar progressBar;
    ImageView ivClose;
    TextView tvCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);

        context = this;

        client.getUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json)
            {
                try {
                    user = User.fromJson(json.jsonObject);
                    Glide.with(context).load(user.profileImageUrl).transform(new CircleCrop()).into(ivProfilePic3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        ivProfilePic3 = findViewById(R.id.ivProfilePic3);
        progressBar = findViewById(R.id.progressBar);
        ivClose = findViewById(R.id.ivClose);
        tvCounter = findViewById(R.id.tvCounter);

        ivClose.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });

        tvCounter.setText(String.format("%d/%d",0,MAX_TWEET_LENGTH));

        etCompose.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                int counter = charSequence.length();
                progressBar.setProgress(counter);
                tvCounter.setText(String.format("%d/%d",counter,MAX_TWEET_LENGTH));

                if(counter >= (MAX_TWEET_LENGTH - 20) && counter <= MAX_TWEET_LENGTH)
                {
                    LayerDrawable progressBarDrawable = (LayerDrawable) progressBar.getProgressDrawable();
                    Drawable backgroundDrawable = progressBarDrawable.getDrawable(0);
                    Drawable progressDrawable = progressBarDrawable.getDrawable(1);

                    backgroundDrawable.setColorFilter(0xFF4F5254, PorterDuff.Mode.SRC_IN);
                    progressDrawable.setColorFilter(ContextCompat.getColor(ComposeActivity.this, android.R.color.holo_orange_light), PorterDuff.Mode.SRC_IN);

                }

                if(counter > MAX_TWEET_LENGTH)
                {
                    LayerDrawable progressBarDrawable = (LayerDrawable) progressBar.getProgressDrawable();
                    Drawable backgroundDrawable = progressBarDrawable.getDrawable(0);
                    Drawable progressDrawable = progressBarDrawable.getDrawable(1);

                    backgroundDrawable.setColorFilter(0xFF4F5254, PorterDuff.Mode.SRC_IN);
                    progressDrawable.setColorFilter(ContextCompat.getColor(ComposeActivity.this, android.R.color.holo_red_light), PorterDuff.Mode.SRC_IN);
                }

                if(counter < (MAX_TWEET_LENGTH-20))
                {
                    LayerDrawable progressBarDrawable = (LayerDrawable) progressBar.getProgressDrawable();
                    Drawable backgroundDrawable = progressBarDrawable.getDrawable(0);
                    Drawable progressDrawable = progressBarDrawable.getDrawable(1);

                    backgroundDrawable.setColorFilter(0xFF4F5254, PorterDuff.Mode.SRC_IN);
                    progressDrawable.setColorFilter(ContextCompat.getColor(ComposeActivity.this, android.R.color.holo_blue_bright), PorterDuff.Mode.SRC_IN);                }
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });

        // Set Click Listener on button
        btnTweet.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final String tweetContent = etCompose.getText().toString();
                if(tweetContent.isEmpty())
                {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet can not be empty", Toast.LENGTH_LONG).show();
                    return;
                }

                if (tweetContent.length() > MAX_TWEET_LENGTH)
                {
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet is too long", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_LONG).show();
                // Make an API Call to Twitter to publish a Tweet
                client.publishTweet(tweetContent, new JsonHttpResponseHandler()
                {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json)
                    {
                        Log.i(TAG, "onSuccess to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published tweet says: " + tweet.body);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            // Set result code and bundle data for response
                            setResult(RESULT_OK, intent);
                            // Closes the activity, pass data to parent
                            finish();
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable)
                    {
                        Log.e(TAG, "onFailure to publish tweet", throwable);
                    }
                });

            }
        });
    }
}