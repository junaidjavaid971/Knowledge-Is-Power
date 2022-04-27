package app.com.knowledge.power.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import app.com.knowledge.power.R
import app.com.knowledge.power.databinding.ActivityCreateGroupBinding
import app.com.knowledge.power.databinding.ActivityCreateJoinGroupBinding

class CreateJoinGroupActivity : AppCompatActivity() {

    lateinit var binding: ActivityCreateJoinGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_join_group)

        manageClicks()
    }

    private fun manageClicks() {
        binding.btnCreateGroup.setOnClickListener {
            startActivity(Intent(this@CreateJoinGroupActivity, CreateGroupActivity::class.java))
        }
        binding.btnJoinGroup.setOnClickListener {
            startActivity(Intent(this@CreateJoinGroupActivity, JoinGroupActivity::class.java))
        }
    }
}