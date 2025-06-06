package com.example.docapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

public class Main extends AppCompatActivity {
    private boolean isLoggingOut = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        showLoginFragment();
    }

    public void showLoginFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
    }

    public void navigateToDoctors() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new DoctorsFragment())
                .addToBackStack("doctors")
                .commit();
    }

    public void logout() {
        isLoggingOut = true;
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        showLoginFragment();
    }
}