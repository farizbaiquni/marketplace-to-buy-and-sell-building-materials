package com.example.e_commercetokobangunan_koma.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commercetokobangunan_koma.R
import com.example.e_commercetokobangunan_koma.models.ProductForListModel

class ExploreAdapter ()
    : RecyclerView.Adapter<ExploreAdapter.ViewHolder>() {

    private var products: MutableList<ProductForListModel>? = null

    fun setProducts(products: MutableList<ProductForListModel>){
        this.products = products
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageProduct: ImageView
        val name: TextView
        val harga: TextView

        init {
            imageProduct = view.findViewById(R.id.product_list_photo)
            name = view.findViewById(R.id.product_list_name)
            harga = view.findViewById(R.id.product_list_price)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_list, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(products != null){
            if(products?.get(position) != null ){
                holder.name.text =  products!!.get(position).name
                holder.harga.text = "Rp." + products!!.get(position).harga.toString()
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