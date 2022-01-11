package com.example.casttotv.ui.activities.browser

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import com.example.casttotv.adapter.BookmarkAdapter
import com.example.casttotv.database.entities.BookmarkEntity
import com.example.casttotv.databinding.ActivityBookmarkBinding
import com.example.casttotv.utils.MySingleton.toastShort
import com.example.casttotv.viewmodel.BrowserViewModel

class BookmarkActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityBookmarkBinding
    private val binding get() = _binding

    private val browserViewModel: BrowserViewModel by viewModels {
        BrowserViewModel.BrowserViewModelFactory(this)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bookmarks = this
        loadBookmark()
    }

    private fun loadBookmark() {
        val adapter = BookmarkAdapter(::onBookmarkClicked, this)
        binding.recyclerView.adapter = adapter
        browserViewModel.getBookmarks().asLiveData().observe(this) {
            it?.let { bookmarks ->
                adapter.submitList(bookmarks)
            }
        }
    }

    private fun onBookmarkClicked(bookmark: BookmarkEntity, longClick: Boolean) {

        if (longClick) {
//            browserViewModel.deleteBookMarkDialog(bookmark)
            browserViewModel.editBottomSheet(bookmark, false)
        } else {
            toastShort(bookmark.link)
//            startActivity(Intent(this,))
//            browserViewModel.searchFromHistory(bookmark.link)
//            this.dismiss()
        }
    }

    fun back() {
        finish()
    }
}