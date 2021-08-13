package com.skysam.hchirinos.rosqueteslucy.ui.sales

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import java.text.DateFormat

/**
 * Created by Hector Chirinos (Home) on 13/8/2021.
 */
class SalesAdapter(private var sales: MutableList<Sale>):
    RecyclerView.Adapter<SalesAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_sale_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SalesAdapter.ViewHolder, position: Int) {
        val item = sales[position]
        holder.name.text = "${item.costumer?.name} - ${item.location?.name}"
        holder.price.text = item.price.toString()
        holder.date.text = DateFormat.getDateInstance().format(item.date)
    }

    override fun getItemCount(): Int = sales.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val price: TextView = view.findViewById(R.id.tv_price)
        val date: TextView = view.findViewById(R.id.tv_date)
        val ivPaid: ImageView = view.findViewById(R.id.iv_paid)
    }

    fun updateList(newList: MutableList<Sale>) {
        sales = newList
        notifyDataSetChanged()
    }
}