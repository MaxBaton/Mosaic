package com.example.mosaic.splitImage

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.mosaic.beforeSplitting.SaveBitmap
import com.example.mosaic.beforeSplitting.SplitActivity
import com.example.mosaic.databinding.WholeImageFragmentBinding

class WholeImageFragment: Fragment() {
    private var viewBinding: WholeImageFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = WholeImageFragmentBinding.inflate(layoutInflater)
        return viewBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val keySelected = arguments!!.getInt(SplitActivity.KEY_SELECTED)
        val wholeSelectedImage = if (keySelected != 1) {
            arguments!!.getString(SplitActivity.PHOTO_BITMAP)
        }else {
            SaveBitmap.bitmap!!.copy(Bitmap.Config.ARGB_8888,true)
        }
        val maxImageHeight  = arguments!!.getInt(SplitActivity.MAX_IMAGE_HEIGHT)

        with(viewBinding) {
            this!!.imageViewWholeImageFragment.maxHeight = maxImageHeight

            Glide
                .with(view.context)
                .load(wholeSelectedImage)
                .into(this.imageViewWholeImageFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
    }
}