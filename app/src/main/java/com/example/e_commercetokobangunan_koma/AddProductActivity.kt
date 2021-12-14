package com.example.e_commercetokobangunan_koma

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.e_commercetokobangunan_koma.adapters.AddProductAdapter
import com.example.e_commercetokobangunan_koma.databinding.ActivityAddProductBinding
import com.example.e_commercetokobangunan_koma.models.AddProductModel
import com.example.e_commercetokobangunan_koma.viewmodels.AddProductViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates


class AddProductActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityAddProductBinding
    private lateinit var adapterProduct: AddProductAdapter
    private lateinit var viewModel: AddProductViewModel
    private lateinit var builderLoadingDialog: AlertDialog.Builder
    private lateinit var loadingDialog: AlertDialog

    private var REQUEST_CODE: Int = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Action Bar Name
        getSupportActionBar()?.setTitle("Tambah Produk")

        // Initialize Firebase Auth
        auth = Firebase.auth

        //Laoding Dialog
        builderLoadingDialog = AlertDialog.Builder(this)
        var inflater: LayoutInflater = layoutInflater
        builderLoadingDialog.setView(inflater.inflate(R.layout.loading_dialog, null))
        builderLoadingDialog.setCancelable(false)
        loadingDialog = builderLoadingDialog.create()

        //RecyclerView Adapter
        adapterProduct = AddProductAdapter(this)
        var gridLayoutManager: GridLayoutManager = GridLayoutManager(this, 3)
        binding.recyclerViewUploadImagesProduct.layoutManager = gridLayoutManager
        binding.recyclerViewUploadImagesProduct.adapter = adapterProduct

        viewModel = ViewModelProvider(this).get(AddProductViewModel::class.java)
        viewModel.getSelectedPhotos().observe(this) { images ->
            if (images != null) {
                adapterProduct.setSelectedImages(images)
            }
        }
    }


    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){

            var permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
            )

            lateinit var nama: String
            lateinit var harga: String
            lateinit var deskripsi: String
            var linkVideo: String = ""
            lateinit var jumlahStok: String
            var kondisiBaru by Delegates.notNull<Boolean>()
            var kondisiBekas by Delegates.notNull<Boolean>()
            lateinit var berat: String

            binding.btnAddProduct.setOnClickListener(View.OnClickListener {
                nama = binding.etNama.text.toString().trim()
                harga = binding.etHarga.text.toString().trim()
                deskripsi = binding.etDeskripsi.text.toString().trim()
                linkVideo = binding.etLinkVideo.text.toString().trim()
                jumlahStok = binding.etJumlahStok.text.toString().trim()
                berat = binding.etBerat.text.toString().trim()
                kondisiBaru = binding.radioButtonBaru.isChecked
                kondisiBekas = binding.radioButtonBekas.isChecked

                if(validateForm(nama, harga, deskripsi, jumlahStok, berat, kondisiBaru, kondisiBekas,
                        viewModel.getSelectedPhotos().value!!
                    )){
                    addProduct(currentUser.uid, nama, harga, deskripsi, linkVideo, berat, jumlahStok, kondisiBaru,)
                }

            })

            binding.textViewSelectImage.setOnClickListener(View.OnClickListener {
                if(isAllPermissionsGranted(permissions.toList()).equals(false)){
                    requestMultiplePermissions.launch(permissions)
                } else {
                    var intent = Intent()
                    intent.type = "image/*"
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    intent.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult( Intent.createChooser(intent, "Choose Pictures"), REQUEST_CODE)
                }
            })

        }else{
            startActivity(Intent(this, WelcomeActivity::class.java))
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var imageListUri: MutableList<Uri> = ArrayList()

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE && data != null){
            // if multiple images are selected else single image selected
            if (data.getClipData() != null) {
                var count = data.clipData?.itemCount
                if (count != null) {
                    for (i in 0..count - 1) {
                        imageListUri.add(data.clipData!!.getItemAt(i).uri)
                    }
                }
                    viewModel.setSelectedPhotos(imageListUri)
            }else if(data?.getData() != null) {
                var imageUri: Uri? = data.data
                if (imageUri != null) {
                    imageListUri.add(imageUri)
                    viewModel.setSelectedPhotos(imageListUri)
                }
            }
        }// End if
    }



    fun isAllPermissionsGranted(permissions: List<String>): Boolean{
        for(permission in permissions){
            if(ContextCompat.checkSelfPermission(this@AddProductActivity, permission) == PackageManager.PERMISSION_DENIED){
                return false
            }
        }
        return true
    }



    val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
