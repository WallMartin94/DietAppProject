package com.example.dietappproject.fooditemtab

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import com.example.dietappproject.R

private const val CAMERA_REQUEST_CODE = 101


class BarcodeScannerActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_barcode)
        setupPermissions()
        codeScanner()
    }

    private fun codeScanner() {

        val codeTextView = findViewById<TextView>(R.id.code_text_view)
        val scanner_view = findViewById<CodeScannerView>(R.id.scanner_view)
        codeScanner = CodeScanner(this, scanner_view)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ONE_DIMENSIONAL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                runOnUiThread {

                    codeTextView.text = it.text
                }
                errorCallback = ErrorCallback {
                    runOnUiThread {
                        Log.e("Error", "Camera Failed to initalize: ${it.message}")
                    }
                }


            }
        }


    }

    override fun onResume() {

        super.onResume()

        codeScanner.startPreview()

    }

    override fun onPause() {
        super.onPause()
        codeScanner.releaseResources()

    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {

            makeRequest()
        }

    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Camera permission needed to scan code", Toast.LENGTH_LONG)
                } else {
                    //Successful
                }
            }
        }
    }
}