package io.hppi.adapters

import android.net.Uri
import android.view.View
import android.widget.MediaController
import android.widget.VideoView
import androidx.databinding.BindingAdapter
import com.facebook.shimmer.ShimmerFrameLayout

@BindingAdapter("src")
fun VideoView.setSource(filename: String) {
    val srcUri = Uri.parse("android.resource://${context.packageName}/raw/$filename")
    setMediaController(MediaController(context).apply { this.visibility = View.GONE })
    setVideoURI(Uri.parse(srcUri.toString()))
    seekTo(0)
    requestFocus()
    setOnPreparedListener {
        it.isLooping = true
        it.setVolume(0f, 0f)
        it.isLooping = true
        start()
    }
}

@BindingAdapter("startAnim")
fun ShimmerFrameLayout.startAnim(start: Boolean) {
    when (start) {
        true -> startShimmer()
        else -> stopShimmer()
    }
}
