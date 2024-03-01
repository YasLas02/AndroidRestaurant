package fr.isen.lasri.androiderestaurant

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import fr.isen.lasri.androiderestaurant.NetWork.Dish
import fr.isen.lasri.androiderestaurant.Panier.Panier


class DetailActivity : ComponentActivity() {
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val  dish = intent.getSerializableExtra(DISH_EXTRA_KEY) as? Dish
        setContent {

            val ingredient = dish?.ingredients?.map { tabIngredient ->
                (tabIngredient.name)
            }?.joinToString(", ") ?: ""
            val pagerState = rememberPagerState(pageCount = {
                dish?.images?.count() ?: 0
            })

            Column {
                Text(
                    text = "${dish?.name} \n ",
                    Modifier
                        .padding(10.dp)
                        .height(15.dp)
                        .align(Alignment.CenterHorizontally))
                HorizontalPager(state = pagerState) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(dish?.images?.get(it))
                            .build(),
                        null,
                        placeholder = painterResource(R.drawable.image_error),
                        error = painterResource(R.drawable.image_error),
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .height(270.dp)
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }

                Box (
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .border(1.dp, Color.Black)
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        "Liste d'ingrédients :\t\t${ingredient}",fontWeight = FontWeight.Bold, fontSize = 11.sp,

                    )
                }
                var quantity by remember { mutableIntStateOf(1) }

                NumberIncrementerBox { newQuantity ->
                    quantity = newQuantity
                }
                       val intent = intent
                    PanierCommadeButton(intent)
                DishPriceBox(dish = dish, quantity = quantity)
            }
        }
                  }
    companion object{
        val  DISH_EXTRA_KEY = " DISH_EXTRA_KEY"
    }
}

@Composable
fun NumberIncrementerBox(onQuantityChanged: (Int) -> Unit) {
    val context = LocalContext.current
    var count by remember { mutableStateOf(1) }
    var showAlert by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                if (count > 1) {
                    count--
                    onQuantityChanged(count)
                } else {
                    showAlert = true
                }
            },
            modifier = Modifier.padding(end = 16.dp)
        ) {
            Text("-")
        }
        Text(
            text = count.toString(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Button(
            onClick = {
                if (count < 20) {
                    count++
                    onQuantityChanged(count)
                } else {
                    showAlert = true
                }
            },
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text("+")
        }
    }

    if (showAlert) {
        AlertDialog(
            onDismissRequest = { showAlert = false },
            title = { Text("Limite atteinte") },
            text = { Text("La quantité ne peut pas être inférieure à 1 ou supérieure à 20.") },
            confirmButton = {
                Button(onClick = {
                    showAlert = false
                    val intent = Intent(context, HomeActivity::class.java)
                    context.startActivity(intent)
                }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun DishPriceBox(dish: Dish?, quantity: Int) {
    val totalPrice = (dish?.prices?.firstOrNull()?.price?.toDouble() ?: 0.0) * quantity.toDouble()

    Button(
        onClick = { /* TODO */ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(50.dp)
    ) {
        Text(
            text = "Prix : $totalPrice €",
            modifier = Modifier
                .width(80.dp)
                .height(20.dp)
        )
    }
}

@Composable
fun PanierCommadeButton(intent: Intent) {
    val dish = intent.getSerializableExtra(DetailActivity.DISH_EXTRA_KEY) as? Dish
    val context = LocalContext.current
    var quantity by remember { mutableStateOf(1) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = {
                if (dish != null) {
                    Panier.current(context).add(dish, quantity, context)
                    Toast.makeText(context, "${dish?.name} ajouté au panier", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "Ajouter au panier")
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(
            onClick = {
                val intent = Intent(context, PanierActivity::class.java)
                context.startActivity(intent) },
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "Commander")
        }
    }
}

