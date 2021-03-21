package com.example.myhomebuddy.ui.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.myhomebuddy.R;
import com.example.myhomebuddy.ScheduleItemAdapter;

import java.util.ArrayList;

public class ScheduleFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_schedule, container, false);
        ListView lvSchedule = root.findViewById(R.id.lvSchedules);
        ArrayList<ScheduleItem> scheduleItems = new ArrayList<>();
        scheduleItems.add(new ScheduleItem(
                2,
                "Feb",
                18,
                "10:00 AM",
                "Alkaline Mineral Water",
                "https://www.water.com.ph/wp-content/uploads/2018/04/ccc-1-300x300.png"
        ));
        scheduleItems.add(new ScheduleItem(
                2,
                "Feb",
                25,
                "10:00 AM",
                "Alkaline Mineral Water",
                "https://www.water.com.ph/wp-content/uploads/2018/04/ccc-1-300x300.png"
        ));
        scheduleItems.add(new ScheduleItem(
                1,
                "Mar",
                1,
                "5:00 PM",
                "Petron LPG Gasul",
                "https://gosul.ph/wp-content/uploads/2020/06/Petron-Gasul-Pol-Valve-11kgs.png"
        ));
        scheduleItems.add(new ScheduleItem(
                1,
                "Mar",
                4,
                "10:00 AM",
                "Alkaline Mineral Water",
                "https://www.water.com.ph/wp-content/uploads/2018/04/ccc-1-300x300.png"
        ));
        scheduleItems.add(new ScheduleItem(
                2,
                "Mar",
                11,
                "10:00 AM",
                "Alkaline Mineral Water",
                "https://www.water.com.ph/wp-content/uploads/2018/04/ccc-1-300x300.png"
        ));
        scheduleItems.add(new ScheduleItem(
                1,
                "Mar",
                18,
                "10:00 AM",
                "Alkaline Mineral Water",
                "https://www.water.com.ph/wp-content/uploads/2018/04/ccc-1-300x300.png"
        ));
        scheduleItems.add(new ScheduleItem(
                1,
                "Mar",
                25,
                "10:00 AM",
                "Alkaline Mineral Water",
                "https://www.water.com.ph/wp-content/uploads/2018/04/ccc-1-300x300.png"
        ));
        scheduleItems.add(new ScheduleItem(
                1,
                "Apr",
                1,
                "5:00 PM",
                "Petron LPG Gasul",
                "https://gosul.ph/wp-content/uploads/2020/06/Petron-Gasul-Pol-Valve-11kgs.png"
        ));
        ScheduleItemAdapter scheduleItemAdapter = new ScheduleItemAdapter(
                this.getContext(),
                R.layout.fragment_schedule_item,
                scheduleItems
        );
        lvSchedule.setAdapter(scheduleItemAdapter);

        return root;
    }
}