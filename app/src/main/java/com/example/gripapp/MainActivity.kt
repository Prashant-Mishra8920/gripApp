package com.example.gripapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.example.gripapp.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.core.app.ActivityCompat.startActivityForResult

import android.content.Intent
import android.net.Uri
import com.facebook.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.common.api.ApiException

import com.facebook.login.LoginResult

import com.facebook.login.widget.LoginButton
import java.util.*
import com.facebook.login.LoginManager
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    val RC_SIGN_IN = 100
    val EMAIL = "email"
    lateinit var binding: ActivityMainBinding
    private lateinit var googleSignInClient:GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        auth = Firebase.auth
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        val account = GoogleSignIn.getLastSignedInAccount(this)

        binding.signInButton.setOnClickListener(View.OnClickListener {
            signIn()
        })

        callbackManager = CallbackManager.Factory.create()

        val loginButton = binding.loginButton
        loginButton.setReadPermissions(listOf("email","user_birthday"))

        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                loadUserProfile()
            }
            override fun onCancel() {
            }
            override fun onError(exception: FacebookException) {
            }
        })


    }

    private fun loadUserProfile(){
        val request:GraphRequest = GraphRequest.newMeRequest(
            AccessToken.getCurrentAccessToken()
        ) { `object`, response ->
            if (`object` != null) {
                Log.d("fields", "onCompleted: $`object`")
            }
        }
//        val intent = Intent(applicationContext,FacebookActivity::class.java)
        val bundle = Bundle()
        bundle.putString("fields","gender,name,id,first_name,last_name")

        request.parameters = bundle
        request.executeAsync()
    }

    val accessTokenTracker = object : AccessTokenTracker(){
        override fun onCurrentAccessTokenChanged(
            oldAccessToken: AccessToken?,
            currentAccessToken: AccessToken?
        ) {
            if(currentAccessToken == null){

            }
        }

    }

    private fun signIn() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("GoogleSignIn", "signInWithCredential:success")
                    val user = auth.currentUser
                } else {
                    Log.w("GoogleSignIn", "signInWithCredential:failure", task.exception)
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }

        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val acct = GoogleSignIn.getLastSignedInAccount(this)

            val intent = Intent(this,SecondActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable("acc",acct)
            intent.putExtras(bundle)
            startActivity(intent)
        } catch (e: ApiException) {
            Log.d("signInResult_failed", e.statusCode.toString())
        }
    }

    override fun onStart() {
        super.onStart()
        var currentUser = auth.currentUser
    }
}