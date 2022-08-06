package com.example.beerbrary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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

public class UserProfileActivity extends AppCompatActivity {

    Button follow_button;
    TextView followers;
    TextView ratings;
    TextView following;
    TextView username;
    ImageView avatar;
    boolean is_following;
    int followers_num;
    int following_num;
    int rating_num;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        following = (TextView) findViewById(R.id.following_number);
        ratings = (TextView) findViewById(R.id.reviews_number);
        followers = (TextView) findViewById(R.id.followers_number_static);
        follow_button = (Button) findViewById(R.id.change_avatar_btn);
        username = (TextView) findViewById(R.id.userName);
        avatar = (ImageView) findViewById(R.id.user_avatar);
        followers_num = 0;
        following_num = 0;
        rating_num = 0;
        Intent intent = getIntent();
        user = intent.getStringExtra("Id");
        username.setText(intent.getStringExtra("Username"));
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("follows");
        String firebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot firstLevelSnapshot : task.getResult().getChildren()) {
                        if (firstLevelSnapshot.getKey().equals(user)) {
                            followers_num = (int)firstLevelSnapshot.getChildrenCount();
                            is_following = firstLevelSnapshot.hasChild(firebaseUser);
                        } else {
                            if (firstLevelSnapshot.hasChild(user)) {
                                following_num ++;
                            }
                        }
                    }
                    followers.setText(Integer.valueOf(followers_num).toString());
                    following.setText(Integer.valueOf(following_num).toString());
                } else {
                    System.out.println("error");
                }
            }
        });
        setReviews(user);
        RequestOptions options = new RequestOptions().error(R.drawable.default_avatar);
        Glide.with(this)
                .load(FirebaseStorage.getInstance()
                        .getReference().child("Avatars/"+user))
                .apply(options)
                .into(avatar);
    }

    private void setReviews(String user_id) {
        DatabaseReference db_rating = FirebaseDatabase.getInstance().getReference("rating");
        db_rating.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                ArrayList<ReviewModel> reviews= new ArrayList<>();
                if (task.isSuccessful()) {
                    for (DataSnapshot firstLevelSnapshot : task.getResult().getChildren()) {
                        if (firstLevelSnapshot.child("user").getValue().toString().equals(user_id)) {
                            rating_num ++;
                            if (firstLevelSnapshot.hasChild("review")) {
                                ReviewModel rev = new ReviewModel();
                                rev.setReview(firstLevelSnapshot.child("review").getValue().toString());
                                rev.setBeerId(Integer.valueOf(firstLevelSnapshot.child("beer").getValue().toString()));
                                rev.setScore(Integer.valueOf(firstLevelSnapshot
                                        .child("score").getValue().toString()));
                                rev.setUserId(firstLevelSnapshot.child("user").getValue().toString());
                                rev.setUser(firstLevelSnapshot.child("username").getValue().toString());
                                rev.setShowBeer(true);
                                reviews.add(rev);
                            }
                        }
                    }
                    ratings.setText(Integer.valueOf(rating_num).toString());
                    setBeerNames(reviews);
                } else {
                    System.out.println("error");
                }
            }
        });
    }

    private void setBeerNames(ArrayList<ReviewModel> reviews) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("beer");
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (ReviewModel review: reviews) {
                        StringBuilder name = new StringBuilder();
                        name.append(task.getResult().child(String.valueOf(review.getBeerId()))
                                .child("Line").getValue().toString());
                        name.append(" ");
                        name.append(task.getResult().child(String.valueOf(review.getBeerId()))
                                .child("Name").getValue().toString());
                        review.setBeer(name.toString());
                    }
                    inflater(reviews);
                } else {
                    System.out.println("error");
                }
            }
        });
    }

    private void inflater(ArrayList<ReviewModel> reviews) {
        RecyclerView reviewView = (RecyclerView) findViewById(R.id.user_reviews_recycler_view);
        reviewView.setAdapter(new ReviewListAdapter(this, reviews));
        reviewView.setLayoutManager(new LinearLayoutManager(this));
        followingManager();
    }

    private void followingManager() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("follows");
        String firebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    is_following = task.getResult().child(getIntent().getStringExtra("Id")).hasChild(firebaseUser);
                    follow_button.setPressed(is_following);
                    String foll = is_following ? "Following" : "Follow";
                    int color = is_following ? R.color.grey : R.color.orange;
                    follow_button.setBackgroundColor(getColor(color));
                    follow_button.setText(foll);
                    follow_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DatabaseReference db = FirebaseDatabase.getInstance().getReference("follows").child(user).child(firebaseUser);
                            db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DataSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        is_following = !is_following;
                                        follow_button.setPressed(is_following);
                                        String foll = is_following ? "Following" : "Follow";
                                        follow_button.setText(foll);
                                        int color = is_following ? R.color.grey : R.color.orange;
                                        follow_button.setBackgroundColor(getColor(color));
                                        int newfoll = Integer.valueOf(followers.getText().toString());
                                        if (is_following) {
                                            db.setValue("");
                                            newfoll++;
                                        } else {
                                            db.removeValue();
                                            newfoll--;
                                        }
                                        followers.setText(String.valueOf(newfoll));
                                    } else {
                                        System.out.println("error");
                                    }
                                }
                            });
                        }
                    });
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