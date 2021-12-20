package com.example.casttotv.ui.activities.browser

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.casttotv.databinding.ActivityDataControlBinding
import com.example.casttotv.databinding.LayoutBodyPosAndNegButtonBinding
import com.example.casttotv.utils.AppApplication
import com.example.casttotv.viewmodel.BrowserViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class DataControlActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityDataControlBinding
    private val binding get() = _binding

    private val browserViewModel: BrowserViewModel by viewModels {
        BrowserViewModel.BrowserViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDataControlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            browserVM = browserViewModel
            dataControlActivity = this@DataControlActivity
        }
    }


    fun deleteDatabaseBottomSheet(body: String, pos: String, nag: String) {
        val bottomSheet = BottomSheetDialog(this)
        val view = LayoutBodyPosAndNegButtonBinding.inflate(LayoutInflater.from(this), null, false)
        bottomSheet.setContentView(view.root)
        view.apply {
            textviewBody.text = body
            textviewPositive.text = pos
            textviewNegative.text = nag
            textviewPositive.setOnClickListener {
                bottomSheet.cancel()
            }
            textviewNegative.setOnClickListener {
                bottomSheet.cancel()
            }
        }


        bottomSheet.show()
    }
}