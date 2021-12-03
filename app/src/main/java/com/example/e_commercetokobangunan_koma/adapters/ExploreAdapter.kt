package com.example.e_commercetokobangunan_koma.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.e_commercetokobangunan_koma.ProductDetailActivity
import com.example.e_commercetokobangunan_koma.R
import com.example.e_commercetokobangunan_koma.models.ProductListModel

class ExploreAdapter (ctx: Context)
    : RecyclerView.Adapter<ExploreAdapter.ViewHolder>() {

    private var mContext: Context = ctx
    private var products: MutableList<ProductListModel>? = null

    fun setProducts(products: MutableList<ProductListModel>){
        this.products = products
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardView: CardView
        val imageProduct: ImageView
        val name: TextView
        val harga: TextView

        init {
            cardView = view.findViewById(R.id.card_view_product_list)
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
            if(products?.get(position) != null){
                holder.name.text =  products!!.get(position).name
                holder.harga.text = "Rp." + products!!.get(position).price.toString()
                holder.cardView.setOnClickListener(View.OnClickListener {
                    var intent: Intent = Intent(mContext, ProductDetailActivity::class.java)
                    intent.putExtra("idProduct", products!!.get(position).idUser)
                    intent.putExtra("idUser", products!!.get(position).idUser)
                    mContext.startActivity(intent)
                })
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