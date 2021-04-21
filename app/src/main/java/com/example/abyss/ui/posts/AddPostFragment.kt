package com.example.abyss.ui.posts

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.abyss.model.data.internal.PostData
import com.example.abyss.databinding.FragmentAddPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import timber.log.Timber
import java.nio.file.Paths


class AddPostFragment : Fragment() {

    //    private lateinit var viewModel: AddPostViewModel
    private lateinit var binding: FragmentAddPostBinding

    private lateinit var imagePath: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

//        viewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)
        binding = FragmentAddPostBinding.inflate(inflater, container, false)
//        binding.loginViewModel = viewModel
//        binding.lifecycleOwner = viewLifecycleOwner
        binding.buttonSelectImg.setOnClickListener {
            startImageChooser()
        }
        binding.buttonDownload.setOnClickListener {
            downloadImageInFirebase()
        }

        return binding.root
    }

    private fun downloadImageInFirebase() {
        if (imagePath != null) {

            //собиралась брать имя файла, не используется
            var name = Paths.get(imagePath.path).getFileName()
//            var pd = ProgressDialog(this)
//            var imageRef = FirebaseStorage.getInstance().reference.child("postImages/f1.jpg")
//            imageRef.putFile(imagePath)

            var storage = FirebaseStorage.getInstance()
//            var storageReference = storage!!.reference

            val key = FirebaseDatabase.getInstance().getReference("posts").push().getKey()

            val imageRef = FirebaseStorage.getInstance().getReference("postImages/$key")
            imageRef.putFile(imagePath)
                .addOnSuccessListener {
                    Timber.i("Успешно: ${it.metadata?.path}")
         /////////////беру url, ассинхронно
                    imageRef.downloadUrl.addOnSuccessListener {
                        val imageUrl = it.toString()
//////////////пост в базу
                        AddPost( imageUrl, key)
                    }

                    comeBack()
                }
                .addOnFailureListener {
                    Timber.i("Ошибка: ${it.message}")
                }
                .addOnProgressListener {
//                        p ->
//                    var progress = (100.0 * p.bytesTransferred)/ p.totalByteCount
//                    pd.setMessage("Выполено: ${progress}%")
                }
        }
    }

    private fun AddPost( imageUrl: String, key: String?) {
        val database = FirebaseDatabase.getInstance()
//        val key = database.getReference("posts").push().getKey()
        val ref = database.getReference("posts").child(key!!)
        val uid = FirebaseAuth.getInstance().uid ?: ""

        val post = PostData(imageUrl, binding.textInputEditText.text.toString(), uid)
        ref.setValue(post)
            .addOnSuccessListener {
                Timber.i("пост сохранен")
            }
            .addOnFailureListener {
                Timber.i("ошибка с постом: ${it.message}")
            }
    }

    private fun comeBack() {
        findNavController().navigate(AddPostFragmentDirections.actionAddPostFragmentToProfileFragment())
    }

    private fun startImageChooser() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(
            Intent.createChooser(intent, "Выберите изображение"), 1111
        )
    }

    @SuppressLint("CheckResult")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1111 && resultCode == Activity.RESULT_OK && data != null) {

            imagePath = data.data!!

            Glide.with(this)
                .load(imagePath)
                .centerCrop()
                .into(binding.imageViewNewPost)
        }
    }

}