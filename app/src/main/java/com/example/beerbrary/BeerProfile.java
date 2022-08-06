package com.example.beerbrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.beerbrary.models.BeerModel;
import com.example.beerbrary.models.ReviewModel;
import com.example.beerbrary.review.ReviewListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;

public class BeerProfile extends AppCompatActivity {

    ImageView image;
    ImageView brewery_logo;
    RatingBar ratingBar;
    TextView name;
    TextView rating;
    TextView details;
    Button reviewButton;
    int id;
    String username;
    String review;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beer_profile);
        ratingBar = (RatingBar) findViewById(R.id.beerProfile_ratingBar);
        name = (TextView) findViewById(R.id.beerProfile_name);
        details = (TextView) findViewById(R.id.beerProfile_details);
        rating = (TextView) findViewById(R.id.beerProfile_rating);
        image = (ImageView) findViewById(R.id.beerProfile_image);
        brewery_logo = (ImageView) findViewById(R.id.brewery_logo);
        reviewButton = (Button) findViewById(R.id.reviewButton);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(), R.style.AlertDialogCustom);
                builder.setTitle("Add a review");
                View viewInflated = LayoutInflater.from(view.getContext()).inflate(
                        R.layout.add_review, (ViewGroup) findViewById(android.R.id.content), false);
                final EditText input = (EditText) viewInflated.findViewById(R.id.input);
                builder.setView(viewInflated);
                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        review = input.getText().toString();
                        reviewManager();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
        reviewButton.setEnabled(false);
        modelCreator();
        setReviews();
    }

    private int returnThisId() {
        return this.id;
    }

    private void setRating(Float new_rating) {
        rating.setText(String.valueOf(new_rating));
    }

    private void updateAverage() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("rating");
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    int sum = 0;
                    int count = 0;
                    for (DataSnapshot firstLevelSnapshot : task.getResult().getChildren()) {
                        int check_id = Integer.valueOf((String) firstLevelSnapshot.child("beer").getValue());
                        if (check_id == returnThisId()) {
                            count++;
                            sum += Integer.valueOf((String) firstLevelSnapshot.child("score").getValue());
                        }
                    }
                    float rating = ((float) sum) / count;
                    if (count != 0) {
                        rating = (float) (Math.floor(rating * 100) / 100);
                        setRating(rating);
                    } else {
                        setRating((float)0);
                    }
                } else {
                    System.out.println("error");
                }
            }
        });
    }

    private void getRating(BeerModel model) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("rating");
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
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
                    setter(model);
                } else {
                    System.out.println("error");
                }
            }
        });
    }

    private void modelCreator() {
        Intent intent = getIntent();
        this.id = intent.getIntExtra("Id", 0);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("beer/" + this.id);
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot snap = task.getResult();
                    BeerModel model = new BeerModel();
                    model.setType(Integer.valueOf((String) snap.child("Type").getValue()));
                    model.setName((String) snap.child("Name").getValue());
                    model.setLine((String) snap.child("Line").getValue());
                    model.setFlavour((String) snap.child("Flavour").getValue());
                    model.setVoltage(Float.valueOf((String) snap.child("Voltage").getValue()).floatValue());
                    model.setBrewery(Integer.valueOf((String) snap.child("Brewery").getValue()));
                    model.setId(returnThisId());
                    getRating(model);
                } else {
                    System.out.println("error");
                }
            }
        });
    }

    private void setter(BeerModel model) {
        String name_morphed = model.getLine() + " "
                + model.getName();
        name.setText(name_morphed);
        getType(model.getType(),
                model.getVoltage() + "%");
        rating.setText(Float.valueOf(model.getRating()).toString());
        getUserRating(this.id);
        getUsername();
        Glide.with(this)
                .load(FirebaseStorage.getInstance()
                        .getReference().child("Beers/" + this.id + ".jpg"))
                .into(image);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                reviewEnabler(true);
                DatabaseReference db = FirebaseDatabase.getInstance().getReference("rating");
                db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()) {
                            String firebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            boolean found = false;
                            for (DataSnapshot snapshot : task.getResult().getChildren()) {
                                if (snapshot.child("beer").getValue().toString().equals(Integer.toString(id))
                                        && snapshot.child("user").getValue().toString().equals(firebaseUser)) {
                                    DatabaseReference db_inner = FirebaseDatabase.getInstance()
                                            .getReference("rating/" + snapshot.getKey() + "/score");
                                    db_inner.setValue(Integer.toString((int) (v * 2)));
                                    found = true;
                                }
                            }
                            if (!found) {
                                HashMap<String, Object> map = new HashMap<>();
                                map.put("beer", Integer.toString(id));
                                map.put("score", Integer.toString((int) (v * 2)));
                                map.put("user", firebaseUser);
                                map.put("username", getThisUsername());
                                HashMap<String, Object> map_out = new HashMap<>();
                                DatabaseReference db_push = db.push();
                                map_out.put(db_push.getKey(), map);
                                db_push.updateChildren(map);
                            }
                            updateAverage();
                        } else {
                            System.out.println("error");
                        }
                    }
                });
            }
        });
        brewery_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), BreweryProfileActivity.class);
                intent.putExtra("Id", Integer.toString(model.getBrewery()));
                startActivity(intent);
            }
        });
        Glide.with(this)
                .load(FirebaseStorage.getInstance()
                        .getReference().child("Brewery/" + model.getBrewery() + ".png"))
                .into(brewery_logo);
    }

    private void getType(int id, String voltage) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("type");
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot temp = task.getResult().child(Integer.toString(id)).child("name");
                    details.setText(temp.getValue().toString() + ";  " + voltage);
                } else {
                    System.out.println("error");
                }
            }
        });
    }

    private void getUserRating(int id) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("rating");
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    String firebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        if (snapshot.child("beer").getValue().toString().equals(Integer.toString(id))
                                && snapshot.child("user").getValue().toString().equals(firebaseUser)) {
                            ratingBar.setRating(Float.valueOf(snapshot.child("score").
                                    getValue().toString()).floatValue() / 2);
                            reviewEnabler(true);
                        }
                    }
                } else {
                    System.out.println("error");
                }
            }
        });
    }

    private void getUsername() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("user");
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    String firebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    setUsername(task.getResult().child(firebaseUser).getValue().toString());
                } else {
                    System.out.println("error");
                }
            }
        });
    }

    private void setUsername(String name) {
        this.username = name;
    }

    private String getThisUsername() {
        return this.username;
    }

    private void setReviews() {
        DatabaseReference db_rating = FirebaseDatabase.getInstance().getReference("rating");
        db_rating.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                ArrayList<ReviewModel> reviews = new ArrayList<>();
                if (task.isSuccessful()) {
                    for (DataSnapshot firstLevelSnapshot : task.getResult().getChildren()) {
                        if (firstLevelSnapshot.child("beer").getValue().toString().equals(String.valueOf(returnThisId()))) {
                            if (firstLevelSnapshot.hasChild("review")) {
                                ReviewModel rev = new ReviewModel();
                                rev.setReview(firstLevelSnapshot.child("review").getValue().toString());
                                rev.setBeerId(Integer.valueOf(firstLevelSnapshot.child("beer").getValue().toString()));
                                rev.setScore(Integer.valueOf(firstLevelSnapshot
                                        .child("score").getValue().toString()));
                                rev.setUserId(firstLevelSnapshot.child("user").getValue().toString());
                                rev.setUser(firstLevelSnapshot.child("username").getValue().toString());
                                rev.setShowBeer(false);
                                reviews.add(rev);
                            }
                        }
                    }
                    inflater(reviews);
                } else {
                    System.out.println("error");
                }
            }
        });
    }

    private void inflater(ArrayList<ReviewModel> reviews) {
        RecyclerView reviewView = (RecyclerView) findViewById(R.id.review_recycler_view);
        reviewView.setAdapter(new ReviewListAdapter(this, reviews));
        reviewView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void reviewEnabler(boolean bool) {
        reviewButton.setEnabled(bool);
    }

    private String reviewGetter() {
        return this.review;
    }

    private void reviewManager() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("rating");
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    String firebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        if (snapshot.child("beer").getValue().toString().equals(Integer.toString(id))
                                && snapshot.child("user").getValue().toString().equals(firebaseUser)) {
                            DatabaseReference db_inner = FirebaseDatabase.getInstance()
                                    .getReference("rating/" + snapshot.getKey() + "/review");
                            db_inner.setValue(reviewGetter());
                            setReviews();
                        }
                    }
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
}