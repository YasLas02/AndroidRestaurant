package fr.isen.lasri.androiderestaurant

import android.content.Intent
import android.os.Bundle

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import fr.isen.lasri.androiderestaurant.NetWork.Category
import fr.isen.lasri.androiderestaurant.NetWork.Dish
import fr.isen.lasri.androiderestaurant.NetWork.MenuData
import fr.isen.lasri.androiderestaurant.NetWork.NetworkContantes
import org.json.JSONObject

class MenuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val type = (intent.getSerializableExtra(CATEGROY_EXTRA_KEY) as? MenuType) ?: MenuType.STARTER

        setContent {
            MenuView(type)
        }
        Log.d("lifeCycle", "Menu Activity - OnCreate")
    }

    override fun onPause() {
        Log.d("lifeCycle", "Menu Activity - OnPause")
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        Log.d("lifeCycle", "Menu Activity - OnResume")
    }

    override fun onDestroy() {
        Log.d("lifeCycle", "Menu Activity - onDestroy")
        super.onDestroy()
    }
    companion object{
        val  CATEGROY_EXTRA_KEY = "CATEGROY_EXTRA_KEY"
    }
}

@Composable
fun MenuView(type: MenuType){
    val  category = remember {
        mutableStateOf<Category?>(null)
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally){
        Text(type.title())
        LazyColumn{
            category.value?.let {myCategory->
                items(myCategory.items){contentCategory ->
                    dishRow(contentCategory)
                }
            }
        }
    }
    PostData(type ,category)
}
@Composable
fun AllMenusView() {
    Column {
        for (type in MenuType.entries) {
            MenuView(type)
        }
    }
}
@Composable
fun dishRow(dish: Dish){
    val context = LocalContext.current
    Card (border = BorderStroke(1.dp, color = Color.Black),
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .clickable {
                val IntentOfDetail = Intent(context, DetailActivity::class.java)
                IntentOfDetail.putExtra(DetailActivity.DISH_EXTRA_KEY, dish)
                context.startActivity(IntentOfDetail)
            }
    ){

        Row {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(dish.images.first())
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


            Text( dish.name,
                Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .padding(8.dp)
            )
            Spacer(Modifier.weight(1f))

            Text( " ${dish.prices.first().price} € ",
                Modifier.align(alignment = Alignment.CenterVertically)
            )
        }

    }
}
@Composable
fun PostData(type: MenuType, category: MutableState<Category?>) {
    val currentCategory = type.title()
    val context = LocalContext.current
    val queue = Volley.newRequestQueue(context)

    val params = JSONObject()
    params.put(NetworkContantes.ID_SHOP, "1")

    val request = JsonObjectRequest(
        Request.Method.POST,
        NetworkContantes.URL,
        params,
        {response ->
            Log.d("request", response.toString(2))
            val result = GsonBuilder().create().fromJson(response.toString(), MenuData::class.java)
            val filteredResult = result.data.first {category -> category.name == currentCategory}
            category.value = filteredResult
            Log.d("result", "")
        },
        {ResponseError ->
            Log.e("request", ResponseError.toString())
        }
    )

    queue.add(request)
}