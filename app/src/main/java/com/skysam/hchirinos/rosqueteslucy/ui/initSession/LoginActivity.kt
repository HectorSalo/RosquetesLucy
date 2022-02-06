package com.skysam.hchirinos.rosqueteslucy.ui.initSession

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setTheme(R.style.Theme_RosquetesLucy)
        setContentView(binding.root)

        supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_login) as NavHostFragment
    }
}