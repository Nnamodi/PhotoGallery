package com.bignerdranch.android.photogallery.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.TrafficStats
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.work.*
import com.bignerdranch.android.photogallery.R
import com.bignerdranch.android.photogallery.api.FlickrFetchr
import com.bignerdranch.android.photogallery.data.QueryPreferences
import com.bignerdranch.android.photogallery.data.ThumbnailDownloader
import com.bignerdranch.android.photogallery.model.GalleryItem
import com.bignerdranch.android.photogallery.util.PollWorker
import com.bignerdranch.android.photogallery.util.VisibleFragment
import com.bignerdranch.android.photogallery.webUi.PhotoPageActivity
import java.util.concurrent.TimeUnit

private const val TAG = "PhotoGalleryFragment"
private const val POLL_WORK = "POLL_WORK"

class PhotoGalleryFragment : VisibleFragment() {
    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var photoGalleryViewModel: PhotoGalleryViewModel
    private lateinit var thumbnailDownloader: ThumbnailDownloader<PhotoHolder>
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val switchState = QueryPreferences.getSwitchState(requireContext())
        if (switchState) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        StrictMode.enableDefaults()
        TrafficStats.setThreadStatsTag(1)
        retainInstance = true
        setHasOptionsMenu(true)
        photoGalleryViewModel = ViewModelProvider(this)
            .get(PhotoGalleryViewModel::class.java)
        val responseHandler = Handler()
        thumbnailDownloader = ThumbnailDownloader(responseHandler) { photoHolder, bitmap ->
            val drawable = BitmapDrawable(resources, bitmap)
            photoHolder.bindDrawable(drawable)
        }
        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewLifecycleOwnerLiveData.observe(
            viewLifecycleOwner,
            { it?.lifecycle?.addObserver(thumbnailDownloader) }
        )
        val view = inflater.inflate(R.layout.fragment_photo_gallery, container, false)
        photoRecyclerView = view.findViewById(R.id.photo_recycler_view)
        progressBar = view.findViewById(R.id.progress_bar)
        progressBar.visibility = View.VISIBLE
        swipeRefresh = view.findViewById(R.id.swipe_refresh)
        swipeRefresh.setOnRefreshListener {
            poll(requireContext())
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /** Based on challenge. */
        // TODO
        photoRecyclerView.layoutManager = GridLayoutManager(context, 2)//.also {
//            it.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
//                override fun getSpanSize(position: Int): Int {
//                    return if (position % 3 == 0)
//                        2 else 1
//                }
//            }
//        }
        photoGalleryViewModel.galleryItemLiveData.observe(
            viewLifecycleOwner,
            { galleryItems ->
                Log.d(TAG, "Gallery items received from ViewModel: $galleryItems")
                photoRecyclerView.adapter = PhotoAdapter(galleryItems)
                progressBar.visibility = View.GONE
                photoRecyclerView.visibility = View.VISIBLE
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewLifecycleOwner.lifecycle.removeObserver(thumbnailDownloader)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_photo_gallery, menu)
        val searchItem: MenuItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as SearchView
        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    Log.d(TAG, "QueryTextSubmit: $query")
                    photoGalleryViewModel.fetchPhotos(query)
                    progressBar.visibility = View.VISIBLE
                    photoRecyclerView.visibility = View.GONE
                    searchView.clearFocus()
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    Log.d(TAG, "QueryTextChange: $query")
                    return false
                }
            })
            setOnSearchClickListener {
                searchView.setQuery(photoGalleryViewModel.searchTerm, false)
            }
        }

        val toggleItem = menu.findItem(R.id.menu_item_toggle_polling)
        val isPolling = QueryPreferences.isPolling(requireContext())
        val toggleItemTitle = if (isPolling) {
            R.string.stop_polling
        } else {
            R.string.start_polling
        }
        toggleItem.setTitle(toggleItemTitle)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_clear -> {
                photoGalleryViewModel.fetchPhotos(query = "")
                progressBar.visibility = View.VISIBLE
                photoRecyclerView.visibility = View.GONE
                true
            }
            R.id.menu_item_toggle_polling -> {
                val isPolling = QueryPreferences.isPolling(requireContext())
                if (isPolling) {
                    WorkManager.getInstance(requireContext()).cancelUniqueWork(POLL_WORK)
                    QueryPreferences.setPolling(requireContext(), false)
                } else {
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.UNMETERED)
                        .build()
                    val periodicRequest = PeriodicWorkRequest
                        .Builder(PollWorker::class.java, 15, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .build()
                    WorkManager.getInstance(requireContext())
                        .enqueueUniquePeriodicWork(POLL_WORK,
                        ExistingPeriodicWorkPolicy.KEEP,
                        periodicRequest)
                    QueryPreferences.setPolling(requireContext(), true)
                }
                activity?.invalidateOptionsMenu()
                true
            }
            R.id.menu_item_settings -> {
                val intent = PhotoSettingsActivity.newIntent(requireContext())
                startActivity(intent)
                true
            }
            R.id.menu_item_history -> {
                HistoryDialog.newInstance().apply {
                    show(this@PhotoGalleryFragment.childFragmentManager, "history")
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private inner class PhotoHolder(itemImageView: ImageView)
        : RecyclerView.ViewHolder(itemImageView), View.OnClickListener {

        private lateinit var galleryItem: GalleryItem
        init {
            itemView.setOnClickListener(this)
        }
        val bindDrawable: (Drawable) -> Unit = itemImageView::setImageDrawable
        fun bindGalleryItem(item: GalleryItem) {
            galleryItem = item
        }

        override fun onClick(view: View) {
            val color = if (QueryPreferences.getSwitchState(requireContext())) {
                Color.rgb(0, 0, 0) // Black
            } else {
                Color.rgb(44, 0, 145) // Blue
            }
            val defaultColors = CustomTabColorSchemeParams.Builder()
                .setToolbarColor(color)
                .build()
            when (QueryPreferences.getBrowserChoice(requireContext())) {
                "WebView" -> {
                    val intent = PhotoPageActivity.newIntent(requireContext(), galleryItem.photoPageUri)
                    startActivity(intent)
                }
                "Custom View" -> {
                    CustomTabsIntent.Builder()
                        .setShowTitle(true)
                        .setDefaultColorSchemeParams(defaultColors)
                        .setStartAnimations(requireContext(), R.anim.slide_in_left, R.anim.slide_out_right)
                        .setExitAnimations(requireContext(), R.anim.slide_in_right, R.anim.slide_out_left)
                        .build()
                        .launchUrl(requireContext(), galleryItem.photoPageUri)
                }
                else -> { // Browser app
                    val intent = Intent(Intent.ACTION_VIEW, galleryItem.photoPageUri)
                    startActivity(intent)
                }
            }
        }
    }

    private inner class PhotoAdapter(private val galleryItems: List<GalleryItem>) : RecyclerView.Adapter<PhotoHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val view = layoutInflater.inflate(R.layout.list_item_gallery, parent, false) as ImageView
            return PhotoHolder(view)
        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val galleryItem = galleryItems[position]
            holder.bindGalleryItem(galleryItem)
            val placeHolder: Drawable = ContextCompat.getDrawable(
                requireContext(), R.drawable.photo
            ) ?: ColorDrawable()
            holder.bindDrawable(placeHolder)
            thumbnailDownloader.queueThumbnail(holder, galleryItem.url)
        }

        override fun getItemCount(): Int = galleryItems.size
    }

    private fun poll(context: Context) {
        val query = QueryPreferences.getStoredQuery(context)
        val lastResultId = QueryPreferences.getLastResultId(context)
        val items: List<GalleryItem> = if (query.isEmpty()) {
            FlickrFetchr().fetchPhotosRequest()
                .execute()
                .body()
                ?.photos
                ?.galleryItems
        } else {
            FlickrFetchr().searchPhotosRequest(query)
                .execute()
                .body()
                ?.photos
                ?.galleryItems
        } ?: emptyList()
        val result = items.first().id
        if (result != lastResultId) {
            QueryPreferences.setLastResultId(context, result)
            startActivity(PhotoGalleryActivity.newIntent(context))
        } else {
            Toast.makeText(context, R.string.no_new_pic, Toast.LENGTH_SHORT).show()
        }
        swipeRefresh.isRefreshing = false
    }

    companion object {
        fun newInstance() = PhotoGalleryFragment()
    }
}