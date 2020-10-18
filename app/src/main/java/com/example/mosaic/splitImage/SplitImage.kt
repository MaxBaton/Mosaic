package com.example.mosaic.splitImage

import android.graphics.Bitmap
import kotlin.math.sqrt

object SplitImage {
    fun splitImage(photoBitmap: Bitmap, size: Int): Pair<MutableList<Bitmap>,MutableList<Bitmap>> {
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

        val oldChunksPhoto = newChunksPhoto
        newChunksPhoto = newChunksPhoto.shuffled().toMutableList()
        return oldChunksPhoto to newChunksPhoto
    }
}