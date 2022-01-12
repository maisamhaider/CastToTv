package com.example.casttotv.viewmodel

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.text.format.Formatter
import android.util.Patterns
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.casttotv.R
import com.example.casttotv.adapter.LanguagesAdapter
import com.example.casttotv.databinding.LayoutLanguagesBinding
import com.example.casttotv.databinding.OrientationDialogBinding
import com.example.casttotv.databinding.RateDialogBinding
import com.example.casttotv.databinding.ThemeDialogBinding
import com.example.casttotv.interfaces.MyCallBack
import com.example.casttotv.dataclasses.Lang
import com.example.casttotv.utils.*
import com.example.casttotv.utils.MySingleton.changeTheme
import com.example.casttotv.utils.MySingleton.toastLong
import com.example.casttotv.utils.Pref.getPrefs
import com.example.casttotv.utils.Pref.putPrefs
import java.util.*


class MainViewModel : ViewModel() {

    private val _darkAndLight: MutableLiveData<String> = MutableLiveData()
    private val _orientation: MutableLiveData<String> = MutableLiveData()
    private val _autoStop: MutableLiveData<Boolean> = MutableLiveData()
    private val _autoMinimize: MutableLiveData<Boolean> = MutableLiveData()

    val darkAndLight: LiveData<String> = _darkAndLight
    val orientation: LiveData<String> = _orientation
    val autoStop: LiveData<Boolean> = _autoStop
    val autoMinimize: LiveData<Boolean> = _autoMinimize

    private var languageDialog: MutableLiveData<AlertDialog> = MutableLiveData(null)

    fun list(): MutableList<Lang> {
        return MySingleton.listOfLanguages
    }

    fun preLoadData(context: Context) {
        val dark = context.getPrefs(THEME_DARK, false)
        if (dark) {
            _darkAndLight.value = context.getString(R.string.dark)
        } else _darkAndLight.value = context.getString(R.string.light)

        //orientation
        val orient = context.getPrefs(AUTO_ROTATION, false)
        if (orient) {
            _orientation.value = context.getString(R.string.auto)
        } else _orientation.value = context.getString(R.string.manual)

        //Auto Stop
        _autoStop.value = context.getPrefs(AUTO_STOP, false)
        //Auto Minimize
        _autoMinimize.value = context.getPrefs(AUTO_MINIMIZE, false)
    }

    fun changeAutoStop(context: Context) {
        context.putPrefs(AUTO_STOP, !context.getPrefs(AUTO_STOP, false))
        _autoStop.value = context.getPrefs(AUTO_STOP, false)
    }

    fun changeAutoMinimize(context: Context) {
        context.putPrefs(AUTO_MINIMIZE, !context.getPrefs(AUTO_MINIMIZE, false))
        _autoMinimize.value = context.getPrefs(AUTO_MINIMIZE, false)
    }


    fun orientationDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        val binding = OrientationDialogBinding.inflate(LayoutInflater.from(context), null, false)
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val orient = context.getPrefs(AUTO_ROTATION, false)
        val dark = context.getPrefs(THEME_DARK, false)

