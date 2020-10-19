package com.example.mosaic.splitImage

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.mosaic.R
import com.example.mosaic.beforeSplitting.SaveBitmap
import com.example.mosaic.beforeSplitting.SplitActivity
import com.example.mosaic.databinding.SplitImageFragmentBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chunk_image.view.*
import kotlin.math.sqrt

class SplitImageFragment: Fragment() {
    private var viewBinding: SplitImageFragmentBinding? = null
    private var chunksImage: MutableList<Bitmap>? = null
    //private lateinit var selectedImage: String

    companion object {
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
        val selectedImage = if (keySelected != 1) {
            arguments!!.getString(SplitActivity.PHOTO_BITMAP)!!
        }else {
            SaveBitmap.bitmap!!.copy(Bitmap.Config.ARGB_8888,true)
        }
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
                        var actionBarHeight = 0
                        val tv = TypedValue()
                        if (activity!!.theme.resolveAttribute(android.R.attr.actionBarSize,tv,true)) {
                            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,resources.displayMetrics)
                        }
                        val additionalSpaces = (sqrt(numOfChunks.toDouble()).toInt()  - 1) * SPACE_IN_PIXEL

                        photoBitmap = Bitmap.createScaledBitmap(resource,imageViewWidth,
                                        imageViewHeight - actionBarHeight - additionalSpaces,true)

        val groupAdapter = GroupAdapter<GroupieViewHolder>()

