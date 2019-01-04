package towntalk.com.imageeditor

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.CursorLoader
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.File


class MainActivity : AppCompatActivity() {


    private val activity = this@MainActivity

    lateinit var chooseImageView: TextView
    lateinit var closeView: TextView
    lateinit var imageView: TouchImageView
    lateinit var imageLayout: LinearLayout

    private val PERMISSION_REQUEST_CAMERA_CODE = 201
    private val PERMISSION_REQUEST_GALLARY_CODE = 202
    private val PERMISSION_REQUEST_CODE = 200

    var selectedImagePath: String = ""


    private val mMatrix = Matrix()
    private var mScale = 1f


    var imageFlag: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
    }

    fun init() {
        chooseImageView = findViewById(R.id.chooseImageView)
        closeView = findViewById(R.id.closeView)
        imageView = findViewById(R.id.imageView)
        imageLayout = findViewById(R.id.imageLayout)

        chooseImageView.setOnClickListener(clickListener)
        closeView.setOnClickListener(clickListener)
    }

    private val clickListener: View.OnClickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.chooseImageView -> {
                if (!checkPermission()) {
                    requestPermission()
                } else {
                    takePicture()
                }
            }

            R.id.closeView -> {
                imageFlag = true
                imageLayout.visibility = View.GONE
                chooseImageView.visibility = View.VISIBLE
            }
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            mScale = mScale * detector.scaleFactor
            mScale = Math.max(0.1f, Math.min(mScale, 5.0f))
            mMatrix.setScale(mScale, mScale)
            imageView.setImageMatrix(mMatrix)
            return true
        }
    }


    override fun onBackPressed() {
        if (imageFlag) {
            super.onBackPressed()
        } else {
            imageFlag = true
            imageLayout.visibility = View.GONE
            chooseImageView.visibility = View.VISIBLE
        }
    }

    fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission() {
        ActivityCompat.requestPermissions(
            activity, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {

            PERMISSION_REQUEST_CODE -> {
                if (grantResults.size > 0) {
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val readExternalAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    val writeExternalAccepted = grantResults[2] == PackageManager.PERMISSION_GRANTED

                    if (cameraAccepted && readExternalAccepted && writeExternalAccepted) {
                        takePicture()
                    } else {
                        Toast.makeText(this, "Permission not granted.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun takePicture() {
        val options = arrayOf<CharSequence>("Camera", "Gallery", "Cancel")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Option")
        builder.setItems(options) { dialog, item ->
            if (options[item] == "Camera") {

                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, PERMISSION_REQUEST_CAMERA_CODE)


            } else if (options[item] == "Gallery") {

                val pickPhoto =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhoto, PERMISSION_REQUEST_GALLARY_CODE)

            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PERMISSION_REQUEST_CAMERA_CODE) {

                val bmp = data!!.getExtras().get("data") as Bitmap
                val stream = ByteArrayOutputStream()
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                val destination = File(
                    Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis().toString() + ".jpg"
                )
                val byteArray = stream.toByteArray()
                val bitmap = BitmapFactory.decodeByteArray(
                    byteArray, 0,
                    byteArray.size
                )
                imageFlag = false
                imageLayout.visibility = View.VISIBLE
                chooseImageView.visibility = View.GONE
                selectedImagePath = destination.toString()
                imageView.setImageBitmap(bitmap);

            } else if (requestCode == PERMISSION_REQUEST_GALLARY_CODE) {

                val selectedImageUri = data!!.getData()
                val projection = arrayOf(MediaStore.MediaColumns.DATA)
                val cursorLoader = CursorLoader(
                    this, selectedImageUri, projection,
                    null, null, null
                )
                val cursor = cursorLoader.loadInBackground()
                val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                cursor.moveToFirst()

                imageFlag = false
                imageLayout.visibility = View.VISIBLE
                chooseImageView.visibility = View.GONE

                imageView.setImageURI(selectedImageUri)
                selectedImagePath = cursor.getString(column_index)
            }
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        imageLayout.visibility = View.GONE
        chooseImageView.visibility = View.VISIBLE
    }
}
