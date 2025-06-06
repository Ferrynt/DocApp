package com.example.docapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;

public class DoctorsFragment extends Fragment {
    private Button btnLogout;
    private ListView lvDoctors;
    private DoctorAdapter adapter;
    private ArrayList<Doctor> doctors;
    private DB db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctors, container, false);
        db = new DB(getActivity());

        btnLogout = view.findViewById(R.id.btn_logout);
        lvDoctors = view.findViewById(R.id.lv_doctors);

        btnLogout.setOnClickListener(v -> ((Main) getActivity()).logout());

        doctors = new ArrayList<>();
        doctors.add(new Doctor(1, "Иванов И.И.", "Терапевт"));
        doctors.add(new Doctor(2, "Петрова А.С.", "Хирург"));
        doctors.add(new Doctor(3, "Сидоров В.К.", "Невролог"));
        doctors.add(new Doctor(4, "Козлова Е.П.", "Офтальмолог"));
        doctors.add(new Doctor(5, "Фёдоров М.А.", "Кардиолог"));

        adapter = new DoctorAdapter(getActivity(), doctors, db);
        lvDoctors.setAdapter(adapter);

        return view;
    }
}