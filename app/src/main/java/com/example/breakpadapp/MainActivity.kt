package com.example.breakpadapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.breakpadapp.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        val breakpadDumpFile = getExternalFilesDir("breakpadDump")
//            ?: File(filesDir, "/breakpadDump/")
        binding.btNativeCrash.setOnClickListener {
            NativeBridgeLoder.load().makeCrash()
        }
    }
}