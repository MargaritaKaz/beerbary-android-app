package com.example.beerbrary;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.beerbrary.models.ReviewModel;
import com.example.beerbrary.review.ReviewListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserOwnProfileActivity extends AppCompatActivity {

    TextView followers;
    TextView ratings;
    TextView following;
    TextView username;
    ImageView avatar;
    Button avatar_change;
    boolean is_following;
    int followers_num;
    int following_num;
    int rating_num;
    String user;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own_user_profile);
        following = (TextView) findViewById(R.id.following_number);
        ratings = (TextView) findViewById(R.id.reviews_number);
        followers = (TextView) findViewById(R.id.followers_number_static);
        username = (TextView) findViewById(R.id.userName);
        avatar = (ImageView) findViewById(R.id.user_avatar);
        avatar_change = (Button) findViewById(R.id.change_avatar_btn);
        avatar_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
        followers_num = 0;
        following_num = 0;
        rating_num = 0;
        Intent intent = getIntent();
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        username.setText(intent.getStringExtra("Username"));
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("follows");
        usernameSetter();
        String firebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    for (DataSnapshot firstLevelSnapshot : task.getResult().getChildren()) {
                        if (firstLevelSnapshot.getKey().equals(user)) {
                            followers_num = (int) firstLevelSnapshot.getChildrenCount();
                            is_following = firstLevelSnapshot.hasChild(firebaseUser);
                        } else {
                            if (firstLevelSnapshot.hasChild(user)) {
                                following_num++;
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
        avatarSetter();
    }

    private void usernameSetter() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("user");
        String firebaseUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    setUsername(task.getResult().child(firebaseUser).getValue().toString());
                } else {
                    System.out.println("error");
                }
            }
        });
    }

    private void setUsername(String name) {
        this.username.setText(name);
    }

    private void setReviews(String user_id) {
        DatabaseReference db_rating = FirebaseDatabase.getInstance().getReference("rating");
        db_rating.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                ArrayList<ReviewModel> reviews = new ArrayList<>();
                if (task.isSuccessful()) {
                    for (DataSnapshot firstLevelSnapshot : task.getResult().getChildren()) {
                        if (firstLevelSnapshot.child("user").getValue().toString().equals(user_id)) {
                            rating_num++;
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
                    for (ReviewModel review : reviews) {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.search:
                startActivity(new Intent(this, ChooseTypeActivity.class));
                break;
            case R.id.home:
                startActivity(new Intent(this, BeerlistActivity.class));
                break;
            case R.id.profile:
                Toast.makeText(this, "Already at own profile", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            uploadImage();
        }
    }

    private void uploadImage() {
        if (filePath != null) {
            System.out.println("Yes");
            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref
                    = FirebaseStorage.getInstance().getReference().child(
                    "Avatars/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.dismiss();
                                    Toast.makeText(UserOwnProfileActivity.this,
                                            "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                    avatarSetter();
                                }
                            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UserOwnProfileActivity.this,
                                    "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(
                                UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()
                                    / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    private void avatarSetter() {
        RequestOptions options = new RequestOptions().error(R.drawable.default_avatar);
        Glide.with(this)
                .load(FirebaseStorage.getInstance()
                        .getReference().child("Avatars/"+user))
                .apply(options)
                .into(avatar);
    }
}