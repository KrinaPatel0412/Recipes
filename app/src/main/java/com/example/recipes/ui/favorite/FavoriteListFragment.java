package com.example.recipes.ui.favorite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipes.R;
import com.example.recipes.data.responses.Recipe;
import com.example.recipes.ui.recipe.RecipeListAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FavoriteListFragment extends Fragment implements RecipeListAdapter.RecipeEventListener {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private RecipeListAdapter recipeListAdapter;
    private ArrayList<Recipe> recipes;
    private NavController navController;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        recipeListAdapter = new RecipeListAdapter(this);
        recipes = new ArrayList<>();
        navController = Navigation.findNavController(view);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        recyclerView = view.findViewById(R.id.recycler_view);
        progressBar = view.findViewById(R.id.progress_bar);
        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());
        recyclerView.setAdapter(recipeListAdapter);
        loadFavorites();
    }

    private void loadFavorites() {
        String userId = auth.getCurrentUser().getUid();
        firestore.collection("users").document(userId).collection("favorites").get().addOnSuccessListener(snapshot -> {
            for (DocumentSnapshot document : snapshot.getDocuments()) {
                Recipe recipe = document.toObject(Recipe.class);
                recipes.add(recipe);
            }
            recipeListAdapter.submitList(recipes);
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onClick(Recipe recipe) {
        navController.navigate(FavoriteListFragmentDirections.actionFavoriteListFragmentToRecipeDetailFragment(recipe));
    }
}