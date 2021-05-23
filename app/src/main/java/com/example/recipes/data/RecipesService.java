package com.example.recipes.data;

import com.example.recipes.data.responses.GetRecipes;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipesService {

    @GET("search?app_id=e59f9fe2&app_key=a2bfc1a3ed3a816201e42b43c05f0538")
    Call<GetRecipes> getRecipes(@Query("q") String query, @Query("from") int from, @Query("to") int to);
}