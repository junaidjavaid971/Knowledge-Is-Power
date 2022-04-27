package app.com.knowledge.power.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import app.com.knowledge.power.R
import app.com.knowledge.power.databinding.ActivityJoinGroupBinding

class JoinGroupActivity : AppCompatActivity() {

    lateinit var binding: ActivityJoinGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_join_group)

        binding.btnJoinGroup.setOnClickListener {
            startActivity(
                Intent(this@JoinGroupActivity, DashboardActivity::class.java).addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
            finish()
        }
    }
}