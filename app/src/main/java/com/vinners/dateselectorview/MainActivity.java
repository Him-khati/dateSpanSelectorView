package com.vinners.dateselectorview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.vinners.dateselector.DateSpan;
import com.vinners.dateselector.DateSpanSelectListener;
import com.vinners.dateselector.DateSpanSelectorView;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DateSpanSelectorView dateSpanSelectorView = findViewById(R.id.date_selector);
        dateSpanSelectorView.setDateSpanSelectListener(new DateSpanSelectListener() {
            @Override
            public void onDateSpanSelected(DateSpan dateSpan, Date startDate, Date endDate) {
                Log.d("TAG", "onDateSpanSelected: " + dateSpan.toString() + " " + startDate.toString() + " " + endDate.toString());
            }
        });
    }
}
