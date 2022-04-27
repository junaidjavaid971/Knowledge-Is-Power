package app.com.knowledge.power.views.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.TextureView
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.com.knowledge.power.R
import app.com.knowledge.power.adapters.MembersAdapter
import app.com.knowledge.power.databinding.ActivityDashboardBinding
import app.com.knowledge.power.views.BaseActivity
import app.com.knowledge.power.views.fragments.MapFragment


class DashboardActivity : BaseActivity() {
    lateinit var mapsFragment: MapFragment

    lateinit var binding: ActivityDashboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        mapsFragment = MapFragment()
        setFragment()
        bindGroupMembersRv()
        manageClicks()
    }

    private fun manageClicks() {
        binding.ivGroups.setOnClickListener {
            val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
            val inflater = this.layoutInflater
            val dialogView: View = inflater.inflate(R.layout.group_dialog_row, null)
            dialogBuilder.setView(dialogView)

            val rvGroups: RecyclerView = dialogView.findViewById(R.id.rvGroups)
            val btnJoinGroup: TextView = dialogView.findViewById(R.id.btnJoinGroup)
            val btnCreateGroup: TextView = dialogView.findViewById(R.id.btnCreateGroup)

            btnJoinGroup.setOnClickListener {
                startActivity(Intent(this@DashboardActivity, JoinGroupActivity::class.java))
            }
            btnCreateGroup.setOnClickListener {
                startActivity(Intent(this@DashboardActivity, CreateGroupActivity::class.java))
            }
            val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            val memberAdapter = MembersAdapter(this)
            rvGroups.layoutManager = linearLayoutManager
            rvGroups.adapter = memberAdapter

            val alertDialog: AlertDialog = dialogBuilder.create()
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertDialog.show()
        }

        binding.bottomSheet.layoutAddMember.setOnClickListener {
            startActivity(Intent(this@DashboardActivity, InvitationActivity::class.java))
        }
    }

    private fun bindGroupMembersRv() {
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val memberAdapter = MembersAdapter(this)
        binding.bottomSheet.rvGroupMembers.layoutManager = linearLayoutManager
        binding.bottomSheet.rvGroupMembers.adapter = memberAdapter
    }


    private fun setFragment() {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.flMap, mapsFragment)
        ft.commit()
    }

}