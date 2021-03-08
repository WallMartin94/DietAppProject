package com.example.dietappproject.fooditemtab;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executors;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import com.example.dietappproject.R;

public class AddItemFragment extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_additem);

        Button scanButton = findViewById(R.id.scanButton);

        scanButton.setOnClickListener(view -> {
            launchCamera();


        });


    }

    public void launchCamera(){

        Intent startScanIntent = new Intent (this,BarcodeScannerActivity.class);
        try{
            startActivity(startScanIntent);
        }catch (ActivityNotFoundException e){

        }
    }

}

