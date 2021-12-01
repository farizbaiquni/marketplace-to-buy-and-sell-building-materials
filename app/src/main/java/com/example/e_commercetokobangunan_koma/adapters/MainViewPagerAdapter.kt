package com.example.e_commercetokobangunan_koma.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.e_commercetokobangunan_koma.ChartsFragment
import com.example.e_commercetokobangunan_koma.ExploreFragment

private const val NUM_TABS = 2

class MainViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :

    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        if(position == 0){
            return ExploreFragment()
        }else{
            return ChartsFragment()
        }
    }
}