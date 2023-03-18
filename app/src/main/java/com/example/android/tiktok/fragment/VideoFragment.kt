package com.example.android.tiktok.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.android.tiktok.R
import com.example.android.tiktok.VideoData
import com.example.android.tiktok.adapter.MainVideoAdapter
import com.example.android.tiktok.databinding.FragmentVideoBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.*

class VideoFragment : Fragment() {

    private lateinit var binding: FragmentVideoBinding
    private val TAG = "myTag"

    val firebaseRef = FirebaseStorage.getInstance().reference
    private val fireStoreRef = Firebase.firestore
    var list = mutableListOf<VideoData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FragmentVideoBinding>(inflater,
            R.layout.fragment_video,container,false)

        val context = container!!.context

        fireStoreRef.collection("AllVideos").get()
            .addOnSuccessListener {snapshot ->
                GlobalScope.launch {
                    var deferredList = mutableListOf<VideoData>()
                    for (document in snapshot){
                        val videoData = GlobalScope.async(Dispatchers.IO) {
                            val name = document.data["name"]
                            val photo = document.data["userProfile"]
                            val videoUri = document.data["videoUri"]
                            val like = document.data["like"]
                            val uniqueId = document.data["uniqueId"]

                            VideoData(name as String,Uri.parse(videoUri as String),Uri.parse(photo as String),like as Long,uniqueId as String)
                        }
                        deferredList.add(videoData.await())
                    }
                    list = deferredList
                    withContext(Dispatchers.Main) {
                        val adapter = MainVideoAdapter(context,list)
                        binding.vpMain.adapter = adapter
                    }
                }
            }.addOnFailureListener{
                Log.d(TAG, "onCreateView: $it.message")
            }

        return binding.root
    }
}