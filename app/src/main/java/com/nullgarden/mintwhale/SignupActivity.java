package com.nullgarden.mintwhale;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ProgressDialog progress;
    DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        progress = new ProgressDialog(this);
        final TextView nameField = findViewById(R.id.name2Field);
        final TextView passwordField = findViewById(R.id.password2Field);
        Button signupBtn = findViewById(R.id.signupBtn);
        Button goLoginBtn = findViewById(R.id.goLoginBtn);
        final TextView newName = findViewById(R.id.newNameField);

        goLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String finalName = nameField.getText().toString();
                final String finalPassword = passwordField.getText().toString();
                final String finalNewName = newName.getText().toString();
                if(TextUtils.isEmpty(finalName) || TextUtils.isEmpty(finalPassword) || TextUtils.isEmpty(finalNewName)){
                    Toast.makeText(getApplicationContext(),"Field cannot be empty.",Toast.LENGTH_SHORT).show();
                }else{

                    progress.setMessage("Loading..");
                    progress.show();

                    mAuth.createUserWithEmailAndPassword(finalName, finalPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String userID = mAuth.getCurrentUser().getUid();
                                String userEmail = mAuth.getCurrentUser().getEmail();
                                DatabaseReference user = mDatabase.child("MintUsers").child(userID);
                                user.child("email").setValue(userEmail);
                                user.child("Name").setValue(finalNewName);
                                progress.dismiss();
                                startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                finish();
                            }else{
                                progress.dismiss();
                                Toast.makeText(getApplicationContext(), "Signup failed, please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }



            }
        });

    }
}