        binding.apply {
            if (orient) {
                radioButton1.isChecked = true
                radioButton2.isChecked = false
                if (dark) {
                    textviewManual.setTextColor(ContextCompat.getColor(context, R.color.cr_white))
                } else {
                    textviewManual.setTextColor(ContextCompat.getColor(context, R.color.cr_black_80))
                }
                textviewAuto.setTextColor(ContextCompat.getColor(context,
                    R.color.cr_dodger_blue_light_2))

            } else {
                radioButton1.isChecked = false
                radioButton2.isChecked = true
                if (dark) {
                    textviewAuto.setTextColor(ContextCompat.getColor(context, R.color.cr_white))
                } else {
                    textviewAuto.setTextColor(ContextCompat.getColor(context, R.color.cr_black_80))
                }
                textviewManual.setTextColor(ContextCompat.getColor(context,
                    R.color.cr_dodger_blue_light_2))
                _orientation.value = context.getString(R.string.manual)

            }
            viewCancel.setOnClickListener { dialog.dismiss() }
            clRatio1.setOnClickListener {
                context.putPrefs(AUTO_ROTATION, true)
                radioButton1.isChecked = true
                radioButton2.isChecked = false
                textviewAuto.setTextColor(ContextCompat.getColor(context,
                    R.color.cr_dodger_blue_light_2))
                if (dark) {
                    textviewManual.setTextColor(ContextCompat.getColor(context, R.color.cr_white))
                } else {
                    textviewManual.setTextColor(ContextCompat.getColor(context, R.color.cr_black_80))
                }
                _orientation.value = context.getString(R.string.auto)
                context.changeTheme()
            }
            clRatio2.setOnClickListener {
                context.putPrefs(AUTO_ROTATION, false)
                radioButton1.isChecked = false
                radioButton2.isChecked = true
                if (dark) {
                    textviewAuto.setTextColor(ContextCompat.getColor(context, R.color.cr_white))
                } else {
                    textviewAuto.setTextColor(ContextCompat.getColor(context, R.color.cr_black_80))
                }
                textviewManual.setTextColor(ContextCompat.getColor(context,
                    R.color.cr_dodger_blue_light_2))
                _orientation.value = context.getString(R.string.manual)
                context.changeTheme()
            }
        }
        dialog.show()
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
                    R.color.cr_dodger_blue_light_2))
                textviewLight.setTextColor(ContextCompat.getColor(context, R.color.cr_white))
                _darkAndLight.value = context.getString(R.string.dark)
            } else {
                radioButtonDark.isChecked = false
                radioButtonLight.isChecked = true
                textviewDark.setTextColor(ContextCompat.getColor(context, R.color.cr_black_80))
                textviewLight.setTextColor(ContextCompat.getColor(context,
                    R.color.cr_dodger_blue_light_2))
                _darkAndLight.value = context.getString(R.string.light)
            }
            viewCancel.setOnClickListener { dialog.dismiss() }
            clDark.setOnClickListener {
                context.putPrefs(THEME_DARK, true)
                radioButtonDark.isChecked = true
                radioButtonLight.isChecked = false
                textviewDark.setTextColor(ContextCompat.getColor(context,
                    R.color.cr_dodger_blue_light_2))
                textviewLight.setTextColor(ContextCompat.getColor(context, R.color.cr_white))
                _darkAndLight.value = context.getString(R.string.dark)
                context.changeTheme()
                dialog.dismiss()
            }
            clLight.setOnClickListener {
                context.putPrefs(THEME_DARK, false)
                radioButtonDark.isChecked = false
                radioButtonLight.isChecked = true
                textviewDark.setTextColor(ContextCompat.getColor(context, R.color.cr_black_80))
                textviewLight.setTextColor(ContextCompat.getColor(context,
                    R.color.cr_dodger_blue_light_2))
                _darkAndLight.value = context.getString(R.string.light)
                context.changeTheme()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    fun sendEmail(
        context: Context,
        feedback: String?,
        senderEmail: String?,
        list: ArrayList<Uri>,
    ) {
        var feedback = feedback
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        if (feedback == null || feedback.isEmpty()) {
            context.toastLong(context.getString(R.string.provide_feedback))
        } else if (senderEmail != null && senderEmail.isNotEmpty() &&
            !Patterns.EMAIL_ADDRESS.matcher(senderEmail).matches()
        ) {
            context.toastLong(context.getString(R.string.invalid_email_provided))
        } else {
            feedback = """
            ${context.getString(R.string.feedback)} $feedback
           ${context.getString(R.string.model)} ${Build.MODEL}
            ${context.getString(R.string.brand)} ${Build.BRAND}
           ${context.getString(R.string.os_version)} ${Build.VERSION.SDK_INT}
          ${context.getString(R.string.manufacturer)} ${Build.MANUFACTURER}
          ${context.getString(R.string.ram)} ${
                Formatter.formatFileSize(context,
                    memoryInfo.totalMem)
            }
            """.trimIndent()
            val TO = arrayOf("hr@syncmedia.net")
            val emailIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
            emailIntent.type = "image/*"
            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO)
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "feedback")
            emailIntent.putExtra(Intent.EXTRA_TEXT, feedback)
            if (list.isNotEmpty()) {
                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, list)
            }
            try {
                context.startActivity(Intent.createChooser(emailIntent,
                    "${context.getString(R.string.send_email)}"))
            } catch (ex: ActivityNotFoundException) {
                context.toastLong(context.getString(R.string.invalid_email_provided))
            }
        }
    }

    fun rateUs(context: Context, myCallBack: MyCallBack) {
        val builder = AlertDialog.Builder(context)
        val binding = RateDialogBinding.inflate(LayoutInflater.from(context), null, false)
        builder.setView(binding.root)
        builder.setCancelable(true)
        val dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
        binding.rating.setOnRatingBarChangeListener { ratingBar, rate, b ->
            if (rate == 5f) {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id="
                                + context.packageName)))
                } catch (e: ActivityNotFoundException) {
                    context.startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" +
                                context.packageName)))
                }
            } else {
                myCallBack.callback()
            }
            dialog.dismiss()

        }
        binding.viewNotKnow.setOnClickListener {
            dialog.dismiss()
        }

    }

    fun languageDialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        val binding = LayoutLanguagesBinding.inflate(LayoutInflater.from(context), null, false)
        builder.setView(binding.root)
        builder.setCancelable(true)
        languageDialog.value = builder.create()
        languageDialog.value!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        languageDialog.value!!.show()
        val adapter = LanguagesAdapter(context)
        val recyclerView = binding.recyclerview
        recyclerView.adapter = adapter
        try {
            adapter.submitList(list())
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    fun cancelLanguageDialog() {
        if (languageDialog.value != null) {
            if (languageDialog.value!!.isShowing) {
                languageDialog.value!!.dismiss()
            }
        }
    }

    fun languageDialogIsShowing(): Boolean {
        return if (languageDialog.value == null) {
            false
        } else languageDialog.value!!.isShowing
    }


}