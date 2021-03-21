package com.example.myhomebuddy.ui.regisration;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myhomebuddy.R;
import com.example.myhomebuddy.RegistrationSuccessful;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link registrationUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class registrationUserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Boolean blnProceedRegister;
    private static final String host = "ec2-54-89-125-177.compute-1.amazonaws.com";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public registrationUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment registrationUserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static registrationUserFragment newInstance(String param1, String param2) {
        registrationUserFragment fragment = new registrationUserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(
            R.layout.fragment_registration_user,
            container,
            false
        );

        EditText etxtFirstName = view.findViewById(R.id.etxtFirstName);
        EditText etxtMiddleName = view.findViewById(R.id.etxtMiddleName);
        EditText etxtLastName = view.findViewById(R.id.etxtLastName);
        Spinner spnMonth = view.findViewById(R.id.spnMonth);
        Spinner spnDay = view.findViewById(R.id.spnDay);
        Spinner spnYear = view.findViewById(R.id.spnYear);
        Spinner spnGender = view.findViewById(R.id.spnGender);
        EditText etxtPhBlkLtUnitBldgNo = view.findViewById(R.id.etxtPhBlkLtUnitBldgNo);
        EditText etxtSubdisivion = view.findViewById(R.id.etxtSubdisivion);
        EditText etxtStreet = view.findViewById(R.id.etxtStreet);
        EditText etxtBarangay = view.findViewById(R.id.etxtBarangay);
        EditText etxtCity = view.findViewById(R.id.etxtCity);
        EditText etxtZip = view.findViewById(R.id.etxtZip);
        EditText etxtPhoneNo = view.findViewById(R.id.etxtPhoneNo);
        EditText etxtEmail = view.findViewById(R.id.etxtEmail);
        EditText etxtPassword = view.findViewById(R.id.etxtPassword);
        EditText etxtVerifyPassword = view.findViewById(R.id.etxtVerifyPassword);
        CheckBox chkbAccept = view.findViewById(R.id.chkbAccept);
        TextView txtvTermsConditions = view.findViewById(R.id.txtvTermsConditions);
        Button btnRegister = view.findViewById(R.id.btnRegister);

        txtvTermsConditions.setMovementMethod(LinkMovementMethod.getInstance());

        ArrayList<String> years = new ArrayList<String>();
        for (int i = Calendar.getInstance().get(Calendar.YEAR); i > 1900; i--) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this.getActivity(),
                android.R.layout.simple_spinner_item,
                years
        );
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spnYear.setAdapter(adapter);

        chkbAccept.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.getId() == R.id.chkbAccept) {
                btnRegister.setEnabled(isChecked);
            }
        });

        ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setTitle("Loading");
        progress.setMessage("Please wait...");
        progress.setCancelable(false);

        btnRegister.setOnClickListener(v -> {
            blnProceedRegister = true;
            if (
                etxtFirstName.getText().toString().isEmpty() ||
                etxtLastName.getText().toString().isEmpty() ||
                etxtPassword.getText().toString().isEmpty() ||
                etxtPhBlkLtUnitBldgNo.getText().toString().isEmpty() ||
                etxtSubdisivion.getText().toString().isEmpty() ||
                etxtStreet.getText().toString().isEmpty() ||
                etxtBarangay.getText().toString().isEmpty() ||
                etxtCity.getText().toString().isEmpty() ||
                etxtZip.getText().toString().isEmpty() ||
                etxtPhoneNo.getText().toString().isEmpty() ||
                etxtEmail.getText().toString().isEmpty() ||
                etxtVerifyPassword.getText().toString().isEmpty() ||
                !etxtPassword.getText().toString().equals(etxtVerifyPassword.getText().toString())
            ) {
                blnProceedRegister = false;
            }
            if (blnProceedRegister) {
                progress.show();
                OkHttpClient client = new OkHttpClient();
                String json = "{"
                    + "\"first_name\" : \"" + etxtFirstName.getText().toString() + "\","
                    + "\"middle_name\" : \"" + etxtMiddleName.getText().toString() + "\","
                    + "\"last_name\" : \"" + etxtLastName.getText().toString() + "\","
                    + "\"email\" : \"" + etxtEmail.getText().toString() + "\","
                    + "\"password\" : \"" + etxtPassword.getText().toString() + "\","
                    + "\"birthdate_month\" : \"" + spnMonth.getSelectedItem().toString() + "\","
                    + "\"birthdate_day\" : \"" + spnDay.getSelectedItem().toString() + "\","
                    + "\"birthdate_year\" : \"" + spnYear.getSelectedItem().toString() + "\","
                    + "\"gender\" : \"" + spnGender.getSelectedItem().toString() + "\","
                    + "\"address_unit\" : \"" + etxtPhBlkLtUnitBldgNo.getText().toString() + "\","
                    + "\"address_subdivision\" : \"" + etxtSubdisivion.getText().toString() + "\","
                    + "\"address_street\" : \"" + etxtStreet.getText().toString() + "\","
                    + "\"address_barangay\" : \"" + etxtBarangay.getText().toString() + "\","
                    + "\"address_city\" : \"" + etxtCity.getText().toString() + "\","
                    + "\"address_zip\" : \"" + etxtZip.getText().toString() + "\","
                    + "\"phone_no\" : \"" + etxtPhoneNo.getText().toString() + "\""
                + "}";
                Log.d("json", json);
                RequestBody body = RequestBody.create(JSON, json);
                Request request = new Request.Builder()
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json")
                        .url("http://" + host + "/api/register/consumer")
                        .post(body)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject responseObj = new JSONObject(response.body().string());
                                Log.d("Code", String.valueOf(response.code()));
                                Log.i("RS", String.valueOf(responseObj));
                                if (response.code() == 201) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            progress.dismiss();
                                            Intent intent = new Intent(
                                                    getActivity(),
                                                    RegistrationSuccessful.class
                                            );
                                            startActivity(intent);
                                            getActivity().finish();
                                        }
                                    });
                                }
                            } catch (JSONException | IOException e) {
                                Log.e("RSx", e.getMessage());
                            }
                        } else {
                            try {
                                JSONObject responseObj = new JSONObject(response.body().string());
                                String msg = String.valueOf(responseObj.get("error"));
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.dismiss();
                                        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                                    }
                                });
                            } catch (JSONException | IOException e) {
                                Log.e("RFx", e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                progress.dismiss();
                            }
                        });
                        Log.e("Fx", e.getMessage());
                    }
                });
            } else {
                String errMsg = "Please fill up all the fields.";
                if (
                    !etxtPassword
                        .getText()
                        .toString()
                        .equals(etxtVerifyPassword.getText().toString())
                ) {
                    errMsg = "Password didn't match";
                }
                Toast.makeText(
                    this.getContext(),
                    errMsg,
                    Toast.LENGTH_LONG
                ).show();
            }
        });

        return view;
    }
}