package app.com.knowledge.power.views.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import app.com.knowledge.power.R
import app.com.knowledge.power.databinding.FragmentProfilePictureBinding
import app.com.knowledge.power.interfaces.NextFragmentCallback
import app.com.knowledge.power.views.activities.DashboardActivity
import app.com.knowledge.power.views.activities.MainActivity

class ProfilePictureFragment(var callback: NextFragmentCallback) : Fragment() {

    lateinit var binding: FragmentProfilePictureBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_picture, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnNext.setOnClickListener {
            startActivity(Intent(requireActivity(), DashboardActivity::class.java))
        }
    }
}