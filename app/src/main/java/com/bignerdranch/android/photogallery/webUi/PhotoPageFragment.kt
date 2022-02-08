package com.bignerdranch.android.photogallery.webUi

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.android.photogallery.R
import com.bignerdranch.android.photogallery.util.VisibleFragment

private const val ARG_URI = "photo_page_url"

class PhotoPageFragment : VisibleFragment() {
    private lateinit var progressBar: ProgressBar
    private lateinit var uri: Uri
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uri = arguments?.getParcelable(ARG_URI) ?: Uri.EMPTY
        setHasOptionsMenu(true)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_photo_page, container, false)
        progressBar = view.findViewById(R.id.progress_bar)
        progressBar.max = 100
        webView = view.findViewById(R.id.web_view)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.loadUrl(uri.toString())
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                } else {
                    progressBar.visibility = View.VISIBLE
                    progressBar.progress = newProgress
                }
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                (activity as? AppCompatActivity)?.supportActionBar?.subtitle = title
            }
        }
        /** Based on a challenge. */
        activity?.onBackPressedDispatcher?.addCallback(this) {
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                requireActivity().finish()
            }
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_photo_page, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            activity?.onBackPressed()
        }
        if (item.itemId == R.id.share_link) {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.link, uri))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_link_subject))
            }.also { intent ->
                val chooserIntent = Intent.createChooser(intent, null)
                startActivity(chooserIntent)
            }
        } else {
            super.onOptionsItemSelected(item)
        }
        return true
    }

    companion object {
        fun newInstance(uri: Uri) : PhotoPageFragment {
            return PhotoPageFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_URI, uri)
                }
            }
        }
    }
}