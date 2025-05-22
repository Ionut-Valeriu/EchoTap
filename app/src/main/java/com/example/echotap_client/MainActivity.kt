package com.example.echotap_client

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.echotap_client.ui.theme.EchoTapClientTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class AudioRecorder {
    private var recorder: MediaRecorder? = null
    private var outputFile: File? = null

    fun startRecording(context: Context): File {
        val outputDir = context.cacheDir
        outputFile = File.createTempFile("audio_", ".m4a", outputDir)

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(outputFile!!.absolutePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            prepare()
            start()
        }

        return outputFile!!
    }

    fun stopRecording(): File? {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        return outputFile
    }
}

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                200 // Arbitrary request code
            )
        }

        auth = Firebase.auth

        enableEdgeToEdge()
        setContent {
            EchoTapClientTheme {
                MyApp()
            }
        }
    }
}

fun signOutAndNavigateToLogin(context: Context) {
    Firebase.auth.signOut()
    val intent = android.content.Intent(context, LoginActivity::class.java)
    context.startActivity(intent)
}

@Composable
fun MyApp(modifier: Modifier = Modifier) {
    val bgImg = painterResource(R.drawable.echotap_bg)
    Box(modifier.fillMaxSize()) {
        Image(
            painter = bgImg,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Column(modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.size(20.dp))
            UserDetails(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(25.dp)
            )
//            val recognizedSong = remember { mutableStateOf<SongInfo?>(null) }
//            val message = remember { mutableStateOf<String?>(null) }
//            val uiUpdateTrigger = remember { mutableStateOf(0) }

            val context = LocalContext.current

            MicDetails(
                onSongRecognized = { songInfo ->
//                    recognizedSong.value = songInfo
//                    message.value = if (songInfo != null) null else "No song recognized."
//                    uiUpdateTrigger.value++
                    if (songInfo != null) {
                        val intent = Intent(context, SongDetailsActivity::class.java).apply {
                            putExtra("song_info_json", Gson().toJson(songInfo))
                        }
                        context.startActivity(intent)
                    } else {
                        // Handle case where no song is recognized (e.g., show a Toast or dialog)
                        Log.d("SongRecognition", "No song recognized.")
                    }
                },
                onRecognitionError = { errorMessage ->
//                    recognizedSong.value = null
//                    message.value = errorMessage
//                    uiUpdateTrigger.value++
                    Log.e("SongRecognition", "Error: $errorMessage")
                }
            )
//            recognizedSong.value?.let {
//                SongDisplayPanel(songInfo = it)
//            }
//            message.value?.let { msg ->
//                Text(
//                    text = msg,
//                    color = Color.White,
//                    fontSize = 18.sp,
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .fillMaxWidth()
//                        .wrapContentWidth(Alignment.CenterHorizontally)
//                )
//            }
        }
    }
}

@Composable
fun UserDetails(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val auth = Firebase.auth
    val currentUser = auth.currentUser

    // Display user email if available, otherwise a generic message
    var userDisplayName: String = currentUser?.email ?: "Guest User"
    val userImg: Painter = painterResource(R.drawable.user_logged)
    var userTint: Color = Color.Unspecified
    var buttonDesc: String = "Log out"

    Row(
        modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            color = Color.White,
            fontSize = 23.sp,
            text = buttonDesc
        )
        Spacer(modifier = Modifier.size(20.dp))
        IconButton(
            onClick = { signOutAndNavigateToLogin(context) }
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
fun MicDetails(
    modifier: Modifier = Modifier,
    onSongRecognized: (SongInfo?) -> Unit,
    onRecognitionError: (String) -> Unit
) {

    val isListening = remember { mutableStateOf(false) }

    val textState = if (isListening.value) {
        "Listening..."
    } else {
        "Press to start"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .offset(y = (-20).dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = textState,
            color = Color.White,
            fontSize = 30.sp
        )
        Spacer(modifier = Modifier.size(75.dp))
        val context = LocalContext.current
        val recorder = remember { AudioRecorder() }
        val recordedFile = remember { mutableStateOf<File?>(null) }

        MicButton(
            isListening = isListening.value,
            onToggle = {
                isListening.value = !isListening.value
                if (isListening.value) {
                    recordedFile.value = recorder.startRecording(context)
                } else {
                    val audioFile = recorder.stopRecording()
                    audioFile?.let {
                        sendAudioToAudd(
                            it,
                            "token"
                        ) { result ->
                            Log.d("AuddResult", result ?: "No result") // Log the raw result
                            if (result != null) {
                                try {
                                    val auddResponse =
                                        Gson().fromJson(result, AuddResponse::class.java)
                                    if (auddResponse.status == "success" && auddResponse.result != null) {
                                        val songInfo = SongInfo(
                                            title = auddResponse.result.title ?: "Unknown Title",
                                            artist = auddResponse.result.artist ?: "Unknown Artist",
                                            album = auddResponse.result.album ?: "Unknown Album"
                                        )
                                        onSongRecognized(songInfo)
                                    } else {
                                        onSongRecognized(null) // No song recognized or an error occurred
                                    }
                                } catch (e: Exception) {
                                    Log.e(
                                        "AuddParseError",
                                        "Failed to parse Audd.io response: ${e.message}"
                                    )
                                    onSongRecognized(null)
                                }
                            } else {
                                onSongRecognized(null)
                            }
                        }
                    }
                }
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun MicButton(
    isListening: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    var stateIcon: Painter
    var stateIconSize: Float

    if (isListening) {
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

//@Composable
//fun SongDisplayPanel(songInfo: SongInfo) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 24.dp, vertical = 16.dp)
//            .background(
//                Color(0xCC2F036D),
//                shape = MaterialTheme.shapes.medium
//            ) // Dark purple, slightly transparent
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Text(
//            text = "Song Recognized!",
//            color = Color.White,
//            fontSize = 22.sp,
//            modifier = Modifier.padding(bottom = 8.dp)
//        )
//        Text(
//            text = "Title: ${songInfo.title}",
//            color = Color.White,
//            fontSize = 18.sp
//        )
//        Text(
//            text = "Artist: ${songInfo.artist}",
//            color = Color.White,
//            fontSize = 16.sp
//        )
//        Text(
//            text = "Album: ${songInfo.album}",
//            color = Color.White,
//            fontSize = 14.sp
//        )
//    }
//}

// Data class to hold parsed song information
data class SongInfo(
    val title: String,
    val artist: String,
    val album: String
)

// Data classes for parsing Audd.io JSON response
data class AuddResponse(
    val status: String,
    val result: AuddResult?
)

data class AuddResult(
    val artist: String?,
    val title: String?,
    val album: String?
)


@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    MyApp()
}

fun sendAudioToAudd(audioFile: File, apiKey: String, onResult: (String?) -> Unit) {
    val client = OkHttpClient()
    val mediaType = "audio/mpeg".toMediaTypeOrNull()

    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("api_token", apiKey)
        .addFormDataPart("return", "apple_music,spotify") // or "lyrics"
        .addFormDataPart(
            "file", audioFile.name,
            audioFile.asRequestBody(mediaType)
        )
        .build()

    val request = Request.Builder()
        .url("https://api.audd.io/")
        .post(requestBody)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            onResult(null)
        }

        override fun onResponse(call: Call, response: Response) {
            onResult(response.body?.string())
        }
    })
}
