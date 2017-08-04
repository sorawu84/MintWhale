package com.nullgarden.mintwhale;

import android.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.*;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class PostActivity extends AppCompatActivity {

    StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    Uri resultUri;
    int G_CODE = 123;
    final int REQUEST_CODE = 2;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        ImageView doneBtn = findViewById(R.id.doneBtn);
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImageData();
            }
        });

        ImageView returnBtn = findViewById(R.id.returnBtn);
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageButton selectImageBtn = findViewById(R.id.selectImageBtn);
        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Build.VERSION.SDK_INT > 23){
                    if(ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
                        cropImage();
                    }else{
                        cropImage();
                    }
                }else{
                    cropImage();
                }
            }
        });

    }

    private void cropImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, G_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(), "Permission Granted.", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(getApplicationContext(), "Permission Denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == G_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            CropImage.activity(uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    resultUri = result.getUri();
                    ImageButton selectImageBtn = findViewById(R.id.selectImageBtn);
                    Picasso.with(PostActivity.this).load(resultUri).fit().centerCrop().into(selectImageBtn);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
    }

    private void saveImageData(){

        TextView titleField = findViewById(R.id.titleField);
        TextView contentField = findViewById(R.id.contentField);
        String title = titleField.getText().toString();
        String content = contentField.getText().toString();

        if(TextUtils.isEmpty(title) || TextUtils.isEmpty(content)){
            Toast.makeText(this, "Please fill in both title and content.", Toast.LENGTH_SHORT).show();
        }else if(resultUri == null){
            Toast.makeText(this, "Please select a picture.", Toast.LENGTH_SHORT).show();
        }else{

            final ProgressDialog progress;
            progress = new ProgressDialog(this);
            progress.setMessage("Uploading...");
            progress.show();

            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            final DatabaseReference mPost = mDatabase.child("MintPost").push();
            String simpleDate = new SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date());
            String simpleTime = new SimpleDateFormat("h:mm a").format(new java.util.Date());
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
            Long timeStampP = Long.parseLong(timeStamp);

            String timeStampR = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
            Long timeStampRR = Long.parseLong(timeStampR);
            Long timeStampRRR = -1 * timeStampRR;

            mDatabase.child("MintUsers").child(mAuth.getCurrentUser().getUid()).child("Name").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.getValue().toString();
                    mPost.child("Name").setValue(name);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mPost.child("TimeStampR").setValue(timeStampRRR);
            mPost.child("TimeStamp").setValue(timeStampP);
            mPost.child("Time").setValue(simpleTime);
            mPost.child("Date").setValue(simpleDate);
            mPost.child("Images").setValue(resultUri.toString());
            mPost.child("Poster").setValue(mAuth.getCurrentUser().getEmail());
            mPost.child("Title").setValue(titleField.getText().toString());
            mPost.child("Content").setValue(contentField.getText().toString());
            mPost.child("UID").setValue(mAuth.getCurrentUser().getUid());



            //post to self post
            DatabaseReference mSelfPost = mDatabase.child("MintSelfPost").child(mAuth.getCurrentUser().getUid()).push();
            mSelfPost.child("TimeStampR").setValue(timeStampRRR);
            mSelfPost.child("TimeStamp").setValue(timeStampP);
            mSelfPost.child("Time").setValue(simpleTime);
            mSelfPost.child("Date").setValue(simpleDate);
            mSelfPost.child("Images").setValue(resultUri.toString());
            mSelfPost.child("Title").setValue(titleField.getText().toString());
            mSelfPost.child("Content").setValue(contentField.getText().toString());


            StorageReference filepath = mStorage.child("MintImages").child(mAuth.getCurrentUser().getUid()).child(resultUri.getLastPathSegment());
            filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(PostActivity.this, "Post successful.", Toast.LENGTH_SHORT).show();
                    progress.dismiss();
                    finish();
                }
            });

        }
    }
}