//        permissions ->
//            permissions.entries.forEach {
//                Toast.makeText(this, "${it.key} = ${it.value}", Toast.LENGTH_SHORT).show()
//                Log.e("DEBUG", "${it.key} = ${it.value}")
//            }
    }



    fun validateForm(nama: String, harga: String, deskripsi: String, jumlakStok: String,
    berat: String, kondisiBaru: Boolean, kondisiBekas: Boolean, photo: MutableList<Uri>) : Boolean{

        if(nama.isBlank() || harga.isBlank() || deskripsi.isBlank() || jumlakStok.isBlank() || berat.isBlank()
            || kondisiBaru.equals(false) && kondisiBekas.equals(false) || photo.isNullOrEmpty()){

            if(photo.isNullOrEmpty()){
                binding.textViewErrorSelectPhoto.visibility = View.VISIBLE
            } else {
                binding.textViewErrorSelectPhoto.visibility = View.GONE
            }

            if(nama.isBlank()){
                binding.textFieldNama.error = "Tidak boleh kosong"
            } else {
                binding.textFieldNama.error = null
            }

            if(harga.isBlank()){
                binding.textFieldHarga.error = "Tidak boleh kosong"
            } else {
                binding.textFieldHarga.error = null
            }

            if(deskripsi.isBlank()){
                binding.textFieldDeskripsi.error = "Tidak boleh kosong"
            } else {
                binding.textFieldDeskripsi.error = null
            }

            if(jumlakStok.isBlank()){
                binding.textFieldJumlahStok.error = "Tidak boleh kosong"
            } else {
                binding.textFieldJumlahStok.error = null
            }

            if(berat.isBlank()){
                binding.textFieldBerat.error = "Tidak boleh kosong"
            } else {
                binding.textFieldBerat.error = null
            }

            if(kondisiBaru.equals(false) && kondisiBekas.equals(false)){
                binding.textFieldKondisi.error = "Harus pilih salah satu"
            } else {
                binding.textFieldKondisi.error = null
            }

            return false
        } else {
            binding.textFieldNama.error = null
            binding.textFieldHarga.error = null
            binding.textFieldDeskripsi.error = null
            binding.textFieldLinkVideo.error = null
            binding.textFieldJumlahStok.error = null
            binding.textFieldBerat.error = null
            binding.textFieldKondisi.error = null
            return  true
        }

    }



    fun addProduct(idUser: String, nama: String, harga: String, deskripsi: String, linkVideo: String, berat: String,
                   jumlahStok: String, kondisiBaru: Boolean){

        loadingDialog.show()

        val data = AddProductModel(idUser, nama, harga.toLong(), deskripsi, linkVideo, jumlahStok.toLong(), berat.toDouble(),
        kondisiBaru)

        Firebase.firestore.collection("product")
            .add(data)
            .addOnSuccessListener { documentReference ->
                viewModel.setIdProduct(documentReference.id)
                viewModel.getSelectedPhotos().value?.let { uploadImages(it) }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal menambahkan produks", Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
            }
    }



    fun uploadImages(imagesUri: MutableList<Uri>){
        viewModel.setPhotosProductUrl(mutableListOf())
        var uploadedImagesUrl: MutableList<String> = mutableListOf()
        var storageRef = Firebase.storage.reference
        val iterator = imagesUri.iterator()
        var fileName: UUID?

        val totalImages = imagesUri.size
        var index = 0

        while (iterator.hasNext()) {
            fileName = UUID.randomUUID()
            val ref = storageRef.child("product_photos/$fileName")
            var uploadTask = ref.putFile(iterator.next())
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    uploadedImagesUrl.add(task.result.toString())
                    index++
                    if(index.equals(totalImages)){
                        addPhotoProductUrl(uploadedImagesUrl)
                    }
                } else {
                    // Handle failures
                    index++
                    if(index.equals(totalImages)){
                        addPhotoProductUrl(uploadedImagesUrl)
                    }
                }
            }
        }
    }



    fun addPhotoProductUrl(photosUrl: MutableList<String>){

        val productRef = Firebase.firestore.collection("product").document(viewModel.getIdProduct().value.toString())
        var productPhotosRef = viewModel.getIdProduct().value?.let {
            Firebase.firestore.collection("product").document(it).collection("photos").document()
        }

        // Update Default Photo
        productRef.update("default_photo", photosUrl[0])

        // Add url photo
        Firebase.firestore.runBatch { batch ->

            val iterator = photosUrl.iterator()
            while (iterator.hasNext()) {

                val data = hashMapOf(
                    "photo_url" to iterator.next(),
                )

                if (productPhotosRef != null) {
                    batch.set(productPhotosRef!!, data)
                }

                productPhotosRef = viewModel.getIdProduct().value?.let {
                    Firebase.firestore.collection("product").document(it).collection("photos").document()
                }
            }
        }.addOnSuccessListener {
            Toast.makeText(this, "Produk Berhaasil ditambahkan", Toast.LENGTH_SHORT).show()
            loadingDialog.dismiss()
            startActivity(Intent(this, ShopProductListActivity::class.java))
        }.addOnFailureListener {
            Toast.makeText(this, "Produk gagal ditambahkan", Toast.LENGTH_SHORT).show()
            loadingDialog.dismiss()
            startActivity(Intent(this, ShopProductListActivity::class.java))
        }
    }



} // End class