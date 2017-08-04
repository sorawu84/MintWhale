package com.nullgarden.mintwhale;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class AccountActivity extends AppCompatActivity {

    DatabaseReference mRef;
    FirebaseAuth mAuth;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        recyclerView = findViewById(R.id.selfPost);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final ImageView userProfilePic = findViewById(R.id.userProfilePic);
        final TextView userNameTv = findViewById(R.id.userNameTv);

        ImageView postBtn = findViewById(R.id.postBtn);
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountActivity.this,PostActivity.class));
                finish();
            }
        });

        ImageView goHomeBtn = findViewById(R.id.goHomeBtn);
        goHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountActivity.this, MainActivity.class));
                finish();
            }
        });

        TextView editProfile = findViewById(R.id.editProfileTv);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AccountActivity.this, EditProfileActivity.class));
                finish();
            }
        });

        mRef = FirebaseDatabase.getInstance().getReference();
        mRef.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        mRef.child("MintUsers").child(mAuth.getCurrentUser().getUid()).child("Name")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getValue() != null){
                            String name = dataSnapshot.getValue(String.class);
                            userNameTv.setText(name);
                        }else {
                            userNameTv.setText("Noname");
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        mRef.child("MintUsers").child(mAuth.getCurrentUser().getUid()).child("ProfilePicUri")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getValue() != null){
                            final String uri = dataSnapshot.getValue(String.class);
                            Picasso.with(AccountActivity.this).load(uri).networkPolicy(NetworkPolicy.OFFLINE).fit().centerCrop().into(userProfilePic, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    Picasso.with(AccountActivity.this).load(uri).fit().centerCrop().into(userProfilePic);
                                }
                            });

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    public static class ViewHolderSelf extends RecyclerView.ViewHolder{

        View mView;

        public ViewHolderSelf(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title){
            TextView setTitle = mView.findViewById(R.id.selfTitleTv);
            setTitle.setText(title);
        }

        public void setContent(String content){
            TextView setContent = mView.findViewById(R.id.selfContentTv);
            setContent.setText(content);
        }

        public void setDate(String date){
            TextView setDate = mView.findViewById(R.id.selfDateTv);
            setDate.setText(date);
        }

        public void setTime(String time){
            TextView setTime = mView.findViewById(R.id.selfTimeTv);
            setTime.setText(time);
        }

        public void setImages(final Context context, final String images){
            final ImageView setImage = mView.findViewById(R.id.selfPostImage);
            Picasso.with(context).load(images).networkPolicy(NetworkPolicy.OFFLINE).into(setImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context).load(images).into(setImage);
                    }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        DatabaseReference mSelfRef = FirebaseDatabase.getInstance().getReference().child("MintSelfPost").child(mAuth.getCurrentUser().getUid());
        mSelfRef.keepSynced(true);

        Query query = mSelfRef.orderByChild("TimeStampR");
        FirebaseRecyclerAdapter<SelfTicketCard,ViewHolderSelf> fra = new FirebaseRecyclerAdapter<SelfTicketCard, ViewHolderSelf>(
                SelfTicketCard.class,
                R.layout.selk_card_ticket,
                ViewHolderSelf.class,
                query
        ) {
            @Override
            protected void populateViewHolder(ViewHolderSelf viewHolder, SelfTicketCard model, int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setContent(model.getContent());
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());
                viewHolder.setImages(getApplicationContext(),model.getImages());
            }
        };
        recyclerView.setAdapter(fra);
    }

}


