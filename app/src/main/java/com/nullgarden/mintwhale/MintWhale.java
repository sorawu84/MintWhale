package com.nullgarden.mintwhale;

import android.app.Application;
import android.os.Build;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;


public class MintWhale extends Application{

    @Override
    public void onCreate(){
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);



        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);


    }

}
