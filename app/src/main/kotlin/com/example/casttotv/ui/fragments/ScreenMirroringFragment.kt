package com.example.casttotv.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.casttotv.R
import android.widget.Toast


import android.content.Intent
import com.example.casttotv.utils.MySingleton.enablingWiFiDisplay


class ScreenMirroringFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_screen_mirroring, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance() = ScreenMirroringFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireContext().enablingWiFiDisplay()
    }


}