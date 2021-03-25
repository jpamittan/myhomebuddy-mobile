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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myhomebuddy.ui.reviews.Reviews;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
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
import okhttp3.ResponseBody;

public class SellerProductDetailActivity extends AppCompatActivity {

    private Boolean blnProceedRegister = false;
//    private static final String host = "192.168.254.101:8000";
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
    ProgressDialog progress;
    ImageView imgvProductDetailsImage;
    Button btnProductDetailsUpload;
    TextView etxtProductDetailsName;
    TextView etxtmProductDetailsDesc;
    Spinner spnProductDetailsCategory;
    Spinner spnProductDetailsSubCategory;
    TextView etxtProductDetailsPrice;
    TextView etxtProductDetailsQuantity;
    TextView etxtProductDetailsStockThreshold;
    Button btnProductDetailsDelete;
    Button btnProductDetailsSave;
    TextView txtvProductDetailsRatings;
    RatingBar rbProductDetails;
    TextView txtvCutomerNoReviews;
    ListView lvCustomerReviews;
    ArrayList<String> categoryArrLst;
    ArrayList<String> subCategoryArrLst;
    ArrayList<Reviews> reviews;
    ReviewItemAdapter reviewItemAdapter;
    int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_product_detail);

        imgvProductDetailsImage = findViewById(R.id.imgvProductDetailsImage);
        btnProductDetailsUpload = findViewById(R.id.btnProductDetailsUpload);
        etxtProductDetailsName = findViewById(R.id.etxtProductDetailsName);
        etxtmProductDetailsDesc = findViewById(R.id.etxtmProductDetailsDesc);
        spnProductDetailsCategory = findViewById(R.id.spnProductDetailsCategory);
        spnProductDetailsSubCategory = findViewById(R.id.spnProductDetailsSubCategory);
        etxtProductDetailsPrice = findViewById(R.id.etxtProductDetailsPrice);
        etxtProductDetailsQuantity = findViewById(R.id.etxtProductDetailsQuantity);
        etxtProductDetailsStockThreshold = findViewById(R.id.etxtProductDetailsStockThreshold);
        btnProductDetailsDelete = findViewById(R.id.btnProductDetailsDelete);
        btnProductDetailsSave = findViewById(R.id.btnProductDetailsSave);
        txtvProductDetailsRatings = findViewById(R.id.txtvProductDetailsRatings);
        rbProductDetails = findViewById(R.id.rbProductDetails);
        lvCustomerReviews = findViewById(R.id.lvCustomerReviews);
        txtvCutomerNoReviews = findViewById(R.id.txtvCutomerNoReviews);

        SharedPreferences shared = getSharedPreferences(SHARED_PREFS_TOKEN, MODE_PRIVATE);
        token_type = shared.getString(TOKEN_TYPE, "Bearer");
        token = (shared.getString(TOKEN, ""));
        user_id = (shared.getString(USER_ID, ""));
        user_properties = (shared.getString(USER_PROPERTIES, ""));

        categoryArrLst = new ArrayList<>();
        subCategoryArrLst = new ArrayList<>();

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
        spnProductDetailsCategory.setAdapter(categoryAdapter);

        ArrayAdapter<String> subCategoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                subCategoryArrLst
        );
        subCategoryAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnProductDetailsSubCategory.setAdapter(subCategoryAdapter);

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Please wait...");
        progress.setCancelable(false);
        progress.show();

        reviews = new ArrayList<>();

        Intent intent = getIntent();
        productId = intent.getIntExtra("id", 0);
        fetchProduct(productId);

        btnProductDetailsUpload.setOnClickListener(v -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1);
        });

        btnProductDetailsDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Delete Product")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                            .header("Accept", "application/json")
                            .header("Content-Type", "application/json")
                            .header(
                                    "Authorization",
                                    token_type + " " + token
                            )
                            .url("http://" + host + "/api/seller/product/" + productId)
                            .delete()
                            .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override public void onFailure(Call call, IOException e) {
                                Log.e("Fail", e.getMessage());
                                runOnUiThread(() -> {
                                    progress.dismiss();
                                    Toast.makeText(SellerProductDetailActivity.this,
                                        "An error has occured. Please try again.",
                                        Toast.LENGTH_LONG
                                    ).show();
                                });
                            }

                            @SuppressLint("SetTextI18n")
                            @Override public void onResponse(Call call, Response response) throws IOException {
                                try {
                                    ResponseBody responseBody = response.body();
                                    JSONObject joProduct = new JSONObject(responseBody.string());

                                    if (!response.isSuccessful()) {
                                        runOnUiThread(() -> {
                                            progress.dismiss();
                                            try {
                                                Toast.makeText(
                                                    SellerProductDetailActivity.this,
                                                    joProduct.getString("error"),
                                                    Toast.LENGTH_LONG
                                                ).show();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        });
                                    } else {
                                        runOnUiThread(() -> progress.dismiss());
                                        Log.i("Delete", joProduct.getString("message"));
                                        Intent returnIntent = new Intent();
                                        setResult(
                                                SellerAddProductActivity.RESULT_OK,
                                                returnIntent
                                        );
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    Log.e("Ex", Log.getStackTraceString(e));
                                    runOnUiThread(() -> {
                                        Toast.makeText(SellerProductDetailActivity.this,
                                            "An error has occured. Please try again.",
                                            Toast.LENGTH_LONG
                                        ).show();
                                    });
                                }
                            }
                        });
                    } catch (Exception e) {
                        progress.dismiss();
                        Log.e("Ex", Log.getStackTraceString(e));
                        Toast.makeText(this,
                                "An error has occured. Please try again.",
                                Toast.LENGTH_LONG
                        ).show();
                    }

                })
                .setNegativeButton(android.R.string.no, null).show();
        });

        btnProductDetailsSave.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Update Product")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    blnProceedRegister = !etxtProductDetailsName.getText().toString().isEmpty() &&
                        !etxtmProductDetailsDesc.getText().toString().isEmpty() &&
                        !etxtProductDetailsPrice.getText().toString().isEmpty() &&
                        !etxtProductDetailsQuantity.getText().toString().isEmpty() &&
                        !etxtProductDetailsStockThreshold.getText().toString().isEmpty();
                    if (blnProceedRegister) {
                        progress.show();
                        OkHttpClient client = new OkHttpClient();
                        String json = "{"
                            + "\"name\" : \"" + etxtProductDetailsName
                            .getText()
                            .toString() + "\","
                            + "\"description\" : \"" + etxtmProductDetailsDesc
                            .getText()
                            .toString() + "\","
                            + "\"category\" : \"" + spnProductDetailsCategory
                            .getSelectedItem()
                            .toString() + "\","
                            + "\"sub_category\" : \"" + spnProductDetailsSubCategory
                            .getSelectedItem()
                            .toString() + "\","
                            + "\"price\" : " + etxtProductDetailsPrice.getText().toString() + ","
                            + "\"quantity\" : " + etxtProductDetailsQuantity.getText().toString() + ","
                            + "\"stock_threshold\" : " + etxtProductDetailsStockThreshold
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
                            .url("http://" + host + "/api/seller/product/" + productId)
                            .put(body)
                            .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) {
                                runOnUiThread(() -> progress.dismiss());
                                if (response.isSuccessful()) {
                                    try {
                                        JSONObject res = new JSONObject(response.body().string());
                                        Log.d("Code", String.valueOf(response.code()));
                                        Log.i("RS", String.valueOf(res));
                                        if (response.code() == 200) {
                                            Intent returnIntent = new Intent();
                                            setResult(
                                                SellerAddProductActivity.RESULT_OK,
                                                returnIntent
                                            );
                                            finish();
                                        }
                                    } catch (JSONException | IOException e) {
                                        Log.e("RSx", e.getMessage());
                                    }
                                } else {
                                    try {
                                        JSONObject res = new JSONObject(response.body().string());
                                        Log.d("RF", res.toString());
                                        new Handler(Looper.getMainLooper()).post(() -> {
                                            Toast.makeText(
                                                SellerProductDetailActivity.this,
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
                                runOnUiThread(() -> progress.dismiss());
                                Log.e("Fx", e.getMessage());
                            }
                        });
                    } else {
                        Toast.makeText(
                            SellerProductDetailActivity.this,
                            "Please fill up all the fields.",
                            Toast.LENGTH_LONG
                        ).show();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
        });
    }

    private void fetchProduct(int productId) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header(
                        "Authorization",
                        token_type + " " + token
                )
                .url("http://" + host + "/api/seller/product/" + productId)
                .get()
                .build();

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    Log.e("Fail", e.getMessage());
                    runOnUiThread(() -> {
                        progress.dismiss();
                        Toast.makeText(SellerProductDetailActivity.this,
                                "An error has occured. Please try again.",
                                Toast.LENGTH_LONG
                        ).show();
                    });
                }

                @SuppressLint("SetTextI18n")
                @Override public void onResponse(Call call, Response response) throws IOException {
                    try {
                        ResponseBody responseBody = response.body();
                        JSONObject joProduct = new JSONObject(responseBody.string());
                        if (!response.isSuccessful()) {
                            runOnUiThread(() -> {
                                progress.dismiss();
                                try {
                                    Toast.makeText(
                                        SellerProductDetailActivity.this,
                                        joProduct.getString("error"),
                                        Toast.LENGTH_LONG
                                    ).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            JSONObject data = joProduct.getJSONObject("data");
                            Log.i("Product", data.toString());

                            runOnUiThread(() -> {
                                progress.dismiss();
                                try {
                                    Picasso.get().load(data.getString("image"))
                                            .into(imgvProductDetailsImage);
                                    etxtProductDetailsName.setText(data.getString("name"));
                                    etxtmProductDetailsDesc.setText(data.getString("description"));
                                    spnProductDetailsCategory.setSelection(
                                        categoryArrLst.indexOf(data.getString("category"))
                                    );
                                    spnProductDetailsSubCategory.setSelection(
                                        subCategoryArrLst.indexOf(data.getString("sub_category"))
                                    );
                                    etxtProductDetailsPrice.setText(data.getString("price"));
                                    etxtProductDetailsQuantity.setText(
                                        String.valueOf(data.getInt("quantity"))
                                    );
                                    etxtProductDetailsStockThreshold.setText(
                                        String.valueOf(data.getInt("stock_threshold"))
                                    );
                                    Float rt = Float.parseFloat(data.getString("ratings"));
                                    txtvProductDetailsRatings.setText("Product ratings: (" + rt + ")");
                                    rbProductDetails.setRating(rt);

                                    JSONArray jaReviews = data.getJSONArray("reviews");

                                    if (jaReviews.length() > 0) {
                                        txtvCutomerNoReviews.setVisibility(View.GONE);
                                    }

                                    reviews.clear();
                                    for (int i = 0; i < jaReviews.length(); i++) {
                                        reviews.add(new Reviews(
                                            jaReviews.getJSONObject(i).getInt("id"),
                                            jaReviews.getJSONObject(i).getJSONObject("user"),
                                            Float.parseFloat(jaReviews.getJSONObject(i).getString("ratings")),
                                            jaReviews.getJSONObject(i).getString("message"),
                                            jaReviews.getJSONObject(i).getString("created_at")
                                        ));
                                    }
                                    reviewItemAdapter = new ReviewItemAdapter(
                                        SellerProductDetailActivity.this,
                                        R.layout.fragment_reviews_item,
                                        reviews
                                    );
                                    runOnUiThread(() -> lvCustomerReviews.setAdapter(reviewItemAdapter));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        Log.e("Ex", Log.getStackTraceString(e));
                        runOnUiThread(() -> {
                            Toast.makeText(SellerProductDetailActivity.this,
                                "An error has occured. Please try again.",
                                Toast.LENGTH_LONG
                            ).show();
                        });
                    }
                }
            });
        } catch (Exception e) {
            progress.dismiss();
            Log.e("Ex", Log.getStackTraceString(e));
            Toast.makeText(this,
                    "An error has occured. Please try again.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                progress.show();
                Uri selectedImage = data.getData();
                String filePath = ImageFilePath.getPath(
                        SellerProductDetailActivity.this,
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

                    imgvProductDetailsImage.setImageResource(android.R.color.transparent);
                    imgvProductDetailsImage.setImageBitmap(bmp);
                    imgvProductDetailsImage.setVisibility(View.VISIBLE);

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
                                Log.i("ImgURL", downloadUrl.toString());

                                OkHttpClient client = new OkHttpClient();
                                String json = "{"
                                    + "\"image\" : \"" + downloadUrl.toString() + "\""
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
                                    .url("http://" + host + "/api/seller/product/" + productId)
                                    .put(body)
                                    .build();

                                client.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                                        progress.dismiss();
                                        if (response.isSuccessful()) {
                                            try {
                                                JSONObject res = new JSONObject(response.body().string());
                                                Log.d("Code", String.valueOf(response.code()));
                                                Log.i("RS", String.valueOf(res));
                                            } catch (JSONException | IOException e) {
                                                Log.e("RSx", e.getMessage());
                                            }
                                        } else {
                                            try {
                                                JSONObject res = new JSONObject(response.body().string());
                                                Log.d("RF", res.toString());
                                                new Handler(Looper.getMainLooper()).post(() -> {
                                                    Toast.makeText(
                                                        SellerProductDetailActivity.this,
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
                                        progress.dismiss();
                                        Log.e("Fx", e.getMessage());
                                    }
                                });
                            })
                        );
                } else {
                    Toast.makeText(
                            SellerProductDetailActivity.this,
                            "Invalid file format.",
                            Toast.LENGTH_LONG
                    ).show();
                }
            }
        }
    }
}