package com.example.recipes.ui.recipe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.recipes.R;
import com.example.recipes.data.responses.Recipe;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class RecipeListAdapter extends ListAdapter<Recipe, RecipeListAdapter.RecipeViewHolder> {

    private final RecipeEventListener eventListener;

    public RecipeListAdapter(RecipeEventListener eventListener) {
        super(new RecipeDiffCallback());
        this.eventListener = eventListener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = getItem(position);
        Picasso.get().load(recipe.getImage()).placeholder(R.drawable.ic_recipe_image).into(holder.recipeImage);
        holder.labelText.setText(recipe.getLabel());
        String calories = String.format(Locale.getDefault(), "%d Calories", recipe.getCalories().intValue());
        holder.caloriesText.setText(calories);
        int recipeTime = recipe.getTotalTime().intValue();
        if (recipeTime == 0) {
            recipeTime = 30;
        }
        String time = String.format(Locale.getDefault(), "%d Minutes", recipeTime);
        holder.timeText.setText(time);
        holder.itemView.setOnClickListener(v -> {
            eventListener.onClick(recipe);
        });
    }

    public interface RecipeEventListener {
        void onClick(Recipe recipe);
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {

        ImageView recipeImage;
        TextView labelText, caloriesText, timeText;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipe_image);
            labelText = itemView.findViewById(R.id.label_text);
            caloriesText = itemView.findViewById(R.id.calories_text);
            timeText = itemView.findViewById(R.id.time_text);
        }
    }

    static class RecipeDiffCallback extends DiffUtil.ItemCallback<Recipe> {
        @Override
        public boolean areItemsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
            return oldItem.getUrl().equals(newItem.getUrl());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
            return oldItem.getLabel().equals(newItem.getLabel());
        }
    }
}
