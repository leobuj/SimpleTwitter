package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.Viewholder> {

    // Pass in context and lists of tweets
    Context context;
    List<Tweet> tweets;

    public TweetsAdapter(Context context, List<Tweet> tweets){
        this.context = context;
        this.tweets = tweets;

    }

    // For each row, inflate the layout
    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new Viewholder(view);
    }

    // Bind values based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        // Get data at position
        Tweet tweet = tweets.get(position);
        // Bind the tweet with he viewholder
        holder.bind(tweet);

    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }


    // Define a viewholder
    public class Viewholder extends RecyclerView.ViewHolder{

        ImageView ivPofileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView tvTimestamp;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            ivPofileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);

        }

        public void bind(Tweet tweet){
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            Glide.with(context).load(tweet.user.profileImageUrl).into(ivPofileImage);
            tvTimestamp.setText(tweet.getRelativeTimeAgo(tweet.createdAt));

        }
    }

}
