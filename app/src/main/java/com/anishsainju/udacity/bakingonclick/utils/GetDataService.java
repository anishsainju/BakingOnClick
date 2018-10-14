package com.anishsainju.udacity.bakingonclick.utils;

import com.anishsainju.udacity.bakingonclick.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetDataService {

    @GET("/topher/2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> getAllRecipes();
}
