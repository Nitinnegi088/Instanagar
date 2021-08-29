package com.example.instanagar.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.example.instanagar.Model.Post
import com.example.instanagar.Model.User
import com.example.instanagar.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(private val mcontext:Context, private val mPost:List<Post>):
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var firebaseUser: FirebaseUser? = null

    inner class PostViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var profileImage: CircleImageView
        var postImage: ImageView
        var likeButton: ImageView
        var commentButton: TextView
        var saveButton: ImageView
        var username: TextView
        var likes: TextView
        var publisher: TextView
        var description: TextView
        var comments: TextView

        init {
            profileImage = itemView.findViewById(R.id.user_profile_image_post)
            postImage = itemView.findViewById(R.id.post_image_home)
            likeButton = itemView.findViewById(R.id.post_image_like_btn)
            commentButton = itemView.findViewById(R.id.comments)
            saveButton = itemView.findViewById(R.id.post_save_comment_btn)
            username = itemView.findViewById(R.id.user_name_post)
            likes = itemView.findViewById(R.id.likes)
            publisher = itemView.findViewById(R.id.publisher)
            description = itemView.findViewById(R.id.description)
            comments = itemView.findViewById(R.id.comments)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.post_layout,null))
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        val post = mPost[position]

        Picasso.get().load(post.postimage).into(holder.postImage)

        publisherInfo(holder.profileImage,holder.username,holder.publisher, post.publisher)
    }

    override fun getItemCount(): Int = mPost.size

    private fun publisherInfo(profileImage: CircleImageView, username: TextView, publisher: TextView, publisher1: String?) {

            val usersRef = FirebaseDatabase.getInstance().reference.child("users").child(publisher1.toString())

            usersRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists())
                    {
                        val user = p0.getValue<User>(User::class.java)

                        Picasso.get().load(user!!.image).placeholder(R.drawable.profile).into(profileImage)
                        username.text = user!!.userName
                        publisher.text = user!!.fullName
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
}