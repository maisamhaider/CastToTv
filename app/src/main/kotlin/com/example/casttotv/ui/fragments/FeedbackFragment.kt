package com.example.casttotv.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.casttotv.R
import com.example.casttotv.adapter.FeedbackAdapter
import com.example.casttotv.databinding.FragmentFeedbackBinding
import com.example.casttotv.dataclasses.Feedback
import com.example.casttotv.utils.Internet
import com.example.casttotv.utils.MySingleton.toastLong
import com.example.casttotv.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FeedbackFragment : Fragment() {
    private lateinit var _binding: FragmentFeedbackBinding
    private val binding get() = _binding
    private val mainViewModel: MainViewModel by viewModels()
    lateinit var adapter: FeedbackAdapter

    private var imagesUriArrayList = ArrayList<Uri>()

    private val adapterList: MutableList<Feedback> = mutableListOf(Feedback("Add".toUri()),
        Feedback(null), Feedback(null))

    private val adapterList2: MutableList<Feedback> = mutableListOf(Feedback("Add".toUri()))
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFeedbackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = FeedbackAdapter(requireContext(), ::onItemClick)

        binding.apply {
            feedback = this@FeedbackFragment
            recyclerView.adapter = adapter
            adapter.submitList(adapterList)
            textviewSend.setOnClickListener {
                if (Internet(requireContext()).isInternetAvailable()) {
                    mainViewModel.sendEmail(requireContext(), etInput.text.toString(),
                        etEmail.text.toString(), imagesUriArrayList)
                } else {
                    requireContext().toastLong(getString(R.string.check_your_internet_connection))
                }
            }
        }
    }

    private var someActivityResultLauncher = registerForActivityResult(
        StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null
        ) {
            val clipData = result.data?.dataString

            if (adapterList2.size < 6) {
                val uri = clipData.toString().toUri()
                clipData?.let {
                    adapterList2.add(Feedback(uri = uri))
                }
            }

            CoroutineScope(Dispatchers.IO).launch {
                val filteredList =
                    adapterList2.filter { !it.uri.toString().contains("Add") && it.uri != null }
                filteredList.forEach {
                    imagesUriArrayList.add(it.uri!!)
                }
            }
            adapter.submitList(adapterList2)
            adapter.notifyDataSetChanged()
        }
    }

    fun back() {
        findNavController().navigate(R.id.action_feedbackFragment_to_homeFragment)
    }

    fun onItemClick(feedback: Feedback) {
        if (adapterList2.size < 5) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            intent.type = "image/*"
            someActivityResultLauncher.launch(Intent.createChooser(intent,
                getString(R.string.select_picture)))
        } else {
            requireContext().toastLong(getString(R.string.more_then_5_images_can_not_be_picked))
        }
    }
}