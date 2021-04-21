package com.example.abyss.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.abyss.adapters.ProfilePostsRecyclerViewAdapter
import com.example.abyss.model.data.dto.PostDTO
import com.example.abyss.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import timber.log.Timber


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private var mFirebaseDatabase: DatabaseReference? = null
    private var mFirebaseInstance: FirebaseDatabase? = null


    var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding = FragmentProfileBinding.inflate(inflater, container, false)

        getUser()
        getPosts()

        return binding.root
    }


    private fun getPosts() {
         var recyclerView: RecyclerView? = null
         var charItem: ArrayList<PostDTO>? = null
         var gridLayoutManager: GridLayoutManager? = null
         var alphaAdapters: ProfilePostsRecyclerViewAdapter? = null
        var arrayList: ArrayList<PostDTO> = ArrayList()
        recyclerView = binding.profilePostsRecyclerView
        gridLayoutManager = GridLayoutManager(context, 3, LinearLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.setHasFixedSize(true)


        var auth = FirebaseAuth.getInstance()
        var FirebaseUser = auth.getCurrentUser()!!
        val uid = auth.getUid()!!

        val fireBase = FirebaseDatabase.getInstance()

        var ref = fireBase.getReference("posts").orderByChild("userId").equalTo(uid)


        val value = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (item in dataSnapshot.children) {

                    val image = item.child("imageUrl").getValue(String::class.java)
                    val text = item.child("text").getValue(String::class.java)
                    arrayList.add(PostDTO(image.toString(), text.toString()))


//                    Glide.with(this@ProfileFragment)
//                        .load(image)
//                        .centerCrop()
//                        .into(binding.imageViewPost)

                }
                charItem = arrayList
                alphaAdapters =
                    activity?.let { ProfilePostsRecyclerViewAdapter(it.applicationContext, charItem!!) }
                recyclerView?.adapter = alphaAdapters
            }
            override fun onCancelled(error: DatabaseError) {
                Timber.i(error.getMessage())
            }
        }
        ref.addListenerForSingleValueEvent(value)
    }


    private fun getUser() {

        var auth = FirebaseAuth.getInstance()
        var FirebaseUser = auth.getCurrentUser()!!

        val uid = auth.getUid()!!

        val fireBase = FirebaseDatabase.getInstance()

        //получаем ссылку на узел users
        var ref = fireBase.getReference("users").child(uid)

        val value = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (item in dataSnapshot.children) {
                    val u = dataSnapshot.child("userName").getValue(String::class.java)
//                        val username = item.child("users").child(uid).child("userName").getValue(String::class.java)
                    Timber.i("имя пользователя: %s", u)
                    binding.userNameTextView.text = u
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.i(error.getMessage())
            }
        }
        ref.addListenerForSingleValueEvent(value)

    }

//    private  fun ListFile() = CoroutineScope(Dispatchers.IO).launch {
//        try {
//            val image = FirebaseDatabase.getInstance()
//                .getReference("posts")
//                .orderByChild("userId")
//                .equalTo(true)
//
//        }
//    }

}




