package com.example.jetweatherforecast.screen.main

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.jetweatherforecast.R
import com.example.jetweatherforecast.data.DataOrException
import com.example.jetweatherforecast.model.Weather
import com.example.jetweatherforecast.model.WeatherItem
import com.example.jetweatherforecast.util.formatDate
import com.example.jetweatherforecast.util.formatDateTime
import com.example.jetweatherforecast.util.formatDecimals
import com.example.jetweatherforecast.widget.WeatherAppBar

@Composable
fun MainScreen(navController: NavController,
               mainViewModel: MainViewModel = hiltViewModel()) {

    val weatherData = produceState<DataOrException<Weather, Boolean, Exception>>(
        initialValue = DataOrException(loading = true)
    ) {
        value = mainViewModel.getWeatherData(city = "maputo")
    }.value

    if (weatherData.loading == true) {
        CircularProgressIndicator()
    } else if(weatherData.data != null) {
        MainScaffold(weather = weatherData.data!!, navController)
    }
}

@Composable
fun MainScaffold(weather: Weather, navController: NavController) {
    Scaffold(
        topBar = {
        WeatherAppBar(title = weather.city.name + " ,${weather.city.country}",
            navController = navController,
            elevation = 5.dp) {
            Log.e("TAG", "MainScaffold: Button Clicked")
        }
    }) {
        Column(modifier = Modifier.padding(top = it.calculateTopPadding())) {
            MainContent(data = weather)
        }
    }
}

@Composable
fun MainContent(data: Weather) {
    val weatherItem = data.list[0]
    val imageUrl = "https://openweathermap.org/img/wn/${weatherItem.weather[0].icon}.png"

    Column(modifier = Modifier
        .padding(4.dp)
        .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = formatDate( weatherItem.dt),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(6.dp)
        )

        Surface(modifier = Modifier
            .padding(4.dp)
            .size(200.dp),
            shape = CircleShape,
            color = Color(0xFFFFC400)
        ) {
           Column(
               verticalArrangement = Arrangement.Center,
               horizontalAlignment = Alignment.CenterHorizontally
           ) {
               WeatherStateImage(imageUrl = imageUrl)
               Text(
                   text = formatDecimals(weatherItem.temp.day) + "º",
                   style = MaterialTheme.typography.headlineMedium,
                   fontWeight = FontWeight.ExtraBold
               )
               Text(
                   text = weatherItem.weather[0].main,
                   fontStyle = FontStyle.Italic
               )
           }
        }
        HumidityWindPressureRow(weather = weatherItem)
        Divider()
        SunsetSunRiseRow(weather = weatherItem)
        Text(text = "This Week",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Surface(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
            color = Color(0xFFEEF1EF),
            shape = RoundedCornerShape(14.dp)
        ) {
            LazyColumn(modifier = Modifier
                .padding(2.dp),
                contentPadding = PaddingValues(1.dp)
            ) {
                items(items = data.list) {item: WeatherItem ->
                    WeatherDetailRow(weather = item)
                }
            }
        }
    }


}

@Composable
fun WeatherStateImage(imageUrl: String) {
    Image(
        painter = rememberImagePainter(imageUrl),
        contentDescription = "icon image",
        modifier = Modifier
            .size(80.dp)
    )
}

@Composable
fun HumidityWindPressureRow(weather: WeatherItem) {
    Row(modifier = Modifier
        .padding(12.dp)
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier
            .padding(4.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.humidity),
                contentDescription = "humidity icon",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "${weather.humidity}%",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row() {
            Icon(
                painter = painterResource(id = R.drawable.pressure),
                contentDescription = "pressure icon",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "${weather.pressure} psi",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row() {
            Icon(
                painter = painterResource(id = R.drawable.wind),
                contentDescription = "wind icon",
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "${weather.humidity} mph", //TODO Check this
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun SunsetSunRiseRow(weather: WeatherItem) {
    Row(modifier = Modifier
        .padding(top = 12.dp, bottom = 6.dp)
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier
            .padding(4.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.sunset),
                contentDescription = "sunset icon",
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = formatDateTime(weather.sunset),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row() {
            Icon(
                painter = painterResource(id = R.drawable.sunrise),
                contentDescription = "sunrise icon",
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = formatDateTime(weather.sunrise),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun WeatherDetailRow(weather: WeatherItem) {
    val imageUrl = "https://openweathermap.org/img/wn/${weather.weather[0].icon}.png"

    Surface(modifier = Modifier
        .padding(3.dp)
        .fillMaxWidth(),
        shape = CircleShape.copy(topEnd = CornerSize(6.dp)),
        color = Color.White
    ) {
        Row(modifier = Modifier
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatDate(weather.dt)
                    .split(",")[0],
                modifier = Modifier
                    .padding(start = 5.dp)
            )
            WeatherStateImage(imageUrl = imageUrl)
            Surface(modifier = Modifier
                .padding(0.dp),
                shape = CircleShape,
                color = Color(0xFFFFC400)
            ) {
                Text(
                    text = weather.weather[0].description,
                    modifier = Modifier
                        .padding(4.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(text = buildAnnotatedString {
                withStyle(SpanStyle(
                    color = Color.Blue.copy(alpha = 0.7f),
                    fontWeight = FontWeight.SemiBold
                )) {
                    append(formatDecimals(weather.temp.max) + "º")
                }

                withStyle(SpanStyle(
                    color = Color.LightGray
                )) {
                    append(formatDecimals(weather.temp.min) + "º")
                }
            })
        }
    }
}
