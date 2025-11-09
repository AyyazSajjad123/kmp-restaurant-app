package com.example.restaurantapp.data

import com.example.restaurantapp.shared.MenuItem
import com.example.restaurantapp.shared.RestaurantRepository
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseRestaurantRepository : RestaurantRepository {

    private val db = Firebase.firestore

    override suspend fun loadMenu(): List<MenuItem> {
        val snapshot = db.collection("menu").get().await()
        return snapshot.documents.mapNotNull { doc ->
            val name = doc.getString("name")
            val price = doc.getDouble("price")
            if (name != null && price != null) {
                MenuItem(
                    id = doc.id,
                    name = name,
                    price = price
                )
            } else null
        }
    }
}
