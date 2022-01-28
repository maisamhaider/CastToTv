package com.example.casttotv.viewmodel

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.view.drawToBitmap
import androidx.core.view.isNotEmpty
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import com.example.casttotv.R
import com.example.casttotv.adapter.SearchEngineAdapter
import com.example.casttotv.database.entities.BookmarkEntity
import com.example.casttotv.database.entities.FavoritesEntity
import com.example.casttotv.database.entities.HistoryEntity
import com.example.casttotv.database.entities.HomeEntity
import com.example.casttotv.databinding.*
import com.example.casttotv.dataclasses.Tabs
import com.example.casttotv.datasource.DataSource
import com.example.casttotv.ui.activities.MainActivity
import com.example.casttotv.utils.*
import com.example.casttotv.utils.MySingleton.funCopy
import com.example.casttotv.utils.MySingleton.shareWithText
import com.example.casttotv.utils.MySingleton.toastShort
import com.example.casttotv.utils.Pref.getPrefs
import com.example.casttotv.utils.Pref.putPrefs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class BrowserViewModel(private var cxt: Context) : ViewModel() {

    private val TAG = "BrowserViewModel"
    private val bookmarkDao = (cxt.applicationContext as AppApplication).database.bookmarkDao()
    private val favoritesDao = (cxt.applicationContext as AppApplication).database.favoriteDao()
    private val homeDao = (cxt.applicationContext as AppApplication).database.homeDao()
    private val historyDao = (cxt.applicationContext as AppApplication).database.historyDao()

    private var container: MutableLiveData<FrameLayout> = MutableLiveData(null)

    private val _showTabFragment: MutableLiveData<Boolean> = MutableLiveData(false)
    val showTabFragment: LiveData<Boolean> = _showTabFragment

    val checkDefaultSite: MutableLiveData<Boolean> = MutableLiveData(false)

    private val _showBrowserHome: MutableLiveData<Boolean> = MutableLiveData(true)
    val showBrowserHome: LiveData<Boolean> = _showBrowserHome

    private val _mainActivityBackPress: MutableLiveData<Boolean> = MutableLiveData(false)
    val mainActivityBackPress: LiveData<Boolean> = _mainActivityBackPress


    private val _webView: MutableLiveData<ObservableWebView> = MutableLiveData()
    val webView: MutableLiveData<ObservableWebView> get() = _webView
    private var _tabs: MutableLiveData<List<Tabs>> = MutableLiveData()
    var liveTabs: LiveData<List<Tabs>> = _tabs
    var tabs: MutableLiveData<MutableList<Tabs>> = MutableLiveData(mutableListOf())

    private val _searchText: MutableLiveData<String> = MutableLiveData("")
    val searchText: LiveData<String> = _searchText
    var _currentTabIndex: MutableLiveData<Int> = MutableLiveData(0)


    private var bookmarkItem: MutableLiveData<BookmarkEntity> = MutableLiveData()
    private var favoriteItem: MutableLiveData<FavoritesEntity> = MutableLiveData()
    private var homeItem: MutableLiveData<HomeEntity> = MutableLiveData()
    private var historyItem: MutableLiveData<HistoryEntity> = MutableLiveData()


    val engines = DataSource().engines // search engines urls

    fun putPrefs(key: String, value: Boolean) = cxt.putPrefs(key, value)
    fun putPrefs(key: String, value: String) = cxt.putPrefs(key, value)

    fun getPrefs(key: String, value: Boolean) = cxt.getPrefs(key, value)
    fun getPrefs(key: String, value: String) = cxt.getPrefs(key, value)


    fun getCurrentTab(): Tabs {
        return tabs.value!!.get(index = _currentTabIndex.value!!)
    }

    fun getCurrentTab(int: Int): Tabs {
        return tabs.value!!.get(index = int)
    }

    private fun getCurrentWebView(): WebView {
        return getCurrentTab().webView
    }

    fun initWebViewContainer(container: FrameLayout) {
        this.container.value = container
    }

    fun reAddWebViewsToContainer() {
        if (!tabs.value.isNullOrEmpty()) {
            if (!container.value!!.isNotEmpty()) {
                for (i in 0 until tabs.value!!.size) {
                    searchAfterThemeChanged(tabs.value!![i].webView.url.toString())
                }
                for (i in 0 until tabs.value!!.size / 2) {
                    closeTab2(0)
                }
            }
        }
    }

    fun loadFragment(fragment: Fragment, fragmentManager: FragmentManager, container: Int) {
        fragmentManager.beginTransaction().replace(container, fragment, "").commit()
    }

    fun clickTabLayout() {
        _showTabFragment.value = !_showTabFragment.value!!
    }

    fun tabFragmentIsShowing() = _showTabFragment.value!!

    fun date() = Date().time.toString()

    @SuppressLint("SimpleDateFormat")
    fun day(): String {
        val sFormat = SimpleDateFormat("dd.MM.yyyy")
        sFormat.isLenient = false

        return sFormat.format(Date())
    }

    fun showBrowserHome(value: Boolean) {
        _showBrowserHome.value = value
    }

    fun mainBackPress(value: Boolean) {
        _mainActivityBackPress.value = value
    }

    fun setSearchText(value: String) {
        _searchText.value = value
    }


    /**
     * set web view properties
     */
    @SuppressLint("SetJavaScriptEnabled")
    fun newTabWebView(wv: ObservableWebView) {
        _webView.value = wv
        container.value?.addView(wv)

        showBrowserHome(false)

        _webView.value!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                fourData(url, url, date(), day())

                Log.e(TAG, "title---->${view.title.toString()}\n url----->${view.url.toString()}")
                setSearchText(url)
                if (getPrefs(START_CONTROL_HISTORY, true)) {
                    insertHistory()
                }
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
            }
        }
        val engine = engines[getPrefs(SELECTED_ENGINE, "google").lowercase()]?.link
            ?: "https://www.google.com/search?q="
        _webView.value!!.loadUrl(engine)

        setSearchText(_webView.value!!.url.toString())
        tabs.value?.add(Tabs(_webView.value!!))
        _tabs.value = tabs.value // set tabs to mutablelive list
        _currentTabIndex.value = tabs.value?.size?.minus(1)
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun newTabWebView2(wv: ObservableWebView) {
        _webView.value = wv
        container.value?.addView(wv)

        _webView.value!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                fourData(url, url, date(), day())

                Log.e(TAG, "title---->${view.title.toString()}\n url----->${view.url.toString()}")
                setSearchText(url)
                if (getPrefs(START_CONTROL_HISTORY, true)) {
                    insertHistory()
                }
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
            }
        }
        val engine = engines[getPrefs(SELECTED_ENGINE, "google").lowercase()]?.link
            ?: "https://www.google.com/search?q="
        _webView.value!!.loadUrl(engine)

        setSearchText(_webView.value!!.url.toString())
        tabs.value?.add(Tabs(_webView.value!!))
        _tabs.value = tabs.value // set tabs to mutablelive list
        _currentTabIndex.value = tabs.value?.size?.minus(1)
    }


    fun switchToTab(tab: Int) {
        getCurrentWebView().visibility = View.GONE
        _currentTabIndex.value = tab
        _tabs.value?.let { _webView.value = it[_currentTabIndex.value!!].webView }
        getCurrentWebView().visibility = View.VISIBLE
        setSearchText(getCurrentWebView().url.toString())
        getCurrentWebView().requestFocus()
        showBrowserHome(false)
    }

    private fun closeTab(tab: Int) {
        if (tabs.value!!.contains(tabs.value!![tab])) {
            container.value?.removeView(getCurrentTab(tab).webView)
            getCurrentTab(tab).webView.destroy()

            tabs.value?.removeAt(tab)
            _tabs.value = tabs.value!!

            if (tabs.value!!.size != 0) {
                if (_currentTabIndex.value!! >= tabs.value!!.size) {
                    _currentTabIndex.value = tabs.value!!.size - 1
                }

                if (_currentTabIndex.value != -1) {
                    getCurrentWebView().visibility = View.VISIBLE
                    getCurrentWebView().requestFocus()
                    setSearchText(getCurrentWebView().url.toString())
                }
            } else {

                if (getPrefs(BEHAVIOR_UI_REOPEN_LAST_TAB, false)) {
                    newTabWebView(ObservableWebView(cxt))
                } else {
                    showBrowserHome(true)
                    _currentTabIndex.value = -1
                }

            }
        }
    }

    private fun closeTab2(tab: Int) {
        if (tabs.value!!.contains(tabs.value!![tab])) {
            container.value?.removeView(getCurrentTab(tab).webView)
            getCurrentTab(tab).webView.destroy()

            tabs.value?.removeAt(tab)
            _tabs.value = tabs.value!!

            if (tabs.value!!.size != 0) {
                if (_currentTabIndex.value!! >= tabs.value!!.size) {
                    _currentTabIndex.value = tabs.value!!.size - 1
                }
            }
            if (_currentTabIndex.value != -1) {
                getCurrentWebView().visibility = View.VISIBLE
                getCurrentWebView().requestFocus()
                setSearchText(getCurrentWebView().url.toString())
            }
        }
    }

    private fun onBrowserExit() {
        tabs.value!!.clear()
        _tabs.value = tabs.value!!
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
        _webView.value?.clearCookies()
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
        return (_webView.value != null && _webView.value!!.isFocused && _webView.value!!.canGoBack())
    }

    fun switchToTabBack() {
        if (_tabs.value != null &&
            _currentTabIndex.value!! >= 1
        ) {
            switchToTab(_currentTabIndex.value!! - 1)
        } else {
            showBrowserHome(true)
        }
    }

    fun switchToTabForward() {
        if (_tabs.value != null &&
            _currentTabIndex.value!! < _tabs.value!!.size - 1
        ) {
            switchToTab(_currentTabIndex.value!! + 1)
            showBrowserHome(false)
        } else if (_tabs.value != null && _currentTabIndex.value!! == 0) {
            switchToTab(_currentTabIndex.value!!)
            showBrowserHome(false)
        }
    }

    fun search(searchText: String) {

        val engine = engines[getPrefs(SELECTED_ENGINE, "google").lowercase()]?.link
            ?: "https://www.google.com/search?q="

        val url = when {
            engine.isEmpty() -> {
                "https://www.google.com/search?q=$searchText"
            }
            searchText.contains(engine) -> {
                searchText
            }
            else -> {
                engine + searchText
            }
        }
        newTabWebView(ObservableWebView(cxt))

        _webView.value!!.loadUrl(url)

        fourData(searchText, url, date(), day())

        Log.e(TAG, "title---->$searchText\n url----->${_webView.value!!.url.toString()}")
        setSearchText(url)

        if (getPrefs(START_CONTROL_HISTORY, true)) {
            insertHistory()
        }
        showBrowserHome(false)
    }

    fun searchAfterThemeChanged(searchText: String) {
        newTabWebView2(ObservableWebView(cxt))

        _webView.value!!.loadUrl(searchText)
    }

    fun searchFromHistory(searchText: String) {
        newTabWebView(ObservableWebView(cxt))
        _webView.value!!.loadUrl(searchText)

        history(searchText, _webView.value!!.url.toString(), date(), day())
        bookmark(searchText, _webView.value!!.url.toString(), date())
        favorite(searchText, _webView.value!!.url.toString(), date())
        home(searchText, _webView.value!!.url.toString(), date())

        Log.e(TAG, "title---->$searchText\n url----->${_webView.value!!.url.toString()}")
        setSearchText(searchText)
        if (getPrefs(START_CONTROL_HISTORY, true)) {
            if (searchText.isNotBlank()) {
                insertHistory()
            }
        }
        showBrowserHome(false)
    }


    fun searchReload(searchText: String) {
        if (_currentTabIndex.value!! == -1) {
            newTabWebView(ObservableWebView(cxt))
        }
        showBrowserHome(false)
        _webView.value!!.loadUrl(searchText)
        fourData(searchText, _webView.value!!.url.toString(), date(), day())

        Log.e(TAG, "title---->$searchText\n url----->${_webView.value!!.url.toString()}")
        setSearchText(searchText)
        if (getPrefs(START_CONTROL_HISTORY, true)) {
            if (searchText.isNotBlank()) {
                insertHistory()
            }
        }

    }

    fun isSearchValid(searchText: String): Boolean {
        return searchText.isNotBlank()
    }

    fun webViewVisible(): Boolean {
        return container.value != null && container.value?.isVisible!!
    }

    fun shareLink() {
        if (_webView.value == null || _webView.value?.url.toString() == "null" || _webView.value?.url.toString()
                .isEmpty()
        ) {
            cxt.toastShort(cxt.getString(R.string.no_site_loaded))
        } else {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, _webView.value!!.url.toString())
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, "share with")
            cxt.startActivity(shareIntent)
        }


    }

    fun share(string: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, string)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "share with")
        cxt.startActivity(shareIntent)
    }

    fun copyToClipBoard() {
        if (_webView.value == null || _webView.value?.url.toString() == "null" || _webView.value?.url.toString()
                .isEmpty()
        ) {
            cxt.toastShort(cxt.getString(R.string.no_site_loaded))
        } else {
            cxt.funCopy(_webView.value!!.url.toString())
            cxt.toastShort(cxt.getString(R.string.copied))
        }

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


    fun saveScreenShot(isShare: Boolean): String {
        return try {
            if (webViewVisible()) {
                val bitmap = _webView.value!!.drawToBitmap()
                val time =
                    SimpleDateFormat("EEE-dd-yyyy h:mm s a", Locale.getDefault()).format(Date())

                val dir = if (isShare) {
                    cxt.filesDir
                } else {
                    cxt.getExternalFilesDir("Download")
                }
                val file = File(dir, "screenshot$time.png")
                val out = FileOutputStream(file)
                bitmap.setHasAlpha(true)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
                out.close()
                file.absolutePath
            } else {
                "error"
            }

        } catch (e: IOException) {
            e.printStackTrace()
            "error"
        }

    }

    fun shareSS(path: String) {
        val binding = LayoutBodyPosAndNegButtonBinding.inflate(LayoutInflater.from(cxt), null,
            false)
        val bottomSheetDialog = BottomSheetDialog(cxt)
        bottomSheetDialog.setContentView(binding.root)

        binding.apply {
            textviewBody.text = cxt.getText(R.string.download_complete_share_image)
            textviewPositive.text = cxt.getText(R.string.yes_2)
            textviewNegative.text = cxt.getText(R.string.no_2)
            textviewPositive.setOnClickListener {
                bottomSheetDialog.dismiss()
                cxt.shareWithText(path)
            }
            textviewNegative.setOnClickListener { bottomSheetDialog.dismiss() }
        }
        bottomSheetDialog.create()
        bottomSheetDialog.show()
    }


    fun <T> editBottomSheet(input: T, fav: Boolean) {
        val binding = BottomSheetEditLayoutBinding.inflate(LayoutInflater.from(cxt), null,
            false)
        val bottomSheet = BottomSheetDialog(cxt)
        bottomSheet.setContentView(binding.root)
        bottomSheet.create()
        var favEntity: FavoritesEntity? = null
        var book: BookmarkEntity? = null
        binding.apply {
            if (fav) {
                favEntity = (input as FavoritesEntity)
                edittextTitle.setText(favEntity!!.title)
                edittextUrl.setText(favEntity!!.link)
            } else {
                book = (input as BookmarkEntity)
                edittextTitle.setText(book!!.title)
                edittextUrl.setText(book!!.link)
            }

            textviewUpdate.setOnClickListener {
                if (fav) {
                    favoriteItem.value =
                        FavoritesEntity(id = favEntity!!.id, title = edittextTitle.text.toString(),
                            link = edittextUrl.text.toString(), date = Date().time.toString())
                    updateFavorites()
                } else {
                    bookmarkItem.value =
                        BookmarkEntity(id = book!!.id, title = edittextTitle.text.toString(),
                            link = edittextUrl.text.toString(), date = Date().time.toString())
                    updateBookmark()
                }

                bottomSheet.dismiss()
            }
        }
        bottomSheet.show()

    }

    fun showBottomSheet(fragmentManager: FragmentManager, bottomSheet: BottomSheetDialogFragment) {
        bottomSheet.show(fragmentManager, null)
    }


    fun engineDialog() {
        val binding = LayoutSearchEnginesBinding.inflate(LayoutInflater.from(cxt), null,
            false)
        val adapter = SearchEngineAdapter(cxt)
        val builder = AlertDialog.Builder(cxt)
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        binding.apply {
            browserVM = this@BrowserViewModel
            mButtonCancel.setOnClickListener { dialog.dismiss() }
            recyclerView.adapter = adapter
            adapter.submitList(DataSource().enginesList)

        }
        dialog.show()
    }

    fun closeDialog(tab: Int) {
        if (getPrefs(BEHAVIOR_UI_CONFIRM_TAB_CLOSE, false)) {
            val binding =
                LayoutTitleBodyPosAndNegButtonBinding.inflate(LayoutInflater.from(cxt), null,
                    false)
            val builder = AlertDialog.Builder(cxt)
            builder.setView(binding.root)
            val dialog = builder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            binding.apply {
                textviewTitle.text = cxt.getString(R.string.tab_exit)
                textviewBody.text = cxt.getString(R.string.do_you_want_to_close)
                textviewPositiveText.text = cxt.getString(R.string.yes_2)
                textviewNegativeText.text = cxt.getString(R.string.no_2)
                textviewPositiveClick.setOnClickListener {
                    closeTab(tab)
                    dialog.dismiss()
                }
                textviewNegativeClick.setOnClickListener { dialog.dismiss() }
            }
            dialog.show()
        } else {
            closeTab(tab)
        }
    }

    fun exitDialog(activity: Activity) {
        val mainActivity = activity as MainActivity

        if (getPrefs(BEHAVIOR_UI_CONFIRM_BROWSER_EXIT, false)) {
            val binding =
                LayoutTitleBodyPosAndNegButtonBinding.inflate(LayoutInflater.from(cxt), null,
                    false)
            val builder = AlertDialog.Builder(cxt)
            builder.setView(binding.root)
            val dialog = builder.create()
            dialog.window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            binding.apply {
                textviewTitle.text = activity.getString(R.string.exit_browser)
                textviewBody.text = activity.getString(R.string.browser_exit_body)
                textviewPositiveText.text = activity.getString(R.string.yes_2)
                textviewNegativeText.text = activity.getString(R.string.no_2)
                textviewPositiveClick.setOnClickListener {
                    dialog.dismiss()
                    onBrowserExit()
                    mainActivity.fromBrowserBack()
                }
                textviewNegativeClick.setOnClickListener { dialog.dismiss() }
            }
            dialog.show()
        } else {
            onBrowserExit()
            mainActivity.fromBrowserBack()
        }

    }

    fun inputDialog(title: String, prefKey: String) {
        val binding = InputDialogLayoutBinding.inflate(LayoutInflater.from(cxt), null,
            false)
        val builder = AlertDialog.Builder(cxt)
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        binding.apply {
            textviewTitle.text = title
            textviewOkText.text = cxt.getString(R.string.ok)
            textviewCancelText.text = cxt.getString(R.string.cancel)
            textInputEditText.setText(cxt.getPrefs(prefKey, ""))

            textviewOkClick.setOnClickListener {
                val input = textInputEditText.text.toString()
                if (TextUtils.isEmpty(input)) {
                    if (prefKey == CUSTOM_SEARCH_ENGINE) {
                        cxt.putPrefs(SELECTED_ENGINE, "")
                    } else
                        cxt.putPrefs(prefKey, "")
                } else {
                    if (prefKey == CUSTOM_SEARCH_ENGINE) {
                        cxt.putPrefs(SELECTED_ENGINE, input)
                    } else
                        cxt.putPrefs(prefKey, input)
                }
                dialog.dismiss()
            }
            textviewCancelClick.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    fun inputHomeDialog(title: String) {
        val binding = InputDialogLayoutBinding.inflate(LayoutInflater.from(cxt), null,
            false)
        val builder = AlertDialog.Builder(cxt)
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        binding.apply {
            textviewTitle.text = title
            textviewOkText.text = cxt.getString(R.string.add)
            textviewCancelText.text = cxt.getString(R.string.cancel)

            textviewOkClick.setOnClickListener {
                val input = textInputEditText.text.toString()
                if (TextUtils.isEmpty(input)) {
                    cxt.toastShort(cxt.getString(R.string.no_input_provided))
                } else {
                    home(input, input, date())
                    insertHome()
                }
                dialog.dismiss()
            }
            textviewCancelClick.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    fun deleteDialog(id: Int) {
        val binding = DeleteDialogLayoutBinding.inflate(LayoutInflater.from(cxt), null,
            false)
        val builder = AlertDialog.Builder(cxt)
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        binding.apply {
            textviewOkText.text = cxt.getString(R.string.delete)
            textviewCancelText.text = cxt.getString(R.string.cancel)
            textviewOkClick.setOnClickListener {
                deleteHome(id)
                dialog.dismiss()
            }
            textviewCancelClick.setOnClickListener {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    fun fourData(searchText: String, url: String, date: String, day: String) {
        history(searchText, url, date, day)
        bookmark(searchText, url, date)
        favorite(searchText, url, date)
        home(searchText, url, date)
    }

    // Database Area
    fun getBookmarks() = bookmarkDao.getBookmark()

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

    // favorites record functions

    fun getFavorites() = favoritesDao.getFavorites()

    fun favorite(title: String, link: String, date: String) {
        favoriteItem.value = FavoritesEntity(title = title, link = link, date = date)
    }

    fun insertFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            if (favoriteItem.value != null) {
                favoritesDao.insert(favoritesEntity = favoriteItem.value!!)
            }
        }
    }

    fun updateFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            favoritesDao.update(favoritesEntity = favoriteItem.value!!)
        }
    }

    /**
     * delete a single favorites record
     * */
    fun deleteFavorites(favoriteItem: FavoritesEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            favoritesDao.delete(favoritesEntity = favoriteItem)
        }
    }

    // home data functions
    fun getHome(): LiveData<List<HomeEntity>> = homeDao.getHome().asLiveData()


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


    fun deleteHome(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            homeDao.delete(id)
        }
    }

    // History data functions
    fun getHistoryGroupBtDay() = historyDao.getDateMilli()

    fun history(title: String, link: String, date: String, day: String) {
        historyItem.value = HistoryEntity(title = title, link = link, date = date, day = day)
    }

    fun insertHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            historyDao.insert(historyEntity = historyItem.value!!)
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

    fun deleteHistory(from: Long, to: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            historyDao.delete(from, to)
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

