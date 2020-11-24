package com.food.foodella.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class RestaurantEntity(
    @ColumnInfo(name = "restaurant_id") @PrimaryKey var restaurant_id: String,

    @ColumnInfo(name = "restaurantName") var restaurantName: String

)