package com.example.myhomebuddy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ConsumerPlaceOrderActivity extends AppCompatActivity {

    int productId;
    float productPrice;
    String productImage;
    String productName;
    int qtyCtr = 0;
    private Boolean blnProceedRegister;
    String dateFrom;
    String dateto;
    CalendarView cvSubscriptionStart;
    CalendarView cvSubscriptionUntil;
    Spinner spnFrequency;
    LinearLayout llWeekdays;
    LinearLayout llMonthDays;
    CheckBox cbMonday, cbTuesday, cbWednesday, cbThursday, cbFriday, cbSaturday, cbSunday,
            cb1, cb2, cb3, cb4, cb5, cb6, cb7, cb8, cb9, cb10, cb11, cb12, cb13, cb14, cb15,
            cb16, cb17, cb18, cb19, cb20, cb21, cb22, cb23, cb24, cb25, cb26, cb27, cb28, cb29, cb30;
    EditText ettMonday, ettTuesday, ettWednesday, ettThursday, ettFriday, ettSaturday, ettSunday,
             etnMonday, etnTuesday, etnWednesday, etnThursday, etnFriday, etnSaturday, etnSunday,
             ett1, ett2, ett3, ett4, ett5, ett6, ett7, ett8, ett9, ett10, ett11, ett12, ett13, ett14, ett15,
             ett16, ett17, ett18, ett19, ett20, ett21, ett22, ett23, ett24, ett25, ett26, ett27, ett28, ett29, ett30,
             etn1, etn2, etn3, etn4, etn5, etn6, etn7, etn8, etn9, etn10, etn11, etn12, etn13, etn14, etn15,
             etn16, etn17, etn18, etn19, etn20, etn21, etn22, etn23, etn24, etn25, etn26, etn27, etn28, etn29, etn30;
    Button btnOrderSubmit;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer_place_order);

        cvSubscriptionStart = findViewById(R.id.cvSubscriptionStart);
        cvSubscriptionUntil = findViewById(R.id.cvSubscriptionUntil);
        spnFrequency = findViewById(R.id.spnFrequency);
        llWeekdays = findViewById(R.id.llWeekdays);
        llMonthDays = findViewById(R.id.llMonthDays);

        cbMonday = findViewById(R.id.cbMonday);
        cbTuesday = findViewById(R.id.cbTuesday);
        cbWednesday = findViewById(R.id.cbWednesday);
        cbThursday = findViewById(R.id.cbThursday);
        cbFriday = findViewById(R.id.cbFriday);
        cbSaturday = findViewById(R.id.cbSaturday);
        cbSunday = findViewById(R.id.cbSunday);

        ettMonday = findViewById(R.id.ettMonday);
        ettTuesday = findViewById(R.id.ettTuesday);
        ettWednesday = findViewById(R.id.ettWednesday);
        ettThursday = findViewById(R.id.ettThursday);
        ettFriday = findViewById(R.id.ettFriday);
        ettSaturday = findViewById(R.id.ettSaturday);
        ettSunday = findViewById(R.id.ettSunday);

        etnMonday = findViewById(R.id.etnMonday);
        etnTuesday = findViewById(R.id.etnTuesday);
        etnWednesday = findViewById(R.id.etnWednesday);
        etnThursday = findViewById(R.id.etnThursday);
        etnFriday = findViewById(R.id.etnFriday);
        etnSaturday = findViewById(R.id.etnSaturday);
        etnSunday = findViewById(R.id.etnSunday);

        cb1 = findViewById(R.id.cb1);
        cb2 = findViewById(R.id.cb2);
        cb3 = findViewById(R.id.cb3);
        cb4 = findViewById(R.id.cb4);
        cb5 = findViewById(R.id.cb5);
        cb6 = findViewById(R.id.cb6);
        cb7 = findViewById(R.id.cb7);
        cb8 = findViewById(R.id.cb8);
        cb9 = findViewById(R.id.cb9);
        cb10 = findViewById(R.id.cb10);
        cb11 = findViewById(R.id.cb11);
        cb12 = findViewById(R.id.cb12);
        cb13 = findViewById(R.id.cb13);
        cb14 = findViewById(R.id.cb14);
        cb15 = findViewById(R.id.cb15);
        cb16 = findViewById(R.id.cb16);
        cb17 = findViewById(R.id.cb17);
        cb18 = findViewById(R.id.cb18);
        cb19 = findViewById(R.id.cb19);
        cb20 = findViewById(R.id.cb20);
        cb21 = findViewById(R.id.cb21);
        cb22 = findViewById(R.id.cb22);
        cb23 = findViewById(R.id.cb23);
        cb24 = findViewById(R.id.cb24);
        cb25 = findViewById(R.id.cb25);
        cb26 = findViewById(R.id.cb26);
        cb27 = findViewById(R.id.cb27);
        cb28 = findViewById(R.id.cb28);
        cb29 = findViewById(R.id.cb29);
        cb30 = findViewById(R.id.cb30);

        ett1 = findViewById(R.id.ett1);
        ett2 = findViewById(R.id.ett2);
        ett3 = findViewById(R.id.ett3);
        ett4 = findViewById(R.id.ett4);
        ett5 = findViewById(R.id.ett5);
        ett6 = findViewById(R.id.ett6);
        ett7 = findViewById(R.id.ett7);
        ett8 = findViewById(R.id.ett8);
        ett9 = findViewById(R.id.ett9);
        ett10 = findViewById(R.id.ett10);
        ett11 = findViewById(R.id.ett11);
        ett12 = findViewById(R.id.ett12);
        ett13 = findViewById(R.id.ett13);
        ett14 = findViewById(R.id.ett14);
        ett15 = findViewById(R.id.ett15);
        ett16 = findViewById(R.id.ett16);
        ett17 = findViewById(R.id.ett17);
        ett18 = findViewById(R.id.ett18);
        ett19 = findViewById(R.id.ett19);
        ett20 = findViewById(R.id.ett20);
        ett21 = findViewById(R.id.ett21);
        ett22 = findViewById(R.id.ett22);
        ett23 = findViewById(R.id.ett23);
        ett24 = findViewById(R.id.ett24);
        ett25 = findViewById(R.id.ett25);
        ett26 = findViewById(R.id.ett26);
        ett27 = findViewById(R.id.ett27);
        ett28 = findViewById(R.id.ett28);
        ett29 = findViewById(R.id.ett29);
        ett30 = findViewById(R.id.ett30);

        etn1 = findViewById(R.id.etn1);
        etn2 = findViewById(R.id.etn2);
        etn3 = findViewById(R.id.etn3);
        etn4 = findViewById(R.id.etn4);
        etn5 = findViewById(R.id.etn5);
        etn6 = findViewById(R.id.etn6);
        etn7 = findViewById(R.id.etn7);
        etn8 = findViewById(R.id.etn8);
        etn9 = findViewById(R.id.etn9);
        etn10 = findViewById(R.id.etn10);
        etn11 = findViewById(R.id.etn11);
        etn12 = findViewById(R.id.etn12);
        etn13 = findViewById(R.id.etn13);
        etn14 = findViewById(R.id.etn14);
        etn15 = findViewById(R.id.etn15);
        etn16 = findViewById(R.id.etn16);
        etn17 = findViewById(R.id.etn17);
        etn18 = findViewById(R.id.etn18);
        etn19 = findViewById(R.id.etn19);
        etn20 = findViewById(R.id.etn20);
        etn21 = findViewById(R.id.etn21);
        etn22 = findViewById(R.id.etn22);
        etn23 = findViewById(R.id.etn23);
        etn24 = findViewById(R.id.etn24);
        etn25 = findViewById(R.id.etn25);
        etn26 = findViewById(R.id.etn26);
        etn27 = findViewById(R.id.etn27);
        etn28 = findViewById(R.id.etn28);
        etn29 = findViewById(R.id.etn29);
        etn30 = findViewById(R.id.etn30);

        btnOrderSubmit = findViewById(R.id.btnOrderSubmit);

        String[] daysArr = {
            "1", "2", "3", "4", "5", "6", "0",
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"
        };

        CheckBox[] cbDaysArr = {
            cbMonday, cbTuesday, cbWednesday, cbThursday, cbFriday, cbSaturday, cbSunday,
            cb1, cb2, cb3, cb4, cb5, cb6, cb7, cb8, cb9, cb10, cb11, cb12, cb13, cb14, cb15,
            cb16, cb17, cb18, cb19, cb20, cb21, cb22, cb23, cb24, cb25, cb26, cb27, cb28, cb29, cb30
        };

        EditText[] etTimeArr = {
            ettMonday, ettTuesday, ettWednesday, ettThursday, ettFriday, ettSaturday, ettSunday,
            ett1, ett2, ett3, ett4, ett5, ett6, ett7, ett8, ett9, ett10, ett11, ett12, ett13, ett14, ett15,
            ett16, ett17, ett18, ett19, ett20, ett21, ett22, ett23, ett24, ett25, ett26, ett27, ett28, ett29, ett30
        };

        EditText[] etQtyArr = {
            etnMonday, etnTuesday, etnWednesday, etnThursday, etnFriday, etnSaturday, etnSunday,
            etn1, etn2, etn3, etn4, etn5, etn6, etn7, etn8, etn9, etn10, etn11, etn12, etn13, etn14, etn15,
            etn16, etn17, etn18, etn19, etn20, etn21, etn22, etn23, etn24, etn25, etn26, etn27, etn28, etn29, etn30
        };

        Intent intent = getIntent();
        productId = intent.getIntExtra("id", 0);
        productPrice = intent.getFloatExtra("productPrice", 0);
        productImage = intent.getStringExtra("productImage");
        productName = intent.getStringExtra("productName");

        spnFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    llWeekdays.setVisibility(View.VISIBLE);
                    llMonthDays.setVisibility(View.GONE);
                } else {
                    llWeekdays.setVisibility(View.GONE);
                    llMonthDays.setVisibility(View.VISIBLE);
                }
                // Unchecked all
                for (int i = 0; i < cbDaysArr.length; i++) {
                    if (cbDaysArr[i].isChecked()) {
                        cbDaysArr[i].setChecked(false);
                        etTimeArr[i].setText("");
                        etQtyArr[i].setText("");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cvSubscriptionStart.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            dateFrom = year + "-" + (month + 1) + "-" + dayOfMonth;
        });

        cvSubscriptionUntil.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            dateto = year + "-" + (month + 1) + "-" + dayOfMonth;
        });

        btnOrderSubmit.setOnClickListener(v -> {
            blnProceedRegister = true;
            qtyCtr = 0;
            String errMsg = "";
            List<String> selectedDays = new ArrayList<>();
            List<String> times = new ArrayList<>();
            List<Integer> pcs = new ArrayList<>();
            String str_date = dateFrom;
            String end_date = dateto;
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = null;
            Date endDate = null;

            ArrayList<String> dates = new ArrayList<>();
            ArrayList<String> dateTimes = new ArrayList<>();
            ArrayList<Integer> datePcs = new ArrayList<>();

            StringBuilder ddObj = new StringBuilder();
            int cbCheckedCtr = 0;
            for (int i = 0; i < cbDaysArr.length; i++) {
                if (cbDaysArr[i].isChecked()) {
                    cbCheckedCtr++;
                    if (
                        etQtyArr[i].getText().toString().equals("") ||
                        etTimeArr[i].getText().toString().equals("")
                    ) {
                        errMsg = "Missing time/quantity in selected day.";
                        blnProceedRegister = false;
                        break;
                    }
                    selectedDays.add(daysArr[i]);
                    pcs.add(Integer.valueOf(etQtyArr[i].getText().toString()));
                    times.add(etTimeArr[i].getText().toString());
                    ddObj.append(
                        "{"
                            + "\"day\": \"" + daysArr[i] + "\","
                            + "\"time\": \"" + etTimeArr[i].getText().toString() + "\","
                            + "\"pc\": " + etQtyArr[i].getText().toString() + ""
                        + "},"
                    );
                }
            }

            if (cbCheckedCtr <= 0) {
                errMsg = "No day selected.";
                blnProceedRegister = false;
            } else {
                try {
                    startDate = formatter.parse(str_date);
                    endDate = formatter.parse(end_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (Objects.requireNonNull(endDate).before(startDate)) {
                    errMsg = "End date is earlier than start date.";
                    blnProceedRegister = false;
                }
            }

            if (blnProceedRegister) {
                String delivery_days = "[" + ddObj.substring(0, ddObj.length() - 1) + "]";
                long interval = 24*1000 * 60 * 60;
                long endTime = Objects.requireNonNull(endDate).getTime();
                long curTime = Objects.requireNonNull(startDate).getTime();
                while (curTime <= endTime) {
                    Date d = new Date(curTime);
                    Calendar cal = Calendar.getInstance();
                    if (spnFrequency.getSelectedItem().toString().equals("Weekly")) {
                        for (int i = 0; i < selectedDays.size(); i++) {
                            if (selectedDays.get(i).equals(String.valueOf(d.getDay()))) {
                                dates.add(formatter.format(d));
                                dateTimes.add(times.get(i));
                                datePcs.add(pcs.get(i));
                                break;
                            }
                        }
                    } else {
                        // Monthly
                        cal.setTime(d);
                        for (int i = 0; i < selectedDays.size(); i++) {
                            if (
                                selectedDays.get(i)
                                    .equals(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)))
                            ) {
                                dates.add(formatter.format(d));
                                dateTimes.add(times.get(i));
                                datePcs.add(pcs.get(i));
                                break;
                            }
                        }
                    }
                    curTime += interval;
                }
                for(int i = 0; i < dates.size(); i++){
                    qtyCtr += datePcs.get(i);
                    Log.i("Date", dates.get(i));
                    Log.i("Time", dateTimes.get(i));
                    Log.i("Pc", String.valueOf(datePcs.get(i)));
                    Log.i("br", "----------------------------");
                }

                float total_amount = productPrice * qtyCtr;

                Intent checkout = new Intent(this, ConsumerCheckoutMainActivity.class);
                checkout.putExtra("productId", productId);
                checkout.putExtra("dateFrom", dateFrom);
                checkout.putExtra("dateto", dateto);
                checkout.putExtra("frequency", spnFrequency.getSelectedItem().toString());
                checkout.putExtra("delivery_days", delivery_days);
                checkout.putExtra("total_quantity", qtyCtr);
                checkout.putExtra("total_amount", total_amount);
                checkout.putExtra("productPrice", productPrice);
                checkout.putExtra("productImage", productImage);
                checkout.putExtra("productName", productName);
                checkout.putStringArrayListExtra("dates", dates);
                checkout.putStringArrayListExtra("dateTimes", dateTimes);
                checkout.putIntegerArrayListExtra("datePcs", datePcs);
                startActivity(checkout);
            } else {
                Toast.makeText(
                    this,
                    errMsg,
                    Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}