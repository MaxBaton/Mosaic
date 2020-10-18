package com.example.mosaic.splitImage

import android.graphics.Bitmap
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import java.util.*

object SwapElements {
    fun swapElement(groupAdapter: GroupAdapter<GroupieViewHolder>, shuffleChunksPhoto: MutableList<Bitmap>,
                            itemSwiped: Item<GroupieViewHolder>, itemTarget: Item<GroupieViewHolder>, positionSwiped: Int,
                            positionTarget: Int) {

        Collections.swap(shuffleChunksPhoto,positionSwiped,positionTarget)

//        change position in items
        val itemSwipedChunkPhotoItem = (itemSwiped as SplitImageFragment.ChunkPhotoItem)
        val itemTargetChunkPhotoItem = (itemTarget as SplitImageFragment.ChunkPhotoItem)
        val swipePosition = itemSwipedChunkPhotoItem.position
        itemSwipedChunkPhotoItem.position = itemTargetChunkPhotoItem.position
        itemTargetChunkPhotoItem.position = swipePosition


        groupAdapter.removeGroupAtAdapterPosition(positionTarget)
        groupAdapter.add(positionTarget,itemSwiped)
        groupAdapter.removeGroupAtAdapterPosition(positionSwiped)
        groupAdapter.add(positionSwiped,itemTarget)
    }
}