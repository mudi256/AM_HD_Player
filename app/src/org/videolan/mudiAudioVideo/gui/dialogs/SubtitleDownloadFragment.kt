package org.videolan.mudiAudioVideo.gui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import org.videolan.mudiAudioVideo.R
import org.videolan.mudiAudioVideo.databinding.SubtitleDownloadFragmentBinding
import org.videolan.mudiAudioVideo.gui.OnItemSelectListener
import org.videolan.mudiAudioVideo.gui.helpers.UiTools
import org.videolan.mudiAudioVideo.util.AndroidDevices
import org.videolan.mudiAudioVideo.viewmodels.SubtitlesModel
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

class SubtitleDownloadFragment : Fragment() {
    private lateinit var viewModel: SubtitlesModel
    private lateinit var adapter: SubtitlesAdapter
    lateinit var mediaPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaPath = arguments?.getString(MEDIA_PATH, "") ?: ""
        viewModel = ViewModelProviders.of(requireActivity(), SubtitlesModel.Factory(requireContext(), mediaPath)).get(mediaPath, SubtitlesModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val binding = SubtitleDownloadFragmentBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel

        if (!AndroidDevices.isAndroidTv)
            //Prevent opening soft keyboard automatically
            binding.constraintLayout.isFocusableInTouchMode = true

        adapter = SubtitlesAdapter((parentFragment as SubtitleDownloaderDialogFragment).listEventActor)
        val recyclerView = binding.subtitleList
        recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        viewModel.result.observe(this, Observer {
            adapter.setList(it)
            if (it.isNotEmpty()) focusOnView(binding.scrollView, binding.swipeContainer)
        })

        binding.searchButton.setOnClickListener {
            UiTools.setKeyboardVisibility(it, false)
            viewModel.search(false)
            focusOnView(binding.scrollView, binding.swipeContainer)
        }


        val allValuesOfLanguages = resources.getStringArray(R.array.language_values)
        val allEntriesOfLanguages = resources.getStringArray(R.array.language_entries)
        binding.languageListSpinner.setOnItemsSelectListener(object: OnItemSelectListener {
            override fun onItemSelect(selectedItems: List<Int>) {
                val selectedLanguages = if (selectedItems.size == allValuesOfLanguages.size) listOf<String>()
                else selectedItems.map { allValuesOfLanguages[it] }
                viewModel.observableSearchLanguage.set(selectedLanguages)
            }
        })

        binding.languageListSpinner.setItems(allEntriesOfLanguages.toList())
        binding.languageListSpinner.setSelection(viewModel.getLastUsedLanguage().map { allValuesOfLanguages.indexOf(it) })

        return binding.root
    }

    private fun focusOnView(scrollView: NestedScrollView, view: View) {
        scrollView.smoothScrollTo(0, view.top)
    }

    companion object {
        fun newInstance(mediaPath: String): SubtitleDownloadFragment {
            val subtitleDownloadFragment = SubtitleDownloadFragment()
            subtitleDownloadFragment.arguments = Bundle(1).apply { putString(MEDIA_PATH, mediaPath) }
            return subtitleDownloadFragment
        }
    }
}