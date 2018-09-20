package com.vinners.dateselector;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateSpanSelectorView extends RelativeLayout {

    private static final int START_DATE = 11;
    private static final int END_DATE = 12;

    /**
     * Listener that will be used to revert {@link DateSpan} user select back
     */
    private DateSpanSelectListener dateSpanSelectListener;
    private boolean dateOptionViewHolderVisible = false;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    private int currentlySelectingDate = -1;
    private Date startSelectedDate, endSelectedDate;

    private EditText fromDateEditText;
    private EditText toDateEditText;
    private AlertDialog selectDateRangeDialog;

    /**
     * Views
     */
    private TextView currentDateSpanSelectedTV;

    private TextView dateSpanTodayTV;
    private TextView dateSpanYesterdayTV;
    private TextView dateSpanThisMonthTV;
    private TextView dateSpanPreviousMonthTV;
    private TextView dateSpanThisWeekTV;
    private TextView dateSpanCustomTV;

    private RelativeLayout dateOptionHolderRL;

    public DateSpanSelectorView(Context context) {
        super(context);
        initView();
    }

    public DateSpanSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DateSpanSelectorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        final View inflatedView = inflate(getContext(), R.layout.layout_date_span_selector, null);
        addView(inflatedView);

        findViews();

        findViewsAndSetListeners();
    }

    private void findViews() {

        this.datePickerDialog = new DatePickerDialog(getContext(),
                (view, year, month, dayOfMonth) -> {

                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, month, dayOfMonth);

                    if (currentlySelectingDate == START_DATE) {
                        startSelectedDate = newDate.getTime();
                        fromDateEditText.setText(simpleDateFormat.format(startSelectedDate));

                    } else if (currentlySelectingDate == END_DATE) {
                        endSelectedDate = newDate.getTime();
                        toDateEditText.setText(simpleDateFormat.format(endSelectedDate));
                    }

                },
                -1,
                -1,
                -1);
    }

    public void setDateSpanSelectListener(DateSpanSelectListener dateSpanSelectListener) {
        this.dateSpanSelectListener = dateSpanSelectListener;
    }

    private void findViewsAndSetListeners() {

        dateOptionHolderRL = findViewById(R.id.date_span_option_holder_layout);

        currentDateSpanSelectedTV = findViewById(R.id.date_span_selected_text);

        dateSpanTodayTV = findViewById(R.id.today_period);
        dateSpanYesterdayTV = findViewById(R.id.yesterday_period);
        dateSpanThisMonthTV = findViewById(R.id.current_month_period);
        dateSpanPreviousMonthTV = findViewById(R.id.previous_month_period);
        dateSpanThisWeekTV = findViewById(R.id.this_week_period);
        dateSpanCustomTV = findViewById(R.id.custom_period);

        currentDateSpanSelectedTV.setOnClickListener(view -> toggleDateOptionHolderVisibilityToggle());
        dateSpanTodayTV.setOnClickListener(view -> selectDateSpan(DateSpan.TODAY));
        dateSpanYesterdayTV.setOnClickListener(view -> selectDateSpan(DateSpan.YESTERDAY));
        dateSpanThisMonthTV.setOnClickListener(view -> selectDateSpan(DateSpan.CURRENT_MONTH));
        dateSpanPreviousMonthTV.setOnClickListener(view -> selectDateSpan(DateSpan.LAST_MONTH));
        dateSpanThisWeekTV.setOnClickListener(view -> selectDateSpan(DateSpan.THIS_WEEK));
        dateSpanCustomTV.setOnClickListener(view -> selectDateSpan(DateSpan.CUSTOM));

    }

    private void showSelectDateRangeDialog() {

        if (selectDateRangeDialog == null) {
            final Context context = getContext();
            final View dialogView = inflate(context, R.layout.layout_select_date_range, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setView(dialogView)
                    .setPositiveButton("Okay", (dialog, which) -> {

                        if (startSelectedDate == null) {
                            Toast.makeText(context, "Please Provide Start Date", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (endSelectedDate == null) {
                            Toast.makeText(context, "Please Provide End Date", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        toggleDateOptionHolderVisibilityToggle();
                        dateSpanSelectListener.onDateSpanSelected(DateSpan.CUSTOM, startSelectedDate, endSelectedDate);

                    });

            selectDateRangeDialog = builder.create();
            selectDateRangeDialog.setTitle("Select Date Range");
            fromDateEditText = dialogView.findViewById(R.id.select_start_date);
            toDateEditText = dialogView.findViewById(R.id.select_end_date);

            fromDateEditText.setOnClickListener(v -> {

                if (endSelectedDate != null) {
                    final Calendar instance = Calendar.getInstance();
                    instance.setTime(endSelectedDate);
                    datePickerDialog.getDatePicker().setMaxDate(instance.getTimeInMillis());

                    if (startSelectedDate != null)
                        instance.setTime(endSelectedDate);
                    else
                        instance.add(Calendar.DATE, -1);

                    datePickerDialog.getDatePicker().updateDate(instance.get(Calendar.YEAR),
                            instance.get(Calendar.MONTH),
                            instance.get(Calendar.DAY_OF_MONTH));


                } else if (startSelectedDate != null) {
                    final Calendar instance = Calendar.getInstance();
                    instance.setTime(startSelectedDate);

                    datePickerDialog.getDatePicker().updateDate(instance.get(Calendar.YEAR),
                            instance.get(Calendar.MONTH),
                            instance.get(Calendar.DAY_OF_MONTH));
                } else {
                    final Calendar instance = Calendar.getInstance();
                    datePickerDialog.getDatePicker().updateDate(instance.get(Calendar.YEAR),
                            instance.get(Calendar.MONTH),
                            instance.get(Calendar.DAY_OF_MONTH));
                }

                currentlySelectingDate = START_DATE;
                datePickerDialog.show();

            });

            toDateEditText.setOnClickListener(v -> {


                if (startSelectedDate != null) {
                    final Calendar instance = Calendar.getInstance();
                    instance.setTime(startSelectedDate);

                    datePickerDialog.getDatePicker().setMinDate(instance.getTimeInMillis());

                    if (endSelectedDate != null)
                        instance.setTime(endSelectedDate);
                    else
                        instance.add(Calendar.DATE, 1);

                    datePickerDialog.getDatePicker().updateDate(instance.get(Calendar.YEAR),
                            instance.get(Calendar.MONTH),
                            instance.get(Calendar.DAY_OF_MONTH));

                    //Disable Calendar with here
                } else if (endSelectedDate != null) {
                    final Calendar instance = Calendar.getInstance();
                    instance.setTime(endSelectedDate);

                    datePickerDialog.getDatePicker().updateDate(instance.get(Calendar.YEAR),
                            instance.get(Calendar.MONTH),
                            instance.get(Calendar.DAY_OF_MONTH));
                } else {
                    final Calendar instance = Calendar.getInstance();
                    datePickerDialog.getDatePicker().updateDate(instance.get(Calendar.YEAR),
                            instance.get(Calendar.MONTH),
                            instance.get(Calendar.DAY_OF_MONTH));
                }

                currentlySelectingDate = END_DATE;
                datePickerDialog.show();

            });


        }
        selectDateRangeDialog.show();
    }

    /**
     * Toggles the Visibility of DateSpan Options
     */
    private void toggleDateOptionHolderVisibilityToggle() {
        if (dateOptionViewHolderVisible) {
            dateOptionHolderRL.setVisibility(View.GONE);
            dateOptionViewHolderVisible = false;
        } else {
            dateOptionHolderRL.setVisibility(View.VISIBLE);
            dateOptionViewHolderVisible = true;
        }
    }

    private Date getTodaysDate() {
        return new Date(System.currentTimeMillis());
    }

    private Date getYesterdayDate() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    private Date getPreviousMonthFirstDay() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DATE, 1);
        return cal.getTime();
    }

    private Date getPreviousMonthLastDay() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE)); // changed calendar to cal
        return cal.getTime();
    }


    private Date getCurrentMonthFirstDate() {
        Calendar c = Calendar.getInstance();   // this takes current date
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    private void selectDateSpan(DateSpan dateSpanSelected) {

        switch (dateSpanSelected) {
            case CUSTOM:
                showSelectDateRangeDialog();
                break;

            case TODAY:
                dateSpanSelectListener.onDateSpanSelected(dateSpanSelected, getTodaysDate(), getTodaysDate());
                break;

            case CURRENT_MONTH:
                dateSpanSelectListener.onDateSpanSelected(dateSpanSelected, getCurrentMonthFirstDate(), getTodaysDate());
                break;

            case YESTERDAY:
                dateSpanSelectListener.onDateSpanSelected(dateSpanSelected, getYesterdayDate(), getYesterdayDate());
                break;


            case LAST_MONTH:
                dateSpanSelectListener.onDateSpanSelected(dateSpanSelected, getPreviousMonthFirstDay(), getPreviousMonthLastDay());

        }

        if (dateSpanSelected != DateSpan.CUSTOM)
            toggleDateOptionHolderVisibilityToggle();
    }
}
