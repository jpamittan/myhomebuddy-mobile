package com.example.myhomebuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhomebuddy.ui.reviews.Reviews;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ConsumerProductDetailActivity extends AppCompatActivity {

    private static final String host = "192.168.254.101:8000";
//    private static final String host = "ec2-54-89-125-177.compute-1.amazonaws.com";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String SHARED_PREFS_TOKEN = "sharedPrefsToken";
    public static final String TOKEN = "token";
    public static final String TOKEN_TYPE = "token_type";
    String token_type;
    String token;
    ProgressDialog progress;
    ImageView imgvCProductDetailsImage;
    EditText etxtCProductDetailsName;
    EditText etxtmCProductDetailsDesc;
    TextView txtvCProductDetailsRatings;
    RatingBar rbCProductDetails;
    TextView txtvCProductDetailsCategory;
    TextView txtvCProductDetailsSubCategory;
    TextView txtvCProductDetailsPrice;
    TextView txtvCProductDetailsQuantity;
    TextView txtvSellerName;
    TextView txtvSellerAddress;
    TextView txtvSellerAddress2;
    TextView txtvSellerAddress3;
    TextView txtvCCutomerNoReviews;
    ListView lvCCustomerReviews;
    Button btnSubmitReview;
    Button btnPlaceOrder;
    ArrayList<Reviews> reviews;
    ReviewItemAdapter reviewItemAdapter;
    int productId;
    float productPrice;
    String productImage;
    String productName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_product_detail);

        imgvCProductDetailsImage = findViewById(R.id.imgvCProductDetailsImage);
        etxtCProductDetailsName = findViewById(R.id.etxtCProductDetailsName);
        etxtmCProductDetailsDesc = findViewById(R.id.etxtmCProductDetailsDesc);
        txtvCProductDetailsRatings = findViewById(R.id.txtvCProductDetailsRatings);
        rbCProductDetails = findViewById(R.id.rbCProductDetails);
        txtvCProductDetailsCategory = findViewById(R.id.txtvCProductDetailsCategory);
        txtvCProductDetailsSubCategory = findViewById(R.id.txtvCProductDetailsSubCategory);
        txtvCProductDetailsPrice = findViewById(R.id.txtvCProductDetailsPrice);
        txtvCProductDetailsQuantity = findViewById(R.id.txtvCProductDetailsQuantity);
        txtvSellerName = findViewById(R.id.txtvSellerName);
        txtvSellerAddress = findViewById(R.id.txtvSellerAddress);
        txtvSellerAddress2 = findViewById(R.id.txtvSellerAddress2);
        txtvSellerAddress3 = findViewById(R.id.txtvSellerAddress3);
        txtvCCutomerNoReviews = findViewById(R.id.txtvCCutomerNoReviews);
        lvCCustomerReviews = findViewById(R.id.lvCCustomerReviews);
        btnSubmitReview = findViewById(R.id.btnSubmitProductReview);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        Intent intent = getIntent();
        productId = intent.getIntExtra("id", 0);
        Log.i("ID", String.valueOf(productId));

        SharedPreferences shared = getSharedPreferences(SHARED_PREFS_TOKEN, MODE_PRIVATE);
        token_type = shared.getString(TOKEN_TYPE, "Bearer");
        token = (shared.getString(TOKEN, ""));

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Please wait...");
        progress.setCancelable(false);
        progress.show();

        reviews = new ArrayList<>();

        fetchProduct(productId);

        btnSubmitReview.setOnClickListener(v -> {
            Intent review = new Intent(
                this,
                ConsumerProductReviewActivity.class
            );
            review.putExtra("id", productId);
            startActivityForResult(review, 1);
        });

        btnPlaceOrder.setOnClickListener(v -> {
            Intent order = new Intent(
                    this,
                    ConsumerPlaceOrderActivity.class
            );
            order.putExtra("id", productId);
            order.putExtra("productPrice", productPrice);
            order.putExtra("productImage", productImage);
            order.putExtra("productName", productName);
            startActivityForResult(order, 1);
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
                .url("http://" + host + "/api/consumer/product/" + productId)
                .get()
                .build();

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    Log.e("Fail", e.getMessage());
                    runOnUiThread(() -> {
                        progress.dismiss();
                        Toast.makeText(ConsumerProductDetailActivity.this,
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
                                            ConsumerProductDetailActivity.this,
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
                                    productImage = data.getString("image");
                                    Picasso.get().load(data.getString("image"))
                                        .into(imgvCProductDetailsImage);
                                    productName = data.getString("name");
                                    etxtCProductDetailsName.setText(data.getString("name"));
                                    etxtmCProductDetailsDesc.setText(
                                        data.getString("description")
                                    );
                                    float rt = Float.parseFloat(data.getString("ratings"));
                                    txtvCProductDetailsRatings.setText("Ratings: (" + rt + ")");
                                    rbCProductDetails.setRating(rt);
                                    txtvCProductDetailsCategory.setText(
                                        "Category: " + data.getString("category")
                                    );
                                    txtvCProductDetailsSubCategory.setText(
                                        "Sub-category: " + data.getString("sub_category")
                                    );
                                    productPrice = Float.parseFloat(data.getString("price"));
                                    txtvCProductDetailsPrice.setText(
                                        "Price: ₱" + data.getString("price")
                                    );
                                    if (data.getInt("quantity") > 0) {
                                        txtvCProductDetailsQuantity.setText("Available");
                                        txtvCProductDetailsQuantity.setTextColor(
                                            Color.parseColor("#28B463")
                                        );
                                    } else {
                                        txtvCProductDetailsQuantity.setText("Out of stock");
                                        txtvCProductDetailsQuantity.setTextColor(
                                            Color.parseColor("#E74C3C")
                                        );
                                        runOnUiThread(() -> btnPlaceOrder.setVisibility(View.GONE));
                                    }
                                    JSONObject joSeller = data.getJSONObject("seller");
                                    JSONObject joProperties =
                                        new JSONObject(joSeller.getString("properties"));
                                    JSONObject joBusiness = joProperties.getJSONObject("business");
                                    JSONObject joAddress = joProperties.getJSONObject("address");
                                    txtvSellerName.setText(
                                        "Name: " + joBusiness.getString("name")
                                    );
                                    txtvSellerAddress.setText(
                                        "Address: " + joAddress.getString("unit")
                                    );
                                    txtvSellerAddress2.setText(
                                        joAddress.getString("street") + ", "
                                        + joAddress.getString("barangay")
                                    );
                                    txtvSellerAddress3.setText(
                                        joAddress.getString("city") + ", "
                                        + joAddress.getString("zip")
                                    );
                                    JSONArray jaReviews = data.getJSONArray("reviews");
                                    if (jaReviews.length() > 0) {
                                        txtvCCutomerNoReviews.setVisibility(View.GONE);
                                    }
                                    reviews.clear();
                                    for (int i = 0; i < jaReviews.length(); i++) {
                                        reviews.add(new Reviews(
                                            jaReviews.getJSONObject(i).getInt("id"),
                                            jaReviews.getJSONObject(i).getJSONObject("user"),
                                            Float.parseFloat(
                                                jaReviews.getJSONObject(i)
                                                    .getString("ratings")
                                            ),
                                            jaReviews.getJSONObject(i).getString("message"),
                                            jaReviews.getJSONObject(i).getString("created_at")
                                        ));
                                    }
                                    reviewItemAdapter = new ReviewItemAdapter(
                                            ConsumerProductDetailActivity.this,
                                            R.layout.fragment_reviews_item,
                                            reviews
                                    );
                                    runOnUiThread(() -> {
                                        lvCCustomerReviews.setAdapter(reviewItemAdapter);
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        Log.e("Ex", Log.getStackTraceString(e));
                        runOnUiThread(() -> {
                            Toast.makeText(ConsumerProductDetailActivity.this,
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == ConsumerProductReviewActivity.RESULT_OK){
                fetchProduct(productId);
            }
            if(resultCode == ConsumerCheckoutMainActivity.RESULT_OK){
                fetchProduct(productId);
            }
        }
    }//onActivityResult
}