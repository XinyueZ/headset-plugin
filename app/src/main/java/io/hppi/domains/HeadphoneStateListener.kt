package io.hppi.domains

interface HeadphoneStateListener {
    fun onPlugIn()
    fun onPlugOut()
}

object SimpleHeadphoneStateListener : HeadphoneStateListener {
    override fun onPlugIn() = Unit
    override fun onPlugOut() = Unit
}
