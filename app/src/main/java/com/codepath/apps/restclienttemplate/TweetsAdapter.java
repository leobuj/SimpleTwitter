package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.Viewholder> {

    // Pass in context and lists of tweets
    Context context;
    List<Tweet> tweets;
    onClickListener onClickListener;

    public interface onClickListener{
        void onClickLike(int position);
        void onClickRetweet(int position);
    }

    public TweetsAdapter(Context context, List<Tweet> tweets, onClickListener onClickListener){
        this.context = context;
        this.tweets = tweets;
        this.onClickListener = onClickListener;

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
        ImageView ivImage;
        ImageView ivLike;
        ImageView ivRetweet;
        TextView tvTwitterHandle;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            ivPofileImage = itemView.findViewById(R.id.ivProfileImage);
            ivImage = itemView.findViewById(R.id.tweetImage);
            ivLike = itemView.findViewById(R.id.ivLike);
            tvBody = itemView.findViewById(R.id.tvBody);
            ivRetweet = itemView.findViewById(R.id.ivRetweet);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvTwitterHandle = itemView.findViewById(R.id.tvTwitterHandle);




        }

        public void bind(Tweet tweet){ // helped to onBindViewholder



            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Glide.with(context)
                            .load(R.drawable.ic_vector_heart)
                            .override(70, 80)
                            .into(ivLike);
                    onClickListener.onClickLike(getAdapterPosition());
                }
            });

            ivRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    onClickListener.onClickRetweet(getAdapterPosition());
                }
            });

            if(tweet.liked){
                Glide.with(context)
                        .load(R.drawable.ic_vector_heart)
                        .override(70, 70)
                        .into(ivLike);
            }
            else
                Glide.with(context).load(R.drawable.ic_vector_heart_stroke).override(70,70).into(ivLike);

            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.screenName);
            tvTwitterHandle.setText("@"+tweet.user.twitterHandle);
            tvTimestamp.setText(tweet.getRelativeTimeAgo(tweet.createdAt));


            Glide.with(context)
                    .load(tweet.user.profileImageUrl)
                    .circleCrop()
                    .into(ivPofileImage);



            Glide.with(context)
                    .load(tweet.https_url)
                    .into(ivImage);


        }
    }

    // Two methods used in implementing scroll up to refresh
    public void clear(){
        tweets.clear();
        notifyDataSetChanged();
    }
    public void addAll(List<Tweet> list){
        tweets.addAll(list);
        notifyDataSetChanged();
    }
}
