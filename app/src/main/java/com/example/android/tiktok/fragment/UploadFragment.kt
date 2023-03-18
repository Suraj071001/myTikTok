package com.example.android.tiktok.fragment

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.android.tiktok.R
import com.example.android.tiktok.VideoData
import com.example.android.tiktok.databinding.FragmentUploadBinding
import com.google.api.LogDescriptor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.log


const val PICKER_REQ_CODE = 1001

class UploadFragment : Fragment() {

    val firebaseRef = FirebaseStorage.getInstance().reference
    val fireStoreRef = Firebase.firestore

    private lateinit var binding: FragmentUploadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_upload,container,false)

        binding.btnChooseFile.setOnClickListener{
            uploadVideo()
        }

        return binding.root
    }

    private fun uploadVideo() {

        Intent().also {
            it.type = "video/*"
            it.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(it,"select video"), PICKER_REQ_CODE)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICKER_REQ_CODE && resultCode == AppCompatActivity.RESULT_OK){
            val uri = data!!.data
            Log.d("myTag", "onActivityResult: ${uri.toString()}")
            if (uri != null) {
                val currentUserName = FirebaseAuth.getInstance().currentUser!!.displayName
                val time = System.currentTimeMillis().toString()
                currentUserName?.let {userName->
                    firebaseRef.child(userName).child("videos").child("$userName$time").putFile(uri).addOnSuccessListener {
                        Toast.makeText(context,"file uploaded successfully",Toast.LENGTH_SHORT).show()

                        val selectedId = binding.rgMode.checkedRadioButtonId
                        val selectedMode = binding.root.findViewById<RadioButton>(selectedId)
                        val mode = selectedMode.text.toString()

                        Log.d(TAG, "onActivityResult: $mode")
                        GlobalScope.launch {
                            firebaseRef.child(userName).child("videos").child("$userName$time").downloadUrl.addOnSuccessListener {
                                val currentUser = FirebaseAuth.getInstance().currentUser
                                val userProfile = currentUser!!.photoUrl
                                val userId: String? = FirebaseAuth.getInstance().uid

                                val videoData = hashMapOf(
                                    "name" to userName,
                                    "videoUri" to it.toString(),
                                    "userProfile" to userProfile,
                                    "like" to 0,
                                    "uniqueId" to "$userId$time"
                                )
                                
                                fireStoreRef.collection(userId!!).document("$userId$time")
                                    .set(videoData)
                                    .addOnSuccessListener {
                                        Log.d(TAG, "onActivityResult: collection successfully added in id $userId ")
                                    }.addOnFailureListener{
                                        Log.d(TAG, "onActivityResult: $it.message")
                                    }

                                if(mode == "public"){
                                    fireStoreRef.collection("AllVideos").document("$userId$time")
                                        .set(videoData)
                                        .addOnSuccessListener {
                                            Log.d(TAG, "onActivityResult: collection successfully added in id $userId ")
                                        }.addOnFailureListener{
                                            Log.d(TAG, "onActivityResult: $it.message")
                                        }
                                }

                            }
                        }

                    }.addOnFailureListener{ error ->
                        Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
                    }

                }

            }
        }
    }

}