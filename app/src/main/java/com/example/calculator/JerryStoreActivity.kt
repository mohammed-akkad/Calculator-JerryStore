package com.example.calculator

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.calculator.databinding.ActivityJerryStoreBinding

class JerryStoreActivity : AppCompatActivity() {
    lateinit var binding: ActivityJerryStoreBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityJerryStoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}