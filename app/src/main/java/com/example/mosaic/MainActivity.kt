package com.example.mosaic

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.mosaic.beforeSplitting.KeysSelectedFrom
import com.example.mosaic.beforeSplitting.SaveBitmap
import com.example.mosaic.beforeSplitting.SplitActivity
import com.example.mosaic.databinding.ActivityMainBinding
import com.example.mosaic.pickImage.PickImageFragment
import kotlinx.android.parcel.Parcelize

@Parcelize
class MainActivity : AppCompatActivity(), Parcelable {
    private lateinit var binding: ActivityMainBinding
    private val pickImageFragment = PickImageFragment()

    private companion object {
        const val GALLERY_REQUEST_CODE = 0
        const val CAMERA_REQUEST_CODE = 1
    }

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
                val intent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                intent.type = "image/*"
                startActivityForResult(intent, GALLERY_REQUEST_CODE)
            }

            btnSelectDeafultPicture.setOnClickListener {
                supportFragmentManager.beginTransaction().add(
                    android.R.id.content,
                    pickImageFragment
                )
                    .addToBackStack("pickImageFragment").commit()
            }

            btnTakePhoto.setOnClickListener {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (intent.resolveActivity(packageManager) != null) {
                    startActivityForResult(intent, CAMERA_REQUEST_CODE)
                }
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
            SaveBitmap.bitmap = bitmap
            //intent.putExtra(KeysSelectedFrom.SELECTED_IMAGE_KEY, KeysSelectedFrom.GALLERY_IMAGE_KEY)
            startActivity(intent)
        }else if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val bitmap:Bitmap = data.extras!!["data"] as Bitmap

//            val selectedPhotoUri = data.data
//            val bitmap = if (Build.VERSION.SDK_INT < 28) {
//                MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
//            } else {
//                val source = ImageDecoder.createSource(contentResolver, selectedPhotoUri!!)
//                ImageDecoder.decodeBitmap(source)
//            }
            val intent = Intent(this, SplitActivity::class.java)
            SaveBitmap.bitmap = bitmap
//            //intent.putExtra(KeysSelectedFrom.SELECTED_IMAGE_KEY, KeysSelectedFrom.CAMERA_IMAGE_KEY)
            startActivity(intent)
        }
    }
}