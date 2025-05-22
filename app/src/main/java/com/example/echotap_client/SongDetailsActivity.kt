// SongDetailsActivity.kt
package com.example.echotap_client

import android.content.Intent // Import Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults // Import ButtonDefaults for colors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext // Import LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.echotap_client.ui.theme.EchoTapClientTheme
import com.google.gson.Gson

class SongDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the JSON string from the intent
        val songInfoJson = intent.getStringExtra("song_info_json")
        val songInfo = Gson().fromJson(songInfoJson, SongInfo::class.java)

        setContent {
            EchoTapClientTheme {
                SongDetailsScreen(songInfo = songInfo) {
                    // This lambda will be called when the back button is pressed
                    finish() // Close the current activity and go back to the previous one
                }
            }
        }
    }
}

@Composable
fun SongDetailsScreen(
    songInfo: SongInfo?,
    onBackClicked: () -> Unit
) { // Add a lambda for back action
    val bgImg = painterResource(R.drawable.echotap_bg)
    val context = LocalContext.current // Get the current context

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = bgImg,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            songInfo?.let {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .background(
                            Color(0xCC2F036D),
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Song Recognized!",
                        color = Color.White,
                        fontSize = 22.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Spacer(modifier = Modifier.size(20.dp))
                    Text(
                        text = "Title: ${it.title}",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Artist: ${it.artist}",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Album: ${it.album}",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.size(50.dp))
                    Button(
                        onClick = {
                            // Option 1: Simply finish this activity (recommended)
                            onBackClicked()

                            // Option 2: Explicitly start MainActivity (less common for back navigation)
                            // val intent = Intent(context, MainActivity::class.java)
                            // intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            // context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White) // Set button background color
                    ) {
                        Text(
                            text = "Back",
                            color = Color.Black // Set text color to black
                        )
                    }
                }
            } ?: run {
                Text(
                    text = "No song information available.",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SongDetailsScreenPreview() {
    EchoTapClientTheme {
        SongDetailsScreen(
            songInfo = SongInfo(
                "Love You Like A Love Song",
                "Selena Gomez & The Scene",
                "When The Sun Goes Down"
            )
        ) {} // Empty lambda for preview
    }
}