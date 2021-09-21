package com.example.gripapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.example.gripapp.databinding.ActivityFacebookBinding

class FacebookActivity : AppCompatActivity() {
    lateinit var binding: ActivityFacebookBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFacebookBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        val data = intent.extras
        binding.id.text = data?.getString("id")
        binding.name.text = data?.getString("name")
//        binding.email.text = data?.getString("birth")
        Glide.with(this)
            .load(data?.getString("photo"))
            .into(binding.image)

    }
}