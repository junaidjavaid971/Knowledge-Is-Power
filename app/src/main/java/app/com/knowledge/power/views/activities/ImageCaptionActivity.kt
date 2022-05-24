package app.com.knowledge.power.views.activities

import android.R.attr.data
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import app.com.knowledge.power.R
import app.com.knowledge.power.databinding.ActivityImageCaptionBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class ImageCaptionActivity : AppCompatActivity() {
    lateinit var binding: ActivityImageCaptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(
            this@ImageCaptionActivity,
            R.layout.activity_image_caption
        )

        val uri = intent?.getStringExtra("image")

        val imageUri: Uri? = uri?.toUri()
        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)

        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.image_load)
            .error(R.drawable.image_load)

        Glide.with(this).load(bitmap).apply(options).into(binding.selectedImg)
        binding.selectedImg.setImageBitmap(bitmap)

        binding.sendMessage.setOnClickListener {
            val intent = Intent()
            intent.putExtra("caption", binding.message.text.toString())
            intent.putExtra("uri", uri)
            setResult(RESULT_OK, intent)
            finish()
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
    }
}