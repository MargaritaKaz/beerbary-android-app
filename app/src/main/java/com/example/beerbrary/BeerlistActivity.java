package com.example.beerbrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.beerbrary.beerlist.BeerListAdapter;
import com.example.beerbrary.models.BeerModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Comparator;

public class BeerlistActivity extends AppCompatActivity {


    private void inflater(ArrayList<BeerModel> beers) {
        RecyclerView beerView = (RecyclerView) findViewById(R.id.beer_recycler_view_list);
        beerView.setAdapter(new BeerListAdapter(this, beers));
        beerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getRatings(ArrayList<BeerModel> beers) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("rating");
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (BeerModel model: beers) {
                        int sum = 0;
                        int count = 0;
                        for (DataSnapshot firstLevelSnapshot : task.getResult().getChildren()) {
                            int check_id = Integer.valueOf((String) firstLevelSnapshot.child("beer").getValue());
                            if (check_id == model.getId()) {
                                count++;
                                sum += Integer.valueOf((String) firstLevelSnapshot.child("score").getValue());
                            }
                        }
                        float rating = ((float) sum)/count;
                        if (count != 0) {
                            model.setRating(rating);
                        } else {
                            model.setRating(0);
                        }
                    }
                    beers.sort(new Comparator<BeerModel>() {
                        @Override
                        public int compare(BeerModel beerModel, BeerModel t1) {
                            return Float.compare(beerModel.getRating(), t1.getRating());
                        }
                    });
                    ArrayList<BeerModel> top = new ArrayList<>();
                    for (int i=beers.size()-1; i>beers.size()-11; i--) {
                        top.add(beers.get(i));
                    }
                    inflater(top);
                } else {
                    System.out.println("error");
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);;
        setContentView(R.layout.activity_beerlist);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("beer");
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<BeerModel> beers = new ArrayList<>();
                    for (DataSnapshot firstLevelSnapshot : task.getResult().getChildren()) {
                        BeerModel model = new BeerModel();
                        model.setId(Integer.valueOf(firstLevelSnapshot.getKey()));
                        model.setType(Integer.valueOf((String) firstLevelSnapshot.child("Type").getValue()));
                        model.setName((String) firstLevelSnapshot.child("Name").getValue());
                        model.setLine((String) firstLevelSnapshot.child("Line").getValue());
                        model.setFlavour((String) firstLevelSnapshot.child("Flavour").getValue());
                        model.setVoltage(Float.valueOf((String) firstLevelSnapshot.child("Voltage").getValue()).floatValue());;
                        model.setBrewery(Integer.valueOf((String) firstLevelSnapshot.child("Brewery").getValue()));
                        beers.add(model);
                    }
                    getRatings(beers);
                } else {
                    System.out.println("error");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {

        switch (item.getItemId()){
            case R.id.search:
                startActivity(new Intent(this, ChooseTypeActivity.class));
                break;
            case R.id.home:
                Toast.makeText(this, "Already at home page", Toast.LENGTH_SHORT).show();
                break;
            case R.id.profile:
                startActivity(new Intent(this, UserOwnProfileActivity.class));
                break;
            case R.id.video:
                startActivity(new Intent(this, VideoActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}