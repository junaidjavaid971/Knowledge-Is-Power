package app.com.knowledge.power.views.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import app.com.knowledge.power.R
import app.com.knowledge.power.adapters.GroupSuggestionsAdapter
import app.com.knowledge.power.adapters.MessagesAdapter

class CreateGroupActivity : AppCompatActivity() {
    lateinit var binding: app.com.knowledge.power.databinding.ActivityCreateGroupBinding
    lateinit var adapter: GroupSuggestionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_group)

        manageClick()
        bindSuggestionsRV()
    }

    private fun bindSuggestionsRV() {
        adapter = GroupSuggestionsAdapter(prepareSuggestionsList(), this)

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
            onBackPressed()
        }
    }
}