package app.com.knowledge.power.views

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import app.com.knowledge.power.adapters.CategoriesBottomSheetAdapter
import app.com.knowledge.power.databinding.CategoriesBottomSheetBinding
import app.com.knowledge.power.models.Category
import app.com.knowledge.power.views.activities.CommentsActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CategoriesBottomSheet(
    var locationId: String,
    var latitude: Double,
    var longitude: Double,
    var categoriesList: ArrayList<Category>,
    var isAdmin: Boolean
) : BottomSheetDialogFragment(), CategoriesBottomSheetAdapter.CategoryCallback {
    var binding: CategoriesBottomSheetBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = CategoriesBottomSheetBinding.inflate(inflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet = view.parent as View
        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR
        bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT)
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)

        val categoriesAdapter =
            CategoriesBottomSheetAdapter(
                categoriesList,
                requireActivity(),
                locationId,
                latitude,
                longitude = longitude,
                isAdmin,
                this
            )
        val linearLayoutManager = LinearLayoutManager(
            requireActivity(),
            LinearLayoutManager.VERTICAL,
            false
        )
        binding?.rvCategories?.layoutManager = linearLayoutManager
        binding?.rvCategories?.adapter = categoriesAdapter
    }

    override fun onCategoryClicked(category: Category) {
        startActivity(
            Intent(context, CommentsActivity::class.java).putExtra(
                "locationId",
                locationId
            ).putExtra("latitude", latitude)
                .putExtra("longitude", longitude)
                .putExtra("isAdmin", isAdmin)
                .putExtra("colorCode", category.color)
        )
        activity?.supportFragmentManager!!.beginTransaction()
            .remove(this@CategoriesBottomSheet).commit()
    }
}