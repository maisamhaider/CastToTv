import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.casttotv.adapter.TabsAdapter
import com.example.casttotv.databinding.FragmentTabs2Binding
import com.example.casttotv.viewmodel.BrowserViewModel

class TabsFragment : Fragment() {

    private lateinit var _binding: FragmentTabs2Binding
    private val binding get() = _binding
    private val viewModel: BrowserViewModel by activityViewModels {
        BrowserViewModel.BrowserViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTabs2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadTab()
        binding.apply {
            viewNewTab.setOnClickListener {
//                this@BrowserBottomSheetFragment.dismiss()
                viewModel.newTabWebView(WebView(requireContext()))
                loadTab()
            }
        }
    }

//    textviewOpenTabPreview.setOnClickListener {
//        browserViewModel.showBottomSheetTabPreview(fragmentManager = requireActivity().supportFragmentManager)
//
//    }
//
//    textviewNewTab.setOnClickListener {
//        browserViewModel.newTabWebView(WebView(requireContext()))
//    }
//    textviewOpenFavorite.setOnClickListener {
//        browserViewModel.openFavorite()
//    }
//
//    textviewCloseTab.setOnClickListener {
//        browserViewModel.closeCurrentTabDialog()
//    }

    private fun loadTab() {
        val adapter = TabsAdapter(::onTabClicked, viewModel, requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.liveTabs.observe(viewLifecycleOwner, {
            it?.let {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        })

    }


    private fun onTabClicked(tab: Int) {
        viewModel.switchToTab(tab)
//        this.dismiss()
    }
}