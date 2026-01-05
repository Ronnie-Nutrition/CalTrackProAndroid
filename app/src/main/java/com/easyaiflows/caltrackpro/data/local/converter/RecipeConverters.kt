package com.easyaiflows.caltrackpro.data.local.converter

import androidx.room.TypeConverter
import com.easyaiflows.caltrackpro.domain.model.RecipeIngredient
import com.easyaiflows.caltrackpro.domain.model.SimpleFoodItem
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * Room TypeConverters for Recipe-related complex types.
 * Uses Moshi for JSON serialization.
 */
class RecipeConverters {

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    // Adapter for List<RecipeIngredient>
    private val ingredientListType = Types.newParameterizedType(
        List::class.java,
        RecipeIngredientJson::class.java
    )
    private val ingredientListAdapter: JsonAdapter<List<RecipeIngredientJson>> =
        moshi.adapter(ingredientListType)

    // Adapter for List<String>
    private val stringListType = Types.newParameterizedType(
        List::class.java,
        String::class.java
    )
    private val stringListAdapter: JsonAdapter<List<String>> =
        moshi.adapter(stringListType)

    /**
     * Convert List<RecipeIngredient> to JSON String for storage.
     */
    @TypeConverter
    fun fromIngredientList(ingredients: List<RecipeIngredient>?): String {
        if (ingredients == null) return "[]"
        val jsonList = ingredients.map { it.toJson() }
        return ingredientListAdapter.toJson(jsonList)
    }

    /**
     * Convert JSON String back to List<RecipeIngredient>.
     */
    @TypeConverter
    fun toIngredientList(json: String?): List<RecipeIngredient> {
        if (json.isNullOrEmpty() || json == "[]") return emptyList()
        val jsonList = ingredientListAdapter.fromJson(json) ?: return emptyList()
        return jsonList.map { it.toDomain() }
    }

    /**
     * Convert List<String> (instructions) to JSON String for storage.
     */
    @TypeConverter
    fun fromStringList(strings: List<String>?): String {
        if (strings == null) return "[]"
        return stringListAdapter.toJson(strings)
    }

    /**
     * Convert JSON String back to List<String> (instructions).
     */
    @TypeConverter
    fun toStringList(json: String?): List<String> {
        if (json.isNullOrEmpty() || json == "[]") return emptyList()
        return stringListAdapter.fromJson(json) ?: emptyList()
    }
}

/**
 * JSON-serializable version of RecipeIngredient.
 * Moshi needs explicit JSON classes since RecipeIngredient has computed properties.
 */
data class RecipeIngredientJson(
    val name: String,
    val brand: String?,
    val barcode: String?,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val servingSize: Double,
    val servingUnit: String,
    val quantity: Double
)

/**
 * Convert RecipeIngredient to JSON-serializable form.
 */
private fun RecipeIngredient.toJson(): RecipeIngredientJson {
    return RecipeIngredientJson(
        name = foodItem.name,
        brand = foodItem.brand,
        barcode = foodItem.barcode,
        calories = foodItem.calories,
        protein = foodItem.protein,
        carbs = foodItem.carbs,
        fat = foodItem.fat,
        servingSize = foodItem.servingSize,
        servingUnit = foodItem.servingUnit,
        quantity = quantity
    )
}

/**
 * Convert JSON-serializable form back to RecipeIngredient.
 */
private fun RecipeIngredientJson.toDomain(): RecipeIngredient {
    return RecipeIngredient(
        foodItem = SimpleFoodItem(
            name = name,
            brand = brand,
            barcode = barcode,
            calories = calories,
            protein = protein,
            carbs = carbs,
            fat = fat,
            servingSize = servingSize,
            servingUnit = servingUnit
        ),
        quantity = quantity
    )
}
