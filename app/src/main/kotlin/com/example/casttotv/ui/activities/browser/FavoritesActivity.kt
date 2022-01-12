package com.example.casttotv.ui.activities.browser

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import com.example.casttotv.R
import com.example.casttotv.adapter.FavoriteAdapter
import com.example.casttotv.database.entities.FavoritesEntity
import com.example.casttotv.databinding.ActivityFavoritesBinding
import com.example.casttotv.interfaces.OptionMenuListener
import com.example.casttotv.utils.MySingleton.toastShort
import com.example.casttotv.viewmodel.BrowserViewModel

class FavoritesActivity : AppCompatActivity(), OptionMenuListener {
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

    private fun onFavoriteClicked(favorite: FavoritesEntity, longClick: Boolean) {

        if (longClick) {
//            browserViewModel.deleteBookMarkDialog(bookmark)
            browserViewModel.editBottomSheet(favorite, false)
        } else {
            toastShort(favorite.link)
//            startActivity(Intent(this,))
//            browserViewModel.searchFromHistory(bookmark.link)
//            this.dismiss()
        }
    }

    fun back() {
        finish()
    }

    override fun <T> item(itemId: Int, dataClass: T) {
        val favoritesEntity: FavoritesEntity = dataClass as FavoritesEntity
        when (itemId) {
            R.id.item_edit -> {
            }
            R.id.item_delete -> {
                browserViewModel.deleteFavorites(favoritesEntity)
            }
            R.id.item_share -> {
                val string = "Website: ${favoritesEntity.title}\nUrl: ${favoritesEntity.link}"
                browserViewModel.share(string)
            }
            else -> {
            }
        }
    }


}