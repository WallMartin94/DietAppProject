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

public class AddItemFragment extends Fragment {

    static final int  REQUEST_IMAGE_CAPTURE = 1;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_additem, container, false);
        Button scanButton = v.findViewById(R.id.scanButton);


        scanButton.setOnClickListener(view -> {
            launchCamera();


        });

        return v;
    }

    public void launchCamera(){

        Intent startScanIntent = new Intent (MediaStore.ACTION_IMAGE_CAPTURE);
        try{
            startActivityForResult(startScanIntent,REQUEST_IMAGE_CAPTURE);
        }catch (ActivityNotFoundException e){

        }
    }

}

