package com.example.e_commercetokobangunan_koma.adapters

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commercetokobangunan_koma.R
import java.io.IOException

class AddProductAdapter(private val mContext: Context)
    : RecyclerView.Adapter<AddProductAdapter.ViewHolder>() {

    private var selectedImages: MutableList<Uri>? = null

    fun setSelectedImages(selectedImages: MutableList<Uri>){
        this.selectedImages = selectedImages
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageProduct: ImageView
        val default: TextView

        init {
            imageProduct = view.findViewById(R.id.img_item_product)
            default = view.findViewById(R.id.text_view_default_photo)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_product, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(!selectedImages.isNullOrEmpty()){
            if(position == 0){
                holder.default.visibility = View.VISIBLE
            }
            var uri: Uri = selectedImages!!.get(position) ?: return

            try {
                var bitmap: Bitmap = MediaStore.Images.Media.getBitmap(mContext.contentResolver, uri)
                holder.imageProduct.setImageBitmap(bitmap)
            } catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    override fun getItemCount(): Int {
        if(selectedImages != null){
            return selectedImages!!.size
        } else {
            return 0
        }
    }
}