        with(viewBinding) {
            val myLayoutManager = object: GridLayoutManager(view.context, sqrt(numOfChunks.toDouble()).toInt()) {
                override fun canScrollVertically() = false

                override fun canScrollHorizontally() = false
            }

            this!!.recyclerViewSplitPhoto.apply {
                layoutManager = myLayoutManager
                addItemDecoration(RecyclerViewItemDecorator(SPACE_IN_PIXEL))
                setHasFixedSize(true)
                adapter = groupAdapter
            }
            if (chunksImage != null) {
                groupAdapter.clear()
            }

            val oldNewImages = SplitImage.splitImage(photoBitmap!!,numOfChunks)
            chunksImage = oldNewImages.first
            val shuffleChunksImage = oldNewImages.second
            shuffleChunksImage.forEachIndexed { index, bitmap ->
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
                            if (!CheckBorderlineSituation.isRightMost((groupAdapter.getItem(viewHolder.adapterPosition)
                                        as ChunkPhotoItem).position, shuffleChunksImage.size)) {
                                val positionSwiped = viewHolder.adapterPosition
                                val positionTarget = positionSwiped + 1

                                val itemSwiped = groupAdapter.getItem(positionSwiped)
                                val itemTarget = groupAdapter.getItem(positionTarget)

                                //groupAdapter.notifyItemMoved(positionSwiped,positionTarget)
                                //groupAdapter.getItem(positionTarget).notifyChanged(itemSwiped)
                                //groupAdapter.getItem(positionSwiped).notifyChanged(itemTarget)

//                               this is work, but very bad!!!
                                SwapElements.swapElement(groupAdapter,shuffleChunksImage,itemSwiped,itemTarget,positionSwiped,positionTarget)
                                if(isListsEquals(shuffleChunksImage)) {
                                    EndOfTheGame.changeMosaicToImage(splitImageFragment = this@SplitImageFragment,
                                        viewBinding = viewBinding!!,photoBitmap = photoBitmap)
                                }
                            }else {
                                val positionSwiped = viewHolder.adapterPosition
                                groupAdapter.notifyItemChanged(positionSwiped)
                            }
                        }
                        ItemTouchHelper.LEFT -> {
                            if (!CheckBorderlineSituation.isLeftMost((groupAdapter.getItem(viewHolder.adapterPosition) as ChunkPhotoItem).position, shuffleChunksImage.size)) {
                                val positionSwiped = viewHolder.adapterPosition
                                val positionTarget = positionSwiped - 1

                                val itemSwiped = groupAdapter.getItem(positionSwiped)
                                val itemTarget = groupAdapter.getItem(positionTarget)

                                //groupAdapter.notifyItemMoved(positionSwiped,positionTarget)
                                //groupAdapter.getItem(positionTarget).notifyChanged(itemSwiped)
                                //groupAdapter.getItem(positionSwiped).notifyChanged(itemTarget)

//                               this is work, but very bad!!!
                                SwapElements.swapElement(groupAdapter,shuffleChunksImage,itemSwiped,itemTarget,positionSwiped,positionTarget)
                                if(isListsEquals(shuffleChunksImage)) {
                                    EndOfTheGame.changeMosaicToImage(splitImageFragment = this@SplitImageFragment,
                                        viewBinding = viewBinding!!,photoBitmap = photoBitmap)
                                }
                            }else {
                                val positionSwiped = viewHolder.adapterPosition
                                groupAdapter.notifyItemChanged(positionSwiped)
                            }
                        }
                        ItemTouchHelper.UP -> {
                            if (!CheckBorderlineSituation.isUpMost((groupAdapter.getItem(viewHolder.adapterPosition) as ChunkPhotoItem).position, shuffleChunksImage.size)) {
                                val positionSwiped = viewHolder.adapterPosition
                                val positionTarget = positionSwiped - sqrt(shuffleChunksImage.size.toDouble()).toInt()

                                val itemSwiped = groupAdapter.getItem(positionSwiped)
                                val itemTarget = groupAdapter.getItem(positionTarget)

                                //groupAdapter.notifyItemMoved(positionSwiped,positionTarget)
                                //groupAdapter.getItem(positionTarget).notifyChanged(itemSwiped)
                                //groupAdapter.getItem(positionSwiped).notifyChanged(itemTarget)

//                               this is work, but very bad!!!
                                SwapElements.swapElement(groupAdapter,shuffleChunksImage,itemSwiped,itemTarget,positionSwiped,positionTarget)
                                if(isListsEquals(shuffleChunksImage)) {
                                    EndOfTheGame.changeMosaicToImage(splitImageFragment = this@SplitImageFragment,
                                        viewBinding = viewBinding!!,photoBitmap = photoBitmap)
                                }
                            }else {
                                val positionSwiped = viewHolder.adapterPosition
                                groupAdapter.notifyItemChanged(positionSwiped)
                            }
                        }
                        ItemTouchHelper.DOWN -> {
                            if (!CheckBorderlineSituation.isDownMost((groupAdapter.getItem(viewHolder.adapterPosition) as ChunkPhotoItem).position, shuffleChunksImage.size)) {
                                val positionSwiped = viewHolder.adapterPosition
                                val positionTarget = positionSwiped + sqrt(shuffleChunksImage.size.toDouble()).toInt()

                                val itemSwiped = groupAdapter.getItem(positionSwiped)
                                val itemTarget = groupAdapter.getItem(positionTarget)

                                //groupAdapter.notifyItemMoved(positionSwiped,positionTarget)
                                //groupAdapter.getItem(positionTarget).notifyChanged(itemSwiped)
                                //groupAdapter.getItem(positionSwiped).notifyChanged(itemTarget)

//                               this is work, but very bad!!!
                                SwapElements.swapElement(groupAdapter,shuffleChunksImage,itemSwiped,itemTarget,positionSwiped,positionTarget)
                                if(isListsEquals(shuffleChunksImage)) {
                                    EndOfTheGame.changeMosaicToImage(splitImageFragment = this@SplitImageFragment,
                                        viewBinding = viewBinding!!,photoBitmap = photoBitmap)
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

    private fun isListsEquals(shuffleChunksPhoto: MutableList<Bitmap>) = shuffleChunksPhoto == chunksImage

    private fun getDirections() = ItemTouchHelper.UP or ItemTouchHelper.DOWN or
                ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT

    override fun onDestroyView() {
        super.onDestroyView()
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
