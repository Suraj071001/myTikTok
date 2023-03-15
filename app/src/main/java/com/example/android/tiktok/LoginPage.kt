package com.example.android.tiktok

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.android.tiktok.databinding.ActivityLoginPageBinding
import com.example.android.tiktok.databinding.FragmentVideoBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception

const val REQ_CODE_SIGN_IN = 1001

class LoginPage : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    lateinit var binding:ActivityLoginPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_login_page)

        if(auth.currentUser != null){
            Log.d(TAG, "onCreate: "+ auth.currentUser!!.uid)
            Toast.makeText(this,"user is login",Toast.LENGTH_SHORT).show()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        binding.imGoogleSignIn.setOnClickListener{
            val option = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client))
                .requestProfile()
                .build()

            val googleClient = GoogleSignIn.getClient(this,option)
            googleClient.signInIntent.also {
                startActivityForResult(it, REQ_CODE_SIGN_IN)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(REQ_CODE_SIGN_IN == requestCode  && data!=null){
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(data).result
                account?.let {
                    googleSignInWithFirebase(it)
                }
            }catch (e:Exception){
                Log.d(TAG, "onActivityResult: ${e.message}")
                Toast.makeText(this,"You don't have google account",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun googleSignInWithFirebase(account:GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.signInWithCredential(credential).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@LoginPage, "login Successfully", Toast.LENGTH_SHORT).show()
                    Intent(this@LoginPage,MainActivity::class.java).also {
                        startActivity(it)
                    }
                }
            }catch(e:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@LoginPage,e.message,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}