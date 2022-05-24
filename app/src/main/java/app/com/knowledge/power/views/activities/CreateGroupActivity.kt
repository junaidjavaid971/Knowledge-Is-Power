package app.com.knowledge.power.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import app.com.knowledge.power.R
import app.com.knowledge.power.adapters.GroupSuggestionsAdapter
import app.com.knowledge.power.adapters.MessagesAdapter
import app.com.knowledge.power.models.Group
import app.com.knowledge.power.models.User
import app.com.knowledge.power.utils.Commons
import com.google.android.gms.common.internal.service.Common
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CreateGroupActivity : AppCompatActivity(), GroupSuggestionsAdapter.GroupCallback {
    lateinit var binding: app.com.knowledge.power.databinding.ActivityCreateGroupBinding
    lateinit var adapter: GroupSuggestionsAdapter

    var user: User? = null
    var isFromLogin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_group)

        isFromLogin = intent?.getBooleanExtra("fromLogin", false)!!
        getCurrentUser()
        manageClick()
        bindSuggestionsRV()
    }

    private fun bindSuggestionsRV() {
        adapter = GroupSuggestionsAdapter(prepareSuggestionsList(), this, this)

        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvSuggestions.layoutManager = layoutManager
        binding.rvSuggestions.setHasFixedSize(true)
        binding.rvSuggestions.adapter = adapter
    }

    private fun prepareSuggestionsList(): ArrayList<String> {
        val arrayList = ArrayList<String>()
        arrayList.add("Family")
        arrayList.add("Friends")
        arrayList.add("Extended Family")
        arrayList.add("Specials")
        arrayList.add("Carpool")
        arrayList.add("Field Group")
        arrayList.add("Colleagues")
        arrayList.add("Partners")

        return arrayList
    }

    private fun manageClick() {
        binding.headerLayout.tvName.text = getString(R.string.createGroup)
        binding.headerLayout.ivBack.setOnClickListener {
            if (isFromLogin) {
                startActivity(
                    Intent(this@CreateGroupActivity, DashboardActivity::class.java).addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                )
                finish()
            } else {
                onBackPressed()
            }
        }

        binding.ivCreateGroup.setOnClickListener {
            if (binding.edGroupName.text.toString().isEmpty()) {
                Commons.showToast(
                    this@CreateGroupActivity,
                    "You must give your group a meaningful name"
                )
            } else if (user == null) {
                Commons.showToast(
                    this@CreateGroupActivity,
                    "There's an issue with your account, the group cannot be created"
                )
            } else {
                val mAuth = FirebaseAuth.getInstance()
                val groupId = Commons.getRandomNumberString(15)
                val group = Group(
                    groupId,
                    mAuth.currentUser?.uid,
                    Commons.formattedDate,
                    Commons.getRandomNumberString(6),
                    binding.edGroupName.text.toString()
                )
                val reference = FirebaseDatabase.getInstance().getReference("Groups").child(groupId)
                reference.setValue(group)
                reference.child("members").child(user?.id.toString()).setValue(user)

                val userGroupRef =
                    FirebaseDatabase.getInstance().getReference("UserGroups")
                        .child(user?.id.toString()).child(groupId)
                userGroupRef.setValue(group)

                Commons.showToast(this@CreateGroupActivity, "Group Created")
                startActivity(
                    Intent(
                        this@CreateGroupActivity,
                        InvitationActivity::class.java
                    ).putExtra("invitationCode", group.groupCode).addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                )
                finish()
            }
        }
    }

    private fun getCurrentUser() {
        Commons.showProgress(this@CreateGroupActivity)
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

    override fun onGroupNameClicked(groupName: String) {
        binding.edGroupName.setText(groupName)
    }
}