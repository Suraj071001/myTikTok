package com.example.android.tiktok

import android.net.Uri

data class VideoData(val creator:String, val videoUri: Uri, val creatorDp: Uri, var videoLike: Long) {

}