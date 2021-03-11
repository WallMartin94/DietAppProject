package com.example.dietappproject.fooditemtab;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;

import com.budiyev.android.codescanner.ScanMode;
import com.example.dietappproject.R;



import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;

import kotlin.jvm.internal.Intrinsics;

public class BarcodeFragment extends Fragment   {
    private CodeScanner mCodeScanner;
    private TextView code_text_view;
    private final int CAMERA_REQUEST_CODE = 101;
    private ArrayList<String> cameraList;
    private CameraListenerAdd addListener;


    public interface CameraListenerAdd{
        void onInputCameraSentItem(String input);

    }




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.fragment_barcode, container, false);
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(activity, scannerView);
        code_text_view = new TextView(this.getContext(), (AttributeSet) code_text_view);


        setUpPermissions();
        codeScanner();


        mCodeScanner.setDecodeCallback(result -> activity.runOnUiThread(()
                -> addListener.onInputCameraSentItem(result.getText())));
                    getFragmentManager().popBackStackImmediate();

        mCodeScanner.setErrorCallback(error -> activity.runOnUiThread(()
                -> Log.e("Main", "Camera failed to initalize:", error)));

        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    private final void codeScanner() {

        if (this.mCodeScanner == null) {
            Intrinsics.throwUninitializedPropertyAccessException("codeScanner");
        }


        this.mCodeScanner.setCamera(-1);
        this.mCodeScanner.setFormats(CodeScanner.ONE_DIMENSIONAL_FORMATS);
        this.mCodeScanner.setAutoFocusMode(AutoFocusMode.SAFE);
        this.mCodeScanner.setScanMode(ScanMode.CONTINUOUS);
        this.mCodeScanner.setAutoFocusEnabled(true);
        this.mCodeScanner.setFlashEnabled(false);


    }

    private void setUpPermissions() {
        int permission = ContextCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.CAMERA);


        if (permission != PackageManager.PERMISSION_GRANTED) {

            makeRequest();
        }

    }

    private void makeRequest() {


        cameraList.add(android.Manifest.permission.CAMERA);
        ActivityCompat.requestPermissions(this.getActivity(), new String[]{cameraList.get(0)}, CAMERA_REQUEST_CODE);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        Intrinsics.checkNotNullParameter(permissions, "permissions");
        Intrinsics.checkNotNullParameter(grantResults, "grantResults");
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this.getActivity(), "You need to allow camera to scan codes", Toast.LENGTH_SHORT).show();
                }
            default:




        }
    }


}