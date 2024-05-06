package com.example.nfcsample

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nfcsample.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initViews()
        initInteractions()
    }

    override fun initViews() {

    }

    override fun initInteractions() {
        binding.writeNfcBtn.setOnClickListener {
            val intent = Intent(this, NfcWriterActivity::class.java)
            startActivity(intent)
        }

        binding.readNfcBtn.setOnClickListener {
            val intent = Intent(this, NfcReaderActivity::class.java)
            startActivity(intent)
        }
    }
}
