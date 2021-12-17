package com.example.e_commercetokobangunan_koma

import android.R
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.e_commercetokobangunan_koma.databinding.FragmentBottomSheetActionProductBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.widget.Toast

import android.content.DialogInterface
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class BottomSheetActionProductFragment(idProduct: String) : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetActionProductBinding? = null
    private val binding get() = _binding!!

    private val idProduct: String = idProduct

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBottomSheetActionProductBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnEditProduct.setOnClickListener(View.OnClickListener {
            startActivity(Intent(requireContext(), UpdateProductActivity::class.java).apply {
                putExtra("idProduct", idProduct)
            })
        })

        binding.btnHapusShop.setOnClickListener(View.OnClickListener {
            AlertDialog.Builder(requireContext())
                .setIcon(R.drawable.ic_delete)
                .setTitle("Hapus Produk")
                .setMessage("Apakah Anda yakin ingin menghapus produk tersebut")
                .setPositiveButton("YA",
                    DialogInterface.OnClickListener { dialog, whichButton ->
                        Firebase.firestore.collection("product").document(idProduct)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Produk berhasil dihapus", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(requireContext(), ShopProductListActivity::class.java))
                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), "Produk gagal dihapus", Toast.LENGTH_SHORT).show()
                            }
                    })
                .setNegativeButton("TIDAK", null).show()
        })

        return view
    }



}