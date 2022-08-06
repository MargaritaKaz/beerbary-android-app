package com.example.beerbrary.beerlist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.beerbrary.BeerProfile;
import com.example.beerbrary.R;
import com.example.beerbrary.models.BeerModel;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class BeerListAdapter extends RecyclerView.Adapter<BeerListViewHolder> implements Filterable {

    Context context;
    ArrayList<BeerModel> beerlist;
    ArrayList<BeerModel> beerlistFull;

    public BeerListAdapter(Context c, ArrayList<BeerModel> list) {
        this.context = c;
        this.beerlist = list;
        this.beerlistFull = new ArrayList<>(list);
    }


    @NonNull
    @Override
    public BeerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).
                inflate(R.layout.beerlist_item,parent,false);
        return new BeerListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BeerListViewHolder holder, int position) {
        holder.name.setText(beerlist.get(position).getLine()+" "+beerlist.get(position).getName());
        holder.rating.setText(String.valueOf(beerlist.get(position).getRating()));
        holder.flavour.setText(beerlist.get(position).getFlavour());
        Glide.with(this.context)
                .load(FirebaseStorage.getInstance()
                        .getReference().child("Beers/"+beerlist.get(position).getId()+".jpg"))
                .into(holder.image);
        Intent intent = new Intent(context, BeerProfile.class);
        intent.putExtra("Id", beerlist.get(position).getId());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return beerlist.size();
    }

    @Override
    public Filter getFilter() {
        return beerFilter;
    }

    private Filter beerFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<BeerModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(beerlistFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (BeerModel item : beerlistFull) {
                    if (item.getLine().toLowerCase().contains(filterPattern)
                    || item.getFlavour().toLowerCase().contains(filterPattern)
                    || item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            beerlist.clear();
            beerlist.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


}
