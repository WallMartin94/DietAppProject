package com.example.dietappproject.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import com.example.dietappproject.MainActivity;
import com.example.dietappproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogIn extends AppCompatActivity {

    private FirebaseAuth auth;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private SharedPreferences sharedPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        sharedPref = getSharedPreferences("settings", MODE_PRIVATE);
        String emailStored = sharedPref.getString("email", null);
        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.editTextTextEmailAddress);

        mPasswordView = (EditText) findViewById(R.id.editTextTextPassword);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        auth = FirebaseAuth.getInstance();

        Button mEmailSignInButton = (Button) findViewById(R.id.button_login);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        Button sighUpButton = findViewById(R.id.button_register_from_login);
        sighUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        if (emailStored != null) {
            mEmailView.setText(emailStored);
            mPasswordView.requestFocus();
        }
    }


    private void signUp() {
        startActivity(new Intent(this, Register.class));
    }


    private void attemptLogin() {
        final String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Login", "signInWithEmail:success");
                    FirebaseUser user = auth.getCurrentUser();
                    Intent intent = new Intent(LogIn.this, MainActivity.class);
                    intent.putExtra("user", user.getUid());

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("email", email);
                    editor.commit();

                    startActivity(intent);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Login", "signInWithEmail:failure", task.getException());
                    Toast.makeText(LogIn.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
