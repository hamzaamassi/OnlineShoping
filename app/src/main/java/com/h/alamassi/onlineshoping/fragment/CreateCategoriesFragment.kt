package com.h.alamassi.onlineshoping.fragment

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.h.alamassi.onlineshoping.R
import com.h.alamassi.onlineshoping.databinding.FragmentCreateCategoriesBinding


class CreateCategoriesFragment : Fragment() {

    private lateinit var createCategoryBinding: FragmentCreateCategoriesBinding
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    companion object {
        private const val TAG = "CreateCategoryFragment"

        const val IMAGE_REQUEST_CODE = 102
    }

    private var imagePath: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        firebaseFirestore = FirebaseFirestore.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()
        createCategoryBinding = FragmentCreateCategoriesBinding.inflate(inflater, null, false)
        return createCategoryBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createCategoryBinding.btnSaveCategory.setOnClickListener {
            createCategory()
        }
        createCategoryBinding.fabChooseImage.setOnClickListener {
            chooseImage()
        }
    }

    private fun chooseImage() {
        val galleryPermission = ActivityCompat.checkSelfPermission(
            requireContext(),
            READ_EXTERNAL_STORAGE
        )
        if (galleryPermission != PackageManager.PERMISSION_DENIED) {
            //Open PickImageActivity
            chooseImageFromGallery()
        } else {
            //Ask User
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(READ_EXTERNAL_STORAGE),
                IMAGE_REQUEST_CODE
            )
        }
    }

    private fun chooseImageFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(
            intent,
            IMAGE_REQUEST_CODE
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            if (data.data != null) {
                val split: Array<String> =
                    data.data!!.path!!.split(":".toRegex()).toTypedArray() //split the path.
                val filePath = split[1] //assign it to a string(your choice).
                val bm = BitmapFactory.decodeFile(filePath)
                createCategoryBinding.ivCategoryImage.setImageBitmap(bm)

                imagePath = filePath
                Log.d(TAG, "onActivityResult: imagePath $imagePath")
            }
        }
    }

    private fun createCategory() {
        val name = createCategoryBinding.etCategoryName.text.toString()
        val image = imagePath

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Name required", Toast.LENGTH_SHORT).show()
        } else {
            val data = HashMap<String, String>()
            data["name"] = name
            data["image"] = image
            firebaseFirestore.collection("categories").add(data)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(context, "Created Successfully", Toast.LENGTH_LONG)
                            .show()
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, CategoryFragment()).commit()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Something error, Please try again later",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }
}