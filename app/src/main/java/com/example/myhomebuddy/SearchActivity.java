package com.example.myhomebuddy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SearchActivity extends AppCompatActivity {

//    private static final String host = "192.168.254.101:8000";
    private static final String host = "ec2-54-89-125-177.compute-1.amazonaws.com";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final String SHARED_PREFS_TOKEN = "sharedPrefsToken";
    public static final String TOKEN = "token";
    public static final String TOKEN_TYPE = "token_type";
    String token_type;
    String token;
    ProgressDialog progress;
    ListView lvSearchItems;
    String query;
    ArrayList<Search> searchItems;
    SearchItemAdapter searchItemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        lvSearchItems = findViewById(R.id.lvSearchItems);
        searchItems = new ArrayList<>();

        Intent intent = getIntent();
        query = intent.getStringExtra("search");
        Log.i("Query", String.valueOf(query));

        SharedPreferences shared = getSharedPreferences(
                SHARED_PREFS_TOKEN,
                Context.MODE_PRIVATE
        );
        token_type = shared.getString(TOKEN_TYPE, "Bearer");
        token = (shared.getString(TOKEN, ""));

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Please wait...");
        progress.setCancelable(false);
        progress.show();

        searchRecords(query);

        lvSearchItems.setOnItemClickListener((parent, view, position, id) -> {
            Intent prodItem = new Intent(this, ConsumerProductDetailActivity.class);
            prodItem.putExtra("id", searchItems.get(position).getId());
            startActivity(prodItem);
        });
    }

    private void searchRecords(String query) {
        try {
            OkHttpClient client = new OkHttpClient();
            String json = "{"
                + "\"query\" : \"" + query + "\""
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
                .url("http://" + host + "/api/search")
                .post(body)
                .build();

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    progress.dismiss();
                    Log.e("Fail", e.getMessage());
                    Toast.makeText(
                        SearchActivity.this,
                        "An error has occured. Please try again.",
                        Toast.LENGTH_LONG
                    ).show();
                }

                @Override public void onResponse(Call call, Response response) throws IOException {
                    progress.dismiss();
                    try {
                        ResponseBody responseBody = response.body();
                        JSONObject joProducts = new JSONObject(responseBody.string());

                        if (!response.isSuccessful()) {
                            runOnUiThread(() -> {
                                try {
                                    Toast.makeText(
                                        SearchActivity.this,
                                        joProducts.getString("error"),
                                        Toast.LENGTH_LONG
                                    ).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            JSONArray dataArr = joProducts.getJSONArray("data");
                            Log.i("Search", dataArr.toString());

                            searchItems.clear();
                            for (int i = 0; i < dataArr.length(); i++) {
                                JSONObject joProps = new JSONObject(
                                    dataArr.getJSONObject(i).getString("properties")
                                );
                                searchItems.add(new Search(
                                    dataArr.getJSONObject(i).getInt("id"),
                                    dataArr.getJSONObject(i).getString("name"),
                                        joProps.getJSONObject("business").getString("name"),
                                    Float.parseFloat(
                                        dataArr.getJSONObject(i).getString("ratings")
                                    ),
                                    dataArr.getJSONObject(i).getString("image")
                                ));
                            }
                            searchItemAdapter = new SearchItemAdapter(
                                SearchActivity.this,
                                R.layout.fragment_search,
                                searchItems
                            );
                            runOnUiThread(() -> lvSearchItems.setAdapter(searchItemAdapter));
                        }
                    } catch (JSONException e) {
                        Log.e("Ex", Log.getStackTraceString(e));
                        Toast.makeText(SearchActivity.this,
                            "An error has occured. Please try again.",
                            Toast.LENGTH_LONG
                        ).show();
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
}