package com.bignerdranch.android.photogallery.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.photogallery.R
import com.bignerdranch.android.photogallery.data.QueryPreferences

class HistoryDialog : DialogFragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var clearHistory: TextView
    private lateinit var cancel: TextView
    private lateinit var history: ArrayList<String>
    private lateinit var photoGalleryViewModel: PhotoGalleryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        photoGalleryViewModel = ViewModelProvider(this)
            .get(PhotoGalleryViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.history_dialog, container, false)
        recyclerView = view.findViewById(R.id.history_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        val historyList = QueryPreferences.getStoredQuery(requireContext())
        val histories = arrayListOf( // A temporal dummy data
            historyList,
            "dog",
            "corn",
            "fish",
            "cream",
            "fire on the house",
            "cattle",
            "ocean",
            "hilltop",
            "snow",
            "cup of tea",
            "bedtimes",
            "night light",
            "lions",
            "singing birds",
            "landscape",
            "seascape"
        )
        history = ArrayList()
        history = histories
        clearHistory = view.findViewById(R.id.clear_history)
        clearHistory.setOnClickListener {
            history.removeAll(ArrayList())
            dismiss()
        }
        cancel = view.findViewById(R.id.cancel)
        cancel.setOnClickListener {
            dismiss()
        }
        recyclerView.adapter = HistoryAdapter(history)
        Log.i("HistoryDialog", "$history from $historyList")
        return view
    }

    private inner class HistoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var text: TextView = itemView.findViewById(R.id.string)
        private var removeHistory: ImageView = itemView.findViewById(R.id.remove_history)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(historyString: String) {
            text.text = historyString
            removeHistory.setOnClickListener {
                history.remove(text.text.toString())
            }
        }

        override fun onClick(view: View) {
            val query = text.text.toString()
            val string = Bundle().apply {
                putSerializable("string", query)
            }
            parentFragmentManager.setFragmentResult("query", string)
            dismiss()
        }
    }

    private inner class HistoryAdapter(private val string: ArrayList<String>) : RecyclerView.Adapter<HistoryHolder>() {
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
