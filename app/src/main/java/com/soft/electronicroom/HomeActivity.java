package com.soft.electronicroom;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.soft.electronicroom.adapter.SubCategoryAdapter;
import com.soft.electronicroom.database.MainApplication;
import com.soft.electronicroom.model.Product;
import com.soft.electronicroom.model.SubCategory;
import com.soft.electronicroom.repo.ProductRepo;
import com.soft.electronicroom.repo.SubCatgoryRepo;

public class HomeActivity extends AppCompatActivity {

    static final String PRODUCT_KEY_ID = "product_id";

    private TextInputEditText edName;
    private TextInputEditText edPrice;
    private TextInputEditText edDescription;
    private TextInputEditText edSubCategory;

    private Spinner subCategorySpinner;

    private Button btnSave;
    private Button btn_Delete;

    private SubCategoryAdapter subCategoryAdapter;

    private Product product;

    private ProductRepo productRepo;
    private SubCatgoryRepo subCatgoryRepo;

    private ArrayAdapter<SubCategory> subCategoryArrayAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        int id = getIntent().getIntExtra(PRODUCT_KEY_ID, 0);

        edName = findViewById(R.id.ed_name);
        edPrice = findViewById(R.id.ed_price);
        edDescription = findViewById(R.id.ed_description);
        subCategorySpinner = findViewById(R.id.spinnerSubCategory);
        edSubCategory = findViewById(R.id.edSubCategory);

        btnSave = findViewById(R.id.btn_save);
        btn_Delete = findViewById(R.id.btn_delete);

        subCategoryArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        subCategoryAdapter = new SubCategoryAdapter();
        productRepo = new ProductRepo(MainApplication.getCreateDatabase(this).productDAO());

        subCatgoryRepo = new SubCatgoryRepo(MainApplication.getCreateDatabase(this).subCategoryDAO());

        Thread adapterThread = new Thread(() -> {
            subCategoryArrayAdapter.addAll(subCatgoryRepo.findAll());
//            Log.d("TAG", "" + subCatgoryRepo.findAll().size());
            subCategorySpinner.post(() -> subCategorySpinner.setAdapter(subCategoryArrayAdapter));

        });

        adapterThread.start();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (id > 0) {
//            Log.d("ID", "id_" + id);
            Thread findThread = new Thread(() -> {
                product = productRepo.findById(id);
                edName.post(() -> edName.setText(product.getName()));
                edPrice.post(() -> edPrice.setText(String.valueOf(product.getPrice())));
                edDescription.post(() -> edDescription.setText(product.getDescription()));
                SubCategory subCategory = subCatgoryRepo.findById(product.getSubCategoryId());
                edSubCategory.post(() -> edSubCategory.setText(subCategory.getName()));
            });
            findThread.start();
        } else {
            product = new Product();
        }

        subCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SubCategory subCategory = subCategoryArrayAdapter.getItem(position);
                edSubCategory.setText(subCategory.getName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        edSubCategory.setOnKeyListener((v, keyCode, event) -> true);

        edSubCategory.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                subCategorySpinner.performClick();
            }
            return true;
        });

        btnSave.setOnClickListener(v -> {
            Thread saveThread = new Thread(() -> {
                product.setName(edName.getText().toString());
                product.setPrice(Double.parseDouble(edPrice.getText().toString()));
                product.setDescription(edDescription.getText().toString());
                SubCategory subCategory = (SubCategory) subCategorySpinner.getSelectedItem();
                product.setSubCategoryId(subCategory.getId());
                productRepo.save(product);
            });
            saveThread.start();
            finish();
        });

        btn_Delete.setOnClickListener(v -> {
            Thread deleteThread = new Thread(() -> productRepo.delete(product));
            deleteThread.start();
            finish();
        });

    }
}
