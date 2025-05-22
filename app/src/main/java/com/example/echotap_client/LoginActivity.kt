package com.example.echotap_client

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.echotap_client.ui.theme.EchoTapClientTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class LoginActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        enableEdgeToEdge()
        setContent {
            EchoTapClientTheme {
                ConnectionPage(auth = auth,
                onAuthSuccess = {
                    updateUI(it) // Call updateUI on successful auth
                })
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            Log.d("LoginActivity", "User is logged in: ${user.email}")
            // Navigate to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finish LoginActivity so user can't go back with back button
        } else {
            Log.d("LoginActivity", "User is not logged in")
            // Stay on the login screen
        }
    }
}

@Composable
fun ConnectionPage(auth: FirebaseAuth, onAuthSuccess: (FirebaseUser?) -> Unit) {
    Box(){
        Image(
            painter = painterResource(R.drawable.login_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        SignUpMenu(auth = auth, onAuthSuccess)
    }
}

@Composable
fun SignUpMenu(auth: FirebaseAuth, onAuthSuccess: (FirebaseUser?) -> Unit) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    val context = LocalContext.current

    val selectedTextColor: Color = Color(0xFFDDDDDD)
    val unselectedTextColor: Color = Color(0xAA4A00B3)
    val selectedButtonColor: Color = Color(0xFF541BA4)
    val unselectedButtonColor: Color = Color(0x99B696E3)

    var loginTextColor: Color
    var createTextColor: Color
    var loginButtonColor: Color
    var createButtonColor: Color
    var summitButtonText: String

    val login = remember { mutableStateOf(false) }

    if (login.value){
        loginTextColor = selectedTextColor
        loginButtonColor = selectedButtonColor

        createTextColor = unselectedTextColor
        createButtonColor = unselectedButtonColor

        summitButtonText = "Login"
    }
    else {
        loginTextColor = unselectedTextColor
        loginButtonColor = unselectedButtonColor

        createTextColor = selectedTextColor
        createButtonColor = selectedButtonColor

        summitButtonText = "Create"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row (){
            Button (
                onClick = {
                    login.value = false
                },
                shape = RoundedCornerShape(topStart = 25.dp, bottomStart = 25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = createButtonColor
                ),
                modifier = Modifier
                    .size(100.dp, 50.dp)
            ){
                Text(
                    text = "Create",
                    color = createTextColor,
                    fontSize = 15.sp
                )
            }
            Button (
                onClick = {
                    login.value = true
                },
                shape = RoundedCornerShape(topEnd = 25.dp, bottomEnd = 25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = loginButtonColor
                ),
                modifier = Modifier
                    .size(100.dp, 50.dp)
            ){
                Text(
                    text = "Login",
                    color = loginTextColor,
                    fontSize = 15.sp
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            RoundedInputField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Enter your email",
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
            )
            RoundedInputField(
                value = pass,
                onValueChange = { pass = it },
                placeholder = "Enter your password",
//                keyboardType = androidx.compose.ui.text.input.KeyboardType.Password // Set keyboard type
            )
            if (!login.value){
                RoundedInputField(
                    value = confirm,
                    onValueChange = { confirm = it },
                    placeholder = "Confirm your password",
//                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Password // Set keyboard type
                )
            }
        }
        Button(
            onClick = {
                if (login.value) {
                    // Login existing user - Add validation here
                    if (email.isEmpty() || pass.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Email and Password cannot be empty.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        auth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("FirebaseAuth", "signInWithEmail:success")
                                    val user = auth.currentUser
                                    Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT)
                                        .show()
                                    onAuthSuccess(user)
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("FirebaseAuth", "signInWithEmail:failure", task.exception)
                                    Toast.makeText(
                                        context,
                                        "Authentication failed: ${task.exception?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    }
                } else {
                    // Create new user - Add validation here
                    if (email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                        Toast.makeText(context, "All fields must be filled.", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        if (pass == confirm) {
                            auth.createUserWithEmailAndPassword(email, pass)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("FirebaseAuth", "createUserWithEmail:success")
                                        val user = auth.currentUser
                                        Toast.makeText(
                                            context,
                                            "Account created successfully!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        onAuthSuccess(user)
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(
                                            "FirebaseAuth",
                                            "createUserWithEmail:failure",
                                            task.exception
                                        )
                                        val errorMessage = when (task.exception) {
                                            is com.google.firebase.auth.FirebaseAuthUserCollisionException -> "An account with this email already exists."
                                            is com.google.firebase.auth.FirebaseAuthWeakPasswordException -> "Password must have at least 6 characters."
                                            is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Invalid email address format."
                                            else -> "Account creation failed: ${task.exception?.message}"
                                        }
                                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG)
                                            .show()
                                    }
                                }
                        } else {
                            Log.e("FirebaseAuth", "Passwords do not match")
                            Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF541BA4)
            ),
            modifier = Modifier
                .size(200.dp, 50.dp)
        ) {
            Text(
                text = summitButtonText,
                color = Color(0xFFDDDDDD),
                fontSize = 20.sp
            )
        }
    }
}


@Composable
fun RoundedInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: androidx.compose.ui.text.input.KeyboardType = androidx.compose.ui.text.input.KeyboardType.Text // Add keyboardType parameter
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color(0xAA2F036D)) },
        shape = RoundedCornerShape(12.dp),
        visualTransformation = visualTransformation,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF6200EE),
            unfocusedBorderColor = Color(0xAA4A00B3),
            focusedTextColor = Color(0xFF000000),
            unfocusedTextColor = Color(0xFF4A00B3),
            cursorColor = Color(0xFF2F036D),
            selectionColors = TextSelectionColors(
                handleColor = Color(0xFF541BA4),
                backgroundColor = Color(0x99B696E3)
            )
        ),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}



// Keeping a simpler preview if needed
@Preview(showBackground = true)
@Composable
fun SignUpMenuPreview() {
    EchoTapClientTheme {
        // Provide a dummy auth object for preview
        SignUpMenu(auth = Firebase.auth, onAuthSuccess = {})
    }
}