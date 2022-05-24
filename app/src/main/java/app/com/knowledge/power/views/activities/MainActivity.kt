package app.com.knowledge.power.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import app.com.knowledge.power.R
import app.com.knowledge.power.adapters.ViewAdapter
import app.com.knowledge.power.databinding.ActivityMainBinding
import app.com.knowledge.power.utils.SharePrefData
import app.com.knowledge.power.views.BaseActivity

class MainActivity : BaseActivity() {
    lateinit var viewAdapter: ViewAdapter
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewAdapter = ViewAdapter(this)
        binding.viewPager.adapter = viewAdapter
        binding.dot2.setViewPager(binding.viewPager)


        SharePrefData.getInstance().setPrefBoolean(this@MainActivity, "introShown", true)

        binding.btnGetStarted.setOnClickListener {
            startActivity(Intent(this@MainActivity, LoginSignupActivity::class.java))
        }
    }
}