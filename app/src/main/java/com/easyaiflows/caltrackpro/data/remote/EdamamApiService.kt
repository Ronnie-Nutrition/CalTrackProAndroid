package com.easyaiflows.caltrackpro.data.remote

import com.easyaiflows.caltrackpro.data.remote.dto.FoodSearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface for the Edamam Food Database API.
 * Base URL: https://api.edamam.com/api/food-database/v2/
 *
 * Note: app_id and app_key are automatically added by EdamamAuthInterceptor.
 */
interface EdamamApiService {

    /**
     * Search for foods using the parser endpoint.
     *
     * @param query The search query (ingredient text)
     * @param nutritionType Type of nutrition data ("logging" for food logging apps)
     * @return FoodSearchResponseDto containing parsed and hint results
     */
    @GET("parser")
    suspend fun searchFoods(
        @Query("ingr") query: String,
        @Query("nutrition-type") nutritionType: String = "logging"
    ): FoodSearchResponseDto

    /**
     * Search for foods with pagination support.
     *
     * @param query The search query (ingredient text)
     * @param nutritionType Type of nutrition data
     * @param session Session key for pagination (from previous response)
     * @return FoodSearchResponseDto containing parsed and hint results
     */
    @GET("parser")
    suspend fun searchFoodsWithSession(
        @Query("ingr") query: String,
        @Query("nutrition-type") nutritionType: String = "logging",
        @Query("session") session: String
    ): FoodSearchResponseDto
}
