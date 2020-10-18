package com.example.mosaic

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import com.example.mosaic.databinding.ActivityMainBinding
import com.example.mosaic.pickImage.PickImageFragment
import com.example.mosaic.splitImage.CloseAppService
import kotlinx.android.parcel.Parcelize
import java.io.*

@Parcelize
class MainActivity : AppCompatActivity(), Parcelable {
    private lateinit var binding: ActivityMainBinding
    private val pickImageFragment = PickImageFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val closeAppService = Intent(this, CloseAppService::class.java)
//        closeAppService.putExtra("activity",this)
//        startService(closeAppService)

        binding = ActivityMainBinding.inflate(layoutInflater)
        with(binding) {
            setContentView(root)

            btnSelectPhoto.setOnClickListener {
                //toast("пока без этого")
                val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                startActivityForResult(intent,0)
            }

            btnSelectDeafultPicture.setOnClickListener {
                supportFragmentManager.beginTransaction().add(android.R.id.content,pickImageFragment)
                    .addToBackStack("pickImageFragment").commit()
            }
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 1) {
                supportActionBar!!.title = "Выбор картинки"

                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
                supportActionBar!!.setDisplayShowHomeEnabled(true)
            }else {
                supportActionBar!!.title = "Mosaic"

                supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                supportActionBar!!.setDisplayShowHomeEnabled(false)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) supportFragmentManager.popBackStack()

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            val selectPhotoUri = data.data

            val bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(contentResolver, selectPhotoUri)
            } else {
                val source = ImageDecoder.createSource(contentResolver, selectPhotoUri!!)
                ImageDecoder.decodeBitmap(source)
            }
            val intent = Intent(this, SplitActivity::class.java)


//            val stream = ByteArrayOutputStream()
//            bitmap!!.compress(Bitmap.CompressFormat.WEBP, 100, stream)
//            val byteArray = stream.toByteArray()
//            intent.putExtra(SELECT_PHOTO_BITMAP, byteArray)

//            val gson = Gson()
//            val json = gson.toJson(bitmap)
//            intent.putExtra(SELECT_PHOTO_BITMAP,json.toString())

//            val gsonBuilder = GsonBuilder()
//            val gson = gsonBuilder.serializeNulls().create()
//            val outputStream = ByteArrayOutputStream()
//            val writer = JsonWriter(OutputStreamWriter(outputStream,"UTF-8"))
//            writer.beginArray()
//            val json = gson.toJson(writer)
//            writer.endArray()
//            writer.close()

            val filePath = tempFileImage(this,bitmap,"bitmap")
//            intent.putExtra(SELECT_PHOTO_BITMAP,filePath)

//            intent.putExtra(SELECT_PHOTO_BITMAP,json)

//            intent.putExtra(SELECT_PHOTO_BITMAP, URL_VAN_DE_BEEK)

            intent.putExtra(KeysSelectedFrom.GALLERY_IMAGE,filePath)
            intent.putExtra(KeysSelectedFrom.SELECTED_IMAGE_KEY,KeysSelectedFrom.GALLERY_IMAGE_KEY)
            startActivity(intent)
        }
    }

    private fun tempFileImage(activity: Activity,bitmap: Bitmap, name: String): String {
        val outputDir = activity.cacheDir
        val imageFile = File(outputDir,"$name.png")
        val outputStream = FileOutputStream(imageFile)
        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        return imageFile.absolutePath
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        DeleteCache.deleteCache(this)
//    }
}