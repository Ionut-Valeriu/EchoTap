package com.example.echotap_client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echotap_client.ui.theme.EchoTapClientTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EchoTapClientTheme {
                MyApp()
            }
        }
    }
}

@Composable
fun MyApp(modifier: Modifier = Modifier) {
    val bgImg = painterResource(R.drawable.echotap_bg)
    Box( modifier.fillMaxSize() ){
        Image(
            painter = bgImg,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Column( modifier.fillMaxSize() ) {
            UserDetails(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            MicDetails()
        }
    }
}

@Composable
fun UserDetails(modifier: Modifier = Modifier){
    val isLogged = remember { mutableStateOf(false) }

    val userImg: Painter
    var userName: String
    var userTint: Color
    var buttonDesc: String

    if (isLogged.value){
        userImg = painterResource(R.drawable.user_logged)
        userName = "Username"
        userTint = Color.Unspecified
        buttonDesc = "Log out"
    }
    else{
        userImg = painterResource(R.drawable.user)
        userName = "Please connect"
        userTint = Color.White
        buttonDesc = "Log in"
    }
    buttonDesc += " button"

    Row (
        modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(
            color = Color.White,
            fontSize = 23.sp,
            text = userName
        )
        IconButton(
            onClick = { isLogged.value = !isLogged.value }
        ) {
            Icon(
                tint = userTint,
                painter = userImg,
                contentDescription = buttonDesc
            )
        }
    }
}

@Composable
fun MicDetails(modifier: Modifier = Modifier) {

    val isListening = remember { mutableStateOf(false) }

    val textState = if (isListening.value){ "Listening..." } else { "Press to start" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = textState,
            color = Color.White,
            fontSize = 30.sp
        )
        Spacer(modifier = Modifier.size(75.dp))
        MicButton(
            isListening = isListening.value,
            onToggle = { isListening.value = !isListening.value },
            modifier.align(alignment = Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun MicButton(
    isListening: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier)
{
    var stateIcon: Painter
    var stateIconSize: Float

    if (isListening){
        stateIcon = painterResource(R.drawable.ear)
        stateIconSize = 0.4F
    } else {
        stateIcon = painterResource(R.drawable.mic)
        stateIconSize = 0.5F
    }

    IconButton(
        onClick = onToggle,
        modifier = modifier
            .size(200.dp)
            .background(
                shape = CircleShape,
                color = Color.hsl(268F, 0.88F, 0.36F)
            )
            .shadow(
                elevation = 8.dp,
                shape = CircleShape
            )
    ) {
        Icon(
            painter = stateIcon,
            contentDescription = "microphone button",
            modifier = modifier.fillMaxSize(stateIconSize)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    MyApp()
}