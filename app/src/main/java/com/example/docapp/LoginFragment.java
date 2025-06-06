package com.example.docapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private DB db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        db = new DB(getActivity());

        etUsername = view.findViewById(R.id.et_username);
        etPassword = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btn_login);

        etUsername.setText("");
        etPassword.setText("");

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            if (db.checkUser(username, password)) {
                ((Main) getActivity()).navigateToDoctors();
            } else {
                Toast.makeText(getActivity(), "Неверные данные", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}