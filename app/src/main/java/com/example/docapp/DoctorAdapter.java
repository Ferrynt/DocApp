package com.example.docapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DoctorAdapter extends ArrayAdapter<Doctor> {
    private DB db;
    private LayoutInflater inflater;

    public DoctorAdapter(Context context, ArrayList<Doctor> doctors, DB db) {
        super(context, 0, doctors);
        this.db = db;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Doctor doctor = getItem(position);
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_doctors, parent, false);
            holder = new ViewHolder();
            holder.llItem = convertView.findViewById(R.id.ll_doctor_item);
            holder.tvName = convertView.findViewById(R.id.tv_doctor_name);
            holder.tvPosition = convertView.findViewById(R.id.tv_doctor_position);
            holder.btnBook = convertView.findViewById(R.id.btn_book);
            holder.btnCancel = convertView.findViewById(R.id.btn_cancel);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (doctor != null) {
            holder.tvName.setText(doctor.getName());
            holder.tvPosition.setText(doctor.getPosition());

            boolean isBooked = db.hasActiveAppointment(doctor.getId());
            updateAppointmentUI(holder, isBooked);

            holder.llItem.setOnClickListener(v -> {
                String info = db.getAppointmentInfo(doctor.getId());
                Toast.makeText(getContext(), info, Toast.LENGTH_SHORT).show();
            });

            holder.btnBook.setOnClickListener(v -> showDateTimePicker(doctor, holder));
            holder.btnCancel.setOnClickListener(v -> showCancelConfirmation(doctor, holder));
        }

        return convertView;
    }

    private void showDateTimePicker(Doctor doctor, ViewHolder holder) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    showTimePicker(doctor, holder, calendar);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showTimePicker(Doctor doctor, ViewHolder holder, Calendar calendar) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minute) -> {
                    if (hourOfDay < 9 || hourOfDay >= 16) {
                        Toast.makeText(getContext(), "Прием ведется с 9:00 до 16:00!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.getTime());
                    String time = String.format("%02d:%02d", hourOfDay, minute);

                    if (db.isTimeSlotAvailable(date, time)) {
                        db.bookAppointment(doctor.getId(), date, time);
                        updateAppointmentUI(holder, true);
                        Toast.makeText(getContext(), "Запись успешно создана!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Вы уже записаны к другому врачу в это время!", Toast.LENGTH_SHORT).show();
                    }
                },
                9, 0, true
        );

        timePickerDialog.updateTime(9, 0);
        timePickerDialog.show();
    }

    private void showCancelConfirmation(Doctor doctor, ViewHolder holder) {
        new AlertDialog.Builder(getContext())
                .setTitle("Подтверждение отмены")
                .setMessage("Вы уверены, что хотите отменить запись?")
                .setPositiveButton("Да", (dialog, which) -> {
                    db.cancelAppointment(doctor.getId());
                    updateAppointmentUI(holder, false);
                    Toast.makeText(getContext(), "Запись отменена", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Нет", null)
                .show();
    }

    private void updateAppointmentUI(ViewHolder holder, boolean isBooked) {
        if (isBooked) {
            holder.llItem.setBackgroundColor(Color.parseColor("#CCFFCC"));
            holder.btnBook.setEnabled(false);
            holder.btnBook.setAlpha(0.5f);
            holder.btnCancel.setEnabled(true);
            holder.btnCancel.setAlpha(1f);
        } else {
            holder.llItem.setBackgroundColor(Color.WHITE);
            holder.btnBook.setEnabled(true);
            holder.btnBook.setAlpha(1f);
            holder.btnCancel.setEnabled(false);
            holder.btnCancel.setAlpha(0.5f);
        }
    }

    static class ViewHolder {
        LinearLayout llItem;
        TextView tvName;
        TextView tvPosition;
        Button btnBook;
        Button btnCancel;
    }
}