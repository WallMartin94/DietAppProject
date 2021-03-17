package com.example.dietappproject.hometab;


import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;


import com.example.dietappproject.R;
import com.example.dietappproject.dbobject.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.Locale;
import android.icu.text.SimpleDateFormat;

public class HomeFragment extends Fragment {

    private FirebaseFirestore db;
    private String userId;

    private TextView  dateTimeDisplay;
    private TextView textViewhome;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        dateTimeDisplay = v.findViewById(R.id.dateTimeDisplay);
        textViewhome = v.findViewById(R.id.textViewhome);

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        dateTimeDisplay.setText(date);

        db = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getUid();
        db.collection("Users").document(userId)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                user.setDocumentId(documentSnapshot.getId());
                textViewhome.setText("Welcome " + String.valueOf(user.getName()));

            }});
        return v;

    }
}