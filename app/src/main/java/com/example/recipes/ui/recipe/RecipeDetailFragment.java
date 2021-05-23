package com.example.recipes.ui.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.recipes.R;
import com.example.recipes.data.responses.Recipe;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class RecipeDetailFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private Recipe recipe;
    private ExtendedFloatingActionButton favoriteButton;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        recipe = RecipeDetailFragmentArgs.fromBundle(getArguments()).getRecipe();
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        ImageView recipeImage = view.findViewById(R.id.recipe_image);
        TextView labelText = view.findViewById(R.id.label_text);
        TextView sourceText = view.findViewById(R.id.source_text);
        TextView ingredientsText = view.findViewById(R.id.ingredients_text);
        favoriteButton = view.findViewById(R.id.favorite_button);
        progressBar = view.findViewById(R.id.progress_bar);
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(view).navigateUp());
        toolbar.setTitle(recipe.getLabel());
        Picasso.get().load(recipe.getImage()).placeholder(R.drawable.ic_recipe_image).into(recipeImage);
        labelText.setText(recipe.getLabel());
        String source = String.format(Locale.getDefault(), "Source: %s", recipe.getSource());
        sourceText.setText(source);
        StringBuilder ingredients = new StringBuilder("Ingredients:");
        for (String ingredient : recipe.getIngredientLines()) {
            ingredients.append("\n").append(ingredient);
        }
        ingredientsText.setText(ingredients);
        favoriteButton.setOnClickListener(v -> {
            addToFavorites();
        });
    }

    private void addToFavorites() {
        favoriteButton.hide();
        progressBar.setVisibility(View.VISIBLE);
        String userId = auth.getCurrentUser().getUid();
        firestore.collection("users").document(userId).collection("favorites").add(recipe).addOnSuccessListener(documentReference -> {
            Toast.makeText(getContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            favoriteButton.show();
        });
    }
}