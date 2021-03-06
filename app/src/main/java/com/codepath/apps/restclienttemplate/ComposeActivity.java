 package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

 public class ComposeActivity extends AppCompatActivity {

    EditText etCompose;
    Button btnTweet;


    TwitterClient client;
    final String TAG = "ComposeActivity.java";


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);

        final int MAX_TWEET_LENGTH = 140;

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);


        // Set Click Listener on button
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String tweetContent = etCompose.getText().toString();   // gets text of what the user wrote
                if (tweetContent.isEmpty()){
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (tweetContent.length() > MAX_TWEET_LENGTH){
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet is too long!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_SHORT).show();
                // Make an API call to Twitter to Publish Tweet

                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.e(TAG, "onSuccess to publish tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "Published tweet says" + tweet);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            // set result code and bundle data for response
                            setResult(RESULT_OK, intent);
                            // closes activity, passing data to the parent
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "On failure to publish tweet", throwable);
                    }
                });
            }
        });

    }



}