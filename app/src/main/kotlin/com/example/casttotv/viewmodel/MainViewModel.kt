package com.example.casttotv.viewmodel

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.casttotv.R
import com.example.casttotv.databinding.ThemeDialogBinding
import com.example.casttotv.models.Lang
import com.example.casttotv.utils.MySingleton
import com.example.casttotv.utils.MySingleton.changeTheme
import com.example.casttotv.utils.Pref.getPrefs
import com.example.casttotv.utils.Pref.putPrefs
import com.example.casttotv.utils.THEME_DARK


class MainViewModel : ViewModel() {

    private val viewLanguage: MutableLiveData<View> = MutableLiveData()
    private val _darkAndLight: MutableLiveData<String> = MutableLiveData()
    val darkAndLight: LiveData<String> = _darkAndLight

    fun setView(view: View) {
        viewLanguage.value = view
    }

    fun viewVisibility(visible: Boolean) {
        if (!visible) viewLanguage.value?.visibility =
            View.GONE else viewLanguage.value?.visibility = View.VISIBLE
    }

    fun list(): MutableList<Lang> {
        return MySingleton.listOfLanguages
    }

    fun preLoadDarkAndLight(context: Context) {
        val dark = context.getPrefs(THEME_DARK, false)
        if (dark) {
            _darkAndLight.value = context.getString(R.string.dark)
        } else _darkAndLight.value = context.getString(R.string.light)

    }

    fun themeDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        val binding = ThemeDialogBinding.inflate(LayoutInflater.from(context), null, false)
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val dark = context.getPrefs(THEME_DARK, false)
        binding.apply {
            if (dark) {
                radioButtonDark.isChecked = true
                radioButtonLight.isChecked = false
                textviewDark.setTextColor(ContextCompat.getColor(context,
                    R.color.dodger_blue_light_2))
                textviewLight.setTextColor(ContextCompat.getColor(context, R.color.white))
                _darkAndLight.value = context.getString(R.string.dark)
            } else {
                radioButtonDark.isChecked = false
                radioButtonLight.isChecked = true
                textviewDark.setTextColor(ContextCompat.getColor(context, R.color.black_80))
                textviewLight.setTextColor(ContextCompat.getColor(context,
                    R.color.dodger_blue_light_2))
                _darkAndLight.value = context.getString(R.string.light)
            }
            viewCancel.setOnClickListener { dialog.dismiss() }
            clDark.setOnClickListener {
                context.putPrefs(THEME_DARK, true)
                radioButtonDark.isChecked = true
                radioButtonLight.isChecked = false
                textviewDark.setTextColor(ContextCompat.getColor(context,
                    R.color.dodger_blue_light_2))
                textviewLight.setTextColor(ContextCompat.getColor(context, R.color.white))
                _darkAndLight.value = context.getString(R.string.dark)
                context.changeTheme()
            }
            clLight.setOnClickListener {
                context.putPrefs(THEME_DARK, false)
                radioButtonDark.isChecked = false
                radioButtonLight.isChecked = true
                textviewDark.setTextColor(ContextCompat.getColor(context, R.color.black_80))
                textviewLight.setTextColor(ContextCompat.getColor(context,
                    R.color.dodger_blue_light_2))
                _darkAndLight.value = context.getString(R.string.light)
                context.changeTheme()
            }
        }
        dialog.show()
    }


}