package app.com.knowledge.power.views.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import app.com.knowledge.power.R
import app.com.knowledge.power.databinding.FragmentEmailBinding
import app.com.knowledge.power.interfaces.NextFragmentCallback
import app.com.knowledge.power.utils.Commons
import com.google.firebase.auth.FirebaseAuth


class EmailFragment(var callback: NextFragmentCallback) : Fragment() {
    lateinit var binding: FragmentEmailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_email, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
            if (binding.edEmail.text.isEmpty()) {
                binding.tvErrorMessage.visibility = View.VISIBLE
                binding.tvErrorMessage.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireActivity(),
                        R.anim.shake
                    )
                );
                return@setOnClickListener
            }
            Commons.showProgress(activity = activity as AppCompatActivity)
            FirebaseAuth.getInstance()
                .fetchSignInMethodsForEmail(binding.edEmail.text.toString()).addOnCompleteListener {
                    Commons.hideProgress()
                    Commons.addLog(it.result?.signInMethods?.size.toString())
                    if (it.result.signInMethods?.isNotEmpty()!!) {
                        callback.onEmailFragmentClicked(
                            binding.edEmail.text.toString(),
                            true
                        )
                    } else {
                        callback.onEmailFragmentClicked(
                            binding.edEmail.text.toString(),
                            false
                        )
                    }
                }.addOnFailureListener {
                    Commons.hideProgress()
                }
        }
    }
}