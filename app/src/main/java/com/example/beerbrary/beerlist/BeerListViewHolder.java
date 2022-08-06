package com.example.beerbrary.beerlist;


import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beerbrary.R;

import org.w3c.dom.Text;

import java.security.acl.Group;

public class BeerListViewHolder extends RecyclerView.ViewHolder {

    TextView rating;
    TextView name;
    TextView flavour;
    ImageView image;
    LinearLayout layout;

    public BeerListViewHolder(@NonNull View itemView) {
        super(itemView);
        rating = (TextView) itemView.findViewById(R.id.avg_rating);
        name = (TextView) itemView.findViewById(R.id.beerListName);
        image = (ImageView) itemView.findViewById(R.id.beerListPhoto);
        flavour = (TextView) itemView.findViewById(R.id.itemFlavour);
        layout = (LinearLayout) itemView.findViewById(R.id.beerItemLayout);
    }
}
