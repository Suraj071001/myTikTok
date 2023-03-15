package com.example.android.tiktok.fragment

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.android.tiktok.LoginPage
import com.example.android.tiktok.MainActivity
import com.example.android.tiktok.R
import com.example.android.tiktok.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class HomeFragment : Fragment() {

    val auth = FirebaseAuth.getInstance()
    lateinit var binding:FragmentHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false)
        val context = container!!.context

        auth.currentUser?.let { showUsersDetail(it,context) }


        binding.btnLogOut.setOnClickListener{
            auth.signOut()
            val intent = Intent(context,LoginPage::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun showUsersDetail(user:FirebaseUser, context:Context){
        binding.tvUserName.text = user.displayName
        Glide.with(context)
            .load(user.photoUrl)
            .into(binding.ivUserProfile)
    }

}