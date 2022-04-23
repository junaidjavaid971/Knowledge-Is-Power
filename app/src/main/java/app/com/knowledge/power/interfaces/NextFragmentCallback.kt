package app.com.knowledge.power.interfaces

import app.com.knowledge.power.models.User

interface NextFragmentCallback {
    fun onNextButtonClicked(user: User?, level: Int)
}