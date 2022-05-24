package app.com.knowledge.power.views.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.Slide
import android.transition.Transition
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import app.com.knowledge.power.R
import app.com.knowledge.power.databinding.ActivitySplashBinding
import app.com.knowledge.power.utils.Commons
import app.com.knowledge.power.utils.SharePrefData
import app.com.knowledge.power.views.BaseActivity
import com.google.android.gms.common.internal.service.Common
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    lateinit var binding: ActivitySplashBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        makeFullScreen()
        binding.tvVersion.text = "App Version: v" + app.com.knowledge.power.BuildConfig.VERSION_NAME
    }


    override fun onStart() {
        super.onStart()

        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            val introShown =
                SharePrefData.getInstance().getPrefBoolean(this@SplashActivity, "introShown")
            if (introShown) {
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(
                        Intent(
                            this@SplashActivity,
                            LoginSignupActivity::class.java
                        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }, 2000)
            } else {
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(
                        Intent(
                            this@SplashActivity,
                            MainActivity::class.java
                        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }, 2000)
            }
        } else {
            findUserInGroup()
        }
    }

    private fun findUserInGroup() {
        val auth = FirebaseAuth.getInstance()
        val reference = FirebaseDatabase.getInstance().getReference("Groups")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value.toString().contains(auth.currentUser?.uid.toString())) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(
                            Intent(
                                this@SplashActivity,
                                DashboardActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }, 2000)
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(
                            Intent(
                                this@SplashActivity,
                                CreateJoinGroupActivity::class.java
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }, 2000)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Commons.addLog(error.message)
            }
        })
    }
}