package com.nullgarden.mintwhale;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.storage.StorageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class EditProfileActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST = 2;
    Uri resultUri;
    DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
    StorageReference mStorage = FirebaseStorage.getInstance().getReference();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final int REQUEST_CODE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ImageView returnEditBtn = findViewById(R.id.returnEditBtn);
        ImageView changePP = findViewById(R.id.changePP);
        TextView changePPTv = findViewById(R.id.changPPTv);
        ImageView doneBtn = findViewById(R.id.doneEditBtn);

        Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditProfileActivity.this, LoginActivity.class));
                mAuth.signOut();
                finish();
            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanged();
            }
        });

        changePP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });

        changePPTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });

        returnEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditProfileActivity.this, AccountActivity.class));
                finish();
            }
        });

    }

    private void cropImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    resultUri = result.getUri();
                    ImageView changepp = findViewById(R.id.changePP);
                    Picasso.with(EditProfileActivity.this).load(resultUri).fit().centerCrop().into(changepp);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }
        }  //end of crop images.

    private void checkPermission(){
        if(Build.VERSION.SDK_INT > 23){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE);

            }else{
                Toast.makeText(getApplicationContext(), "Granted earlier.", Toast.LENGTH_SHORT).show();
                cropImage();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Good old phone.", Toast.LENGTH_SHORT).show();
            cropImage();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(), "Permission Granted.", Toast.LENGTH_SHORT).show();
                    cropImage();
                }else{
                    Toast.makeText(getApplicationContext(), "Permission Denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveChanged(){
        TextView changeNameField = findViewById(R.id.changeNameField);
        String name = changeNameField.getText().toString();
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Loading...");
        progress.show();
        if(TextUtils.isEmpty(name) && resultUri == null){    //no save
            Toast.makeText(getApplicationContext(),"Nothing saved.",Toast.LENGTH_SHORT).show();
            moveOn();
        }else if(TextUtils.isEmpty(name) && resultUri != null){    //only save image
            saveImage();
            Toast.makeText(getApplicationContext(),"Profile picture saved.",Toast.LENGTH_SHORT).show();
            moveOn();
        }else if(!TextUtils.isEmpty(name) && resultUri == null){    //only save name
            saveName();
            Toast.makeText(getApplicationContext(),"User name saved.",Toast.LENGTH_SHORT).show();
            moveOn();
        }else if(!TextUtils.isEmpty(name) && resultUri != null){    //save both
            saveImage();
            saveName();
            Toast.makeText(getApplicationContext(),"Profile picture and user name saved.",Toast.LENGTH_SHORT).show();
            moveOn();
        }
        progress.dismiss();
    }

    private void saveImage(){
        mStorage.child("ProfilePic").child(mAuth.getCurrentUser().getUid()).child("ProfilePic")
                .putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUri = taskSnapshot.getDownloadUrl();
                mReference.child("MintUsers").child(mAuth.getCurrentUser().getUid()).child("ProfilePicUri").setValue(downloadUri.toString());
                moveOn();
            }
        });
    }
    private void saveName(){
        TextView newNameTv = findViewById(R.id.changeNameField);
        String newName = newNameTv.getText().toString();
        mReference.child("MintUsers").child(mAuth.getCurrentUser().getUid()).child("Name").setValue(newName);
        moveOn();
    }

    private void moveOn(){
        startActivity(new Intent(EditProfileActivity.this, AccountActivity.class));
        finish();
    }

}
