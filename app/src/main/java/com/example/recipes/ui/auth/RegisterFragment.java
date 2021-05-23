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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterFragment extends Fragment {

    private final HashMap<String, String> userData = new HashMap<>();
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private LinearLayout registerLayout;
    private TextInputLayout nameInput, emailInput, passwordInput;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        registerLayout = view.findViewById(R.id.register_layout);
        nameInput = view.findViewById(R.id.name_input);
        emailInput = view.findViewById(R.id.email_input);
        passwordInput = view.findViewById(R.id.password_input);
        Button registerButton = view.findViewById(R.id.register_button);
        progressBar = view.findViewById(R.id.progress_bar);
        toolbar.setNavigationOnClickListener(this::goBack);
        registerButton.setOnClickListener(this::register);
    }

    private void goBack(View view) {
        Navigation.findNavController(view).navigateUp();
    }

    private void register(View view) {
        String name = nameInput.getEditText().getText().toString();
        String email = emailInput.getEditText().getText().toString();
        String password = passwordInput.getEditText().getText().toString();
        if (name.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
            Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }
        registerLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            String userId = authResult.getUser().getUid();
            userData.put("name", name);
            userData.put("email", email);
            firestore.collection("users").document(userId).set(userData).addOnSuccessListener(unused -> {
                Toast.makeText(getContext(), "Registered successfully", Toast.LENGTH_SHORT).show();
                goBack(view);
            }).addOnFailureListener(exception -> {
                progressBar.setVisibility(View.GONE);
                registerLayout.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(exception -> {
            progressBar.setVisibility(View.GONE);
            registerLayout.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}