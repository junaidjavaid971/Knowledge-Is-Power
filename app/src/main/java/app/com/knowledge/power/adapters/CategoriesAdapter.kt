package app.com.knowledge.power.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import app.com.knowledge.power.R
import app.com.knowledge.power.models.Category
import app.com.knowledge.power.utils.Commons
import app.com.knowledge.power.views.activities.CategoriesActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class CategoriesAdapter(
    var categoriesList: ArrayList<Category>,
    var context: Context,
    var groupId: String,
    var isFromDashboard: Boolean
) :
    RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem =
            layoutInflater.inflate(R.layout.category_item_row, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (categoriesList.isNotEmpty()) {
            val category = categoriesList[position]

            holder.tvCategoryName.text = category.categoryName

            holder.tvAvatar.text = category.categoryName.substring(0, 2).uppercase()
            val hexColor = String.format("#%06X", 0xFFFFFF and category.color)
            holder.tvAvatar.backgroundTintList =
                ColorStateList.valueOf(category.color)

            if (isFromDashboard) {
                holder.ivDelete.visibility = View.GONE
            }
            holder.ivDelete.setOnClickListener {
                if (!isFromDashboard) {
                    Commons.showConfirmationDialog(
                        "Delete this Category",
                        "Are you sure you want to delete this category?",
                        context as CategoriesActivity,
                        object : Commons.DialogButtonsCallback {
                            override fun onDialogPositiveClick() {
                                val ref = FirebaseDatabase.getInstance().getReference("Categories")
                                    .child(groupId).child(category.id)
                                ref.removeValue()
                                Commons.showToast(context, "Category Deleted")
                            }

                        }
                    )
                }
            }

            holder.itemView.setOnClickListener {
                if (isFromDashboard) {
                    Commons.showToast(context, "Hello")
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return categoriesList.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvAvatar: TextView = itemView.findViewById(R.id.tvAvatar)
        var tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        var ivDelete: ImageView = itemView.findViewById(R.id.ivDelete)
    }
}