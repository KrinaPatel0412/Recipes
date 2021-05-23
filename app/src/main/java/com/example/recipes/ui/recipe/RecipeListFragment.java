package com.example.recipes.ui.recipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipes.R;
import com.example.recipes.data.RecipesClient;
import com.example.recipes.data.responses.GetRecipes;
import com.example.recipes.data.responses.Hit;
import com.example.recipes.data.responses.Recipe;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeListFragment extends Fragment implements RecipeListAdapter.RecipeEventListener {

    private RecipeListAdapter recipeListAdapter;
    private ArrayList<Recipe> recipes;
    private NavController navController;
    private ProgressBar progressBar;
    private NestedScrollView scrollView;
    private TextView errorText;
    private boolean isFirstTime = true;
    private String query = "chicken";
    private int from = 0;
    private int to = 20;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        recipeListAdapter = new RecipeListAdapter(this);
        recipes = new ArrayList<>();
        navController = Navigation.findNavController(view);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        progressBar = view.findViewById(R.id.progress_bar);
        scrollView = view.findViewById(R.id.scroll_view);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        errorText = view.findViewById(R.id.error_text);
        recyclerView.setAdapter(recipeListAdapter);
        loadRecipes();
        toolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.account_item) {
                navController.navigate(RecipeListFragmentDirections.actionRecipeListFragmentToAccountFragment());
                return true;
            } else if (itemId == R.id.favorites_item) {
                navController.navigate(RecipeListFragmentDirections.actionRecipeListFragmentToFavoriteListFragment());
                return true;
            } else {
                return false;
            }
        });
        MenuItem searchItem = toolbar.getMenu().findItem(R.id.search_item);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                recipes.clear();
                recipeListAdapter.submitList(recipes);
                recipeListAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.GONE);
                isFirstTime = true;
                query = text;
                loadRecipes();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        toolbar.getMenu();
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                isFirstTime = false;
                from = to;
                to += 20;
                loadRecipes();
            }
        });
    }

    @Override
    public void onClick(Recipe recipe) {
        navController.navigate(RecipeListFragmentDirections.actionRecipeListFragmentToRecipeDetailFragment(recipe));
    }

    private void loadRecipes() {
        Callback<GetRecipes> callback = new Callback<GetRecipes>() {
            @Override
            public void onResponse(@NonNull Call<GetRecipes> call, @NonNull Response<GetRecipes> response) {
                List<Hit> hits = response.body().getHits();
                for (Hit hit : hits) {
                    recipes.add(hit.getRecipe());
                }
                recipeListAdapter.submitList(recipes);
                if (isFirstTime) {
                    progressBar.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                } else {
                    recipeListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetRecipes> call, @NonNull Throwable t) {
                if (isFirstTime) {
                    progressBar.setVisibility(View.GONE);
                    errorText.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getContext(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                }
            }
        };
        RecipesClient.getInstance().getRecipes(query, from, to).enqueue(callback);
    }
}