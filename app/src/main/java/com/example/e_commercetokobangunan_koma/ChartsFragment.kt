package com.example.e_commercetokobangunan_koma

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.e_commercetokobangunan_koma.databinding.ChartsFragmentBinding
import com.example.e_commercetokobangunan_koma.databinding.ExploreFragmentBinding
import com.example.e_commercetokobangunan_koma.viewmodels.ChartsViewModel

class ChartsFragment : Fragment() {

    private var _binding: ChartsFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ChartsViewModel

    companion object {
        fun newInstance() = ChartsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ChartsFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChartsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}