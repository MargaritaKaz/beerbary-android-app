package com.example.beerbrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.beerbrary.beerlist.BeerListAdapter;
import com.example.beerbrary.models.BeerModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

public class BreweryProfileActivity extends AppCompatActivity {

    androidx.appcompat.widget.SearchView searchView;
    BeerListAdapter adapter;
    ImageView logo;
    TextView header;
    ImageView country;

    private void inflater(ArrayList<BeerModel> beers) {
        RecyclerView beerView = (RecyclerView) findViewById(R.id.beer_view);
        this.adapter = new BeerListAdapter(this, beers);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        beerView.setAdapter(this.adapter);
        beerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getRatings(ArrayList<BeerModel> beers) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("rating");
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (BeerModel model : beers) {
                        int sum = 0;
                        int count = 0;
                        for (DataSnapshot firstLevelSnapshot : task.getResult().getChildren()) {
                            int check_id = Integer.valueOf((String) firstLevelSnapshot.child("beer").getValue());
                            if (check_id == model.getId()) {
                                count++;
                                sum += Integer.valueOf((String) firstLevelSnapshot.child("score").getValue());
                            }
                        }
                        float rating = ((float) sum) / count;
                        if (count != 0) {
                            model.setRating(rating);
                        } else {
                            model.setRating(0);
                        }
                    }
                    inflater(beers);
                } else {
                    System.out.println("error");
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brewery_profile);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("beer");
        Intent intent = getIntent();
        searchView = findViewById(R.id.searchview);
        String id_inner = intent.getStringExtra("Id");
        header = (TextView) findViewById(R.id.brewery_name);
        logo = (ImageView) findViewById(R.id.brewery_logo);
        country = (ImageView) findViewById(R.id.country);
        Glide.with(this)
                .load(FirebaseStorage.getInstance()
                        .getReference().child("Brewery/"+id_inner+".png"))
                .into(logo);
        infoSetter(id_inner);
        String finalId_inner = id_inner;
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<BeerModel> beers = new ArrayList<>();
                    for (DataSnapshot firstLevelSnapshot : task.getResult().getChildren()) {
                        if (firstLevelSnapshot.child("Brewery").getValue().toString().equals(finalId_inner)) {
                            BeerModel model = new BeerModel();
                            model.setId(Integer.valueOf(firstLevelSnapshot.getKey()));
                            model.setType(Integer.valueOf((String) firstLevelSnapshot.child("Type").getValue()));
                            model.setName((String) firstLevelSnapshot.child("Name").getValue());
                            model.setLine((String) firstLevelSnapshot.child("Line").getValue());
                            model.setFlavour((String) firstLevelSnapshot.child("Flavour").getValue());
                            model.setVoltage(Float.valueOf((String) firstLevelSnapshot.child("Voltage").getValue()).floatValue());
                            model.setBrewery(Integer.valueOf((String) firstLevelSnapshot.child("Brewery").getValue()));
                            beers.add(model);
                        }
                    }
                    System.out.println(beers.size());
                    getRatings(beers);
                } else {
                    System.out.println("error");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {

        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected( @NonNull MenuItem item ) {

        switch (item.getItemId()){
            case R.id.search:
                startActivity(new Intent(this, ChooseTypeActivity.class));
                break;
            case R.id.home:
                startActivity(new Intent(this, BeerlistActivity.class));
                break;
            case R.id.profile:
                startActivity(new Intent(this, UserOwnProfileActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void infoSetter(String id) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("brewery");
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    System.out.println(id);
                    String name = task.getResult().child(id).child("Name").getValue().toString();
                    String country = task.getResult().child(id).child("Location").getValue().toString();
                    setData(name, country);
                } else {
                    System.out.println("error");
                }
            }
        });
    }

    private void setData(String name, String country_name) {
        header.setText(name);
        int image;
        switch (country_name.toLowerCase()) {
            case "germany":
                image = R.drawable.germany;
                break;
            case "uk":
                image = R.drawable.uk;
                break;
            default:
                image = R.drawable.poland;
                break;
        }
        country.setImageDrawable(getDrawable(image));
    }
}