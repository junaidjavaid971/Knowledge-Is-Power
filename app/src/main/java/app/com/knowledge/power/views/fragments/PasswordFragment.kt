package app.com.knowledge.power.views.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import app.com.knowledge.power.R
import app.com.knowledge.power.databinding.FragmentPasswordBinding
import app.com.knowledge.power.interfaces.NextFragmentCallback
import app.com.knowledge.power.models.User
import app.com.knowledge.power.utils.Commons
import app.com.knowledge.power.views.activities.CreateJoinGroupActivity
import app.com.knowledge.power.views.activities.DashboardActivity
import com.google.android.gms.common.internal.service.Common
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PasswordFragment(
    var callback: NextFragmentCallback,
    var isSignedUp: Boolean,
    var email: String
) : Fragment() {
    lateinit var binding: FragmentPasswordBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_password, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvEmail.text = email

        binding.btnNext.setOnClickListener {
            if (binding.edPassword.text.toString().isEmpty()) {
                binding.tvErrorMessage.visibility = View.GONE
                binding.tvErrorMessage.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireActivity(),
                        R.anim.shake
                    )
                );
                return@setOnClickListener
            }

            val password = binding.edPassword.text.toString()
            if (isSignedUp) {
                Commons.showProgress(activity = activity as AppCompatActivity)
                val mAuth = FirebaseAuth.getInstance()
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        findUserInGroup()
                    } else {
                        Commons.hideProgress()
                        Commons.showAlertDialog(
                            "Error",
                            it.exception?.localizedMessage.toString(),
                            activity as AppCompatActivity,
                            null
                        )
                    }
                }
            } else {
                callback.onNextButtonClicked(binding.edPassword.text.toString(), 2)
            }
        }
    }

    private fun findUserInGroup() {
        val auth = FirebaseAuth.getInstance()
        val reference = FirebaseDatabase.getInstance().getReference("Groups")

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Commons.hideProgress()
                if (snapshot.value.toString().contains(auth.currentUser?.uid.toString())) {
                    startActivity(
                        Intent(
                            requireActivity(),
                            DashboardActivity::class.java
                        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                } else {
                    startActivity(
                        Intent(
                            requireActivity(),
                            CreateJoinGroupActivity::class.java
                        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Commons.addLog(error.message)
            }
        })
    }
}