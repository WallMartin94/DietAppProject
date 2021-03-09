package com.example.dietappproject.utils;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.budiyev.android.codescanner.ScanMode;
import com.example.dietappproject.R;
import com.google.zxing.Result;

public class BarcodeScannerFragment extends Fragment {
    private static final String TAG = "BarcodeScannerFragment";
    private CodeScanner mCodeScanner;

    //Interface to send scanned string to AddMealFragment, through MainActivity
    public interface CameraListener {
        void onInputCameraSent(String input);
    }
    private CameraListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final Activity activity = getActivity();

        View root = inflater.inflate(R.layout.fragment_barcodescanner, container, false);
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);

        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setCamera(CodeScanner.CAMERA_BACK);
        mCodeScanner.setFormats(CodeScanner.ONE_DIMENSIONAL_FORMATS);
        mCodeScanner.setAutoFocusMode(AutoFocusMode.SAFE);
        mCodeScanner.setScanMode(ScanMode.CONTINUOUS);
        mCodeScanner.setAutoFocusEnabled(true);
        mCodeScanner.setFlashEnabled(false);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(TAG, result.toString());
                        Toast.makeText(activity, "Scanned barcode: " + result.getText(), Toast.LENGTH_SHORT).show();
                        listener.onInputCameraSent(result.getText());
                        getFragmentManager().popBackStackImmediate();
                    }
                });
            }
        });
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof com.example.dietappproject.utils.BarcodeScannerFragment.CameraListener) {
            listener = (com.example.dietappproject.utils.BarcodeScannerFragment.CameraListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement CameraListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}