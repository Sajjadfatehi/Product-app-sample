package com.example.gameapp.features.products

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.gameapp.core.Fail
import com.example.gameapp.core.Loading
import com.example.gameapp.core.Success
import com.example.gameapp.data.model.Header
import com.example.gameapp.data.model.Product
import com.example.gameapp.data.model.ProductsResponse
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


@Composable
fun ProductScreen(navController: NavController) {
    val viewModel = hiltViewModel<ProductsViewModel>()

    Surface(Modifier.background(MaterialTheme.colorScheme.surface)) {
        ProductScreen(viewModel = viewModel) {

        }
    }
}


@Composable
internal fun ProductScreen(viewModel: ProductsViewModel, onAction: (ProductsAction) -> Unit) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    ProductScreen(viewState) {
        viewModel.submitAction(action = it)
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.onEach {

        }.launchIn(this)
    }

}

@Composable
internal fun ProductScreen(
    viewState: ProductsState,
    onAction: (ProductsAction) -> Unit
) {

    when (viewState.productsResponse) {
        is Loading -> {

        }

        is Fail -> {

        }

        is Success -> {
            val products = viewState.productsResponse()!!
            ProductList(productResponse = products)
        }

        else -> Unit
    }
}


@Composable
fun ProductList(modifier: Modifier = Modifier, productResponse: ProductsResponse) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        item {
            Header(Modifier, productResponse.header)
        }
        items(productResponse.products) {
            ProductItem(
                product = it,
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            )
        }
        item {
            Footer()
        }
    }
}

@Composable
fun Header(modifier: Modifier = Modifier, header: Header) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Text(
            text = header.headerTitle,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = header.headerDescription,
            style = MaterialTheme.typography.titleSmall,
            color = Color.Gray
        )
    }
}

@Composable
fun Footer(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "© 2016 Best Company")
    }
}

@Composable
fun ProductItem(modifier: Modifier = Modifier, product: Product) {
    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
            .border(width = 2.dp, color = MaterialTheme.colorScheme.tertiary)
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            AsyncImage(
                model = product.imageURL,
                contentDescription = "icon",
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(8.dp)
            )
            Column {
                Row {
                    Text(text = product.name, style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = product.releaseDate.toString(), Modifier.padding(horizontal = 8.dp))
                }
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.Gray
                )
                Row {
                    val context = LocalContext.current
                    Text(text = product.price.toString())
                    Spacer(modifier = Modifier.weight(1f))
                    StarRatingBar(rating = product.rating.toFloat()) {
                        Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

@Composable
fun StarRatingBar(
    maxStars: Int = 5,
    rating: Float,
    onRatingChanged: (Float) -> Unit
) {
    val density = LocalDensity.current.density
    val starSize = (12f * density).dp
    val starSpacing = (0.5f * density).dp

    Row(
        modifier = Modifier
            .selectableGroup()
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val isSelected = i <= rating
            val icon = if (isSelected) Icons.Filled.Star else Icons.Default.Star
            val iconTintColor = if (isSelected) Color(0xFFFFC700) else Color(0x20FFFFFF)
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTintColor,
                modifier = Modifier
                    .selectable(
                        selected = isSelected,
                        onClick = {
                            onRatingChanged(i.toFloat())
                        }
                    )
                    .width(starSize)
                    .height(starSize)
            )

            if (i < maxStars) {
                Spacer(modifier = Modifier.width(starSpacing))
            }
        }
    }
}