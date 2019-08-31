package io.hppi.extensions

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat

fun Intent.showSingleActivity(cxt: Context) {
    this.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
    ActivityCompat.startActivity(cxt, this, Bundle.EMPTY)
}