package app.com.knowledge.power.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import app.com.knowledge.power.R
import app.com.knowledge.power.databinding.FragmentPasswordBinding
import app.com.knowledge.power.interfaces.NextFragmentCallback
import app.com.knowledge.power.models.User

class PasswordFragment(var callback: NextFragmentCallback) : Fragment() {
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

        binding.btnNext.setOnClickListener {
            callback.onNextButtonClicked(User(), 2)
        }
    }
}