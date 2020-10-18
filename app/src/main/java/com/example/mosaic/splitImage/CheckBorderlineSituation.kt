package com.example.mosaic.splitImage

import kotlin.math.sqrt

object CheckBorderlineSituation {
        fun isRightMost(position: Int, size: Int): Boolean {
            val numOfColumns = sqrt(size.toDouble()).toInt()
            return (position + 1) % numOfColumns == 0
        }

        fun isLeftMost(position: Int, size: Int): Boolean {
            val numOfColumns = sqrt(size.toDouble()).toInt()
            return position == 0 || position%numOfColumns == 0
        }

        fun isUpMost(position: Int, size: Int): Boolean {
            val numOfColumns = sqrt(size.toDouble()).toInt()
            return position < numOfColumns
        }

        fun isDownMost(position: Int, size: Int): Boolean {
            val numOfColumns = sqrt(size.toDouble()).toInt()
            return position > size - numOfColumns - 1
        }
}