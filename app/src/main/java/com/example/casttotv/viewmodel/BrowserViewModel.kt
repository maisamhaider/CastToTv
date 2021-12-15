package com.example.casttotv.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.*
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.lifecycle.*
import com.example.casttotv.R
import com.example.casttotv.database.entities.BookmarkEntity
import com.example.casttotv.database.entities.HistoryEntity
import com.example.casttotv.database.entities.HomeEntity
import com.example.casttotv.databinding.*
import com.example.casttotv.datasource.DataSource
import com.example.casttotv.models.Tabs
import com.example.casttotv.models.TitleAndUrl
import com.example.casttotv.utils.*
import com.example.casttotv.utils.MySingleton.createWebPrintJob
import com.example.casttotv.utils.MySingleton.funCopy
import com.example.casttotv.utils.MySingleton.shareWithText
import com.example.casttotv.utils.MySingleton.tabs
import com.example.casttotv.utils.MySingleton.toastShort
import com.example.casttotv.utils.Pref.getPrefs
import com.example.casttotv.utils.Pref.putPrefs
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class BrowserViewModel(private var cxt: Context) : ViewModel() {

    private val TAG = "BrowserViewModel"
    private val bookmarkDao = (cxt.applicationContext as AppApplication).database.bookmarkDao()
    private val homeDao = (cxt.applicationContext as AppApplication).database.homeDao()
    private val historyDao = (cxt.applicationContext as AppApplication).database.historyDao()

    var container: FrameLayout? = null
    val cookieManager: CookieManager = CookieManager.getInstance()!!

    private val _webView: MutableLiveData<WebView> = MutableLiveData()

    private val _searchText: MutableLiveData<String> = MutableLiveData("")
    val searchText: LiveData<String> = _searchText
    private var _currentTabIndex = 0


    private var bookmarkItem: MutableLiveData<BookmarkEntity> = MutableLiveData()
    private var homeItem: MutableLiveData<HomeEntity> = MutableLiveData()
    private var historyItem: MutableLiveData<HistoryEntity> = MutableLiveData()


    val engines = DataSource().engines // search engines urls

    fun putPrefs(key: String, value: Boolean) = cxt.putPrefs(key, value)
    fun putPrefs(key: String, value: String) = cxt.putPrefs(key, value)

    fun getPrefs(key: String, value: Boolean) = cxt.getPrefs(key, value)
    fun getPrefs(key: String, value: String) = cxt.getPrefs(key, value)


    fun putEngine(value: String) {
        cxt.putPrefs(SELECTED_ENGINE, value)
    }

    fun checkSelectedEngine(value: String): Boolean {
        val getEngine = cxt.getPrefs(SELECTED_ENGINE, "")

        return getEngine == value
    }

    fun getCurrentTab(): Tabs {
        return tabs.get(index = _currentTabIndex )
    }

    private fun getCurrentWebView(): WebView {
        return getCurrentTab().webView
    }

    fun initWebViewContainer(container: FrameLayout) {
        this.container = container
    }

    /**
     * set web view properties
     */
    @SuppressLint("SetJavaScriptEnabled")
    fun newTabWebView(wv: WebView) {
        _webView.value = wv
        _webView.value!!.webViewClient = MyBrowser()
        _webView.value!!.settings.javaScriptEnabled = getPrefs(START_CONTROL_JAVASCRIPT, true)
        _webView.value!!.settings.loadsImagesAutomatically = getPrefs(START_CONTROL_IMAGE, true)
        _webView.value!!.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        _webView.value!!.settings.setGeolocationEnabled(getPrefs(START_CONTROL_LOCATION, false))
        container!!.addView(wv)
        if (getPrefs(START_CONTROL_COOKIES, false)) {
            cookieManager.setAcceptThirdPartyCookies(_webView.value!!, true)
        }
        _webView.value!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                val date = Date().time.toString()
                history(url, url, date)
                bookmark(url, url, date)
                home(url, url, date)

                Log.e(TAG, "title---->${view.title.toString()}\n url----->${view.url.toString()}")
                _searchText.value = url

                if (getPrefs(START_CONTROL_LOCATION, true)) {
                    insertHistory()
                }
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
            }
        }
        val engine = getPrefs(SELECTED_ENGINE, "https://www.google.com/")

        if (getPrefs(FAVORITE_DEFAULT_SITE, "") == "") {
            _webView.value!!.loadUrl(engine)
        } else {
            _webView.value!!.loadUrl(getPrefs(FAVORITE_DEFAULT_SITE, ""))
        }
        _searchText.value = _webView.value!!.url
        tabs.add(Tabs(_webView.value!!))
     }


    fun switchToTab(tab: Int) {
        getCurrentWebView().visibility = View.GONE
        _currentTabIndex = tab
        getCurrentWebView().visibility = View.VISIBLE
//        searchTextInput.setText(getCurrentWebView().url)
        getCurrentWebView().requestFocus()
    }


    fun closeCurrentTab() {
        if (getCurrentWebView().url != null && !getCurrentWebView().url.equals("")) {
            val titleAndUrl = TitleAndUrl()
            titleAndUrl.title = getCurrentWebView().title
            titleAndUrl.url = getCurrentWebView().url
        }
        container!!.removeView(getCurrentWebView())
        getCurrentWebView().destroy()
        tabs.removeAt(_currentTabIndex)
        if (_currentTabIndex >= tabs.size) {
            _currentTabIndex = tabs.size - 1
        }
        if (_currentTabIndex == -1) {
            // We just closed the last tab
            newTabWebView(_webView.value!!)
            _currentTabIndex = 0
        }
        getCurrentWebView().visibility = View.VISIBLE
//        searchTextInput.setText(getCurrentWebView().url)
//        setTabCountText(Tab.tabs.size())
        getCurrentWebView().requestFocus()
    }


    fun onBrowserExit() {
        tabs.clear()
        closeCurrentTab()
        if (getPrefs(DATA_CLEAR_APPLICATION_EXIT, false)) {
            if (getPrefs(DATA_CLEAR_CACHE, false)) {
                clearCache()
            }
            if (getPrefs(DATA_CLEAR_HISTORY, false)) {
                _webView.value!!.clearHistory()
                deleteHistory()
            }
            if (getPrefs(DATA_CLEAR_COOKIES, false)) {
                clearCookies()
            }
        }

    }

    private fun clearCookies() {
        cookieManager.removeAllCookies(null)
        cookieManager.flush()
    }

    private fun clearCache() {
        _webView.value!!.clearCache(true)
    }

    fun back() {
        if (_webView.value!!.isFocused && _webView.value!!.canGoBack()) {
            _webView.value!!.goBack()
        }
    }

    fun canGoBack(): Boolean {
        return (_webView.value!!.isFocused && _webView.value!!.canGoBack())
    }

    fun search(searchText: String) {
        val engine = getPrefs(SELECTED_ENGINE, "https://www.google.com/search?q=")

        val url = if (engine.isEmpty()) {
            "https://www.google.com/search?q=$searchText"
        } else {
            engine + searchText
        }

        _webView.value!!.loadUrl(url)

        val date = Date().time.toString()

        history(searchText, url, date)
        bookmark(searchText, url, date)
        home(searchText, url, date)

        Log.e(TAG, "title---->$searchText\n url----->${_webView.value!!.url.toString()}")
        _searchText.value = url
        if (getPrefs(START_CONTROL_LOCATION, true)) {
            insertHistory()
        }
    }

    fun searchFromHistory(searchText: String) {
        _webView.value!!.loadUrl(searchText)
        val date = Date().time.toString()

        history(searchText, _webView.value!!.url.toString(), date)
        bookmark(searchText, _webView.value!!.url.toString(), date)
        home(searchText, _webView.value!!.url.toString(), date)

        Log.e(TAG, "title---->$searchText\n url----->${_webView.value!!.url.toString()}")
        _searchText.value = searchText
        if (getPrefs(START_CONTROL_LOCATION, true)) {
            if (searchText.isNotBlank()) {
                insertHistory()
            }
        }
    }

    fun isSearchValid(searchText: String): Boolean {
        return searchText.isNotBlank()
    }

    fun print() {
        cxt.createWebPrintJob(_webView.value!!)
    }

    fun shareLink() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, _webView.value!!.url.toString())
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "share with")
        cxt.startActivity(shareIntent)
    }

    fun copyToClipBoard() {
        cxt.funCopy(_webView.value!!.url.toString())
    }

    fun openDownload() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.type = "*/*"
        cxt.startActivity(intent)
    }

    fun openFileManager() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.type = "*/*"
        cxt.startActivity(intent)
    }


    fun openWith() {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(_webView.value!!.url)
        cxt.startActivity(i)
    }

    fun openFavorite() {
        val url = getPrefs(FAVORITE_DEFAULT_SITE, "")
        searchFromHistory(url)
    }

    fun addToFavorite() {
        putPrefs(FAVORITE_DEFAULT_SITE, _webView.value!!.url.toString())
    }

    fun saveScreenShot(isShare: Boolean): String {

        return try {
//            val picture = webView.capturePicture()
            val bitmap = _webView.value!!.drawToBitmap()
            val time = SimpleDateFormat("EEE-dd-yyyy h:mm s a", Locale.getDefault()).format(Date())

            val dir = if (isShare) {
                cxt.filesDir
            } else {
                cxt.getExternalFilesDir("Download")
            }
            val file = File(dir, "screenshot$time.png")

//            val bitmap = Bitmap.createBitmap(picture.width,
//                picture.height, Bitmap.Config.ARGB_8888)
//            val c = Canvas(bitmap)
//
//            picture.draw(c)

            val out = FileOutputStream(file)
            bitmap.setHasAlpha(true)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            out.flush()
            out.close()
            file.absolutePath

        } catch (e: IOException) {
            e.printStackTrace()
            "error"
        }

    }

    fun openSS(path: String) {
        val binding =
            LayoutBodyPosAndNegButtonBinding.inflate(LayoutInflater.from(cxt), null,
                false)
        val bottomSheetDialog = BottomSheetDialog(cxt)
        bottomSheetDialog.setContentView(binding.root)

        binding.apply {
            textviewBody.text = "Download complete. Preview image"
            textviewPositive.text = "Yes"
            textviewNegative.text = "No"
            textviewPositive.setOnClickListener {
                bottomSheetDialog.dismiss()
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.type = "image/*"

                val photoURI = FileProvider.getUriForFile(cxt, "${cxt.packageName}.provider",
                    File(path)
                )
                intent.data = photoURI

                cxt.startActivity(Intent.createChooser(intent, "share with"))
                cxt.toastShort(path)

            }
            textviewNegative.setOnClickListener { bottomSheetDialog.dismiss() }
        }
        bottomSheetDialog.create()
        bottomSheetDialog.show()
    }

    fun shareSS(path: String) {
        val binding =
            LayoutBodyPosAndNegButtonBinding.inflate(LayoutInflater.from(cxt), null,
                false)
        val bottomSheetDialog = BottomSheetDialog(cxt)
        bottomSheetDialog.setContentView(binding.root)

        binding.apply {
            textviewBody.text = "Download complete. Share image"
            textviewPositive.text = "Yes"
            textviewNegative.text = "No"
            textviewPositive.setOnClickListener {
                bottomSheetDialog.dismiss()
                cxt.shareWithText(path)
                cxt.toastShort(path)

            }
            textviewNegative.setOnClickListener { bottomSheetDialog.dismiss() }
        }
        bottomSheetDialog.create()
        bottomSheetDialog.show()
    }


    fun deleteDialog(int: Int) {
        val binding = DeleteLayoutBinding.inflate(LayoutInflater.from(cxt), null,
            false)

        val bottomSheet = BottomSheetDialog(cxt)
        bottomSheet.setContentView(binding.root)
        bottomSheet.create()
        binding.apply {
            textviewDelete.setOnClickListener {
                when (int) {
                    2 -> {
                        deleteBookmark()
                    }
                    3 -> {
                        deleteHistory()
                    }
                }
                bottomSheet.dismiss()
            }
        }
        bottomSheet.show()

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
                onBrowserExit()
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
            textInputEditText.setText(cxt.getPrefs(prefKey, ""))

            textviewOk.setOnClickListener {
                val input = textInputEditText.text.toString()
                if (TextUtils.isEmpty(input)) {
                    cxt.putPrefs(prefKey, "")
                } else {
                    cxt.putPrefs(prefKey, input)
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
        bookmarkItem.value = BookmarkEntity(title = title, link = link, date = date)
    }

    fun insertBookmark() {
        viewModelScope.launch(Dispatchers.IO) {
            if (bookmarkItem.value != null) {
                bookmarkDao.insert(bookmarkEntity = bookmarkItem.value!!)
            }
        }
    }

    fun updateBookmark() {
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkDao.update(bookmarkEntity = bookmarkItem.value!!)
        }
    }

    /**
     * delete a single bookmark record
     * */
    fun deleteBookmark(bookmarkEntity: BookmarkEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkDao.delete(bookmarkEntity = bookmarkEntity)
        }
    }

    /**
     * delete all bookmark records
     * */
    fun deleteBookmark() {
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkDao.delete()
        }
    }

    // home data functions
    fun getHome() = homeDao.getHome()


    fun getHome(id: Int) = homeDao.getHome(id)

    fun home(title: String, link: String, date: String) {
        homeItem.value = HomeEntity(title = title, link = link, date = date)
    }

    fun insertHome() {
        viewModelScope.launch(Dispatchers.IO) {
            if (homeItem.value != null) {
                homeDao.insert(homeEntity = homeItem.value!!)
            }
        }
    }

    fun updateHome() {
        viewModelScope.launch(Dispatchers.IO) {
            homeDao.update(homeEntity = homeItem.value!!)
        }
    }

    /**
     * delete a single home record
     * */
    fun deleteHome(homeEntity: HomeEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            homeDao.delete(homeEntity)
        }
    }

    /**
     * delete all home records
     * */
    fun deleteHome() {
        viewModelScope.launch(Dispatchers.IO) {
            homeDao.delete()
        }
    }

    // History data functions
    fun getHistory() = historyDao.getHistory()


    fun getHistory(id: Int) = historyDao.getHistory(id)

    fun history(title: String, link: String, date: String) {
        historyItem.value = HistoryEntity(title = title, link = link, date = date)
    }

    fun insertHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            historyDao.insert(historyEntity = historyItem.value!!)
        }
    }

    fun updateHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            historyDao.update(historyEntity = historyItem.value!!)
        }
    }

    /**
     * delete a single History record
     * */
    fun deleteHistory(historyEntity: HistoryEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            historyDao.delete(historyEntity)
        }
    }

    /**
     * delete all History records
     * */
    fun deleteHistory() {
        viewModelScope.launch(Dispatchers.IO) {
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