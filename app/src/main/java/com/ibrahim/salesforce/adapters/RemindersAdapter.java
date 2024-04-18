package com.ibrahim.salesforce.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import com.ibrahim.salesforce.R;
import com.ibrahim.salesforce.callbacks.ReminderAdapterCallBacks;
import com.ibrahim.salesforce.response.Reminder;
import com.ibrahim.salesforce.utilities.Utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RemindersAdapter extends RecyclerView.Adapter<RemindersAdapter.ViewHolder> {

    private final List<Reminder> arrReminders;
    private final int          rowLayout;
    private ReminderAdapterCallBacks callbacks;
    Context context;
    Calendar myCalendar = Calendar.getInstance();
    private DatePickerDialog.OnDateSetListener date;
    DatePicker datePicker;
    String remeinderDate;
    int position_;
      Reminder reminder;



    public RemindersAdapter(List<Reminder> arrReminders, @LayoutRes int rowLayout,ReminderAdapterCallBacks listeners ,Activity context) {
        this.arrReminders = arrReminders;
        this.rowLayout = rowLayout;
        callbacks = listeners;
        this.context = context;
        startDatePicker();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
     final Reminder data = arrReminders.get(position);
        holder.schoolName.setText(data.getSchoolName());
        holder.dateTime.setText(Utility.setDateTime(data.getReminderDate()));
        holder.cancel_reminders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.dialog_layout, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        callbacks.rescheduleReminder(data,position,userInput.getText().toString());
                                        notifyDataSetChanged();

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });

            holder.reschdule_Reminder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatePickerDialog(context, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                               reminder  = data;
                                position_ = position;
                }

            });
    }

    @Override
    public int getItemCount() {
        return arrReminders.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView schoolName, dateTime,cancel_reminders,reschdule_Reminder;
        private LinearLayout llRoot;
        public ViewHolder(View view) {
            super(view);
            schoolName = view.findViewById(R.id.tv_customer_name);
            dateTime = view.findViewById(R.id.tv_visit_time);
            llRoot = view.findViewById(R.id.llRoot);
            cancel_reminders = view.findViewById(R.id.tvDelete);
            reschdule_Reminder  = view.findViewById(R.id.tvReschedule);
        }
    }
    private void startDatePicker() {
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
    }
    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        remeinderDate   =(sdf.format(myCalendar.getTime()));
         callbacks.rescheduleReminder(reminder,position_,remeinderDate);


    }


}
