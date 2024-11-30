package com.bhub.foodi.ui.general

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bhub.foodi.MainActivity
import com.bhub.foodi.databinding.ActivityNoInternetBinding
import com.bhub.foodi.ui.auth.AuthActivity
import com.bhub.foodi.utilities.NetworkHelper
import com.bhub.foodi.utilities.WARNING_CHECK_AGAIN

class NoInternetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoInternetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoInternetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainLayout.btnTryAgain.setOnClickListener {
            if (NetworkHelper.isNetworkAvailable(this)) {
                val intent = Intent(this, AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                Toast.makeText(this, WARNING_CHECK_AGAIN, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}