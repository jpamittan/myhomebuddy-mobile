package com.example.myhomebuddy.ui.settings;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myhomebuddy.R;
import com.example.myhomebuddy.ui.login.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsFragment extends Fragment {

    public static final String SHARED_PREFS_TOKEN = "sharedPrefsToken";
    public static final String USER_FIRST_NAME = "user_first_name";
    public static final String USER_MIDDLE_NAME = "user_middle_name";
    public static final String USER_LAST_NAME = "user_last_name";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_PROPERTIES = "user_properties";

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        TextView txtvProfileName = root.findViewById(R.id.txtvProfileName);
        TextView txtvProfileEmail = root.findViewById(R.id.txtvProfileEmail);
        TextView txtvProfileBirthday = root.findViewById(R.id.txtvProfileBirthday);
        TextView txtvProfileGender = root.findViewById(R.id.txtvProfileGender);
        TextView txtvProfileContact = root.findViewById(R.id.txtvProfileContact);
        TextView txtvProfileAddressUnit = root.findViewById(R.id.txtvProfileAddressUnit);
        TextView txtvProfileAddressSubdivision = root.findViewById(R.id.txtvProfileAddressSubdivision);
        TextView txtvProfileAddressStreet = root.findViewById(R.id.txtvProfileAddressStreet);
        TextView txtvProfileAddressBarangay = root.findViewById(R.id.txtvProfileAddressBarangay);
        TextView txtvProfileAddressCity = root.findViewById(R.id.txtvProfileAddressCity);
        TextView txtvProfileAddressZip = root.findViewById(R.id.txtvProfileAddressZip);
        Button btnUserLogout = root.findViewById(R.id.btnUserLogout);

        SharedPreferences spUser = getActivity().getSharedPreferences(
            SHARED_PREFS_TOKEN,
            Context.MODE_PRIVATE
        );

        try {
            String fullname = String.format("%s %s %s",
                spUser.getString(USER_FIRST_NAME, ""),
                spUser.getString(USER_MIDDLE_NAME, ""),
                spUser.getString(USER_LAST_NAME, "")
            );

            JSONObject userProperties = new JSONObject(spUser.getString(USER_PROPERTIES, ""));
            JSONObject birthdate = userProperties.getJSONObject("birthdate");
            JSONObject address = userProperties.getJSONObject("address");

            String birthday = String.format("%s %s, %s",
                birthdate.getString("month"),
                birthdate.getString("day"),
                birthdate.getString("year")
            );

            txtvProfileName.setText(fullname);
            txtvProfileEmail.setText(spUser.getString(USER_EMAIL, ""));
            txtvProfileBirthday.setText("Birthday: " + birthday);
            txtvProfileGender.setText("Gender: " + userProperties.getString("gender"));
            txtvProfileContact.setText("Contact: " + userProperties.getString("phone_no"));
            txtvProfileAddressUnit.setText("Unit: " + address.getString("unit"));
            txtvProfileAddressSubdivision.setText("Subdivision: " + address.getString("subdivision"));
            txtvProfileAddressStreet.setText("Street: " + address.getString("street"));
            txtvProfileAddressBarangay.setText("Barangay: " + address.getString("barangay"));
            txtvProfileAddressCity.setText("City: " + address.getString("city"));
            txtvProfileAddressZip.setText("Zip: " + address.getString("zip"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnUserLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this.getContext())
                .setTitle("Logout")
                .setMessage("Do you really want to logout?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {

                    spUser.edit().clear().apply();
                    Intent intent = new Intent(
                        getActivity(),
                        LoginActivity.class
                    );
                    startActivity(intent);
                    getActivity().finish();
                })
                .setNegativeButton(android.R.string.no, null).show();
        });

        return root;
    }
}