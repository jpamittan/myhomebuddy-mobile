package com.example.myhomebuddy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SellerAddProductActivity extends AppCompatActivity {

    ImageView imgvProductImage;
    String productImgUrl;
    private Boolean blnProceedRegister;
    //private static final String host = "192.168.254.101:8000";
    private static final String host = "ec2-54-89-125-177.compute-1.amazonaws.com";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String SHARED_PREFS_TOKEN = "sharedPrefsToken";
    public static final String TOKEN = "token";
    public static final String TOKEN_TYPE = "token_type";
    public static final String USER_ID = "user_id";
    public static final String USER_PROPERTIES = "user_properties";
    String token_type;
    String token;
    String user_id;
    String user_properties;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_add_product);

        imgvProductImage = findViewById(R.id.imgvProductImage);
        imgvProductImage.setImageResource(R.drawable.placeholder_image);

        Button btnProductImage = findViewById(R.id.btnProductImage);
        EditText etxtProductName = findViewById(R.id.etxtProductName);
        EditText etxtmProductDesc = findViewById(R.id.etxtmProductDesc);
        Spinner spnProductCategory = findViewById(R.id.spnProductCategory);
        Spinner spnProductSubCategory = findViewById(R.id.spnProductSubCategory);
        EditText etxtvProductPrice = findViewById(R.id.etxtvProductPrice);
        EditText etxtProductQuantity = findViewById(R.id.etxtProductQuantity);
        EditText etxtProductStockThreshold = findViewById(R.id.etxtProductStockThreshold);
        Button btnProductSave = findViewById(R.id.btnProductSave);

        SharedPreferences shared = getSharedPreferences(SHARED_PREFS_TOKEN, MODE_PRIVATE);
        token_type = shared.getString(TOKEN_TYPE, "Bearer");
        token = (shared.getString(TOKEN, ""));
        user_id = (shared.getString(USER_ID, ""));
        user_properties = (shared.getString(USER_PROPERTIES, ""));

        ArrayList<String> categoryArrLst = new ArrayList<>();
        ArrayList<String> subCategoryArrLst = new ArrayList<>();

        try {
            JSONObject joUserProperties = new JSONObject(user_properties);
            JSONObject joBusiness = joUserProperties.getJSONObject("business");
            if (joBusiness.getString("product").equals("Drinking Water")) {
                categoryArrLst.add("Distilled Water");
                categoryArrLst.add("Mineral Water");
                categoryArrLst.add("Purified Water");
                categoryArrLst.add("Spring Water");
                categoryArrLst.add("Other");

                subCategoryArrLst.add("Alkaline");
                subCategoryArrLst.add("Flavored");
                subCategoryArrLst.add("Natural");
                subCategoryArrLst.add("Sparkling");
                subCategoryArrLst.add("Tap");
                subCategoryArrLst.add("Well");
                subCategoryArrLst.add("Other");
            } else {
                categoryArrLst.add("LPG");
                categoryArrLst.add("Propane");
                categoryArrLst.add("Butane");
                categoryArrLst.add("Other");

                subCategoryArrLst.add("Cylinder");
                subCategoryArrLst.add("Tank");
                subCategoryArrLst.add("Small tank");
                subCategoryArrLst.add("Other");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            categoryArrLst
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnProductCategory.setAdapter(categoryAdapter);

        ArrayAdapter<String> subCategoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                subCategoryArrLst
        );
        subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnProductSubCategory.setAdapter(subCategoryAdapter);

        ProgressDialog progress = new ProgressDialog(SellerAddProductActivity.this);
        progress.setTitle("Loading");
        progress.setMessage("Please wait...");
        progress.setCancelable(false);

        btnProductImage.setOnClickListener(v -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1);
        });

        btnProductSave.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Add Product")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    blnProceedRegister = !productImgUrl.isEmpty() &&
                        !etxtProductName.getText().toString().isEmpty() &&
                        !etxtmProductDesc.getText().toString().isEmpty() &&
                        !etxtvProductPrice.getText().toString().isEmpty() &&
                        !etxtProductQuantity.getText().toString().isEmpty() &&
                        !etxtProductStockThreshold.getText().toString().isEmpty();
                    if (blnProceedRegister) {
                        progress.show();
                        OkHttpClient client = new OkHttpClient();
                        String json = "{"
                            + "\"name\" : \"" + etxtProductName
                                .getText()
                                .toString() + "\","
                            + "\"description\" : \"" + etxtmProductDesc
                                .getText()
                                .toString() + "\","
                            + "\"category\" : \"" + spnProductCategory
                                .getSelectedItem()
                                .toString() + "\","
                            + "\"sub_category\" : \"" + spnProductSubCategory
                                .getSelectedItem()
                                .toString() + "\","
                            + "\"image\" : \"" + productImgUrl + "\","
                            + "\"price\" : " + etxtvProductPrice.getText().toString() + ","
                            + "\"quantity\" : " + etxtProductQuantity.getText().toString() + ","
                            + "\"stock_threshold\" : " + etxtProductStockThreshold
                                .getText()
                                .toString() + ""
                        + "}";
                        Log.d("json", json);
                        RequestBody body = RequestBody.create(JSON, json);
                        Request request = new Request.Builder()
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json")
                            .header(
                                "Authorization",
                                token_type + " " + token
                            )
                            .url("http://" + host + "/api/seller/product")
                            .post(body)
                            .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) {
                                if (response.isSuccessful()) {
                                    try {
                                        JSONObject res = new JSONObject(response.body().string());
                                        Log.d("Code", String.valueOf(response.code()));
                                        Log.i("RS", String.valueOf(res));
                                        if (response.code() == 201) {
                                            new Handler(Looper.getMainLooper()).post(() -> {
                                                progress.dismiss();
                                                Intent returnIntent = new Intent();
                                                setResult(
                                                    SellerAddProductActivity.RESULT_OK,
                                                    returnIntent
                                                );
                                                finish();
                                            });
                                        }
                                    } catch (JSONException | IOException e) {
                                        Log.e("RSx", e.getMessage());
                                    }
                                } else {
                                    try {
                                        JSONObject res = new JSONObject(response.body().string());
                                        Log.d("RF", res.toString());
                                        new Handler(Looper.getMainLooper()).post(() -> {
                                            progress.dismiss();
                                            Toast.makeText(
                                                SellerAddProductActivity.this,
                                                "An error has occured.",
                                                Toast.LENGTH_LONG
                                            ).show();
                                        });
                                    } catch (JSONException | IOException e) {
                                        Log.e("RFx", e.getMessage());
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                new Handler(Looper.getMainLooper()).post(progress::dismiss);
                                Log.e("Fx", e.getMessage());
                            }
                        });
                    } else {
                        Toast.makeText(
                            SellerAddProductActivity.this,
                            "Please fill up all the fields.",
                            Toast.LENGTH_LONG
                        ).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();
                String filePath = ImageFilePath.getPath(
                    SellerAddProductActivity.this,
                    selectedImage
                );
                String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);
                Log.i("Img", filePath);
                Log.i("Ext", file_extn);
                if (
                    file_extn.equals("img") ||
                    file_extn.equals("jpg") ||
                    file_extn.equals("jpeg") ||
                    file_extn.equals("gif") ||
                    file_extn.equals("png")
                ) {
                    Bitmap bmp = null;
                    try {
                        bmp = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(),
                            selectedImage
                        );
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    imgvProductImage.setImageResource(android.R.color.transparent);
                    imgvProductImage.setImageBitmap(bmp);
                    imgvProductImage.setVisibility(View.VISIBLE);

                    @SuppressLint("SimpleDateFormat")
                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")
                            .format(new Date());
                    String hashstr = timeStamp + "." + (Math.random()* 100);

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage
                            .getReferenceFromUrl("gs://myhomebuddy-6e4a8.appspot.com");
                    String imgFileName = String.format("%s-%s-%s",
                        user_id,
                        hashstr.hashCode(),
                        file_extn
                    );

                    StorageReference sr = storageRef.child("products/" + imgFileName);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] bt = baos.toByteArray();

                    UploadTask uploadtask = sr.putBytes(bt);
                    uploadtask.addOnFailureListener(exception ->
                            Log.e("FBFx", exception.getMessage())
                    )
                    .addOnSuccessListener(taskSnapshot ->
                        sr.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                            productImgUrl = downloadUrl.toString();
                            Log.i("ImgURL", downloadUrl.toString());
                        })
                    );
                } else {
                    Toast.makeText(
                        SellerAddProductActivity.this,
                        "Invalid file format.",
                        Toast.LENGTH_LONG
                    ).show();
                }
            }
        }
    }
}