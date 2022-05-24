package app.com.knowledge.power.views.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import app.com.knowledge.power.R
import app.com.knowledge.power.databinding.ActivityJoinGroupBinding
import app.com.knowledge.power.models.Group
import app.com.knowledge.power.models.User
import app.com.knowledge.power.utils.Commons
import com.google.android.gms.common.internal.service.Common
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class JoinGroupActivity : AppCompatActivity() {

    var user: User? = null
    lateinit var binding: ActivityJoinGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_join_group)

        manageClicks()
        getCurrentUser()
    }

    private fun manageClicks() {
        binding.btnJoinGroup.setOnClickListener {
            val groupCode = binding.edGroupCode.text.toString()
            if (groupCode.isEmpty()) {
                Commons.showToast(this@JoinGroupActivity, "Please enter group code to proceed")
                return@setOnClickListener
            } else {
                if (user == null) {
                    Commons.showToast(
                        this@JoinGroupActivity,
                        "There's an issue with your account, you cannot join the group right now!"
                    )
                    return@setOnClickListener
                }
                Commons.showProgress(this@JoinGroupActivity)
                getAllGroups(groupCode)
            }
        }

        binding.headerLayout.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getAllGroups(groupCode: String) {
        var count = 0
        val reference = FirebaseDatabase.getInstance().getReference("Groups")
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (datasnapShot in snapshot.children) {
                    count++
                    val group = datasnapShot.getValue(Group::class.java)
                    if (groupCode.lowercase() == group?.groupCode?.lowercase()) {
                        reference.child(group.id).child("members").child(user?.id.toString())
                            .setValue(user)

                        val userGroupRef =
                            FirebaseDatabase.getInstance().getReference("UserGroups")
                                .child(user?.id.toString()).child(group.id)
                        userGroupRef.setValue(group)

                        Commons.hideProgress()
                        Commons.showToast(this@JoinGroupActivity, "You have joined this group!")
                        startActivity(
                            Intent(this@JoinGroupActivity, DashboardActivity::class.java).addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK
                            ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        )
                        finish()
                        break
                    }

                    if (count == snapshot.childrenCount.toInt()) {
                        Commons.hideProgress()
                        Commons.showAlertDialog(
                            "Error",
                            "We couldn't find any group with this group code",
                            this@JoinGroupActivity,
                            null
                        )
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Commons.addLog(error.message)
            }

        })
    }

    private fun getCurrentUser() {
        Commons.showProgress(this@JoinGroupActivity)
        val reference = FirebaseDatabase.getInstance().getReference("Users")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Commons.hideProgress()
                user = snapshot.getValue(User::class.java)!!
            }

            override fun onCancelled(error: DatabaseError) {
                Commons.hideProgress()
                Commons.addLog(error.message)
            }

        })
    }

}