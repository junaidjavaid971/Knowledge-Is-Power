package app.com.knowledge.power.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import app.com.knowledge.power.R
import app.com.knowledge.power.databinding.FragmentContactNumberBinding
import app.com.knowledge.power.interfaces.NextFragmentCallback
import app.com.knowledge.power.models.User
import app.com.knowledge.power.utils.Commons

class ContactNumberFragment(var callback: NextFragmentCallback) : Fragment() {
    lateinit var binding: FragmentContactNumberBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_contact_number, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ccp.registerCarrierNumberEditText(binding.edContactNumber)

        binding.btnNext.setOnClickListener {
            if (binding.edContactNumber.text.toString().isEmpty()) {
                binding.tvErrorMessage.visibility = View.GONE
                binding.tvErrorMessage.startAnimation(
                    AnimationUtils.loadAnimation(
                        requireActivity(),
                        R.anim.shake
                    )
                );
                return@setOnClickListener
            } else if (!binding.ccp.isValidFullNumber) {
                Commons.showToast(requireActivity(), getString(R.string.enterValidContactNumber))
                return@setOnClickListener
            }

            callback.onNextButtonClicked(binding.ccp.fullNumberWithPlus, 4)
        }
    }
}