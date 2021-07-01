package com.codepath.apps.restclienttemplate.models;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.ComposeActivity;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.TweetsAdapter;
import com.codepath.apps.restclienttemplate.TwitterApp;
import com.codepath.apps.restclienttemplate.TwitterClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity implements TweetsAdapter.onClickListener{

    public static final String TAG = "TimelineActivity";
    private final int REQUEST_CODE = 20;
    private SwipeRefreshLayout swipeContainer;

    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);         // Sets the view to the user, the layout


        Button logoutBtn = findViewById(R.id.logoutBtn);    // Connects to button from xml file
        logoutBtn.setOnClickListener(new View.OnClickListener() {   // Detects if button is interacted with
            @Override
            public void onClick(View v) {
                btnLogout();                                // On interaction with button, launches logout
            }
        });

        client = TwitterApp.getRestClient(this);

        // Find the Recycler View
        rvTweets = findViewById(R.id.rvTweets);
        // Initialize the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets, this);
        // Recycler View setup; layout manager and adapter
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);

        populateHomeTimeline();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync(0);
                //populateHomeTimeline();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


//        TweetsAdapter.onClickListener onClickListener = new TweetsAdapter.onClickListener(){
//            @Override
//            public void onClickLike(int position) {
//                final Tweet likedTweet = tweets.get(position);
//
//                Log.i(TAG, "INSIDE OF onClickLike ~~~~~~~~");
//
//                client.LikeTweet(likedTweet.tweetID, new JsonHttpResponseHandler() {
//                    @Override
//                    public void onSuccess(int statusCode, Headers headers, JSON json) {
//                        Log.i(TAG, "liked tweet says:" + likedTweet.body);
//
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//                        Log.e(TAG, "On failure to like tweet", throwable);
//                    }
//                });}
//
//        };



    }

    private void populateHomeTimeline() {

        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess!" + json.toString());

                JSONArray jsonArray = json.jsonArray;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    Log.e(TAG, "Json exception",e);
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG,"onFailure.." + response, throwable);
            }
        });
    }


    private void btnLogout(){
        client.clearAccessToken();  // Clears the tokens used to logout
        finish();                   // navigate Backwards to the LoginActivity by calling the built-in finish()
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu, this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.compose){                   // Compose Icon is tapped
            // Navigate to the compose activity
            Intent intent = new Intent(this, ComposeActivity.class);
            // Using startActivityForResults instead of startActivity because we need to retrieve
            // data that happens in the ComposeActivity.class. This allows us to do this by
            // using a request code.
            startActivityForResult(intent, REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // get data from the intent (tweet object)
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            // update the recycler view with the tweet
            // modify data source of tweets
            tweets.add(0, tweet);
            // update the adapter
            adapter.notifyItemInserted(0);
            // causes screen to scroll to the position of the latest tweet
            rvTweets.smoothScrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void fetchTimelineAsync(int page){
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i("onSuccessREFRESH", "Refresh was successful!");
                // Clearing out old items before appending new ones
                adapter.clear();
                // Now that data has come back, add new items to adapter
                JSONArray jsonArray = json.jsonArray;
                try {
                    adapter.addAll(Tweet.fromJsonArray(jsonArray));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("DEBUG", "Fetch timeline error:");
            }
        });
    }


    @Override
    public void onClickLike(int position) {
        Log.d(TAG, "onClickLike: " + position);
        Tweet likedTweet = tweets.get(position);



        client.LikeTweet(likedTweet.tweetID, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onClickLike SUCCESS " + "TWEET LIKED!");

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "On failure to like tweet " + response, throwable);
            }
        });
    }

    @Override
    public void onClickRetweet(int position) {
        Log.d(TAG, "onClickRetweet: " + position);
        Tweet likedTweet = tweets.get(position);

        client.RetweetTweet(likedTweet.tweetID, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onClickLike SUCCESS " + "TWEET RETWEETED!");


            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "On failure to RetweetTweet " + response, throwable);
            }
        });
    }
}
