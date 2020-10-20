package com.example.mosaic

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.bumptech.glide.Glide
import com.example.mosaic.beforeSplitting.SaveBitmap
import com.example.mosaic.beforeSplitting.SplitActivity
import com.example.mosaic.databinding.ActivityMainBinding
import com.example.mosaic.pickImage.PickImageFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.way_taking_image.view.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
class MainActivity : AppCompatActivity(), Parcelable {
    private lateinit var binding: ActivityMainBinding
    private val pickImageFragment = PickImageFragment()
    private var currentPhotoPath: String? = null

    companion object {
        const val GALLERY_REQUEST_CODE = 0
        const val CAMERA_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val groupAdapter = GroupAdapter<GroupieViewHolder>()

        with(binding) {
            setContentView(root)

            recyclerViewMain.apply {
                setHasFixedSize(true)
                adapter = groupAdapter
                addItemDecoration(DividerItemDecoration(this@MainActivity,DividerItemDecoration.VERTICAL))
            }

            WaysTakingPictures.images.forEachIndexed { index, s ->
                groupAdapter.add(WayTakingImageItem(imageWayTakingImageUrl = s, name = WaysTakingPictures.names[index]))
            }

            groupAdapter.setOnItemClickListener { item, _ ->
                val name = (item as WayTakingImageItem).name
                pickWayForTakingImage(name)
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

    private fun pickWayForTakingImage(name: String) {
        when (name) {
            //camera
            WaysTakingPictures.names[0] -> {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    // Ensure that there's a camera activity to handle the intent
                    takePictureIntent.resolveActivity(packageManager)?.also {
                        // Create the File where the photo should go
                        val photoFile: File? = try {
                            createImageFile()
                        } catch (ex: IOException) {
                            Log.e("CameraIntent",ex.toString())
                            null
                        }
                        // Continue only if the File was successfully created
                        photoFile?.also {
                            val photoURI: Uri = FileProvider.getUriForFile(
                                this@MainActivity,
                                "com.example.android.fileprovider",
                                it
                            )
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
                        }
                    }
                }
            }
            //gallery
            WaysTakingPictures.names[1] -> {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                intent.type = "image/*"
                startActivityForResult(intent, GALLERY_REQUEST_CODE)
            }
            //default image
            WaysTakingPictures.names[2] -> {
                supportFragmentManager.beginTransaction().add(android.R.id.content, pickImageFragment)
                    .addToBackStack("pickImageFragment").commit()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) supportFragmentManager.popBackStack()

        return super.onOptionsItemSelected(item)
    }

    private fun openSplitActivity(bitmap: Bitmap) {
        val intent = Intent(this, SplitActivity::class.java)
        SaveBitmap.bitmap = bitmap
        startActivity(intent)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
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
            openSplitActivity(bitmap)
        }else if (requestCode == 1 && resultCode == Activity.RESULT_OK /*&& data != null*/) {
            val file = File(currentPhotoPath)
            val source = ImageDecoder.createSource(this.contentResolver, Uri.fromFile(file))
            val bitmap = ImageDecoder.decodeBitmap(source)
            openSplitActivity(bitmap)
        }
    }

    inner class WayTakingImageItem( val imageWayTakingImageUrl: String, var name: String) : Item<GroupieViewHolder>() {
        override fun getLayout() = R.layout.way_taking_image

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            Glide
                .with(this@MainActivity)
                .load(imageWayTakingImageUrl)
                .into(viewHolder.itemView.image_view_way_taking_image)

            viewHolder.itemView.text_view_way_taking_image.text = name
        }
    }
}