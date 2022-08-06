package com.example.beerbrary.review;


import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beerbrary.R;

public class ReviewListViewHolder extends RecyclerView.ViewHolder {

    TextView rating;
    TextView review;
    TextView name;
    LinearLayout layout;
    ImageView image;

    public ReviewListViewHolder(@NonNull View itemView) {
        super(itemView);
        rating = (TextView) itemView.findViewById(R.id.avg_rating);
        name = (TextView) itemView.findViewById(R.id.name);
        layout = (LinearLayout) itemView.findViewById(R.id.reviewlist_layout);
        review = (TextView) itemView.findViewById(R.id.review);
        image = (ImageView) itemView.findViewById(R.id.beer_photo);
    }
}
