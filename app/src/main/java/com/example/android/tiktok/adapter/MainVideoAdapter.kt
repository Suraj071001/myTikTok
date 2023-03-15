package com.example.android.tiktok.adapter

import android.content.Context
import android.media.MediaPlayer
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

        binding.ivLike.setOnClickListener{
            Toast.makeText(context,"not implemented yet",Toast.LENGTH_SHORT).show()
        }

        var mediaPlayer:MediaPlayer = MediaPlayer()
        binding.vvItem.setVideoURI(list[position].videoUri)
        binding.vvItem.setOnPreparedListener{
            mediaPlayer = it
            mediaPlayer.start()
        }
        binding.vvItem.setOnCompletionListener {
            mediaPlayer = it
            mediaPlayer.start()
        }

        binding.vvItem.setOnClickListener{
            if(mediaPlayer.isPlaying){
                mediaPlayer.pause()
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

}