package app.com.knowledge.power.interfaces

import app.com.knowledge.power.models.User

interface NextFragmentCallback {
    fun onEmailFragmentClicked(email: String, isSignedUp: Boolean)
    fun onNextButtonClicked(value: String, level: Int)
}