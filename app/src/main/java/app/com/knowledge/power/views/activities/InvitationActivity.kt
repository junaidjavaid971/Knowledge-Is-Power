package app.com.knowledge.power.views.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import app.com.knowledge.power.R
import app.com.knowledge.power.databinding.ActivityInvitationBinding

class InvitationActivity : AppCompatActivity() {
    lateinit var binding: ActivityInvitationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_invitation)

        manageClicks()
    }

    private fun manageClicks() {
        binding.headerLayout.tvName.text = getString(R.string.sendInvite)

        binding.headerLayout.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}