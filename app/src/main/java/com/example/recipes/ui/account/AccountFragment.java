package com.example.recipes.ui.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.recipes.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class AccountFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private NavController navController;
    private ProgressBar progressBar;
    private LinearLayout accountLayout;
    private TextView nameText, emailText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        navController = Navigation.findNavController(view);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        progressBar = view.findViewById(R.id.progress_bar);
        accountLayout = view.findViewById(R.id.account_layout);
        nameText = view.findViewById(R.id.name_text);
        emailText = view.findViewById(R.id.email_text);
        Button logoutButton = view.findViewById(R.id.logout_button);
        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());
        loadUserData();
        logoutButton.setOnClickListener(this::logout);
    }

    private void loadUserData() {
        String userId = auth.getCurrentUser().getUid();
        firestore.collection("users").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            String name = documentSnapshot.getString("name");
            String email = documentSnapshot.getString("email");
            nameText.setText(name);
            emailText.setText(email);
            progressBar.setVisibility(View.GONE);
            accountLayout.setVisibility(View.VISIBLE);
        });
    }

    private void logout(View view) {
        auth.signOut();
        navController.navigate(AccountFragmentDirections.actionAccountFragmentToAuthFragment());
    }
}