package com.example.instanagar.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.instanagar.Fragments.ProfileFragment
import com.example.instanagar.Model.User
import com.example.instanagar.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
        private var mContext: Context,
        private var mUser:List<User>,
        private var isFragment: Boolean =false): RecyclerView.Adapter<UserAdapter.userViewHolder>() {

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userViewHolder {
        return userViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.user_item_layout,null))
    }

    override fun onBindViewHolder(holder: userViewHolder, position: Int) {
        val user:User = mUser[position]
        holder.userFullNameSearch.text = user.fullName
        holder.userNameSearch.text = user.userName
        Picasso.get().load(user.image).placeholder(R.drawable.profile).into(holder.userProfileImage)
        checkFollowingStatus(user.uid.toString(), holder.btnFollow)

        holder.itemView.setOnClickListener(View.OnClickListener {
            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("profileId", user.uid)
            pref.apply()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment()).commit()
        })

        holder.btnFollow.setOnClickListener {
            if(holder.btnFollow.text.toString() == "Follow")
            {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.uid.toString())
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseUser?.uid.let { it ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.uid.toString())
                                        .child("Followers").child(it.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                            }
                                        }
                                }
                            }
                        }
                }
            }
            else
            {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.uid.toString())
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.uid.toString())
                                        .child("Followers").child(it1.toString())
                                        .removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                            }
                                        }
                                }
                            }
                        }
                }
            }
        }
    }

    private fun checkFollowingStatus(uid: String, btnFollow: TextView) {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }

        followingRef.addValueEventListener(object : ValueEventListener
        {
            override fun onDataChange(datasnapshot: DataSnapshot)
            {
                if (datasnapshot.child(uid).exists())
                {
                    btnFollow.text = "Following"
                }
                else
                {
                    btnFollow.text = "Follow"
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


    override fun getItemCount(): Int = mUser.size

   inner class userViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val userProfileImage: CircleImageView = itemView.findViewById(R.id.user_profile_image_search)
        val userNameSearch: TextView = itemView.findViewById(R.id.user_username_search)
        val userFullNameSearch: TextView = itemView.findViewById(R.id.user_profile_username_search)
        val btnFollow: TextView = itemView.findViewById(R.id.follow_search)
    }
}