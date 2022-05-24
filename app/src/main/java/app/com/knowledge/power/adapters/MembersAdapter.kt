package app.com.knowledge.power.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.com.knowledge.power.R
import app.com.knowledge.power.models.Group
import app.com.knowledge.power.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MembersAdapter(
    var membersList: ArrayList<User>,
    var context: Context,
    var callback: MemberCallback,
    var adminId: String
) :
    RecyclerView.Adapter<MembersAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem =
            layoutInflater.inflate(R.layout.group_member_item_row, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (membersList.isNotEmpty()) {
            val member = membersList[position]

            val userId = FirebaseAuth.getInstance().currentUser?.uid

            holder.tvMemberName.text = member.name

            holder.tvAvatar.text = member.name.substring(0, 2).uppercase()
            if (member.id.equals(adminId)) {
                holder.tvAdmin.visibility = View.VISIBLE
            } else if (userId.equals(adminId)) {
                holder.ivArrow.visibility = View.VISIBLE
            }

            holder.itemView.setOnClickListener {
                if (userId.equals(adminId)) {
                    if (member.id.equals(adminId)) {
                        return@setOnClickListener
                    }
                    callback.onMemberClicked(membersList[holder.absoluteAdapterPosition])
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return membersList.size
    }

    interface MemberCallback {
        fun onMemberClicked(user: User)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvAvatar: TextView = itemView.findViewById(R.id.tvAvatar)
        var tvMemberName: TextView = itemView.findViewById(R.id.tvMemberName)
        var tvAdmin: TextView = itemView.findViewById(R.id.tvAdmin)
        var ivArrow: ImageView = itemView.findViewById(R.id.ivRight)

    }
}