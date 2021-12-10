package com.example.casttotv.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.*
import androidx.webkit.WebViewCompat
import com.example.casttotv.R
import com.example.casttotv.database.entities.BookmarkEntity
import com.example.casttotv.database.entities.HistoryEntity
import com.example.casttotv.database.entities.HomeEntity
import com.example.casttotv.databinding.InputDialogLayoutBinding
import com.example.casttotv.databinding.LayoutSearchEnginesBinding
import com.example.casttotv.databinding.LayoutTitleBodyPosAndNegButtonBinding
import com.example.casttotv.datasource.DataSource
import com.example.casttotv.utils.AppApplication
import com.example.casttotv.utils.Pref.getBoolean
import com.example.casttotv.utils.Pref.getString
import com.example.casttotv.utils.Pref.putBoolean
import com.example.casttotv.utils.Pref.putString
import com.example.casttotv.utils.SELECTED_ENGINE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class BrowserViewModel(private var cxt: Context) : ViewModel() {

    private val TAG = "BrowserViewModel"
    private val bookmarkDao = (cxt.applicationContext as AppApplication).database.bookmarkDao()
    private val homeDao = (cxt.applicationContext as AppApplication).database.homeDao()
    private val historyDao = (cxt.applicationContext as AppApplication).database.historyDao()

    private var webView = WebView(cxt)


    private var bookmarkItem: MutableLiveData<BookmarkEntity> = MutableLiveData()
    private var homeItem: MutableLiveData<HomeEntity> = MutableLiveData()
    private var historyItem: MutableLiveData<HistoryEntity> = MutableLiveData()

    val webViewPackageInfo = WebViewCompat.getCurrentWebViewPackage(cxt)


    val engines = DataSource().engines // search engines urls

    fun putBooleanPrefs(key: String, value: Boolean) {
        cxt.putBoolean(key, value)
    }

    fun getBooleanPrefs(key: String, value: Boolean) = cxt.getBoolean(key, value)


    fun putEngine(value: String) {
        cxt.putString(SELECTED_ENGINE, value)
    }

    fun checkSelectedEngine(value: String): Boolean {
        val getEngine = cxt.getString(SELECTED_ENGINE, "")

        return getEngine == value
    }


    /**
     * set web view properties
     */
    @SuppressLint("SetJavaScriptEnabled")
    fun initWebView(wv: WebView) {
        webView = wv
        webView.webViewClient = MyBrowser()
        webView.settings.javaScriptEnabled = true
        webView.settings.loadsImagesAutomatically = true
        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                history(view.title.toString(), view.url.toString(), Date().time.toString())
                Log.e(TAG, "title---->${view.title.toString()}\n url----->${view.url.toString()}")
                insertHistory()
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
            }
        }
    }

    fun back() {
        if (webView.isFocused && webView.canGoBack()) {
            webView.goBack()
        }
    }

    fun canGoBack(): Boolean {
        return (webView.isFocused && webView.canGoBack())
    }

    fun search(searchText: String) {
        var engine = cxt.getString(SELECTED_ENGINE, "https://www.google.com/search?q=")

        engine = if (engine.isEmpty()) {
            "https://www.google.com/search?q="
        } else engine
        webView.loadUrl("$engine${searchText}")
        history(searchText, webView.url.toString(), Date().time.toString())
        Log.e(TAG, "title---->$searchText\n url----->${webView.url.toString()}")
        insertHistory()
    }

    fun isSearchValid(searchText: String): Boolean {
        return searchText.isNotBlank()
    }


    fun engineDialog() {
        val binding = LayoutSearchEnginesBinding.inflate(LayoutInflater.from(cxt), null,
            false)
        val builder = AlertDialog.Builder(cxt)
        builder.setView(binding.root)
        val dialog = builder.create()

        binding.apply {
            browserVM = this@BrowserViewModel
            mButtonCancel.setOnClickListener { dialog.dismiss() }
        }
        dialog.show()
    }

    fun exitDialog(activity: Activity) {
        val binding =
            LayoutTitleBodyPosAndNegButtonBinding.inflate(LayoutInflater.from(cxt), null,
                false)
        val builder = AlertDialog.Builder(cxt)
        builder.setView(binding.root)
        val dialog = builder.create()

        binding.apply {
            textviewTitle.text = "Exit"
            textviewBody.text = "This is exit dialog"
            textviewPositive.text = "Yes"
            textviewNegative.text = "No"
            textviewPositive.setOnClickListener {
                dialog.dismiss()
                activity.finish()
            }
            textviewNegative.setOnClickListener { dialog.dismiss() }
        }

        dialog.show()
    }

    fun inputDialog(title: String, prefKey: String) {
        val binding = InputDialogLayoutBinding.inflate(LayoutInflater.from(cxt), null,
            false)
        val builder = AlertDialog.Builder(cxt)
        builder.setView(binding.root)
        val dialog = builder.create()

        binding.apply {
            textviewTitle.text = title
            textviewOk.text = cxt.getString(R.string.ok)
            textviewCancel.text = cxt.getString(R.string.cancel)
            textInputEditText.setText(cxt.getString(prefKey, ""))

            textviewOk.setOnClickListener {
                val input = textInputEditText.text.toString()
                if (TextUtils.isEmpty(input)) {
                    cxt.putString(prefKey, "")
                } else {
                    cxt.putString(prefKey, input)
                }
                dialog.dismiss()
            }
            textviewCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    // Database Area
    fun getBookmarks() = bookmarkDao.getBookmark()


    fun getBookmark(id: Int) = bookmarkDao.getBookmark(id)

    fun bookmark(title: String, link: String, date: String) {
        bookmarkItem.value = BookmarkEntity(0, title, link, date)
    }

    fun insertBookmark() {
        viewModelScope.launch {
            bookmarkDao.insert(bookmarkEntity = bookmarkItem.value!!)
        }
    }

    fun updateBookmark() {
        viewModelScope.launch {
            bookmarkDao.insert(bookmarkEntity = bookmarkItem.value!!)
        }
    }

    /**
     * delete a single bookmark record
     * */
    fun deleteBookmark(bookmarkEntity: BookmarkEntity) {
        viewModelScope.launch {
            bookmarkDao.delete(bookmarkEntity = bookmarkEntity)
        }
    }

    /**
     * delete all bookmark records
     * */
    fun deleteBookmark() {
        viewModelScope.launch {
            bookmarkDao.delete()
        }
    }

    // home data functions
    fun getHome() = homeDao.getHome()


    fun getHome(id: Int) = homeDao.getHome(id)

    fun home(title: String, link: String, date: String) {
        homeItem.value = HomeEntity(0, title, link, date)
    }

    fun insertHome() {
        viewModelScope.launch {
            homeDao.insert(homeEntity = homeItem.value!!)
        }
    }

    fun updateHome() {
        viewModelScope.launch {
            homeDao.insert(homeEntity = homeItem.value!!)
        }
    }

    /**
     * delete a single home record
     * */
    fun deleteHome(homeEntity: HomeEntity) {
        viewModelScope.launch {
            homeDao.delete(homeEntity)
        }
    }

    /**
     * delete all home records
     * */
    fun deleteHome() {
        viewModelScope.launch {
            homeDao.delete()
        }
    }

    // History data functions
    fun getHistory() = historyDao.getHistory()


    fun getHistory(id: Int) = historyDao.getHistory(id)

    fun history(title: String, link: String, date: String) {
        historyItem.value = HistoryEntity(1, title, link, date)
    }

    fun insertHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            historyDao.insert(historyEntity = historyItem.value!!)
        }
    }

    fun updateHistory() {
        viewModelScope.launch {
            historyDao.insert(historyEntity = historyItem.value!!)
        }
    }

    /**
     * delete a single History record
     * */
    fun deleteHistory(historyEntity: HistoryEntity) {
        viewModelScope.launch {
            historyDao.delete(historyEntity)
        }
    }

    /**
     * delete all History records
     * */
    fun deleteHistory() {
        viewModelScope.launch {
            historyDao.delete()
        }
    }


    class BrowserViewModelFactory(
        private val context: Context,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BrowserViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BrowserViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

class MyBrowser : WebViewClient() {
    val TAG = "MyBrowser"
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//        view.loadUrl(url)
        Log.e(TAG, "title---->${view.title.toString()}\n url----->${view.url.toString()}")
        return true
    }
}