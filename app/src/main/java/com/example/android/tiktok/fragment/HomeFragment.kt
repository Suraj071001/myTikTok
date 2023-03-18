package com.example.android.tiktok.fragment

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.android.tiktok.LoginPage
import com.example.android.tiktok.MainActivity
import com.example.android.tiktok.R
import com.example.android.tiktok.VideoData
import com.example.android.tiktok.adapter.MyVideoAdapter
import com.example.android.tiktok.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class HomeFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val fireStoreRef = Firebase.firestore
    lateinit var binding:FragmentHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false)
        val context = container!!.context

        auth.currentUser?.let { showUsersDetail(it,context) }

        showUploadedVideo(context)

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

    fun showUploadedVideo(context:Context){
        var list: List<VideoData>
        fireStoreRef.collection(auth.currentUser!!.uid).get().addOnSuccessListener {snapshot->
            GlobalScope.launch {
                var deferredList = mutableListOf<VideoData>()
                for (document in snapshot){
                    val videoData = GlobalScope.async(Dispatchers.IO) {
                        val name = document.data["name"]
                        val photo = document.data["userProfile"]
                        val videoUri = document.data["videoUri"]
                        val like = document.data["like"]
                        val uniqueId = document.data["uniqueId"]

                        VideoData(name as String,
                            Uri.parse(videoUri as String),
                            Uri.parse(photo as String),like as Long,uniqueId as String)
                    }
                    deferredList.add(videoData.await())
                }
                list = deferredList
                withContext(Dispatchers.Main){
                    val adapter = MyVideoAdapter(context, list)
                    val layout = GridLayoutManager(context,2)
                    binding.rvPublicVideos.layoutManager = layout
                    binding.rvPublicVideos.adapter = adapter
                }
            }
        }
    }

}