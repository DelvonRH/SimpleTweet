package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class DetailActivity extends AppCompatActivity
{
    Tweet tweet;
    TextView tvName, tvBody, tvCreatedAt, tvScreenName;
    ImageView ivProfileImage2, ivPic, ivReply;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        tweet = Parcels.unwrap(getIntent().getParcelableExtra("Tweet"));

        tvName = findViewById(R.id.tvName);
        tvBody = findViewById(R.id.tvBody);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvScreenName = findViewById(R.id.tvScreenName);
        ivProfileImage2 = findViewById(R.id.ivProfileImage2);
        ivPic = findViewById(R.id.ivPic);
        ivReply = findViewById(R.id.ivReply);

        tvName.setText(tweet.user.name);
        tvBody.setText(tweet.body);
        Glide.with(this).load(tweet.user.profileImageUrl).transform(new CircleCrop()).into(ivProfileImage2);
        tvScreenName.setText("@" + tweet.user.screenName);
        SimpleDateFormat date = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy");
        SimpleDateFormat customDate = new SimpleDateFormat("h:mm a · dd MMM yy");
        if(tweet.mediaURL == null)
        {
            ivPic.setVisibility(View.GONE);
        }
        else
        {
            ivPic.setVisibility(View.VISIBLE);
        }
        Glide.with(this).load(tweet.mediaURL).transform(new RoundedCornersTransformation(50, 30)).into(ivPic);
//        Log.d("MediaLink","Media Path " + tweet.mediaURL);


        try {
            Date Date = date.parse(tweet.createdAt);
            tvCreatedAt.setText(customDate.format(Date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ivReply.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(DetailActivity.this, ComposeActivity.class);
                intent.putExtra("Reply", Parcels.wrap(tweet));
                startActivity(intent);
            }
        });
    }
}