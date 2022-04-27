package app.com.knowledge.power.views.activities

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import app.com.knowledge.power.interfaces.NextFragmentCallback
import app.com.knowledge.power.R
import app.com.knowledge.power.databinding.ActivityLoginSignupBinding
import app.com.knowledge.power.models.User
import app.com.knowledge.power.views.BaseActivity
import app.com.knowledge.power.views.fragments.*

class LoginSignupActivity : BaseActivity(),
    NextFragmentCallback {
    lateinit var binding: ActivityLoginSignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login_signup)

        makeFullScreen()
        replaceFragment(EmailFragment(this), "EmailFragment")
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onNextButtonClicked(value: String, level: Int) {
        when (level) {
            2 -> {
                replaceFragment(NameFragment(this), "PasswordFragment")
            }
            3 -> {
                replaceFragment(ContactNumberFragment(this), "PasswordFragment")
            }
            4 -> {
                replaceFragment(ProfilePictureFragment(this), "PasswordFragment")
            }
        }
    }

    override fun onEmailFragmentClicked(email: String, isSignedUp: Boolean) {
        replaceFragment(PasswordFragment(this , isSignedUp), "PasswordFragment")
    }
}