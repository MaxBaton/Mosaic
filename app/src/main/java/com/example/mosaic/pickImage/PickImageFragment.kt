package com.example.mosaic.pickImage

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.mosaic.beforeSplitting.KeysSelectedFrom
import com.example.mosaic.R
import com.example.mosaic.beforeSplitting.SplitActivity
import com.example.mosaic.databinding.PickImageFragmentBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.default_image.view.*

class PickImageFragment: Fragment() {
    private var viewBinding: PickImageFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = PickImageFragmentBinding.inflate(layoutInflater)
        return viewBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val groupAdapter = GroupAdapter<GroupieViewHolder>()

        with(viewBinding) {
            this!!.recyclerViewPickImage.apply {
                layoutManager = GridLayoutManager(view.context,3)
                adapter = groupAdapter
            }

            UrlPictures.urls.forEach {
                groupAdapter.add(DefaultPicture(it))
            }
        }

        groupAdapter.setOnItemClickListener { item, _ ->
            val url = (item as DefaultPicture).urlPicture
            val intent = Intent(view.context, SplitActivity::class.java)
            intent.putExtra(KeysSelectedFrom.DEFAULT_IMAGE,url)
            intent.putExtra(KeysSelectedFrom.SELECTED_IMAGE_KEY, KeysSelectedFrom.DEFAULT_IMAGE_KEY)
            startActivity(intent)
            //activity!!.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewBinding = null
        //activity!!.supportFragmentManager.popBackStack("pickImageFragment",0)
    }

    inner class DefaultPicture(val urlPicture: String) : Item<GroupieViewHolder>() {
        override fun getLayout() = R.layout.default_image

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            Glide
                .with(view!!.context)
                .load(urlPicture)
                //.placeholder(R.mipmap.ic_not_load_image)
                .into(viewHolder.itemView.image_view_default_image)
        }
    }
}