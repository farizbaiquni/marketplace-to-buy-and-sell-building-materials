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
import com.example.e_commercetokobangunan_koma.MainActivity
import com.example.e_commercetokobangunan_koma.ProductDetailActivity
import com.example.e_commercetokobangunan_koma.R
import com.example.e_commercetokobangunan_koma.ShopActivity
import com.example.e_commercetokobangunan_koma.models.ChartsModel
import com.squareup.picasso.Picasso
import java.lang.Exception

class ChartsAdapter(ctx: Context): RecyclerView.Adapter<ChartsAdapter.ViewHolder>() {

    private var mContext = ctx
    private var chartsList: MutableList<ChartsModel>? = null

    fun setChartsList(data: MutableList<ChartsModel>){
        this.chartsList = data
        notifyDataSetChanged()
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val cardView: CardView
        val index: TextView
        val foto: ImageView
        val nama: TextView
        val provinsi: TextView
        val nilaiUlasan: TextView
        val jumlahUlasan: TextView

        init {
            cardView = view.findViewById(R.id.charts_item_card_view)
            index = view.findViewById(R.id.charts_item_index)
            foto = view.findViewById(R.id.charts_item_foto)
            nama = view.findViewById(R.id.charts_item_nama)
            provinsi = view.findViewById(R.id.charts_item_provinsi)
            nilaiUlasan = view.findViewById(R.id.charts_item_nilai_ulasan)
            jumlahUlasan = view.findViewById(R.id.charts_item_jumlah_ulasan)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shop_charts, parent, false)

        return ChartsAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChartsAdapter.ViewHolder, position: Int) {
        if(!chartsList.isNullOrEmpty()){
            if(chartsList?.get(position) != null){
                holder.index.text = ( position + 1).toString()
                try {
                    Picasso.get().load(chartsList!!.get(position).foto).centerCrop().into(holder.foto);
                }catch(e: Exception){

                }
                holder.nama.text =  chartsList!!.get(position).nama
                holder.provinsi.text =  chartsList!!.get(position).provinsi
                holder.nilaiUlasan.text =  chartsList!!.get(position).nilai_ulasan
                holder.jumlahUlasan.text =  chartsList!!.get(position).jumlah_ulasan + " ulasan"
                holder.cardView.setOnClickListener(View.OnClickListener {
                    var intent: Intent = Intent(mContext, ShopActivity::class.java).apply {
                        putExtra("idShop", chartsList!!.get(position).id_shop)
                    }
                    mContext.startActivity(intent)
                })
            }
        }
    }

    override fun getItemCount(): Int {
        if(!chartsList.isNullOrEmpty()){
            return chartsList!!.size
        }else{
            return 0
        }
    }
}