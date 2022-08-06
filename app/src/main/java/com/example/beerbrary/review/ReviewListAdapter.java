package com.example.beerbrary.review;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.beerbrary.BeerProfile;
import com.example.beerbrary.R;
import com.example.beerbrary.UserOwnProfileActivity;
import com.example.beerbrary.UserProfileActivity;
import com.example.beerbrary.models.ReviewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Objects;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListViewHolder> {

    Context context;
    ArrayList<ReviewModel> reviewlist;

    public ReviewListAdapter(Context c, ArrayList<ReviewModel> list) {
        this.context = c;
        this.reviewlist = list;
    }


    @NonNull
    @Override
    public ReviewListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).
                inflate(R.layout.review,parent,false);
        return new ReviewListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewListViewHolder holder, int position) {
        ReviewModel model = reviewlist.get(position);
        String test = model.getShowBeer() ? model.getBeer() : model.getUser();
        holder.name.setText(model.getShowBeer() ? model.getBeer() : model.getUser());
        holder.rating.setText(String.valueOf(model.getScore()));
        holder.review.setText(model.getReview());
        Intent intent;
        if (model.getShowBeer()) {
            intent = new Intent(context, BeerProfile.class);
            intent.putExtra("Id", model.getBeerId());
            Glide.with(this.context)
                    .load(FirebaseStorage.getInstance()
                            .getReference().child("Beers/"+model.getBeerId()+".jpg"))
                    .into(holder.image);
        } else {
            String curr_user = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if (curr_user.equals(model.getUserId())) {
                intent = new Intent(context, UserOwnProfileActivity.class);
            } else {
                intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("Id", model.getUserId());
                intent.putExtra("Username", model.getUser());
            }
            RequestOptions options = new RequestOptions().error(R.drawable.default_avatar);
            Glide.with(this.context)
                    .load(FirebaseStorage.getInstance()
                            .getReference().child("Avatars/"+model.getUserId()))
                    .apply(options)
                    .into(holder.image);
        }
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return reviewlist.size();
    }
}
