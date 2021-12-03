package com.example.e_commercetokobangunan_koma.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.e_commercetokobangunan_koma.R
import com.smarteist.autoimageslider.SliderViewAdapter
import com.squareup.picasso.Picasso

class SliderExploreAdapter(): SliderViewAdapter<SliderExploreAdapter.SliderViewHolder>() {

    private var photosUrl: MutableList<String>? = null

    fun setPhotosUrl(photos: MutableList<String>){
        this.photosUrl = photos
        notifyDataSetChanged()
    }

    class SliderViewHolder(view: View): ViewHolder(view) {
        val imageView: ImageView
        init {
            imageView = view.findViewById(R.id.image_view_slider_product_detail)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup): SliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slider_product_detail, parent, false)
        return SliderExploreAdapter.SliderViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: SliderViewHolder, position: Int) {
        if(photosUrl != null){
            if(photosUrl?.get(position) != null){
                Picasso.get().load(photosUrl?.get(position)).centerCrop().resize(300, 300).into(viewHolder.imageView)
            }
        }
    }

    override fun getCount(): Int {
        if(photosUrl != null){
            return photosUrl!!.size
        } else {
            return 0
        }
    }

}