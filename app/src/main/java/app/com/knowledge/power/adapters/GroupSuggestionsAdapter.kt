package app.com.knowledge.power.adapters

import android.content.Context
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import app.com.knowledge.power.R
import app.com.knowledge.power.adapters.MembersAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.com.knowledge.power.models.Member
import java.util.ArrayList

class GroupSuggestionsAdapter(var suggestionsList: ArrayList<String>, var context: Context) :
    RecyclerView.Adapter<GroupSuggestionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem =
            layoutInflater.inflate(R.layout.group_name_layout, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (suggestionsList.isNotEmpty()) {
            holder.tvGroupName.text = suggestionsList[position]
        }
    }

    override fun getItemCount(): Int {
        return 5
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvGroupName: TextView = itemView.findViewById(R.id.tvGroupName)

    }
}