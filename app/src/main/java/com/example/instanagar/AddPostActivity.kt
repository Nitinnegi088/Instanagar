package com.example.instanagar

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.internal.StorageReferenceUri
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import org.w3c.dom.Text

class AddPostActivity : AppCompatActivity() {

    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storagePostPicRef: StorageReference? = null

    val description_post : EditText by lazy {
        findViewById(R.id.description_post)
    }
    val save_new_post_button : ImageView by lazy {
        findViewById(R.id.save_new_post_button)
    }
    val iv_image_post : ImageView by lazy {
        findViewById(R.id.iv_image_post)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        save_new_post_button.setOnClickListener {
            uploadImage()
        }

        storagePostPicRef = FirebaseStorage.getInstance().reference.child("Post Pictures")

        CropImage.
        activity().
        setAspectRatio(2,1)
            .start(this@AddPostActivity)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            Picasso.get().load(imageUri).placeholder(R.drawable.profile).into(iv_image_post)
        }
    }

    private fun uploadImage() {
            when{
                imageUri == null -> Toast.makeText(this,"Please select image first",Toast.LENGTH_LONG).show()
                TextUtils.isEmpty(description_post.text.toString()) -> Toast.makeText(this,"Please write description",Toast.LENGTH_LONG).show()
                else ->{
                    val progressDialog = ProgressDialog(this@AddPostActivity)
                    progressDialog.setTitle("Adding New Post")
                    progressDialog.setMessage("Please wait, we are adding a new post")
                    progressDialog.setCanceledOnTouchOutside(false)
                    progressDialog.show()

                    val fileRef = storagePostPicRef!!.child(System.currentTimeMillis().toString()+"jpg")
                    var uploadTask: StorageTask<*>
                    uploadTask = fileRef.putFile(imageUri!!)
                    uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot,Task<Uri>>{task ->
                        if (!task.isSuccessful){
                            task.exception?.let {
                                throw it
                                progressDialog.dismiss()
                            }
                        }
                        return@Continuation fileRef.downloadUrl
                    }).addOnCompleteListener(OnCompleteListener<Uri> { task ->  
                        if (task.isSuccessful){
                            val downloadUrl = task.result
                            myUrl = downloadUrl.toString()

                            val ref = FirebaseDatabase.getInstance().reference.child("Posts")
                            val postId = ref.push().key

                            val postMap = HashMap<String,Any>()
                            postMap["postid"] = postId!!
                            postMap["description"] = description_post.text.toString().toLowerCase()
                            postMap["publisher"] = FirebaseAuth.getInstance().currentUser!!.uid
                            postMap["postimage"] = myUrl

                            ref.child(postId).updateChildren(postMap)

                            Toast.makeText(this,"Post uploaded successfully.",Toast.LENGTH_LONG).show()

                            intent = Intent(this@AddPostActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                            progressDialog.dismiss()
                        }
                    })

                }
            }
    }
}