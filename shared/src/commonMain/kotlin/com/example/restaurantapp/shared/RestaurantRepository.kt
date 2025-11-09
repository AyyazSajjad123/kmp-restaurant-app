package com.example.restaurantapp.shared

interface RestaurantRepository {
    suspend fun loadMenu(): List<MenuItem>
}
