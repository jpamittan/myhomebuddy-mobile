package com.example.myhomebuddy.ui.schedule;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myhomebuddy.ConsumerOrderDetailActivity;
import com.example.myhomebuddy.OrderItemAdapter;
import com.example.myhomebuddy.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ScheduleFragment extends Fragment {

//    private static final String host = "192.168.254.101:8000";
    private static final String host = "ec2-54-89-125-177.compute-1.amazonaws.com";
    public static final String SHARED_PREFS_TOKEN = "sharedPrefsToken";
    public static final String TOKEN = "token";
    public static final String TOKEN_TYPE = "token_type";
    String token_type;
    String token;
    ListView lvSchedule;
    ArrayList<Order> orders;
    OrderItemAdapter orderItemAdapter;
    ProgressDialog progress;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_schedule, container, false);
        lvSchedule = root.findViewById(R.id.lvSchedules);
        orders = new ArrayList<>();

        SharedPreferences shared = getActivity().getSharedPreferences(
                SHARED_PREFS_TOKEN,
                Context.MODE_PRIVATE
        );
        token_type = shared.getString(TOKEN_TYPE, "Bearer");
        token = (shared.getString(TOKEN, ""));

        progress = new ProgressDialog(this.getContext());
        progress.setTitle("Loading");
        progress.setMessage("Please wait...");
        progress.setCancelable(false);
        progress.show();

        fetchOrders();

        lvSchedule.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getActivity(), ConsumerOrderDetailActivity.class);
            intent.putExtra("id", orders.get(position).getId());
            startActivity(intent);
        });

        return root;
    }

    private void fetchOrders() {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header(
                        "Authorization",
                        token_type + " " + token
                )
                .url("http://" + host + "/api/order")
                .get()
                .build();

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    progress.dismiss();
                    Log.e("Fail", e.getMessage());
                    Toast.makeText(
                            getActivity(),
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
                            getActivity()
                                .runOnUiThread(() -> {
                                    try {
                                        Toast.makeText(
                                                getActivity(),
                                                joProducts.getString("error"),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                });
                        } else {
                            JSONArray dataArr = joProducts.getJSONArray("data");
                            Log.i("Order", dataArr.toString());

                            orders.clear();
                            for (int i = 0; i < dataArr.length(); i++) {
                                String sellerProps = dataArr.getJSONObject(i)
                                    .getJSONObject("product")
                                    .getJSONObject("seller")
                                    .getString("properties");
                                JSONObject joSellerProps = new JSONObject(sellerProps);
                                orders.add(new Order(
                                        dataArr.getJSONObject(i).getInt("id"),
                                        dataArr.getJSONObject(i).getJSONObject("product").getInt("id"),
                                        dataArr.getJSONObject(i).getJSONObject("product").getString("image"),
                                        dataArr.getJSONObject(i).getJSONObject("product").getString("name"),
                                        joSellerProps.getJSONObject("business").getString("name"),
                                        dataArr.getJSONObject(i).getJSONObject("product").getString("category"),
                                        dataArr.getJSONObject(i).getJSONObject("product").getString("sub_category")
                                ));
                            }
                            orderItemAdapter = new OrderItemAdapter(
                                getActivity(),
                                R.layout.fragment_order_item,
                                orders
                            );
                            getActivity()
                                    .runOnUiThread(() -> lvSchedule.setAdapter(orderItemAdapter));
                        }
                    } catch (JSONException e) {
                        Log.e("Ex", Log.getStackTraceString(e));
                        Toast.makeText(getActivity(),
                                "An error has occured. Please try again.",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
            });
        } catch (Exception e) {
            progress.dismiss();
            Log.e("Ex", Log.getStackTraceString(e));
            Toast.makeText(getActivity(),
                    "An error has occured. Please try again.",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}