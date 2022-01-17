package com.example.casttotv.ui.activities.browser

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import com.example.casttotv.R
import com.example.casttotv.adapter.BookmarkAdapter
import com.example.casttotv.database.entities.BookmarkEntity
import com.example.casttotv.databinding.ActivityBookmarkBinding
import com.example.casttotv.interfaces.OptionMenuListener
import com.example.casttotv.ui.activities.browser.frags.BrowserHomeFragment
import com.example.casttotv.utils.MySingleton.historyBookFavClose
import com.example.casttotv.viewmodel.BrowserViewModel

class BookmarkActivity : AppCompatActivity(), OptionMenuListener {

    private lateinit var _binding: ActivityBookmarkBinding
    private val binding get() = _binding

    private val browserViewModel: BrowserViewModel by viewModels {
        BrowserViewModel.BrowserViewModelFactory(this)

    }
    val browHome = BrowserHomeFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bookmarks = this
        loadBookmark()
    }

    private fun loadBookmark() {
        val adapter = BookmarkAdapter(::onBookmarkClicked, this, this)
        binding.recyclerView.adapter = adapter
        browserViewModel.getBookmarks().asLiveData().observe(this) {
            it?.let { bookmarks ->
                adapter.submitList(bookmarks)
            }
        }
    }

    private fun onBookmarkClicked(bookmark: BookmarkEntity) {
        historyBookFavClose = bookmark.link
        back()
    }

    fun back() {
        finish()
    }

    override fun <T> item(itemId: Int, dataClass: T) {
        val bookmarkEntity: BookmarkEntity = dataClass as BookmarkEntity
        when (itemId) {
            R.id.item_edit -> {
                browserViewModel.editBottomSheet(bookmarkEntity, false)
            }
            R.id.item_delete -> {
                browserViewModel.deleteBookmark(bookmarkEntity)
            }
            R.id.item_share -> {
                val string = "Website: ${bookmarkEntity.title}\nUrl: ${bookmarkEntity.link}"
                browserViewModel.share(string)
            }
            else -> {
            }
        }
    }


}