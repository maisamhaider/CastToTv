package com.example.casttotv.ui.activities.browser

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.asLiveData
import com.example.casttotv.R
import com.example.casttotv.adapter.FavoriteAdapter
import com.example.casttotv.database.entities.FavoritesEntity
import com.example.casttotv.databinding.ActivityFavoritesBinding
import com.example.casttotv.interfaces.OptionMenuListener
import com.example.casttotv.ui.activities.BaseActivity
import com.example.casttotv.utils.MySingleton.historyBookFavClose
import com.example.casttotv.viewmodel.BrowserViewModel

class FavoritesActivity : BaseActivity(), OptionMenuListener {
    private lateinit var _binding: ActivityFavoritesBinding
    private val binding get() = _binding

    private val browserViewModel: BrowserViewModel by viewModels {
        BrowserViewModel.BrowserViewModelFactory(this)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.favoriteActivity = this
        loadFavorites()
    }

    private fun loadFavorites() {
        val adapter = FavoriteAdapter(::onFavoriteClicked, this, this)
        binding.recyclerView.adapter = adapter
        browserViewModel.getFavorites().asLiveData().observe(this) {
            it?.let { bookmarks ->
                adapter.submitList(bookmarks)
            }
        }
    }

    private fun onFavoriteClicked(favorite: FavoritesEntity) {
        historyBookFavClose = favorite.link
        back()
    }

    fun back() {
        finish()
    }

    override fun <T> item(itemId: Int, dataClass: T) {
        val favoritesEntity: FavoritesEntity = dataClass as FavoritesEntity
        when (itemId) {
            R.id.item_edit -> {
                browserViewModel.editBottomSheet(favoritesEntity, true)
            }
            R.id.item_unfavorite -> {
                browserViewModel.deleteFavorites(favoritesEntity)
            }
            R.id.item_share -> {
                val string = "${getString(R.string.website)} ${favoritesEntity.title}\n" +
                        "${getString(R.string.url)} ${favoritesEntity.link}"

                browserViewModel.share(string)
            }
            else -> {
            }
        }
    }


}