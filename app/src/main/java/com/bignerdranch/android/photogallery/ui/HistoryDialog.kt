package com.bignerdranch.android.photogallery.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.photogallery.R
import com.bignerdranch.android.photogallery.model.Gallery

class HistoryDialog : DialogFragment() {
    private lateinit var historyViewModel: PhotoGalleryViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var clearHistory: TextView
    private lateinit var cancel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        historyViewModel = ViewModelProvider(this) [PhotoGalleryViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.history_dialog, container, false)
        recyclerView = view.findViewById(R.id.history_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        clearHistory = view.findViewById(R.id.clear_history)
        clearHistory.setOnClickListener {
            historyViewModel.clearAll()
            Toast.makeText(context, getString(R.string.history_cleared), Toast.LENGTH_SHORT).show()
            dismiss()
        }
        cancel = view.findViewById(R.id.cancel)
        cancel.setOnClickListener {
            dismiss()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.historyViewModel.getSearches.observe(
            viewLifecycleOwner,
            { gallery ->
                Log.i("HistoryDialog", "SearchHistory received from ViewModel: $gallery")
                recyclerView.adapter = HistoryAdapter(gallery)
            }
        )
    }

    private inner class HistoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private lateinit var gallery: Gallery
        private var text: TextView = itemView.findViewById(R.id.string)
        private var removeHistory: ImageView = itemView.findViewById(R.id.remove_history)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(gallery: Gallery) {
            this.gallery = gallery
            text.text = gallery.search
            removeHistory.setOnClickListener {
                historyViewModel.removeSearch(gallery)
            }
        }

        override fun onClick(view: View) {
            gallery = Gallery()
            val query = text.text.toString()
            gallery.search = query
            val string = Bundle().apply {
                putSerializable("string", query)
            }
            parentFragmentManager.setFragmentResult("query", string)
            historyViewModel.addSearch(gallery)
            dismiss()
        }
    }

    private inner class HistoryAdapter(private val string: List<Gallery>) : RecyclerView.Adapter<HistoryHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryHolder {
            val view = layoutInflater.inflate(R.layout.history_list, parent, false)
            return HistoryHolder(view)
        }

        override fun onBindViewHolder(holder: HistoryHolder, position: Int) {
            val string = string[position]
            holder.bind(string)
        }

        override fun getItemCount(): Int = string.size
    }

    companion object {
        fun newInstance() = HistoryDialog()
    }
}
