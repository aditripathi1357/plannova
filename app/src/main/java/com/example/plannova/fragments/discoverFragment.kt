package com.example.plannova.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.plannova.adapters.ViewPagerAdapter

import com.example.plannova.databinding.FragmentDiscoverBinding


class discoverFragment : Fragment() {
    private lateinit var binding: FragmentDiscoverBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        // Inflate the layout for this fragment
        binding = FragmentDiscoverBinding.inflate(inflater, container, false)

        viewPagerAdapter=ViewPagerAdapter(requireActivity().supportFragmentManager)
        viewPagerAdapter.addFragments(VenueFragment(),"Venue")
        viewPagerAdapter.addFragments(CateringFragment(),"Catering")
        viewPagerAdapter.addFragments(EntertainmentFragment(),"Party")
        viewPagerAdapter.addFragments(PhotographyFragment(),"Photo")
        viewPagerAdapter.addFragments(StaffServicesFragment(),"Services")
        binding.viewPager.adapter=viewPagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)


        return binding.root
    }

    companion object {
    }
}