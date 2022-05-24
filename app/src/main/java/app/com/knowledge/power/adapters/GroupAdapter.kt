package app.com.knowledge.power.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.com.knowledge.power.R
import app.com.knowledge.power.models.Group

class GroupAdapter(
    var groupsList: ArrayList<Group>?,
    var context: Context,
    var callback: GroupCallback
) :
    RecyclerView.Adapter<GroupAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem =
            layoutInflater.inflate(R.layout.group_layout_item, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (groupsList?.isNotEmpty()!!) {
            val group = groupsList!![position]

            holder.groupName.text = group.groupName

            holder.tvAvatar.text = group.groupName.substring(0, 2).uppercase()

            holder.itemView.setOnClickListener {
                callback.onGroupItemClicked(group)
            }
        }
    }

    override fun getItemCount(): Int {
        return groupsList?.size!!
    }

    interface GroupCallback {
        fun onGroupItemClicked(group: Group)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvAvatar: TextView = itemView.findViewById(R.id.tvAvatar)
        var groupName: TextView = itemView.findViewById(R.id.tvMemberName)

    }
}