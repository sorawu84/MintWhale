package com.nullgarden.mintwhale;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //TODO:SortButton, dunno how to do.
        ImageView sortBtn = findViewById(R.id.sortBtn);
        sortBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //End of TODO:

        ImageView goPostBtn = findViewById(R.id.goPostBtn);
        goPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PostActivity.class));
            }
        });

        ImageView goAccountBtn = findViewById(R.id.goAccountBtn);
        goAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AccountActivity.class));
                finish();
            }
        });

    }

    //View Holder for RecyclerView.
    public static class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        ImageView likeBtn;
        DatabaseReference mDatabaseLike2 = FirebaseDatabase.getInstance().getReference().child("Likes");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            likeBtn = mView.findViewById(R.id.likeBtn);
        }

        //Set like Button, change icon.
        public void setLikeBtn(final String postKey){
            mDatabaseLike2.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(postKey).hasChild(mAuth.getCurrentUser().getUid())){
                        likeBtn.setImageResource(R.drawable.ic_favorite_black_24dp);
                    }else{
                        likeBtn.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        //end of set like button.

        //set of various info in each posts.
        public void setName(String name){
            TextView namer = mView.findViewById(R.id.namer);
            namer.setText("Posted by: " + name);
        }

        public void setTitle(String title){
            TextView postTitle = mView.findViewById(R.id.postTitle);
            postTitle.setText(title);
        }

        public void setContent(String content){
            TextView postContent = mView.findViewById(R.id.postContent);
            postContent.setText(content);
        }

        public void setPoster(String poster){
            TextView postPoster = mView.findViewById(R.id.posterName);

            postPoster.setText(poster);
        }

        //if else for Offline view images.
        public void setImages(final Context context, final String images){
            final ImageView postImages = mView.findViewById(R.id.postImage);
            Picasso.with(context).load(images).networkPolicy(NetworkPolicy.OFFLINE).into(postImages, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(images).into(postImages);
                }
            });
        }

        public void setDate(String date){
            TextView postDate = mView.findViewById(R.id.posterDate);
            postDate.setText(date);
        }

        public  void setTime(String time){
            TextView postTime = mView.findViewById(R.id.posterTime);
            postTime.setText(time);
        }
    }
    //end of set info for posts


    private boolean mProccessLike = false;
    private DatabaseReference mDatabaseLike;

    @Override
    protected void onStart() {
        super.onStart();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("MintPost");
        mDatabase.keepSynced(true);
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseLike.keepSynced(true);

        Query query = mDatabase.orderByChild("TimeStampR");
        FirebaseRecyclerAdapter<TicketCard, ViewHolder> firebaseRecyclerAdapter =new FirebaseRecyclerAdapter<TicketCard, ViewHolder>(
                TicketCard.class,
                R.layout.card_ticket,
                ViewHolder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(ViewHolder viewHolder, TicketCard model, final int position) {

                //all info show in post need put here.
                final String postKey = getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setContent(model.getContent());
                viewHolder.setPoster(model.getPoster());
                viewHolder.setImages(getApplicationContext(), model.getImages());
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());
                viewHolder.setLikeBtn(postKey);
                viewHolder.setName(model.getName());

                //onclick listener of each element can set here.

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent postIntent = new Intent(MainActivity.this,SinglePostActivity.class);
                        postIntent.putExtra("blog_id",postKey);
                        startActivity(postIntent);
                    }
                });

                //this is like button.
                viewHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mProccessLike = true;
                        mDatabaseLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if(mProccessLike){
                                    if(dataSnapshot.child(postKey).hasChild(mAuth.getCurrentUser().getUid())){
                                        mDatabaseLike.child(postKey).child(mAuth.getCurrentUser().getUid()).removeValue();
                                        Toast.makeText(MainActivity.this, "Unliked.", Toast.LENGTH_SHORT).show();
                                        mProccessLike = false;
                                    }else{
                                        mDatabaseLike.child(postKey).child(mAuth.getCurrentUser().getUid()).setValue(mAuth.getCurrentUser().getEmail());
                                        Toast.makeText(MainActivity.this, "Liked..", Toast.LENGTH_SHORT).show();
                                        mProccessLike = false;
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        //end of like button.
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}
