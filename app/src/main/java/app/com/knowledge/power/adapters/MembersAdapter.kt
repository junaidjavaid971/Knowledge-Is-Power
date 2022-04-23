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

class MembersAdapter : RecyclerView.Adapter<MembersAdapter.ViewHolder> {
    var membersList: ArrayList<Member>? = ArrayList()
    var context: Context

    constructor(membersList: ArrayList<Member>?, context: Context) {
        this.membersList = membersList
        this.context = context
    }

    constructor(context: Context) {
        this.context = context
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem =
            layoutInflater.inflate(R.layout.group_member_item_row, parent, false)
        return MembersAdapter(context).ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (membersList?.isNotEmpty()!!) {
            val member = membersList!![position]
        }
    }

    override fun getItemCount(): Int {
        return 10
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvAvatar: TextView = itemView.findViewById(R.id.tvAvatar)
        var tvMemberName: TextView = itemView.findViewById(R.id.tvMemberName)
        var tvJoinedSince: TextView = itemView.findViewById(R.id.tvMemberSince)

    }
}