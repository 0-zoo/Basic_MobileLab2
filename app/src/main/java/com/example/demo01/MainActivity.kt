package com.example.demo01

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth
        val id = intent.getStringExtra("EmailAddress")
        val id_split = id?.split(".")

        Glide.with(this).load(R.raw.ecg).override(560, 560).into(imageView)

        // 로그아웃
        logoutbutton.setOnClickListener {
            // 로그인 화면으로
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            auth?.signOut()
        }

        checkbutton.setOnClickListener {
            val intent = Intent(this, SignalActivity::class.java)
            intent.putExtra("Email",id_split?.get(0))
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

        recordbutton.setOnClickListener {
            val intent = Intent(this, RecordActivity::class.java)
            intent.putExtra("Email",id_split?.get(0))
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}
