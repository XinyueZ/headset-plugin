package io.hppi.adapters

import androidx.databinding.BindingAdapter
import com.facebook.shimmer.ShimmerFrameLayout

@BindingAdapter("startAnim")
fun ShimmerFrameLayout.startAnim(start: Boolean) {
    when (start) {
        true -> startShimmer()
        else -> stopShimmer()
    }
}
