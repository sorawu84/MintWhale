package com.nullgarden.mintwhale;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SinglePostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final String postKey = getIntent().getExtras().getString("blog_id");
        final Button removeBtn =findViewById(R.id.removeBtn);
        final TextView singleEmail = findViewById(R.id.singleEmail);
        final TextView singleName = findViewById(R.id.singleName);
        final TextView singleTime = findViewById(R.id.singleTime);
        final TextView singleDate = findViewById(R.id.singleDate);
        final TextView singleContent = findViewById(R.id.singleContent);
        final TextView singleTitle = findViewById(R.id.singleTitle);
        final ImageView singleImage = findViewById(R.id.singleImage);

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("MintPost");

        mDatabase.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String singlePostTitle = (String) dataSnapshot.child("Title").getValue();
                String singlePostContent = (String) dataSnapshot.child("Content").getValue();
                String singlePostDate = (String) dataSnapshot.child("Date").getValue();
                String singlePostTime = (String) dataSnapshot.child("Time").getValue();
                String singlePostEmail = (String)dataSnapshot.child("Poster").getValue();
                String singlePostName = (String)dataSnapshot.child("Name").getValue();
                String singlePostImage = (String)dataSnapshot.child("Images").getValue();
                String singlePostUid =(String)dataSnapshot.child("UID").getValue();

                singleEmail.setText(singlePostEmail);
                singleName.setText(singlePostName);
                singleTitle.setText(singlePostTitle);
                singleTime.setText(singlePostTime);
                singleDate.setText(singlePostDate);
                singleContent.setText(singlePostContent);
                Picasso.with(SinglePostActivity.this).load(singlePostImage).into(singleImage);

                if(mAuth.getCurrentUser().getUid().equals(singlePostUid)){
                    removeBtn.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SinglePostActivity.this);
                builder.setMessage("Are you sure want to delete this post?");
                builder.setCancelable(true);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDatabase.child(postKey).removeValue();
                        finish();
                    }
                });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
            }
        });


    }
}
