package com.example.mosaic

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Parcelable
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.mosaic.databinding.ActivitySplitBinding
import com.example.mosaic.pickImage.UrlPictures
import com.example.mosaic.splitImage.CloseAppService
import com.example.mosaic.splitImage.SplitImageFragment
import com.example.mosaic.splitImage.WholeImageFragment
import kotlinx.android.parcel.Parcelize

@Parcelize
class SplitActivity : AppCompatActivity(), Parcelable {
    private lateinit var binding: ActivitySplitBinding
    private val splitImageFragment = SplitImageFragment()
    private val wholeImageFragment = WholeImageFragment()
    private var myMenu: Menu? = null
    private var maxImageHeight = 0


    companion object {
        const val PHOTO_BITMAP = "photo bitmap"
        const val NUM_OF_CHUNKS = "number of chunks"
        const val IMAGE_VIEW_HEIGHT = "image view height"
        const val IMAGE_VIEW_WIDTH = "image view width"
        const val KEY_SELECTED = "key selected"
        const val MAX_IMAGE_HEIGHT = "maximum height of image"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_split)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

//        val urlVanDeBeek = intent.getStringExtra(MainActivity.SELECT_PHOTO_BITMAP)
//
//        var photoBitmap: Bitmap? = null

        //val filePath = intent.getStringExtra(MainActivity.SELECT_PHOTO_BITMAP)
//        val bitmapOptions = BitmapFactory.Options()
//        bitmapOptions.inJustDecodeBounds = true
        //val photoBitmap = BitmapFactory.decodeFile(filePath/*,bitmapOptions*/)

        val closeAppService = Intent(this,CloseAppService::class.java)
        startService(closeAppService)

        binding = ActivitySplitBinding.inflate(layoutInflater)

        with(binding) {
            setContentView(root)

            val sendImage = selectedFromImage(intent.getStringExtra(KeysSelectedFrom.SELECTED_IMAGE_KEY))
            var keySelected = 0
            val loadImage = if (!UrlPictures.urls.contains(sendImage)) {
                keySelected = 1
                BitmapFactory.decodeFile(sendImage)
            }else {
                sendImage
            }

            btnSplit.setOnClickListener {
                val checkedId = radioGroupButton.checkedRadioButtonId
                val size = checkNumOfChunks(checkedId)
                if (size != 0) {
                    radioGroupButton.clearCheck()
                    val imageViewHeight = imageViewWholeImage.height
                    val imageViewWidth = imageViewWholeImage.width
//                    val imageViewWidth = imageViewWholeImage.width

                    val bundle = Bundle()
                    //bundle.putString(MainActivity.SELECT_PHOTO_BITMAP,filePath)
//                    bundle.putString(MainActivity.SELECT_PHOTO_BITMAP,json)


                    bundle.putString(PHOTO_BITMAP,sendImage)
                    bundle.putInt(NUM_OF_CHUNKS,size)
                    bundle.putInt(IMAGE_VIEW_HEIGHT,imageViewHeight)
                    bundle.putInt(IMAGE_VIEW_WIDTH,imageViewWidth)
                    bundle.putInt(KEY_SELECTED,keySelected)
                    splitImageFragment.arguments = bundle

                    supportFragmentManager.beginTransaction().add(android.R.id.content,splitImageFragment)
                        .addToBackStack("splitImageBackStack").commit()
                }else {
                    toast("Вы ничего не выбрали")
                }
            }

            supportFragmentManager.addOnBackStackChangedListener {
                myMenu!!.getItem(0).isVisible = supportFragmentManager.backStackEntryCount == 1
            }

            val actionBarHeight = getActionBarSize()
            val (widthScreen,heightScreen) = getWidthHeightScreen()
            maxImageHeight = heightScreen - actionBarHeight

            imageViewWholeImage.maxHeight = maxImageHeight
            imageViewWholeImage.maxWidth = widthScreen

            Glide
                .with(this@SplitActivity)
                .load(loadImage)
                .into(imageViewWholeImage)
        }
}

    private fun getWidthHeightScreen(): Pair<Int,Int> {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val widthScreen = metrics.widthPixels
        val heightScreen = metrics.heightPixels
        return widthScreen to heightScreen
    }

    private fun getActionBarSize(): Int {
        var actionBarHeight = 0
        val tv = TypedValue()
        if (theme.resolveAttribute(android.R.attr.actionBarSize,tv,true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,resources.displayMetrics)
        }
        return actionBarHeight
    }

    private fun selectedFromImage(selectedFrom: String?) = when(selectedFrom) {
        KeysSelectedFrom.DEFAULT_IMAGE_KEY -> intent.getStringExtra(KeysSelectedFrom.DEFAULT_IMAGE)
        KeysSelectedFrom.GALLERY_IMAGE_KEY -> intent.getStringExtra(KeysSelectedFrom.GALLERY_IMAGE)
        KeysSelectedFrom.CAMERA_IMAGE_KEY  -> intent.getStringExtra(KeysSelectedFrom.CAMERA_IMAGE)
        else -> ""
    }

    private fun checkNumOfChunks(checkedId: Int) =
        when(checkedId) {
            R.id.radio_button_9_chunks ->  9
            R.id.radio_button_16_chunks -> 16
            R.id.radio_button_25_chunks -> 25
            R.id.radio_button_36_chunks -> 36
            else -> 0
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        myMenu = menu
        menuInflater.inflate(R.menu.menu_splited_image_fragment,menu)
        menu!!.getItem(0).isVisible = false
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_show_whole_image) {
            val bundle = Bundle()
            val sendImage = selectedFromImage(intent.getStringExtra(KeysSelectedFrom.SELECTED_IMAGE_KEY))
            bundle.putString(PHOTO_BITMAP,sendImage)
            bundle.putInt(MAX_IMAGE_HEIGHT,maxImageHeight)
            wholeImageFragment.arguments = bundle

            supportFragmentManager.beginTransaction().add(android.R.id.content,wholeImageFragment)
                .addToBackStack("splitImageBackStack").commit()
        } else if (item.itemId == android.R.id.home) {
            when (supportFragmentManager.backStackEntryCount) {
                0 -> finish()
                1,2 -> supportFragmentManager.popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        DeleteCache.deleteCache(activity = this,application = application)
    }
}