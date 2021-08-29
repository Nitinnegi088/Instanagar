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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var fullName : EditText
    private lateinit var userName : EditText
    private lateinit var email : EditText
    private lateinit var password : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val btn_sign_up_link_bio: Button by lazy {
            findViewById(R.id.sign_up_link_bio)
        }
        val btn_sign_up: Button by lazy {
            findViewById(R.id.btn_sign_up)
        }
        fullName = findViewById(R.id.et_sign_up_full_name)
        userName = findViewById(R.id.et_sign_up_username)
        email = findViewById(R.id.et_sign_up_email)
        password = findViewById(R.id.et_sign_up_password)
        btn_sign_up_link_bio.setOnClickListener {
            startActivity(Intent(this,SignInActivity::class.java))
        }
        btn_sign_up.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {
        val fullName = fullName.text.toString()
        val userName = userName.text.toString()
        val email = email.text.toString()
        val password = password.text.toString()

        when{
            TextUtils.isEmpty(fullName) ->Toast.makeText(this,"Full name is empty",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(userName) ->Toast.makeText(this,"User name is empty",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email) ->Toast.makeText(this,"Email ID is empty",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) ->Toast.makeText(this,"Password is empty",Toast.LENGTH_LONG).show()
            else -> {
                val progressDialog = ProgressDialog(this@SignUpActivity)
                progressDialog.setTitle("SignUp")
                progressDialog.setMessage("Please wait...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()
                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener { task->
                        if (task.isSuccessful){
                            saveUserInfo(fullName,userName,email,progressDialog)
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

    private fun saveUserInfo(fullName: String, userName: String, email: String,progressdialog: ProgressDialog) {
            val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
            val userRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("users")
            val userMap = HashMap<String,Any>()
            userMap["uid"] = currentUserID
            userMap["fullName"] = fullName
            userMap["userName"] = userName
            userMap["email"] = email
            userMap["bio"] = "Hey there"
            userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/instagram-1-99c1b.appspot.com/o/Default%20Images%2FIMG_2666.jpeg?alt=media&token=76a98a4b-453c-4381-b8bb-80bcfd555dce"

                userRef.child(currentUserID).setValue(userMap)
                    .addOnCompleteListener {task->
                        if(task.isSuccessful){
                                progressdialog.dismiss()
                                 Toast.makeText(this,"Account has been created successfully.",Toast.LENGTH_LONG).show()

                                FirebaseDatabase.getInstance().reference
                                    .child("Follow").child(currentUserID)
                                    .child("Following").child(currentUserID)
                                    .setValue(true)
                                intent = Intent(this@SignUpActivity, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                        }else{
                            val message = task.exception!!.toString()
                            Toast.makeText(this,"Error: $message",Toast.LENGTH_LONG).show()
                            FirebaseAuth.getInstance().signOut()
                            progressdialog.dismiss()
                        }

                    }
    }
}