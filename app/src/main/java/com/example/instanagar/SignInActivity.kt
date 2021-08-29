package com.example.instanagar

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var email : EditText
    private lateinit var password : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        val sign_in_link_btn :Button by lazy {
            findViewById(R.id.sign_in_link_bio)
        }
        val btn_sign_in :Button by lazy {
            findViewById(R.id.btn_sign_in)
        }

        email = findViewById(R.id.et_sign_in_email)
        password = findViewById(R.id.et_sign_in_password)

        sign_in_link_btn.setOnClickListener{
            startActivity(Intent(this,SignUpActivity::class.java))
        }
        btn_sign_in.setOnClickListener{
            loginUser()
        }

    }

    private fun loginUser() {
        val email = email.text.toString()
        val password = password.text.toString()

        when{
            TextUtils.isEmpty(email) -> Toast.makeText(this,"Email ID is empty", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this,"Password is empty", Toast.LENGTH_LONG).show()
            else ->
            {
                val progressDialog = ProgressDialog(this@SignInActivity)
                progressDialog.setTitle("SignIn")
                progressDialog.setMessage("Please wait...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener { task->
                        if (task.isSuccessful){
                            progressDialog.dismiss()
                            intent = Intent(this@SignInActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }else{
                            val message = task.exception!!.toString()
                            Toast.makeText(this,"Error: $message",Toast.LENGTH_LONG).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(FirebaseAuth.getInstance().currentUser != null){
            intent = Intent(this@SignInActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}