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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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
    Box(modifier){
        Image(
            painter = bgImg,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        MyMicButton()
    }
}


@Composable
fun MyMicButton() {
//    val hearIcon = painterResource(R.drawable.ear_sound_24px)
    val micIcon = painterResource(R.drawable.mic)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Press to start",
            color = Color.White,
            fontSize = 30.sp
        )
        Spacer(modifier = Modifier.size(75.dp))
        Box(
            modifier = Modifier
                .size(250.dp)
                .padding(16.dp)
                .align(alignment = Alignment.CenterHorizontally)
        ){
            IconButton(
                onClick = {
//                    if (isRecording) stopRecording()
                },
                modifier = Modifier
                    .align(alignment = Alignment.Center)
                    .size(200.dp)
                    .background(
                        shape = CircleShape,
                        color = Color.hsl(268F, 0.88F, 0.36F)
                    )
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        clip = true
                    )
            ) {
                Icon(
                    painter = micIcon,
                    contentDescription = "microphone button",
                    modifier = Modifier.fillMaxSize(0.5F)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    MyApp()
}