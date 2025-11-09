package com.example.restaurantapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.FilledTonalButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.restaurantapp.shared.MenuItem
import com.example.restaurantapp.shared.CartItem
import com.example.restaurantapp.shared.RestaurantRepository
import com.example.restaurantapp.data.FirebaseRestaurantRepository
import kotlinx.coroutines.launch

// ---------- VIEWMODEL ----------
class RestaurantViewModel(
    private val repo: RestaurantRepository = FirebaseRestaurantRepository()
) : ViewModel() {

    var menu by mutableStateOf<List<MenuItem>>(emptyList())
        private set

    var cart by mutableStateOf<List<CartItem>>(emptyList())
        private set

    init {
        loadMenu()
    }

    private fun loadMenu() {
        viewModelScope.launch {
            // menu Firestore سے repository کے ذریعے آئے گا
            menu = repo.loadMenu()
        }
    }

    fun addToCart(item: MenuItem) {
        val existing = cart.find { it.item.id == item.id }
        cart = if (existing == null) {
            cart + CartItem(item, 1)
        } else {
            cart.map {
                if (it.item.id == item.id) it.copy(qty = it.qty + 1) else it
            }
        }
    }

    fun changeQty(itemId: String, delta: Int) {
        cart = cart.mapNotNull { ci ->
            if (ci.item.id != itemId) return@mapNotNull ci
            val newQty = ci.qty + delta
            if (newQty <= 0) null else ci.copy(qty = newQty)
        }
    }

    fun cartCount(): Int = cart.sumOf { it.qty }

    fun cartTotal(): Double = cart.sumOf { it.item.price * it.qty }
}

// ---------- NAV ROUTES ----------
sealed class Screen(val route: String) {
    data object Menu : Screen("menu")
    data object Cart : Screen("cart")
}

// ---------- ACTIVITY ----------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val vm: RestaurantViewModel = viewModel()
            val navController = rememberNavController()
            RestaurantNavApp(navController, vm)
        }
    }
}

// ---------- ROOT WITH NAV ----------
@Composable
fun RestaurantNavApp(
    navController: NavHostController,
    vm: RestaurantViewModel
) {
    MaterialTheme {
        NavHost(
            navController = navController,
            startDestination = Screen.Menu.route
        ) {
            composable(Screen.Menu.route) {
                MenuScaffold(navController, vm)
            }
            composable(Screen.Cart.route) {
                CartScaffold(navController, vm)
            }
        }
    }
}

// ---------- MENU + APP BAR ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScaffold(
    navController: NavHostController,
    vm: RestaurantViewModel
) {
    val cartCount by remember { derivedStateOf { vm.cartCount() } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PKR Restaurant") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Cart.route) }) {
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) {
                                    Badge { Text(cartCount.toString()) }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Cart"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        MenuScreen(
            menu = vm.menu,
            onAddToCart = { vm.addToCart(it) },
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
fun MenuScreen(
    menu: List<MenuItem>,
    onAddToCart: (MenuItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (menu.isEmpty()) {
        Box(modifier = modifier.fillMaxSize()) {
            Text(
                text = "Loading menu or no items...",
                modifier = Modifier.padding(24.dp)
            )
        }
    } else {
        LazyColumn(modifier = modifier) {
            items(menu) { item ->
                MenuRow(item = item, onAddToCart = onAddToCart)
                Divider()
            }
        }
    }
}

@Composable
fun MenuRow(
    item: MenuItem,
    onAddToCart: (MenuItem) -> Unit
) {
    ListItem(
        headlineContent = { Text(item.name) },
        supportingContent = { Text("PKR ${item.price}") },
        trailingContent = {
            FilledTonalButton(onClick = { onAddToCart(item) }) {
                Text("Add")
            }
        }
    )
}

// ---------- CART + APP BAR ----------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScaffold(
    navController: NavHostController,
    vm: RestaurantViewModel
) {
    val cart = vm.cart
    val total = vm.cartTotal()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Cart") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (cart.isNotEmpty()) {
                Surface(tonalElevation = 4.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total: PKR ${"%.0f".format(total)}")
                        Button(onClick = {
                            // اگلے step میں: Firebase order place
                        }) {
                            Text("Place Order")
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (cart.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
            ) {
                Text(
                    text = "Cart is empty",
                    modifier = Modifier.padding(24.dp)
                )
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(cart) { ci ->
                    CartRow(
                        cartItem = ci,
                        onInc = { vm.changeQty(ci.item.id, +1) },
                        onDec = { vm.changeQty(ci.item.id, -1) }
                    )
                    Divider()
                }
            }
        }
    }
}

@Composable
fun CartRow(
    cartItem: CartItem,
    onInc: () -> Unit,
    onDec: () -> Unit
) {
    ListItem(
        headlineContent = { Text(cartItem.item.name) },
        supportingContent = {
            Text("PKR ${cartItem.item.price} × ${cartItem.qty}")
        },
        trailingContent = {
            Row {
                FilledTonalButton(onClick = onDec) { Text("-") }
                Spacer(Modifier.width(8.dp))
                Text(cartItem.qty.toString(), modifier = Modifier.padding(top = 8.dp))
                Spacer(Modifier.width(8.dp))
                FilledTonalButton(onClick = onInc) { Text("+") }
            }
        }
    )
}
