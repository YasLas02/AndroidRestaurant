package fr.isen.lasri.androiderestaurant

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import fr.isen.lasri.androiderestaurant.Panier.Panier
import fr.isen.lasri.androiderestaurant.Panier.PanierElem

class PanierActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
          PanierView()
        }
    }
}

@Composable
fun PanierView() {
    val context = LocalContext.current
    val panierItems = remember {
        mutableStateListOf<PanierElem>()
    }
    var showDialog by remember { mutableStateOf(false) }

    panierItems.clear()
    panierItems.addAll(Panier.current(context).items)

    if (panierItems.isEmpty()) {
        showDialog = true
    }

    LazyColumn {
        items(panierItems) {
            PanierElemView(it, panierItems)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Panier vide") },
            text = { Text("Votre panier est vide. Veuillez ajouter des menus.") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    val intent = Intent(context, HomeActivity::class.java)
                    context.startActivity(intent)
                }) {
                    Text("OK")
                }
            }
        )
    }
    PanierEtPayer(panierItems)
}

@Composable
fun PanierEtPayer(panierItems: MutableList<PanierElem>) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    // Calculer le total
    val total = panierItems.sumByDouble { it.dish.prices.first().price.toDouble() * it.count.toDouble() }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total : $total €",
                style = MaterialTheme.typography.headlineLarge
            )
            Button(onClick = { showDialog = true }) {
                Text("Payer")
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Paiement non disponible") },
                text = { Text("Le paiement n'est pas disponible pour le moment.") },
                confirmButton = {
                    Button(onClick = {
                        showDialog = false
                        val intent = Intent(context, PanierActivity::class.java)
                        context.startActivity(intent)
                    }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}




@Composable
fun PanierElemView(item: PanierElem, basketItems: MutableList<PanierElem>) {
    Card {
        val context = LocalContext.current
        Card(border =  BorderStroke(1.dp, Color.Black),
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Row(Modifier.padding(8.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.dish.images.first())
                        .build(),
                    null,
                    placeholder = painterResource(R.drawable.image_error),
                    error = painterResource(R.drawable.image_error),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .width(80.dp)
                        .height(80.dp)
                        .clip(RoundedCornerShape(10))
                        .padding(8.dp)
                )
                Column(
                    Modifier
                        .align(alignment = Alignment.CenterVertically)
                        .padding(8.dp)
                ) {
                    Text(item.dish.name)
                    Text("${item.dish.prices.first().price} €")
                }

                Spacer(Modifier.weight(1f))
                Text(item.count.toString(),
                    Modifier.align(alignment = Alignment.CenterVertically))
                Button(onClick = {
                    Panier.current(context).delete(item, context)
                    basketItems.clear()
                    basketItems.addAll(Panier.current(context).items)
                }) {
                    Text("X")
                }
            }
        }
    }
}