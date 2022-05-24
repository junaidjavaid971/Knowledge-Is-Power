package app.com.knowledge.power.views.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import app.com.knowledge.power.R
import app.com.knowledge.power.databinding.ActivityViewImageBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ViewImageActivity : AppCompatActivity() {

    lateinit var binding: ActivityViewImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView(this@ViewImageActivity, R.layout.activity_view_image)

        val url = intent?.getStringExtra("image")

        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.image_load)
            .error(R.drawable.image_load)

        Glide.with(this).load(url).apply(options).into(binding.ivZoom)

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}