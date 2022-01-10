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
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.*
import androidx.viewpager2.widget.ViewPager2
import com.example.casttotv.R
import com.example.casttotv.adapter.BrowserAdapter2
import com.example.casttotv.adapter.SearchEngineAdapter
import com.example.casttotv.database.entities.BookmarkEntity
import com.example.casttotv.database.entities.HistoryEntity
import com.example.casttotv.database.entities.HomeEntity
import com.example.casttotv.databinding.*
import com.example.casttotv.datasource.DataSource
import com.example.casttotv.models.Tabs
import com.example.casttotv.ui.activities.MainActivity
import com.example.casttotv.ui.activities.browser.fragments.BrowserBottomSheetFragment
import com.example.casttotv.utils.*
import com.example.casttotv.utils.MySingleton.createWebPrintJob
import com.example.casttotv.utils.MySingleton.funCopy
import com.example.casttotv.utils.MySingleton.shareWithText
import com.example.casttotv.utils.MySingleton.tabs
import com.example.casttotv.utils.MySingleton.toastShort
import com.example.casttotv.utils.Pref.getPrefs
import com.example.casttotv.utils.Pref.putPrefs
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
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
    private val homeDao = (cxt.applicationContext as AppApplication).database.homeDao()
    private val historyDao = (cxt.applicationContext as AppApplication).database.historyDao()

    private var container: FrameLayout? = null
    private val cookieManager: CookieManager = CookieManager.getInstance()!!

    private val _webView: MutableLiveData<WebView> = MutableLiveData()
    private var _tabs: MutableLiveData<List<Tabs>> = MutableLiveData()
    var liveTabs: LiveData<List<Tabs>> = _tabs

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
        return getEngine.contains(value)
    }

    fun getCurrentTab(): Tabs {
        return tabs.get(index = _currentTabIndex)
    }

    private fun getCurrentWebView(): WebView {
        return getCurrentTab().webView
    }

    fun initWebViewContainer(container: FrameLayout) {
        this.container = container
    }

    fun loadFragment(fragment: Fragment, fragmentManager: FragmentManager, container : Int) {
        fragmentManager.beginTransaction().replace(container, fragment, "").commit()
    }

    fun removeFrag(myFrag: Fragment, manager: FragmentManager) {
        val trans: FragmentTransaction = manager.beginTransaction()
        trans.remove(myFrag)
        trans.commit()
        manager.popBackStack()
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
        val engine = engines[getPrefs(SELECTED_ENGINE, "google").lowercase()]?.link
            ?: "https://www.google.com/search?q="
        if (getPrefs(FAVORITE_DEFAULT_SITE, "") == "") {
            _webView.value!!.loadUrl(engine)
        } else {
            _webView.value!!.loadUrl(getPrefs(FAVORITE_DEFAULT_SITE, ""))
        }
        _searchText.value = _webView.value!!.url
        tabs.add(Tabs(_webView.value!!))
        _tabs.value = tabs // set tabs to mutablelive list
        _currentTabIndex = tabs.size.minus(1)
    }


    fun switchToTab(tab: Int) {
        getCurrentWebView().visibility = View.GONE
        _currentTabIndex = tab
        getCurrentWebView().visibility = View.VISIBLE
        _searchText.value = getCurrentWebView().url
        getCurrentWebView().requestFocus()
    }


    fun closeCurrentTab() {

        if (_currentTabIndex != -1) {
            container!!.removeView(getCurrentWebView())
            getCurrentWebView().destroy()

            tabs.removeAt(_currentTabIndex)
            _tabs.value = tabs

            if (tabs.size != 0) {
                if (_currentTabIndex >= tabs.size) {
                    _currentTabIndex = tabs.size - 1
                }

                if (_currentTabIndex != -1) {
                    getCurrentWebView().visibility = View.VISIBLE
                    getCurrentWebView().requestFocus()
                    _searchText.value = getCurrentWebView().url
                }
            } else {
                if (getPrefs(BEHAVIOR_UI_REOPEN_LAST_TAB, false)) {
                    newTabWebView(WebView(cxt))
                } else {
                    exitDialog(cxt as Activity)
                    _currentTabIndex = -1
                }

            }
        }
    }


    fun closeTab(tab: Int) {
        if (tabs.contains(tabs[tab])) {
            container!!.removeView(getCurrentWebView())
            getCurrentWebView().destroy()

            tabs.removeAt(tab)
            _tabs.value = tabs

            if (tabs.size != 0) {
                if (_currentTabIndex >= tabs.size) {
                    _currentTabIndex = tabs.size - 1
                }

                if (_currentTabIndex != -1) {
                    getCurrentWebView().visibility = View.VISIBLE
                    getCurrentWebView().requestFocus()
                    _searchText.value = getCurrentWebView().url
                }
            } else {
                if (getPrefs(BEHAVIOR_UI_REOPEN_LAST_TAB, false)) {
                    newTabWebView(WebView(cxt))
                } else {
                    exitDialog(cxt as Activity)
                    _currentTabIndex = -1
                }

            }
        }
    }

    fun onBrowserExit() {
        tabs.clear()
        _tabs.value = tabs
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
        return (_webView.value != null && _webView.value!!.isFocused && _webView.value!!.canGoBack())
    }

    fun search(searchText: String) {
        if (_currentTabIndex == -1) {
            newTabWebView(WebView(cxt))
        }
        val engine = engines[getPrefs(SELECTED_ENGINE, "google").lowercase()]?.link
            ?: "https://www.google.com/search?q="

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
        newTabWebView(WebView(cxt))
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

    fun searchInBackground(searchText: String) {
        val web = WebView(cxt)
        web.loadUrl(searchText)
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
        tabs.add(Tabs(web))
        _tabs.value = tabs
    }

    fun searchReload(searchText: String) {
        if (_currentTabIndex == -1) {
            newTabWebView(WebView(cxt))
        }
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
            val bitmap = _webView.value!!.drawToBitmap()
            val time = SimpleDateFormat("EEE-dd-yyyy h:mm s a", Locale.getDefault()).format(Date())

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
                    1 -> {
                        deleteBookmark()
                    }
                    2 -> {
                        deleteHistory()
                    }
                }
                bottomSheet.dismiss()
            }
        }
        if (int == 1 || int == 2) {
            bottomSheet.show()
        }

    }

    fun deleteHistoryDialog(historyEntity: HistoryEntity) {
        val binding = DeleteLayoutBinding.inflate(LayoutInflater.from(cxt), null,
            false)
        val bottomSheet = BottomSheetDialog(cxt)
        bottomSheet.setContentView(binding.root)
        bottomSheet.create()
        binding.apply {
            textviewDelete.setOnClickListener {
                deleteHistory(historyEntity)
                bottomSheet.dismiss()
            }
        }
        bottomSheet.show()

    }

    fun deleteBookMarkDialog(bookmarkEntity: BookmarkEntity) {
        val binding = DeleteLayoutBinding.inflate(LayoutInflater.from(cxt), null,
            false)
        val bottomSheet = BottomSheetDialog(cxt)
        bottomSheet.setContentView(binding.root)
        bottomSheet.create()
        binding.apply {
            textviewDelete.setOnClickListener {
                deleteBookmark(bookmarkEntity)
                bottomSheet.dismiss()
            }
        }
        bottomSheet.show()

    }

    fun <T> editBottomSheet(input: T, isHistory: Boolean) {
        val binding = BottomSheetEditLayoutBinding.inflate(LayoutInflater.from(cxt), null,
            false)
        val bottomSheet = BottomSheetDialog(cxt)
        bottomSheet.setContentView(binding.root)
        bottomSheet.create()
        var hist: HistoryEntity? = null
        var book: BookmarkEntity? = null
        binding.apply {
            if (isHistory) {
                hist = (input as HistoryEntity)
                textviewEdit.visibility = View.GONE
                appCompatImageView4.visibility = View.GONE
            } else {
                book = (input as BookmarkEntity)
                edittextTitle.setText(book!!.title)
                edittextUrl.setText(book!!.link)
            }
            textviewNewTabForeground.setOnClickListener {
                when (isHistory) {
                    true -> {
                        searchFromHistory(hist!!.link)
                    }
                    false -> {
                        searchFromHistory(book!!.link)
                    }
                }
                bottomSheet.dismiss()
            }
            textviewNewTabBackground.setOnClickListener {
                when (isHistory) {
                    true -> {
                        searchInBackground(hist!!.link)
                    }
                    false -> {
                        searchInBackground(book!!.link)
                    }
                }
            }
            textviewEdit.setOnClickListener {
                clEdit.visibility = View.VISIBLE
                clButtons.visibility = View.GONE
            }
            textviewSaveAsFavorite.setOnClickListener {
                when (isHistory) {
                    true -> {
                        putPrefs(FAVORITE_DEFAULT_SITE, hist!!.link)
                    }
                    false -> {
                        putPrefs(FAVORITE_DEFAULT_SITE, book!!.link)
                    }
                }
                bottomSheet.dismiss()
            }
            textviewDelete.setOnClickListener {
                when (isHistory) {
                    true -> {
                        deleteHistoryDialog(hist!!)
                    }
                    false -> {
                        deleteBookMarkDialog(book!!)
                    }
                }
                bottomSheet.dismiss()
            }

            textviewUpdate.setOnClickListener {
                bookmarkItem.value =
                    BookmarkEntity(id = book!!.id, title = edittextTitle.text.toString(),
                        link = edittextUrl.text.toString(), date = Date().time.toString())
                updateBookmark()
                bottomSheet.dismiss()
            }
        }
        bottomSheet.show()

    }

    fun showBottomSheet(fragmentManager: FragmentManager, bottomSheet: BottomSheetDialogFragment) {
        bottomSheet.show(fragmentManager, null)
    }

    fun cancelBottomSheet(
        fragmentManager: FragmentManager,
        bottomSheet: BottomSheetDialogFragment,
    ) {
        if (bottomSheet.isVisible) {
            bottomSheet.dismiss()
        }
    }

    fun showBottomSheet(fragmentActivity: FragmentActivity) {

        // on below line we are creating a new bottom sheet dialog.
        val dialog = BottomSheetDialog(cxt)
        // on below line we are inflating a layout file which we have created.

        val bottomBinding =
            LayoutInflater.from(cxt)
                .inflate(R.layout.bottom_sheet_dialog, null, false)


        // closing of dialog box when clicking on the screen.
        dialog.setCancelable(true)

        // on below line we are setting
        // content view to our view.
        dialog.setContentView(bottomBinding)

        val viewpager2 =
            bottomBinding.findViewById<ViewPager2>(R.id.viewpager_2)
        val tabLayout = bottomBinding.findViewById<TabLayout>(com.example.casttotv.R.id.tabLayout)

        val adapter = BrowserAdapter2(fragmentActivity)
        viewpager2.adapter = adapter

        TabLayoutMediator(tabLayout, viewpager2) { tab, position ->
            when (position) {
                0 -> {
                    tab.icon = ContextCompat.getDrawable(cxt, R.drawable.ic_tab)
                }
                1 -> {
                    tab.icon = ContextCompat.getDrawable(cxt, R.drawable.ic_share)
                }
                2 -> {
                    tab.icon = ContextCompat.getDrawable(cxt, R.drawable.ic_save)
                }
                3 -> {
                    tab.icon = ContextCompat.getDrawable(cxt, R.drawable.ic_more_vert)
                }
            }
        }.attach()
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    0 -> {
                        viewpager2.currentItem = 0
                    }
                    1 -> {
                        viewpager2.currentItem = 1
                    }
                    2 -> {
                        viewpager2.currentItem = 2
                    }
                    3 -> {
                        viewpager2.currentItem = 3
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        dialog.show()
    }

    fun showBottomSheetTabPreview(fragmentManager: FragmentManager) {
        BrowserBottomSheetFragment().show(fragmentManager, BrowserBottomSheetFragment.TAG)
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

//    fun closeCurrentTabDialog() {
//        if (getPrefs(BEHAVIOR_UI_CONFIRM_TAB_CLOSE, false)) {
//            val binding =
//                LayoutTitleBodyPosAndNegButtonBinding.inflate(LayoutInflater.from(cxt), null,
//                    false)
//            val builder = AlertDialog.Builder(cxt)
//            builder.setView(binding.root)
//            val dialog = builder.create()
//
//            binding.apply {
//                textviewTitle.text = "Tab Exit"
//                textviewBody.text = "Do you want to close"
//                textviewPositive.text = "Yes"
//                textviewNegative.text = "No"
//                textviewPositive.setOnClickListener {
//                    closeCurrentTab()
//                    dialog.dismiss()
//                }
//                textviewNegative.setOnClickListener { dialog.dismiss() }
//            }
//
//            dialog.show()
//        } else {
//            closeCurrentTab()
//        }
//    }

    fun closTabDialog(tab: Int) {
        if (getPrefs(BEHAVIOR_UI_CONFIRM_TAB_CLOSE, false)) {
            val binding =
                LayoutTitleBodyPosAndNegButtonBinding.inflate(LayoutInflater.from(cxt), null,
                    false)
            val builder = AlertDialog.Builder(cxt)
            builder.setView(binding.root)
            val dialog = builder.create()

            binding.apply {
                textviewTitle.text = "Tab Exit"
                textviewBody.text = "Do you want to close"
                textviewPositiveText.text = "Yes"
                textviewNegativeText.text = "No"
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