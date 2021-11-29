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
import com.example.e_commercetokobangunan_koma.models.ShopProductList
import java.io.IOException

class ShopProductListAdapter (private val mContext: Context)
    : RecyclerView.Adapter<ShopProductListAdapter.ViewHolder>() {

    private var products: MutableList<ShopProductList>? = null

    fun setSelectedImages(products: MutableList<ShopProductList>){
        this.products = products
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageProduct: ImageView
        val name: TextView
        val stock: TextView
        val sold: TextView
        val productStatus: TextView

        init {
            imageProduct = view.findViewById(R.id.img_item_product)
            name = view.findViewById(R.id.shop_product_name)
            stock = view.findViewById(R.id.shop_product_stock)
            sold = view.findViewById(R.id.shop_product_sold)
            productStatus = view.findViewById(R.id.shop_product_product_status)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shop_product_list, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(products != null){
            if(products?.get(position) != null ){

            }
        }
    }

    override fun getItemCount(): Int {
        if(products != null){
            return products!!.size
        } else {
            return 0
        }
    }

}