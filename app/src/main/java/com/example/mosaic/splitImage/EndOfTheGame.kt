package com.example.mosaic.splitImage

import android.app.AlertDialog
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.example.mosaic.R
import com.example.mosaic.beforeSplitting.SplitActivity
import com.example.mosaic.databinding.SplitImageFragmentBinding

object EndOfTheGame {
    fun changeMosaicToImage(splitImageFragment: SplitImageFragment,viewBinding: SplitImageFragmentBinding,photoBitmap: Bitmap?) {
        Glide
            .with(splitImageFragment)
            .load(photoBitmap)
            .into(viewBinding.imageViewEndOfTheGame)

        //use animation
        val wholeImageAnimation = AnimationUtils.loadAnimation(splitImageFragment.context, R.anim.whole_image)
        wholeImageAnimation.setAnimationListener(object: Animation.AnimationListener{
            override fun onAnimationRepeat(animation: Animation?) {
                Log.d("AnimateImageView","repeat animation")
            }

            override fun onAnimationEnd(animation: Animation?) {
                val alertDialog = finalAlertDialog(splitImageFragment.activity)
                alertDialog.show()
            }

            override fun onAnimationStart(animation: Animation?) {
                Log.d("AnimateImageView","start animation")
            }
        })

        viewBinding.recyclerViewSplitPhoto.visibility = View.GONE
        viewBinding.imageViewEndOfTheGame.animation = wholeImageAnimation
        viewBinding.imageViewEndOfTheGame.visibility = View.VISIBLE
        SplitActivity.myMenu!!.getItem(0).isVisible = false //at hte moment so
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
}