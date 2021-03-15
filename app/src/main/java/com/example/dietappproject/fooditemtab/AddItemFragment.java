package com.example.dietappproject.fooditemtab;

import android.content.ActivityNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dietappproject.R;
import com.example.dietappproject.fooditemtab.Dialogs.DatabaseErrorDialog;
import com.example.dietappproject.fooditemtab.Dialogs.ErrorDialogText;
import com.example.dietappproject.fooditemtab.Dialogs.SuccessDialog;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class AddItemFragment extends Fragment implements BarcodeFragment.CameraListenerAdd {
    private static final String TAG = "AddItemFragment";

    View view;
    Button scanButton;
    EditText barCodeTextView;
    EditText nameTextView;
    EditText proteinTextView;
    EditText fatTextView;
    EditText carbTextView;
    EditText calTextView;
    EditText typeTextView;
    Button addButton;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference refFoodItems = db.collection("FoodItems");

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_additem, container, false);
        addButton = view.findViewById(R.id.addButton);
        scanButton = view.findViewById(R.id.scanButton);
        barCodeTextView = view.findViewById(R.id.barcodeTextView);
        nameTextView = view.findViewById(R.id.nameTextView);
        proteinTextView = view.findViewById(R.id.proteinTextView);
        fatTextView = view.findViewById(R.id.fatTextView);
        carbTextView = view.findViewById(R.id.carbTextView);
        calTextView = view.findViewById(R.id.calTextView);
        typeTextView = view.findViewById(R.id.typeTextView);


        addButton.setOnClickListener(view -> {
            //Checking the input in the text field for errors

            try {



                String tempCal = calTextView.getText().toString();
                String tempProtein = proteinTextView.getText().toString();
                String tempCarb = carbTextView.getText().toString();
                String tempFat = fatTextView.getText().toString();
                String tempBarcode = barCodeTextView.getText().toString();

                double cal = Double.parseDouble(tempCal);
                double  protein = Double.parseDouble(tempProtein);
                double  carb = Double.parseDouble(tempCarb);
                double  fat = Double.parseDouble(tempFat);
                double  barcode = Double.parseDouble(tempBarcode);

            } catch (NumberFormatException e) {
                openDialogText();
            }
            String Cal = calTextView.getText().toString();
            String Protein = proteinTextView.getText().toString();
            String Carb = carbTextView.getText().toString();
            String Fat = fatTextView.getText().toString();
            String Barcode = barCodeTextView.getText().toString();
            String Name = nameTextView.getText().toString();
            String Type = typeTextView.getText().toString();
            //Checking the input in the text field so that they are not empty
            if (!Barcode.equals("") && !Name.equals("") && !Protein.equals("") && !Fat.equals("") && !Carb.equals("") && !Cal.equals("")&& !Type.equals("")) {





                Map<String, String> foodItemMap = new HashMap<>();

                foodItemMap.put("Barcode", Barcode);
                foodItemMap.put("Name", Name);
                foodItemMap.put("Protein", Protein);
                foodItemMap.put("Fat", Fat);
                foodItemMap.put("Carbs", Carb);
                foodItemMap.put("Calories", Cal);
                foodItemMap.put("Type", Type);

                refFoodItems.add(foodItemMap)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                openDialogDatabaseError();
                                String error = e.getMessage();
                                Toast.makeText(getContext(), "Error:" + error, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {

                                openDialogSuccess();
                                barCodeTextView.setText("");
                                nameTextView.setText("");
                                proteinTextView.setText("");
                                fatTextView.setText("");
                                carbTextView.setText("");
                                calTextView.setText("");
                                typeTextView.setText("");
                            }
                        });


            } else {
                openDialogText();
            }

        });
        scanButton.setOnClickListener(view -> {
            launchCamera();


        });

        return view;
    }

    public void launchCamera() {


        try {
            Fragment fragment = new BarcodeFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        } catch (ActivityNotFoundException e) {

        }
    }

    public void inputFromCamera(String input) {
        barCodeTextView.setText(input);

    }


    @Override
    public void onInputCameraSentItem(String input) {
        Fragment frag = getActivity().getSupportFragmentManager().findFragmentByTag("AddItemFragment");
        ((AddItemFragment) frag).inputFromCamera(input);
    }


    public void openDialogText() {

        ErrorDialogText errorDialogText = new ErrorDialogText();
        errorDialogText.show(getFragmentManager(), "Incorrect Input");


    }

    public void openDialogSuccess() {
        SuccessDialog successDialog = new SuccessDialog();
        successDialog.show(getFragmentManager(), "Successful Input");


    }

    public void openDialogDatabaseError() {
        DatabaseErrorDialog databaseErrorDialog = new DatabaseErrorDialog();
        databaseErrorDialog.show(getFragmentManager(), "Database Error");


    }

}

