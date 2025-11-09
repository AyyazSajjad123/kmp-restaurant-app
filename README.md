# PKR Restaurant – Kotlin Multiplatform App

A simple Restaurant ordering app built with **Kotlin Multiplatform (KMP)** and **Jetpack Compose**.  
Currently the Android app is working with **Firebase Firestore** for loading the menu.  
The shared `:shared` module is ready for reuse in other platforms (iOS, Desktop) later.

Developer: **Tayyaba Saha**

---

## 📱 Features

- Restaurant home screen: **PKR Restaurant**
- Menu list loaded from **Firestore `menu` collection**
- Each item shows:
  - Name  
  - Price (PKR)
- **Add to Cart** button for each item
- Cart screen:
  - List of selected items
  - Increase / decrease quantity
  - Total amount calculation
- Basic **Kotlin Multiplatform structure** with a `shared` module

---

## 🧱 Tech Stack

- **Language:** Kotlin 2.x
- **Architecture:** simple MVVM-style with `ViewModel`
- **UI:** Jetpack Compose (Material 3)
- **Multiplatform:** Kotlin Multiplatform (`:shared` module)
- **Backend:** Firebase Firestore
- **Build tools:**
  - Android Gradle Plugin 8.6.x+
  - Gradle Kotlin DSL (`build.gradle.kts`)

---

## 🗂 Project Structure

```text
RestaurantApp/
 ├─ app/                       # Android app module
 │   ├─ src/main/java/com/example/restaurantapp/
 │   │    ├─ MainActivity.kt
 │   │    ├─ data/             # (for Android-only data if needed later)
 │   │    └─ ui/theme/         # Compose theme files
 │   └─ google-services.json   # Firebase config (Android)
 │
 └─ shared/                    # Kotlin Multiplatform module
     ├─ src/commonMain/kotlin/com/example/restaurantapp/shared/
     │    ├─ MenuItem.kt       # Shared data model
     │    └─ CartItem.kt       # Shared data model
     └─ src/androidMain/...    # Android-specific KMP code (if any)
