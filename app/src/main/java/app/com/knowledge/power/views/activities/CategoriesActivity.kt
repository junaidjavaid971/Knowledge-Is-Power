package app.com.knowledge.power.views.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import app.com.knowledge.power.R
import app.com.knowledge.power.adapters.CategoriesAdapter
import app.com.knowledge.power.adapters.MembersAdapter
import app.com.knowledge.power.databinding.ActivityCategoriesBinding
import app.com.knowledge.power.models.Category
import app.com.knowledge.power.models.User
import app.com.knowledge.power.utils.Commons
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jaredrummler.android.colorpicker.ColorPickerDialog
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener


class CategoriesActivity : AppCompatActivity(), ColorPickerDialogListener {
    lateinit var binding: ActivityCategoriesBinding
    lateinit var user: User
    var selectedColor = 0

    var defaultGroupId = ""
    val categoriesList = ArrayList<Category>()
    lateinit var colorDialog: ColorPickerDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView(this@CategoriesActivity, R.layout.activity_categories)

        defaultGroupId = intent?.getStringExtra("groupId").toString()
        getCurrentUser()
        manageClicks()
    }

    private fun manageClicks() {
        binding.ivColor.setOnClickListener {
            colorDialog = ColorPickerDialog.newBuilder()
            colorDialog.setColor(
                ContextCompat.getColor(
                    this@CategoriesActivity,
                    R.color.primaryColor
                )
            )
            colorDialog.show(this@CategoriesActivity)

        }

        binding.ivAddCategory.setOnClickListener {
            val categoryName = binding.edCategoryName.text.toString()
            if (categoryName.isEmpty()) {
                Commons.showToast(this@CategoriesActivity, "Please set a category name!")
                return@setOnClickListener
            } else if (selectedColor == 0) {
                Commons.showToast(
                    this@CategoriesActivity,
                    "Please select a color for this category!"
                )
                return@setOnClickListener
            } else {
                val category =
                    Category(
                        selectedColor,
                        categoryName,
                        Commons.getRandomNumberString(10),
                        defaultGroupId
                    )
                addCategory(category)
            }
        }
        binding.headerLayout.ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun addCategory(category: Category) {
        val ref = FirebaseDatabase.getInstance().getReference("Categories").child(defaultGroupId)
            .child(category.id)
        ref.setValue(category)

        binding.edCategoryName.text = null
        Commons.showToast(this@CategoriesActivity, "Category Added!")
    }

    private fun getCurrentUser() {
        Commons.showProgress(this@CategoriesActivity)
        val reference = FirebaseDatabase.getInstance().getReference("Users")
            .child(FirebaseAuth.getInstance().currentUser?.uid.toString())
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Commons.hideProgress()
                user = snapshot.getValue(User::class.java)!!

                getCategories()
            }

            override fun onCancelled(error: DatabaseError) {
                Commons.hideProgress()
                Commons.addLog(error.message)
            }
        })
    }

    private fun getCategories() {
        val ref = FirebaseDatabase.getInstance().getReference("Categories").child(defaultGroupId)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriesList.clear()
                for (dataSnapshot in snapshot.children) {
                    val category = dataSnapshot.getValue(Category::class.java)
                    if (category != null) {
                        categoriesList.add(category)
                    }
                }
                val linearLayoutManager = LinearLayoutManager(
                    this@CategoriesActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                Commons.hideProgress()
                if (categoriesList.isEmpty()) {
                    binding.rvCategories.visibility = View.GONE
                    binding.lottieAnim.visibility = View.VISIBLE
                    return
                } else {
                    binding.rvCategories.visibility = View.VISIBLE
                    binding.lottieAnim.visibility = View.GONE
                }
                val categoriesAdapter =
                    CategoriesAdapter(
                        categoriesList,
                        this@CategoriesActivity,
                        defaultGroupId,
                        false
                    )
                binding.rvCategories.layoutManager = linearLayoutManager
                binding.rvCategories.adapter = categoriesAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Commons.hideProgress()
                Commons.addLog(error.message)
            }

        })
    }

    override fun onColorSelected(dialogId: Int, color: Int) {
        Commons.addLog("Color: $color")
        selectedColor = color
        binding.ivSelectedColor.visibility = View.VISIBLE
        binding.ivSelectedColor.setColorFilter(color)
    }

    override fun onDialogDismissed(dialogId: Int) {
    }
}