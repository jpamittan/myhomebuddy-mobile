package com.example.myhomebuddy.ui.regisration;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.myhomebuddy.ImageFilePath;
import com.example.myhomebuddy.R;
import com.example.myhomebuddy.RegistrationSuccessful;
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
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link registrationSellerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class registrationSellerFragment extends Fragment {

    private String selectedImageView = null;
    private Boolean blnProceedRegister;
    private static final String host = "ec2-54-89-125-177.compute-1.amazonaws.com";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private ImageView imgvBusinessPermit;
    private ImageView imgvValidId;
    private ImageView imgvSelfie;

    Bitmap bmpBusinessPermit = null;
    Bitmap bmpId = null;
    Bitmap bmpSelfie = null;

    String BusinessPermitURL = "";
    String ValidIdURL = "";
    String SelfieURL = "";

    public registrationSellerFragment() {
        // Required empty public constructor
    }

    public static registrationSellerFragment newInstance() {
        return new registrationSellerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(
                R.layout.fragment_registration_seller,
                container,
                false
        );

        EditText etxtBusinessName = view.findViewById(R.id.etxtBusinessName);
        EditText etxtBusinessPermitNo = view.findViewById(R.id.etxtBusinessPermitNo);
        Spinner spnBusinessProductService = view.findViewById(R.id.spnBusinessProductService);
        EditText etxtFirstName = view.findViewById(R.id.etxtFirstName);
        EditText etxtMiddleName = view.findViewById(R.id.etxtMiddleName);
        EditText etxtLastName = view.findViewById(R.id.etxtLastName);
        Spinner spnMonth = view.findViewById(R.id.spnMonth);
        Spinner spnDay = view.findViewById(R.id.spnDay);
        Spinner spnYear = view.findViewById(R.id.spnYear);
        Spinner spnGender = view.findViewById(R.id.spnGender);
        EditText etxtPhBlkLtUnitBldgNo = view.findViewById(R.id.etxtPhBlkLtUnitBldgNo);
        EditText etxtStreet = view.findViewById(R.id.etxtStreet);
        EditText etxtBarangay = view.findViewById(R.id.etxtBarangay);
        EditText etxtCity = view.findViewById(R.id.etxtCity);
        EditText etxtZip = view.findViewById(R.id.etxtZip);
        EditText etxtLandlineNo = view.findViewById(R.id.etxtLandlineNo);
        EditText etxtPhoneNo = view.findViewById(R.id.etxtPhoneNo);
        EditText etxtEmail = view.findViewById(R.id.etxtEmail);
        EditText etxtPassword = view.findViewById(R.id.etxtPassword);
        EditText etxtVerifyPassword = view.findViewById(R.id.etxtVerifyPassword);
        CheckBox chkbAccept = view.findViewById(R.id.chkbAccept);
        TextView txtvTermsConditions = view.findViewById(R.id.txtvTermsConditionsSeller);

        Button btnUploadPermit = view.findViewById(R.id.btnUploadPermit);
        imgvBusinessPermit = view.findViewById(R.id.imgvBusinessPermit);
        Button btnUploadValidId = view.findViewById(R.id.btnUploadValidId);
        imgvValidId = view.findViewById(R.id.imgvValidId);
        Button btnUploadSelfie = view.findViewById(R.id.btnUploadSelfie);
        imgvSelfie = view.findViewById(R.id.imgvSelfie);

        Button btnRegister = view.findViewById(R.id.btnRegister);

        txtvTermsConditions.setMovementMethod(LinkMovementMethod.getInstance());

        ArrayList<String> years = new ArrayList<>();
        for (int i = Calendar.getInstance().get(Calendar.YEAR); i > 1900; i--) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
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

        btnUploadPermit.setOnClickListener(v -> {
            selectedImageView = "permit";
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1);
        });

        btnUploadValidId.setOnClickListener(v -> {
            selectedImageView = "id";
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1);
        });

        btnUploadSelfie.setOnClickListener(v -> {
            selectedImageView = "selfie";
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, 1);
        });

        ProgressDialog progress = new ProgressDialog(getActivity());
        progress.setTitle("Loading");
        progress.setMessage("Please wait...");
        progress.setCancelable(false);

        btnRegister.setOnClickListener(v -> {
            blnProceedRegister = !etxtBusinessName.getText().toString().isEmpty() &&
                    !etxtBusinessPermitNo.getText().toString().isEmpty() &&
                    !etxtFirstName.getText().toString().isEmpty() &&
                    !etxtLastName.getText().toString().isEmpty() &&
                    !etxtPassword.getText().toString().isEmpty() &&
                    !etxtPhBlkLtUnitBldgNo.getText().toString().isEmpty() &&
                    !etxtStreet.getText().toString().isEmpty() &&
                    !etxtBarangay.getText().toString().isEmpty() &&
                    !etxtCity.getText().toString().isEmpty() &&
                    !etxtZip.getText().toString().isEmpty() &&
                    !etxtLandlineNo.getText().toString().isEmpty() &&
                    !etxtPhoneNo.getText().toString().isEmpty() &&
                    !etxtEmail.getText().toString().isEmpty() &&
                    !etxtVerifyPassword.getText().toString().isEmpty() &&
                    etxtPassword.getText().toString().equals(etxtVerifyPassword.getText().toString());
            if (blnProceedRegister) {
                progress.show();
                OkHttpClient client = new OkHttpClient();
                String json = "{"
                    + "\"business_name\" : \"" + etxtBusinessName
                    .getText()
                    .toString() + "\","
                    + "\"business_permit_no\" : \"" + etxtBusinessPermitNo
                    .getText()
                    .toString() + "\","
                    + "\"business_product_no\" : \"" + spnBusinessProductService
                    .getSelectedItem()
                    .toString() + "\","
                    + "\"business_permit_proof\" : \"" + BusinessPermitURL + "\","
                    + "\"business_owner_valid_id\" : \"" + ValidIdURL + "\","
                    + "\"business_owner_selfie\" : \"" + SelfieURL + "\","
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
                    + "\"address_street\" : \"" + etxtStreet.getText().toString() + "\","
                    + "\"address_barangay\" : \"" + etxtBarangay.getText().toString() + "\","
                    + "\"address_city\" : \"" + etxtCity.getText().toString() + "\","
                    + "\"address_zip\" : \"" + etxtZip.getText().toString() + "\","
                    + "\"phone_no\" : \"" + etxtLandlineNo.getText().toString() + "\","
                    + "\"mobile_no\" : \"" + etxtPhoneNo.getText().toString() + "\""
                + "}";
                Log.d("json", json);
                RequestBody body = RequestBody.create(JSON, json);
                Request request = new Request.Builder()
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json")
                        .url("http://" + host + "/api/register/seller")
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
                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        progress.dismiss();
                                        Intent intent = new Intent(
                                                getActivity(),
                                                RegistrationSuccessful.class
                                        );
                                        startActivity(intent);
                                        getActivity().finish();
                                    });
                                }
                            } catch (JSONException | IOException e) {
                                Log.e("RSx", e.getMessage());
                            }
                        } else {
                            try {
                                JSONObject responseObj = new JSONObject(response.body().string());
                                String msg = String.valueOf(responseObj.get("error"));
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    progress.dismiss();
                                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();
                String filePath = ImageFilePath.getPath(getActivity(), selectedImage);
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
                    Bitmap bmp;

                    if (selectedImageView.equals("permit")) {
                        try {
                            bmpBusinessPermit = MediaStore.Images.Media.getBitmap(
                                    getActivity().getContentResolver(),
                                    selectedImage
                            );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        bmp = bmpBusinessPermit;
                        imgvBusinessPermit.setImageBitmap(bmpBusinessPermit);
                        imgvBusinessPermit.setVisibility(View.VISIBLE);
                    } else if (selectedImageView.equals("id")) {
                        try {
                            bmpId = MediaStore.Images.Media.getBitmap(
                                    getActivity().getContentResolver(),
                                    selectedImage
                            );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        bmp = bmpId;
                        imgvValidId.setImageBitmap(bmpId);
                        imgvValidId.setVisibility(View.VISIBLE);
                    } else {
                        try {
                            bmpSelfie = MediaStore.Images.Media.getBitmap(
                                    getActivity().getContentResolver(),
                                    selectedImage
                            );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        bmp = bmpSelfie;
                        imgvSelfie.setImageBitmap(bmpSelfie);
                        imgvSelfie.setVisibility(View.VISIBLE);
                    }

                    @SuppressLint("SimpleDateFormat")
                    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")
                            .format(new Date());
                    String hashstr = timeStamp + "." + (Math.random()* 100);

                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage
                            .getReferenceFromUrl("gs://myhomebuddy-6e4a8.appspot.com");
                    String imgFileName = String.format("%s-%s-%s",
                            hashstr.hashCode(),
                            selectedImageView,
                            file_extn);

                    StorageReference sr = storageRef.child("sellers/" + imgFileName);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] bt = baos.toByteArray();

                    UploadTask uploadtask = sr.putBytes(bt);
                    uploadtask.addOnFailureListener(exception ->
                            Log.e("FBFx", exception.getMessage())
                        )
                        .addOnSuccessListener(taskSnapshot ->
                                sr.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                                if (selectedImageView.equals("permit")) {
                                    BusinessPermitURL = downloadUrl.toString();
                                } else if (selectedImageView.equals("id")) {
                                    ValidIdURL = downloadUrl.toString();
                                } else {
                                    SelfieURL = downloadUrl.toString();
                                }
                                Log.i(selectedImageView, downloadUrl.toString());
                            })
                        );
                } else {
                    Toast.makeText(
                        this.getContext(),
                        "Invalid file format.",
                        Toast.LENGTH_LONG
                    ).show();
                }
            }
        }
    }
}