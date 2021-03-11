
package com.example.dietappproject.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.dietappproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.core.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    private EditText email, password, passwordRepeat, name;
    private String emailText,passwordText, nameText, genderText, dobText;
    private DatePicker dateOfBirth;
    private NumberPicker gender;
    private FirebaseAuth auth;

    private FirebaseFirestore db;
    private Map<String, Object> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);
        passwordRepeat = findViewById(R.id.register_password_repeat);
        name = findViewById(R.id.register_name);
        gender = findViewById(R.id.register_gender);
        dateOfBirth =findViewById(R.id.register_date_of_birth);

        String[] genders = {"Male", "Female"};
        gender.setMaxValue(1);
        gender.setDisplayedValues(genders);
        gender.setValue(0);
        dateOfBirth.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        auth = FirebaseAuth.getInstance();
        dateOfBirth.setMaxDate(System.currentTimeMillis());
        dateOfBirth.init(2000,6,6,null);
        db = FirebaseFirestore.getInstance();
    }

    public void register(android.view.View view) {
        readValues();

        if (validate()) {
            createJsonObject();
            Intent intent = new Intent(getApplicationContext(), LogIn.class);

            SharedPreferences sharedPref = getSharedPreferences("settings",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("email", emailText);
            editor.commit();

            auth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("Registration", "createUserWithEmail:success");
                                FirebaseUser user = auth.getCurrentUser();
                                db.collection("Users").document(user.getUid()).set(data, SetOptions.merge());
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("Registration", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(Register.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void readValues(){
        emailText = email.getText().toString();
        passwordText = password.getText().toString();
        nameText = name.getText().toString();
        genderText = gender.getDisplayedValues()[gender.getValue()];
        dobText = String.valueOf(dateOfBirth.getYear());
        if (dateOfBirth.getMonth()<10) {
            dobText = dobText + "-0" + dateOfBirth.getMonth();
        }else {
            dobText = dobText+"-"+dateOfBirth.getMonth();
        }
        if (dateOfBirth.getDayOfMonth()<10){
            dobText = dobText+"-0"+dateOfBirth.getDayOfMonth();
        }else {
            dobText = dobText+"-"+dateOfBirth.getDayOfMonth();
        }
    }

    private boolean validate(){
        if (!emailText.contains("@")){
            showError("Invalid Email Address.");
            return false;
        }
        if (!passwordText.equals(passwordRepeat.getText().toString())){
            showError("Passwords must match.");
            return false;
        }
        return true;

    }

    private void createJsonObject(){
            data = new HashMap<>();
            data.put("name",nameText);
            data.put("email", emailText);
            data.put("password",passwordText);
            data.put("date_of_birth",dobText);
            data.put("gender",genderText);
            // yyyy-mm-dd - date format
    }

    private void showError(String error_message){
        Toast.makeText(this,error_message, Toast.LENGTH_SHORT).show();
    }
}