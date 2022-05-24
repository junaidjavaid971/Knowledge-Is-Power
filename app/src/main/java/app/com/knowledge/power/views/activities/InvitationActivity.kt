package app.com.knowledge.power.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import app.com.knowledge.power.R
import app.com.knowledge.power.databinding.ActivityInvitationBinding

class InvitationActivity : AppCompatActivity() {
    lateinit var binding: ActivityInvitationBinding
    var groupCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_invitation)

        if (intent?.hasExtra("invitationCode")!!) {
            groupCode = intent?.getStringExtra("invitationCode").toString()
            binding.tvCode.text = groupCode.uppercase()
        }
        manageClicks()
    }

    private fun manageClicks() {
        binding.headerLayout.tvName.text = getString(R.string.sendInvite)

        binding.headerLayout.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.btnSendCode.setOnClickListener {
            shareInvitationCode()
        }
    }

    private fun shareInvitationCode() {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Please join the group on Knowledge is Power using this group code: " + groupCode.uppercase()
        )
        sendIntent.type = "text/plain"

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)

    }

    override fun onBackPressed() {
        startActivity(
            Intent(
                this@InvitationActivity,
                DashboardActivity::class.java
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
        finish()
    }
}