package com.example.casttotv.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.casttotv.BuildConfig
import com.example.casttotv.R
import com.example.casttotv.databinding.FragmentAppSettingsBinding
import com.example.casttotv.utils.Pref.getPrefs
import com.example.casttotv.utils.THEME_DARK
import com.example.casttotv.viewmodel.MainViewModel


class AppSettingsFragment : Fragment() {
    private lateinit var _binding: FragmentAppSettingsBinding
    private val binding get() = _binding
    val version = BuildConfig.VERSION_NAME
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAppSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.preLoadData(requireContext())
        binding.apply {
            thisFrag = this@AppSettingsFragment
            lifecycleOwner = viewLifecycleOwner
            mainVM = viewModel
        }
    }

    fun themeDialog() {
        viewModel.themeDialog(requireContext())
    }

    fun orientationDialog() {
        viewModel.orientationDialog(requireContext())
    }
    fun changeAutoStop() {
        viewModel.changeAutoStop(requireContext())
    }fun changeAutoMinimize() {
        viewModel.changeAutoMinimize(requireContext())
    }

    private fun setIcons() {
        val dark = requireContext().getPrefs(THEME_DARK, false)
        if (dark) {
            binding.imageviewSettingHq.setImageResource(R.drawable.ic_hq_dark)
            binding.imageviewSettingAutoMin.setImageResource(R.drawable.ic_auto_manimize_dark)
            binding.imageviewSettingAutoStop.setImageResource(R.drawable.ic_auto_stop_dark)
            binding.imageviewColorTheme.setImageResource(R.drawable.ic_color_theme_dark)
            binding.imageviewSupportedDevices.setImageResource(R.drawable.supporting_devices_dark)
            binding.imageviewPp.setImageResource(R.drawable.ic_shield_dark)
        } else {
            binding.imageviewSettingHq.setImageResource(R.drawable.ic_hq_light)
            binding.imageviewSettingAutoMin.setImageResource(R.drawable.ic_auto_manimize_light)
            binding.imageviewSettingAutoStop.setImageResource(R.drawable.ic_auto_stop_light)
            binding.imageviewColorTheme.setImageResource(R.drawable.ic_color_theme_light)
            binding.imageviewSupportedDevices.setImageResource(R.drawable.supporting_devices_light)
            binding.imageviewPp.setImageResource(R.drawable.ic_shield_light)
        }
    }

    fun back() {
        findNavController().navigate(R.id.action_appSettingsFragment_to_homeFragment)
    }

    fun goToPrivacyPolicy() {
        findNavController().navigate(R.id.action_appSettingsFragment_to_pricacyPolicyFragment)
    }

    fun goToSupportedDevices() {
        findNavController().navigate(R.id.action_appSettingsFragment_to_supportedDevicesFragment)
    }

    override fun onResume() {
        super.onResume()
        setIcons()
    }
}