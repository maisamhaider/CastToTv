package com.example.casttotv.ui.activities.browser

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import com.example.casttotv.R
import com.example.casttotv.adapter.HistoryAdapter
import com.example.casttotv.database.entities.HistoryEntity
import com.example.casttotv.databinding.ActivityHistoryBinding
import com.example.casttotv.dataclasses.History
import com.example.casttotv.interfaces.MyCallBack
import com.example.casttotv.interfaces.OptionMenuListener
import com.example.casttotv.ui.activities.browser.frags.DeleteHistoryBottomSheetFragment
import com.example.casttotv.viewmodel.BrowserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryActivity : AppCompatActivity(), MyCallBack, OptionMenuListener {
    private lateinit var _binding: ActivityHistoryBinding
    private val binding get() = _binding
    private val map: MutableMap<String, MutableList<HistoryEntity>> = HashMap()

    private val browserViewModel: BrowserViewModel by viewModels {
        BrowserViewModel.BrowserViewModelFactory(this)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.apply {
            viewModel = browserViewModel
            binding.history = this@HistoryActivity
            tvClearHistoryClick.setOnClickListener {
                browserViewModel.showBottomSheet(supportFragmentManager,
                    DeleteHistoryBottomSheetFragment())
            }
        }
        loadHistory()
        browserViewModel.myCallback(this)


    }

    private fun loadHistory() {

        val adapter = HistoryAdapter(::onClick, this, this)
        binding.recyclerView.adapter = adapter
        browserViewModel.getHistory().asLiveData().observe(this) {
            if (map.isNotEmpty()) {
                map.clear()
            }
            CoroutineScope(Dispatchers.IO).launch {
                it?.let { item ->
                    for (hist in item) {
                        if (map[hist.day].isNullOrEmpty()) {
                            map[hist.day] = mutableListOf(hist)
                        } else {
                            val oldList: MutableList<HistoryEntity> = map[hist.day]!!
                            oldList.add(0, hist)
                            map[hist.day] = oldList
                        }
                    }
                }
                val list: MutableList<History> = ArrayList()
                map.forEach { m -> list.add(0, History(m.key, m.value)) }

                launch(Dispatchers.Main) {
                    adapter.submitList(list)
                }
            }
        }
    }


    fun back() {
        finish()
    }

    override fun callback() {
        loadHistory()
    }

    fun onClick(historyEntity: HistoryEntity) {
//         back()
//        browserViewModel.searchFromHistory(historyEntity.link)
    }

    override fun <T> item(itemId: Int, dataClass: T) {
        val historyEntity: HistoryEntity = dataClass as HistoryEntity
        when (itemId) {
            R.id.item_add_to_bookmark -> {
                browserViewModel.bookmark(title = historyEntity.title,
                    link = historyEntity.link,
                    date = historyEntity.date)
                browserViewModel.insertBookmark()
            }
            R.id.item_delete -> {
                browserViewModel.deleteHistory(historyEntity)
            }
            R.id.item_share -> {
                val string = "Website: ${historyEntity.title}" +
                        "\nUrl: ${historyEntity.link}" +
                        "\nDate: ${historyEntity.day}"
                browserViewModel.share(string)
            }
            else -> {
            }
        }
    }
}