package com.example.restaurantapp.shared

data class MenuItem(
    val id: String,
    val name: String,
    val price: Double
)

data class CartItem(
    val item: MenuItem,
    val qty: Int
)
