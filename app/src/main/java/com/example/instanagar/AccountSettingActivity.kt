package com.example.instanagar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth


class AccountSettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)
        val btn_logout: Button by lazy {
            findViewById(R.id.btn_logout)
        }
        val changeImage: TextView by lazy {
            findViewById(R.id.tv_change_image)
        }

        btn_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            intent = Intent(this@AccountSettingActivity, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        changeImage.setOnClickListener {

        }
    }
}