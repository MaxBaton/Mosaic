package com.example.mosaic.splitImage

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.get
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.mosaic.DeleteCache
import com.example.mosaic.R
import com.example.mosaic.SplitActivity
import com.example.mosaic.databinding.SplitImageFragmentBinding
import com.example.mosaic.toast
import com.google.android.flexbox.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chunk_image.view.*
import java.util.*
import kotlin.math.sqrt

class SplitImageFragment: Fragment() {
    private var viewBinding: SplitImageFragmentBinding? = null
    private var chunksPhoto: MutableList<Bitmap>? = null
    private lateinit var selectedImage: String

    companion object {
        const val IS_VISIBLE_MENU_ITEM = "visibility menu item"
        private const val SPACE_IN_PIXEL = 3
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = SplitImageFragmentBinding.inflate(layoutInflater)
        return viewBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val numOfChunks = arguments!!.getInt(SplitActivity.NUM_OF_CHUNKS)
        val imageViewHeight = arguments!!.getInt(SplitActivity.IMAGE_VIEW_HEIGHT)
        val imageViewWidth = arguments!!.getInt(SplitActivity.IMAGE_VIEW_WIDTH)
        val keySelected = arguments!!.getInt(SplitActivity.KEY_SELECTED)
        selectedImage = arguments!!.getString(SplitActivity.PHOTO_BITMAP)!!
//        val byteArray = arguments!!.getByteArray(SplitActivity.PHOTO_BITMAP)
//        val photoBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)

//        val filePath = arguments!!.getString(MainActivity.SELECT_PHOTO_BITMAP)
//        var photoBitmap = BitmapFactory.decodeFile(filePath)


//        var photoBitmap = getSelectedBitmap(keySelected,selectedImage) as Bitmap
        var photoBitmap: Bitmap? = null

        Glide
                .with(view.context)
                .asBitmap()
                .load(selectedImage)
                .into(object: CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        photoBitmap = resource
                        var bitmapHeight = photoBitmap!!.height
                        val bitmapWidth = photoBitmap!!.width

                        var actionBarHeight = 0
                        val tv = TypedValue()
                        if (activity!!.theme.resolveAttribute(android.R.attr.actionBarSize,tv,true)) {
                            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,resources.displayMetrics)
                        }

                        val metrics = getDisplayMetrics()
                        activity!!.windowManager.defaultDisplay.getMetrics(metrics)
                        val widthScreen = metrics.widthPixels
                        val heightScreen = metrics.heightPixels

                        val additionalSpaces = (sqrt(numOfChunks.toDouble()).toInt()  - 1) * SPACE_IN_PIXEL


        if (bitmapHeight >= heightScreen) {
//            val metrics = getDisplayMetrics()
//            val width = metrics.widthPixels
//            val height = metrics.heightPixels
//            bitmapHeight = heightScreen
//            photoBitmap = Bitmap.createScaledBitmap(photoBitmap!!,bitmapWidth,bitmapHeight,true)
                bitmapHeight = heightScreen
        }

//                        photoBitmap = Bitmap.createScaledBitmap(photoBitmap!!,bitmapWidth - additionalSpaces,
//                                                                bitmapHeight - actionBarHeight - additionalSpaces,true)
                        photoBitmap = Bitmap.createScaledBitmap(resource!!,imageViewWidth,
                                        imageViewHeight - actionBarHeight - additionalSpaces,true)

        val groupAdapter = GroupAdapter<GroupieViewHolder>()

        with(viewBinding) {
            val myLayoutManager = object: GridLayoutManager(view.context, sqrt(numOfChunks.toDouble()).toInt()) {
                override fun canScrollVertically() = false

                override fun canScrollHorizontally() = false
            }

//            val flexboxLayoutManager = FlexboxLayoutManager()
//            flexboxLayoutManager.apply {
//                flexWrap = FlexWrap.WRAP
//                flexDirection = FlexDirection.ROW
//                alignItems = AlignItems.STRETCH
//            }

            this!!.recyclerViewSplitPhoto.apply {
                layoutManager = myLayoutManager
                addItemDecoration(RecyclerViewItemDecorator(SPACE_IN_PIXEL))
                setHasFixedSize(true)
                adapter = groupAdapter
            }
            if (chunksPhoto != null) {
                groupAdapter.clear()
            }

            val shuffleChunksPhoto = splitImage(photoBitmap!!, numOfChunks)
            shuffleChunksPhoto.forEachIndexed { index, bitmap ->
                groupAdapter.add(ChunkPhotoItem(chunkPhotoBitmap = bitmap, position = index))
            }

            val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0, getDirections()
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ) = false


                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    when (direction) {
                        ItemTouchHelper.RIGHT -> {
                            if (!CheckBorderlineSituation.isRightMost((groupAdapter.getItem(viewHolder.adapterPosition) as ChunkPhotoItem).position, shuffleChunksPhoto.size)) {
                                val positionSwiped = viewHolder.adapterPosition
                                val positionTarget = positionSwiped + 1

                                val itemSwiped = groupAdapter.getItem(positionSwiped)
                                val itemTarget = groupAdapter.getItem(positionTarget)

                                //groupAdapter.notifyItemMoved(positionSwiped,positionTarget)
                                //groupAdapter.getItem(positionTarget).notifyChanged(itemSwiped)
                                //groupAdapter.getItem(positionSwiped).notifyChanged(itemTarget)

//                               this is work, but very bad!!!
                                swapElement(groupAdapter,shuffleChunksPhoto,itemSwiped,itemTarget,positionSwiped,positionTarget)
                                if(isListsEquals(shuffleChunksPhoto)) {
                                    changeMosaicToImage(photoBitmap)
                                }
                            }else {
                                val positionSwiped = viewHolder.adapterPosition
                                groupAdapter.notifyItemChanged(positionSwiped)
                            }
                        }
                        ItemTouchHelper.LEFT -> {
                            if (!CheckBorderlineSituation.isLeftMost((groupAdapter.getItem(viewHolder.adapterPosition) as ChunkPhotoItem).position, shuffleChunksPhoto.size)) {
                                val positionSwiped = viewHolder.adapterPosition
                                val positionTarget = positionSwiped - 1

                                val itemSwiped = groupAdapter.getItem(positionSwiped)
                                val itemTarget = groupAdapter.getItem(positionTarget)

                                //groupAdapter.notifyItemMoved(positionSwiped,positionTarget)
                                //groupAdapter.getItem(positionTarget).notifyChanged(itemSwiped)
                                //groupAdapter.getItem(positionSwiped).notifyChanged(itemTarget)

//                               this is work, but very bad!!!
                                swapElement(groupAdapter,shuffleChunksPhoto,itemSwiped,itemTarget,positionSwiped,positionTarget)
                                if(isListsEquals(shuffleChunksPhoto)) {
                                    changeMosaicToImage(photoBitmap)
                                }
                            }else {
                                val positionSwiped = viewHolder.adapterPosition
                                groupAdapter.notifyItemChanged(positionSwiped)
                            }
                        }
                        ItemTouchHelper.UP -> {
                            if (!CheckBorderlineSituation.isUpMost((groupAdapter.getItem(viewHolder.adapterPosition) as ChunkPhotoItem).position, shuffleChunksPhoto.size)) {
                                val positionSwiped = viewHolder.adapterPosition
                                val positionTarget = positionSwiped - sqrt(shuffleChunksPhoto.size.toDouble()).toInt()

                                val itemSwiped = groupAdapter.getItem(positionSwiped)
                                val itemTarget = groupAdapter.getItem(positionTarget)

                                //groupAdapter.notifyItemMoved(positionSwiped,positionTarget)
                                //groupAdapter.getItem(positionTarget).notifyChanged(itemSwiped)
                                //groupAdapter.getItem(positionSwiped).notifyChanged(itemTarget)

//                               this is work, but very bad!!!
                                swapElement(groupAdapter,shuffleChunksPhoto,itemSwiped,itemTarget,positionSwiped,positionTarget)
                                if(isListsEquals(shuffleChunksPhoto)) {
                                    changeMosaicToImage(photoBitmap)
                                }
                            }else {
                                val positionSwiped = viewHolder.adapterPosition
                                groupAdapter.notifyItemChanged(positionSwiped)
                            }
                        }
                        ItemTouchHelper.DOWN -> {
                            if (!CheckBorderlineSituation.isDownMost((groupAdapter.getItem(viewHolder.adapterPosition) as ChunkPhotoItem).position, shuffleChunksPhoto.size)) {
                                val positionSwiped = viewHolder.adapterPosition
                                val positionTarget = positionSwiped + sqrt(shuffleChunksPhoto.size.toDouble()).toInt()

                                val itemSwiped = groupAdapter.getItem(positionSwiped)
                                val itemTarget = groupAdapter.getItem(positionTarget)

                                //groupAdapter.notifyItemMoved(positionSwiped,positionTarget)
                                //groupAdapter.getItem(positionTarget).notifyChanged(itemSwiped)
                                //groupAdapter.getItem(positionSwiped).notifyChanged(itemTarget)

//                               this is work, but very bad!!!
                                swapElement(groupAdapter,shuffleChunksPhoto,itemSwiped,itemTarget,positionSwiped,positionTarget)
                                if(isListsEquals(shuffleChunksPhoto)) {
                                    changeMosaicToImage(photoBitmap)
                                }
                            }else {
                                val positionSwiped = viewHolder.adapterPosition
                                groupAdapter.notifyItemChanged(positionSwiped)
                            }
                        }
                    }
                }
            })
            helper.attachToRecyclerView(recyclerViewSplitPhoto)
        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        Log.d("ClearBitmap","bitmap is destroy")
                    }

                })
    }

    private fun getSelectedBitmap(keySelected: Int, selectedImage: String?) = when(keySelected) {
        0 -> {
            var bitmap: Bitmap? = null
                Glide
                    .with(view!!.context)
                    .asBitmap()
                    .load(selectedImage)
                    .into(object: CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            bitmap = resource
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            Log.d("clearBitmap","clear bitmap")
                        }
                    })
            bitmap
        }
        1 -> BitmapFactory.decodeFile(selectedImage)
        else -> null
    }

    private fun getDisplayMetrics(): DisplayMetrics {
        val windowManager: WindowManager = activity!!.getSystemService(Activity.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        return metrics
    }

    private fun changeMosaicToImage(photoBitmap: Bitmap?) {
        Glide
            .with(view!!.context)
            .load(photoBitmap)
            .into(viewBinding!!.imageViewEndOfTheGame)

        //use animation
        val wholeImageAnimation = AnimationUtils.loadAnimation(view!!.context,R.anim.whole_image)
        wholeImageAnimation.setAnimationListener(object: Animation.AnimationListener{
            override fun onAnimationRepeat(animation: Animation?) {
                Log.d("AnimateImageView","repeat animation")
            }

            override fun onAnimationEnd(animation: Animation?) {
                val alertDialog = finalAlertDialog(activity)
                alertDialog.show()
            }

            override fun onAnimationStart(animation: Animation?) {
                Log.d("AnimateImageView","start animation")
            }
        })

        viewBinding!!.recyclerViewSplitPhoto.visibility = View.GONE
        viewBinding!!.imageViewEndOfTheGame.animation = wholeImageAnimation
        viewBinding!!.imageViewEndOfTheGame.visibility = View.VISIBLE
    }

    private fun finalAlertDialog(activity: FragmentActivity?) = with(AlertDialog.Builder(activity)) {
        setTitle("Победа!!")
        setCancelable(false)

        setPositiveButton("Выход") { currentDialog,_ ->
            currentDialog.cancel()
            activity!!.supportFragmentManager.popBackStack()
            //activity!!.supportFragmentManager.beginTransaction().remove(this@SplitImageFragment).commit()
        }
        create()
    }

    private fun isListsEquals(shuffleChunksPhoto: MutableList<Bitmap>) = shuffleChunksPhoto == chunksPhoto

    private fun swapElement(groupAdapter: GroupAdapter<GroupieViewHolder>, shuffleChunksPhoto: MutableList<Bitmap>,
                            itemSwiped: Item<GroupieViewHolder>, itemTarget: Item<GroupieViewHolder>, positionSwiped: Int, positionTarget: Int) {

        Collections.swap(shuffleChunksPhoto,positionSwiped,positionTarget)

//        change position in items
        val itemSwipedChunkPhotoItem = (itemSwiped as ChunkPhotoItem)
        val itemTargetChunkPhotoItem = (itemTarget as ChunkPhotoItem)
        val swipePosition = itemSwipedChunkPhotoItem.position
        itemSwipedChunkPhotoItem.position = itemTargetChunkPhotoItem.position
        itemTargetChunkPhotoItem.position = swipePosition


        groupAdapter.removeGroupAtAdapterPosition(positionTarget)
        groupAdapter.add(positionTarget,itemSwiped)
        groupAdapter.removeGroupAtAdapterPosition(positionSwiped)
        groupAdapter.add(positionSwiped,itemTarget)
    }


    private fun splitImage(photoBitmap: Bitmap, size: Int): MutableList<Bitmap> {
            var newChunksPhoto = mutableListOf<Bitmap>()

            var xOffset = 0
            var yOffset = 0
            val widthChunkPhotoBitmap = photoBitmap.width / sqrt(size.toDouble()).toInt()
            val heightChunkPhotoBitmap = photoBitmap.height / sqrt(size.toDouble()).toInt()

            val excessHeightPixels = 0//if (photoBitmap.height >= 1920)  getExcessHeightPixels(sqrt(size.toDouble()).toInt()) else 0

            for (i in 0 until sqrt(size.toDouble()).toInt()) {
                for (j in 0 until sqrt(size.toDouble()).toInt()) {
                    val chunkPhotoBitmap = Bitmap.createBitmap(
                        photoBitmap,
                        xOffset,
                        yOffset,
                        widthChunkPhotoBitmap,
                        heightChunkPhotoBitmap - excessHeightPixels
                    )
                    newChunksPhoto.add(chunkPhotoBitmap)
                    xOffset += widthChunkPhotoBitmap
                }
                xOffset = 0
                yOffset += heightChunkPhotoBitmap
            }

            chunksPhoto = newChunksPhoto
            newChunksPhoto = newChunksPhoto.shuffled().toMutableList()
            return newChunksPhoto
        }

    private fun getDirections() = ItemTouchHelper.UP or ItemTouchHelper.DOWN or
                ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT

    override fun onDestroyView() {
        super.onDestroyView()
//        DeleteCache.deleteCache(activity = activity as Activity)

//        activity!!.supportFragmentManager.popBackStack()
        viewBinding = null
    }

    inner class ChunkPhotoItem( val chunkPhotoBitmap: Bitmap?, var position: Int) : Item<GroupieViewHolder>() {
        override fun getLayout() = R.layout.chunk_image

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val requestOption = RequestOptions
                                    .fitCenterTransform()
                                    .transform(RoundedCorners(5))
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .override(chunkPhotoBitmap!!.width,chunkPhotoBitmap.height)

            Glide
                .with(view!!.context)
                .load(chunkPhotoBitmap)
                .thumbnail(0.25F)
                .apply(requestOption)
                .into(viewHolder.itemView.image_view_chunk_image)
        }
    }
}
