package com.example.recipes.ui.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.recipes.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {

    private FirebaseAuth auth;
    private LinearLayout loginLayout;
    private TextInputLayout emailInput, passwordInput;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        loginLayout = view.findViewById(R.id.login_layout);
        emailInput = view.findViewById(R.id.email_input);
        passwordInput = view.findViewById(R.id.password_input);
        Button loginButton = view.findViewById(R.id.login_button);
        progressBar = view.findViewById(R.id.progress_bar);
        toolbar.setNavigationOnClickListener(this::goBack);
        loginButton.setOnClickListener(this::login);
    }

    private void goBack(View view) {
        Navigation.findNavController(view).navigateUp();
    }

    private void login(View view) {
        String email = emailInput.getEditText().getText().toString();
        String password = passwordInput.getEditText().getText().toString();
        if (email.trim().isEmpty() || password.trim().isEmpty()) {
            Toast.makeText(getContext(), "Both fields are required", Toast.LENGTH_SHORT).show();
            return;
        }
        loginLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            Toast.makeText(getContext(), "Logged in successfully", Toast.LENGTH_SHORT).show();
            goBack(view);
        }).addOnFailureListener(exception -> {
            progressBar.setVisibility(View.GONE);
            loginLayout.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}