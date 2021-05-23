package com.example.recipes.data;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecipesClient {

    private static final String baseURL = "https://api.edamam.com/";

    private static RecipesService instance;

    public static RecipesService getInstance() {
        if (instance == null) {
            instance = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(baseURL)
                    .build()
                    .create(RecipesService.class);
        }
        return instance;
    }
}