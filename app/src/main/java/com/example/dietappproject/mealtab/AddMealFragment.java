package com.example.dietappproject.mealtab;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dietappproject.R;
import com.example.dietappproject.dbobject.FoodItem;
import com.example.dietappproject.dbobject.Meal;
import com.example.dietappproject.utils.BarcodeScannerFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddMealFragment extends Fragment {
    private static final String TAG = "AddMealFragment";
    View view;

    //Firebase Connection
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference refFoodItems = db.collection("FoodItems");
    private CollectionReference refMeals = db.collection("Meals");
    private FirebaseAuth auth;

    //Firebase New Meal variables
    TimePicker timePickerMealTime;
    DatePicker datePickerMealDate;
    private String mealUser;
    private String mealCategory;
    private Date mealDate;          //Store to Firestore
    private Calendar mealDateCal;   //Temp store for date/time pickers
    private double mealFat = 0, mealCarbs = 0, mealProtein = 0, mealCalories = 0;
    private Map<String, Double> mealItems = new HashMap<>();

    //RecyclerView for added Items
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<AddMealItem> addMealItemList = new ArrayList<>();

    //Fooditem Search
    ImageButton imageButtonSearch;
    ImageButton imageButtonAdd;
    ImageButton imageButtonSave;
    ImageButton imageButtonBarcode;
    EditText editTextBarCodeId;
    EditText editTextAmount;
    TextView textViewResult;
    ArrayList<FoodItem> searchResult = new ArrayList<>();
    double itemAmount;

    //Camera permission
    private static final int CAMERA_PERMISSION_CODE = 1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_meal, container, false);

        imageButtonSearch = view.findViewById(R.id.button_add_meal_search);
        imageButtonAdd = view.findViewById(R.id.button_add_listitem);
        imageButtonSave = view.findViewById(R.id.imagebutton_add_meal_save);
        imageButtonBarcode = view.findViewById(R.id.button_add_meal_barcode);
        editTextBarCodeId = view.findViewById(R.id.edittext_add_meal_search);
        editTextAmount = view.findViewById(R.id.edittext_add_meal_amount);
        textViewResult = view.findViewById(R.id.textview_add_meal_result);
        timePickerMealTime = view.findViewById(R.id.timepicker_add_meal);
        datePickerMealDate = view.findViewById(R.id.datepicker_add_meal);

        //Set UserID from authenticated user
        auth = FirebaseAuth.getInstance();
        mealUser = auth.getUid();

        createSpinner(view);
        setupRecyclerView();

        imageButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String barcodeId = editTextBarCodeId.getText().toString();
                searchFoodItem(barcodeId);
            }
        });

        imageButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFoodItem();
            }
        });

        imageButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMeal();
            }
        });

        imageButtonBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick");
                openScanner();
            }
        });

        return view;
    }

    private void createSpinner(View view) {
        //Spinner Meal Category
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner_add_meal_category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.meal_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mealCategory = String.valueOf(parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupRecyclerView() {
        //Set up recyclerView for added foodItems
        mRecyclerView = view.findViewById(R.id.recycler_view_add_meal);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(view.getContext());
        mAdapter = new AddMealAdapter(addMealItemList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void searchFoodItem(String barcodeId) {
        //Firebase - Search for foodItem with exact barcode
        refFoodItems.whereEqualTo("barcodeId", barcodeId)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            FoodItem foodItem = documentSnapshot.toObject(FoodItem.class);
                            foodItem.setDocumentId(documentSnapshot.getId());

                            String documentId = foodItem.getDocumentId();
                            textViewResult.setText(foodItem.getName());
                            //Clear searchResult Arraylist before adding result
                            if (!searchResult.isEmpty()) {
                                searchResult.clear();
                            }
                            searchResult.add(foodItem);
                        }
                    }
                });
    }

    private void addFoodItem() {
        //Empty search field
        if (searchResult.isEmpty()) {
            Toast.makeText(getActivity(), "Please search for an item to add", Toast.LENGTH_SHORT).show();
            return;
        }
        //Empty amount field
        if (editTextAmount.getText().toString().matches("")) {
            Toast.makeText(getActivity(), "Please enter an amount to add", Toast.LENGTH_SHORT).show();
            return;
        }
        //Amount field needs to be > 0
        if (Double.parseDouble(editTextAmount.getText().toString()) <= 0) {
            Toast.makeText(getActivity(), "Please enter an amount bigger than 0", Toast.LENGTH_SHORT).show();
            return;
        }
        String searchDocumentId = searchResult.get(0).getDocumentId();
        //Item already added to list
        for (AddMealItem item : addMealItemList) {
            if (item.getDocumentId().equals(searchDocumentId)) {
                Toast.makeText(getActivity(), "Item already added to list", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        itemAmount = Double.parseDouble(editTextAmount.getText().toString());

        //Add selected fooditem to recyclerview list
        addMealItemList.add(new AddMealItem(searchDocumentId,
                searchResult.get(0).getName(),
                searchResult.get(0).getBarcodeId(),
                searchResult.get(0).getFat(),
                searchResult.get(0).getCarbs(),
                searchResult.get(0).getProtein(),
                searchResult.get(0).getCalories(),
                itemAmount));
        mAdapter.notifyDataSetChanged();

        //Clear previous search result
        textViewResult.setText("");
        editTextAmount.setText("");
        editTextBarCodeId.setText("");
        searchResult.clear();
    }

    private void saveMeal() {
        if(mealCategory == null) {
            Toast.makeText(getActivity(), "Please select a Category", Toast.LENGTH_SHORT).show();
            return;
        }

        //Store time
        mealDateCal = Calendar.getInstance();
        Log.i(TAG, String.valueOf(mealDateCal.getTime()));

        if (Build.VERSION.SDK_INT < 23){
            mealDateCal.set(datePickerMealDate.getYear(),
                    datePickerMealDate.getMonth(),
                    datePickerMealDate.getDayOfMonth(),
                    timePickerMealTime.getCurrentHour(),
                    timePickerMealTime.getCurrentMinute(),
                    00);
        } else {
            mealDateCal.set(datePickerMealDate.getYear(),
                    datePickerMealDate.getMonth(),
                    datePickerMealDate.getDayOfMonth(),
                    timePickerMealTime.getHour(),
                    timePickerMealTime.getMinute(),
                    00);
        }

        Log.i(TAG, String.valueOf(mealDateCal.getTime()));
        mealDate = new Date(mealDateCal.getTimeInMillis());

        Log.i(TAG, String.valueOf(mealDate.getTime()));

        //Add foodItem totals to Meal variables
        for (AddMealItem item : addMealItemList) {
            double amount = item.getAmount() / 100;     // Divide by 100 to get correct amount
            mealFat += item.getFat() * amount;
            mealCarbs += item.getCarbs() * amount;
            mealProtein += item.getProtein() * amount;
            mealCalories += item.getCalories() * amount;
            mealItems.put(item.getDocumentId(), amount);
        }

        //Create Meal object and add to Firestore
        Meal newMeal = new Meal(mealCategory, mealUser, mealDate,
                mealFat, mealCarbs, mealProtein, mealCalories,
                mealItems);

        //Add meal to Firebase and close fragment
        refMeals.add(newMeal);
        Toast.makeText(getActivity(), "Meal added", Toast.LENGTH_SHORT).show();
        getFragmentManager().popBackStackImmediate();
    }

    private void openScanner() {
        //If camera permission already granted -> Open Camera
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Fragment fragment = new BarcodeScannerFragment();
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment, "BarcodeFragment")
                    .addToBackStack(null)
                    .commit();
        } else {
            //If camera permission not granted, request
            requestCameraPermission();
        }
    }

    //Request camera permission
    private void requestCameraPermission() {
        Log.i(TAG, "request");
        requestPermissions(new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
    }

    //Response to camera permission decision
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "RequestCode: " + requestCode + "--CAMERA_PERM_C: " + CAMERA_PERMISSION_CODE);
        if (requestCode == CAMERA_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "permission if");
                Toast.makeText(getActivity(), "Camera permission granted", Toast.LENGTH_SHORT).show();
                openScanner();
            } else {
                Log.i(TAG, "permission else");
                Toast.makeText(getActivity(), "Camera permission required for barcode scanner", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Input from camera interface, through MainActivity
    public void inputFromCamera (String input) {
        editTextBarCodeId.setText(input);
        searchFoodItem(input);
    }
}
