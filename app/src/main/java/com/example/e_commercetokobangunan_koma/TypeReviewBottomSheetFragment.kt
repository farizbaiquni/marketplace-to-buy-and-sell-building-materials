package com.example.e_commercetokobangunan_koma

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.e_commercetokobangunan_koma.adapters.ExploreAdapter
import com.example.e_commercetokobangunan_koma.databinding.ExploreFragmentBinding
import com.example.e_commercetokobangunan_koma.databinding.FragmentTypeReviewBottomSheetBinding
import com.example.e_commercetokobangunan_koma.viewmodels.ExploreViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TypeReviewBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentTypeReviewBottomSheetBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val TAG = "ModalBottomSheet"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTypeReviewBottomSheetBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.btnReviewProduct.setOnClickListener(View.OnClickListener {
            Toast.makeText(activity, "PRODUCT", Toast.LENGTH_SHORT).show()
        })

        binding.btnReviewShop.setOnClickListener(View.OnClickListener {
            startActivity(Intent(activity, ReviewShopActivity::class.java))

        })
        return view
    }

}