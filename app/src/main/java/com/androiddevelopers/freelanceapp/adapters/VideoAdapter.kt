package com.androiddevelopers.freelanceapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.androiddevelopers.freelanceapp.databinding.VideoItemRowBinding
import com.androiddevelopers.freelanceapp.model.VideoModel
import com.google.android.exoplayer2.ExoPlayer


class VideoAdapter : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    private val diffUtil = object : DiffUtil.ItemCallback<VideoModel>() {
        override fun areItemsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: VideoModel, newItem: VideoModel): Boolean {
            return oldItem == newItem
        }
    }
    private val recyclerListDiffer = AsyncListDiffer(this, diffUtil)

    var videoList: List<VideoModel>
    get() = recyclerListDiffer.currentList
    set(value) = recyclerListDiffer.submitList(value)

    inner class VideoViewHolder( val binding : VideoItemRowBinding) : RecyclerView.ViewHolder(binding.root){
        var player: ExoPlayer? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val binding = VideoItemRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return VideoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = videoList[position]
        holder.binding.webView.settings.javaScriptEnabled = true // JavaScript'i kapatmak, video kontrollerini genellikle gizler
        holder.binding.webView.settings.pluginState = WebSettings.PluginState.ON
        holder.binding.webView.settings.mediaPlaybackRequiresUserGesture = false // Otomatik oynatma için gereklidir
        holder.binding.webView.webViewClient = CustomWebViewClient()
        holder.binding.webView.webChromeClient = CustomWebChromeClient()


        holder.binding.webView.loadUrl(video.videoUrl.toString())
    }

    override fun getItemCount(): Int {
        return videoList.size
    }
    private inner class CustomWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            view?.loadUrl(url.toString())
            return true
        }
    }

    private inner class CustomWebChromeClient : WebChromeClient() {
        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
            // Video oynatıldığında çağrılır, burada özel görünümü gizleyebilirsiniz.
            super.onShowCustomView(view, callback)
        }
    }
}