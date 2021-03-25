package com.example.myhomebuddy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class SellerProfileActivity extends AppCompatActivity {

    public static final String SHARED_PREFS_TOKEN = "sharedPrefsToken";
    public static final String USER_FIRST_NAME = "user_first_name";
    public static final String USER_MIDDLE_NAME = "user_middle_name";
    public static final String USER_LAST_NAME = "user_last_name";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_PROPERTIES = "user_properties";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile);

        SharedPreferences spUser = getSharedPreferences(
                SHARED_PREFS_TOKEN,
                Context.MODE_PRIVATE
        );

        TextView txtvSellerProfileName = findViewById(R.id.txtvSellerProfileName);
        TextView txtvSellerProfileEmail = findViewById(R.id.txtvSellerProfileEmail);
        TextView txtvSellerProfileBirthday = findViewById(R.id.txtvSellerProfileBirthday);
        TextView txtvSellerProfileGender = findViewById(R.id.txtvSellerProfileGender);
        TextView txtvSellerProfilePhoneNo = findViewById(R.id.txtvSellerProfilePhoneNo);
        TextView txtvSellerProfileMobileNo = findViewById(R.id.txtvSellerProfileMobileNo);

        TextView txtvSellerProfileBusinessName = findViewById(R.id.txtvSellerProfileBusinessName);
        TextView txtvSellerProfileBusinessPermitNo =
                findViewById(R.id.txtvSellerProfileBusinessPermitNo);
        TextView txtvSellerProfileBusinessProductService =
                findViewById(R.id.txtvSellerProfileBusinessProductService);

        ImageView imgvSellerProfilePermitNo = findViewById(R.id.imgvSellerProfilePermitNo);
        ImageView imgvSellerProfileValidId = findViewById(R.id.imgvSellerProfileValidId);
        ImageView imgvSellerProfileSelfie = findViewById(R.id.imgvSellerProfileSelfie);

        TextView txtvSellerProfileBusinessAddressUnit =
                findViewById(R.id.txtvSellerProfileBusinessAddressUnit);
        TextView txtvSellerProfileBusinessAddressStreet =
                findViewById(R.id.txtvSellerProfileBusinessAddressStreet);
        TextView txtvSellerProfileBusinessAddressBarangay =
                findViewById(R.id.txtvSellerProfileBusinessAddressBarangay);
        TextView txtvSellerProfileBusinessAddressCity =
                findViewById(R.id.txtvSellerProfileBusinessAddressCity);
        TextView txtvSellerProfileBusinessAddressZip =
                findViewById(R.id.txtvSellerProfileBusinessAddressZip);

        try {
            String fullname = String.format("%s %s %s",
                    spUser.getString(USER_FIRST_NAME, ""),
                    spUser.getString(USER_MIDDLE_NAME, ""),
                    spUser.getString(USER_LAST_NAME, "")
            );

            JSONObject userProperties =
                    new JSONObject(spUser.getString(USER_PROPERTIES, ""));
            JSONObject birthdate = userProperties.getJSONObject("birthdate");
            JSONObject address = userProperties.getJSONObject("address");
            JSONObject business = userProperties.getJSONObject("business");
            JSONObject images = business.getJSONObject("images");

            String birthday = String.format("%s %s, %s",
                    birthdate.getString("month"),
                    birthdate.getString("day"),
                    birthdate.getString("year")
            );

            txtvSellerProfileName.setText(fullname);
            txtvSellerProfileEmail.setText(spUser.getString(USER_EMAIL, ""));
            txtvSellerProfileBirthday.setText("Birthday: " + birthday);
            txtvSellerProfileGender
                .setText("Gender: " + userProperties.getString("gender"));
            txtvSellerProfilePhoneNo
                .setText("Phone no: " + userProperties.getString("phone_no"));
            txtvSellerProfileMobileNo
                .setText("Mobile no: " + userProperties.getString("mobile_no"));

            txtvSellerProfileBusinessName
                .setText("Name: " + business.getString("name"));
            txtvSellerProfileBusinessPermitNo
                .setText("Permit no: " + business.getString("permit_no"));
            txtvSellerProfileBusinessProductService
                .setText("Product/Service: " + business.getString("product"));

            Picasso.get().load(images.getString("permit_proof"))
                .into(imgvSellerProfilePermitNo);
            Picasso.get().load(images.getString("owner_valid_id"))
                .into(imgvSellerProfileValidId);
            Picasso.get().load(images.getString("owner_selfie"))
                .into(imgvSellerProfileSelfie);

            txtvSellerProfileBusinessAddressUnit
                .setText("Unit: " + address.getString("unit"));
            txtvSellerProfileBusinessAddressStreet
                .setText("Street: " + address.getString("street"));
            txtvSellerProfileBusinessAddressBarangay
                .setText("Barangay: " + address.getString("barangay"));
            txtvSellerProfileBusinessAddressCity
                .setText("City: " + address.getString("city"));
            txtvSellerProfileBusinessAddressZip
                .setText("Zip: " + address.getString("zip"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}