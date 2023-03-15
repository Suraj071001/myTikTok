package com.example.android.tiktok

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.android.tiktok.databinding.ActivityMainBinding
import com.example.android.tiktok.fragment.HomeFragment
import com.example.android.tiktok.fragment.UploadFragment
import com.example.android.tiktok.fragment.VideoFragment
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseRef: StorageReference
    private val PICKER_REQ_CODE = 1001

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        supportActionBar!!.hide()
        firebaseRef = FirebaseStorage.getInstance().reference

        val videoFragment = VideoFragment()
        val uploadFragment = UploadFragment()
        val homeFragment = HomeFragment()

        setFragment(homeFragment)

        binding.bnBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.video -> {
                    setFragment(videoFragment)
                }
                R.id.home -> setFragment(homeFragment)
                R.id.upload -> setFragment(uploadFragment)
            }
            true
        }
    }

    fun setFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit()
    }

}