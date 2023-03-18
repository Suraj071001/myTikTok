package com.example.android.tiktok.adapter

import android.content.ContentValues.TAG
import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.tiktok.R
import com.example.android.tiktok.VideoData
import com.example.android.tiktok.databinding.MainItemBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.Listener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

class MainVideoAdapter(val context: Context, val list: List<VideoData>): RecyclerView.Adapter<MainVideoAdapter.MyViewHolder>() {

    private lateinit var binding: MainItemBinding

    inner class MyViewHolder(itemView:MainItemBinding) : RecyclerView.ViewHolder(itemView.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = MainItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context)
            .load(list[position].creatorDp)
            .into(binding.ivItem)

        binding.tvItem.text = list[position].creator
        binding.tvLike.text = list[position].videoLike.toString()

        binding.ivLike.setOnClickListener{
            binding.ivLike.setImageResource(R.drawable.liked)
            Firebase.firestore.collection("AllVideos").document(list[position].uniqueId).get()
                .addOnSuccessListener {document ->
                    if(document!=null){
                        var like:Long = 0
                        GlobalScope.launch(Dispatchers.IO){
                            like = document.data?.get("like") as Long
                        }
                        runBlocking {
                            delay(1000)
                        }

                        Firebase.firestore.collection("AllVideos").document(list[position].uniqueId).update("like",++like).addOnSuccessListener {
                        }.addOnFailureListener{
                            Toast.makeText(context,it.message,Toast.LENGTH_LONG).show()
                        }

                        binding.tvLike.text = like.toString()
                        Log.d(TAG, "onBindViewHolder: $like")
                    }
                }
        }

        var mediaPlayer:MediaPlayer = MediaPlayer()
        binding.vvItem.setVideoURI(list[position].videoUri)
        binding.vvItem.setOnPreparedListener{
            mediaPlayer = it
            mediaPlayer.start()
        }
        binding.vvItem.setOnCompletionListener {
            mediaPlayer.start()
        }

        binding.vvItem.setOnClickListener{
            if (mediaPlayer.isPlaying)
                mediaPlayer.pause()
            else mediaPlayer.start()
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

}