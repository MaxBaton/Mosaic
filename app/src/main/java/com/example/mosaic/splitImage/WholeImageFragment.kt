package com.example.mosaic.splitImage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.mosaic.SplitActivity
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

        val wholeSelectedImage = arguments!!.getString(SplitActivity.PHOTO_BITMAP)
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