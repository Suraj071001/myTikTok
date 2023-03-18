package com.example.android.tiktok.adapter

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android.tiktok.R
import com.example.android.tiktok.VideoData
import com.example.android.tiktok.databinding.PublicItemBinding

class MyVideoAdapter(val context: Context,val videoList:List<VideoData>) :RecyclerView.Adapter<MyVideoAdapter.MyViewHolder>() {

    lateinit var binding: PublicItemBinding

    inner class MyViewHolder(itemView:PublicItemBinding) : RecyclerView.ViewHolder(itemView.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.public_item,parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        binding.videoView.setVideoURI(videoList[position].videoUri)

        var mediaPlayer = MediaPlayer()
        binding.videoView.setOnPreparedListener{
            mediaPlayer = it
            mediaPlayer.start()
            mediaPlayer.seekTo(1000)
            mediaPlayer.pause()
        }

        binding.videoView.setOnClickListener{
            if(!mediaPlayer.isPlaying)
            mediaPlayer.start()
            else{
                mediaPlayer.pause()
            }
        }

    }

    override fun getItemCount(): Int {
        return videoList.size
    }
}