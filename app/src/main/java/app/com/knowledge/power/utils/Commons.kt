package app.com.knowledge.power.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import app.com.knowledge.power.R
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object Commons {
    private const val TAG = "LoadingDialog"
    var loadingDialogFragment = LoadingDialog()
    fun showToast(context: Context?, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showProgress(activity: AppCompatActivity) {
        if (!loadingDialogFragment.isAdded) {
            loadingDialogFragment.show(activity.supportFragmentManager, TAG)
        }
    }

    fun hideProgress() {
        if (loadingDialogFragment.isAdded) {
            loadingDialogFragment.dismissAllowingStateLoss()
        }
    }

    val formattedDate: String
        get() {
            val cal = Calendar.getInstance()
            val format = SimpleDateFormat("dd-MMM-yyyy hh:mm:ss", Locale.getDefault())
            format.timeZone = TimeZone.getTimeZone("GMT")
            return format.format(cal.time)
        }

    fun formattedTime(time: String): String {
        val format = SimpleDateFormat("dd-MMM-yyyy hh:mm:ss", Locale.getDefault())
        val format2 = SimpleDateFormat("dd/MM/yy hh:mm", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("GMT")
        val date = format.parse(time) as Date
        return format2.format(date)
    }

    fun getRandomNumberString(length: Int): String {
        val chars = "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray()

        val sb = StringBuilder(length)
        val random = Random()
        for (i in 0 until length) {
            val c = chars[random.nextInt(chars.size)]
            sb.append(c)
        }
        val output = sb.toString()

        return output
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        var bitmap: Bitmap? = null
        if (drawable is BitmapDrawable) {
            val bitmapDrawable = drawable
            if (bitmapDrawable.bitmap != null) {
                return bitmapDrawable.bitmap
            }
        }
        bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun getFile(context: Context, filename: String, bitmap: Bitmap): Uri {
        val file = File(context.cacheDir, filename)
        file.createNewFile()
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)

        val bitmapdata: ByteArray = bos.toByteArray()
        val fos = FileOutputStream(file)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()

        return file.toUri()
    }

    fun addLog(message: String?) {
        Log.v("LogCat", message.toString())
    }

    fun showAlertDialog(
        title: String?,
        message: String?,
        context: AppCompatActivity?,
        callback: DialogButtonsCallback?
    ) {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = context?.layoutInflater
        val dialogView = inflater?.inflate(R.layout.alert_dialog_layout, null)
        dialogBuilder.setView(dialogView)
        val tvTitle = dialogView?.findViewById<TextView>(R.id.tvHeading)
        val tvMessage = dialogView?.findViewById<TextView>(R.id.tvDetails)
        val btnOk = dialogView?.findViewById<TextView>(R.id.btnOk)
        val alertDialog = dialogBuilder.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        tvMessage?.text = message
        tvTitle?.text = title
        btnOk?.setOnClickListener { view: View? ->
            alertDialog.dismiss()
            callback?.onDialogPositiveClick()
        }
    }

    fun showConfirmationDialog(
        title: String?,
        message: String?,
        context: AppCompatActivity?,
        callback: DialogButtonsCallback?
    ) {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = context?.layoutInflater
        val dialogView = inflater?.inflate(R.layout.confirmation_dialog_layout, null)
        dialogBuilder.setView(dialogView)
        val tvTitle = dialogView?.findViewById<TextView>(R.id.tvHeading)
        val tvMessage = dialogView?.findViewById<TextView>(R.id.tvDetails)
        val btnOk = dialogView?.findViewById<TextView>(R.id.btnOk)
        val btnCancel = dialogView?.findViewById<TextView>(R.id.btnCancel)
        val alertDialog = dialogBuilder.create()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        tvMessage?.text = message
        tvTitle?.text = title
        btnOk?.setOnClickListener { view: View? ->
            alertDialog.dismiss()
            callback?.onDialogPositiveClick()
        }
        btnCancel?.setOnClickListener { view: View? ->
            alertDialog.dismiss()
        }
    }

    interface DialogButtonsCallback {
        fun onDialogPositiveClick()
    }
}