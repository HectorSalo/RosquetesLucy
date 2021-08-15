package com.skysam.hchirinos.rosqueteslucy.ui.sales

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import java.text.DateFormat

/**
 * Created by Hector Chirinos (Home) on 13/8/2021.
 */
class SalesAdapter(private var sales: MutableList<Sale>, private val onClick: OnClick):
    RecyclerView.Adapter<SalesAdapter.ViewHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_sale_item, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SalesAdapter.ViewHolder, position: Int) {
        val item = sales[position]
        holder.name.text = context.getString(R.string.text_costumer_location,
            item.nameCostumer, item.location)
        holder.price.text = item.price.toString()
        holder.date.text = DateFormat.getDateInstance().format(item.date)

        val image = if (item.isPaid) R.drawable.ic_cash_check_56dp else R.drawable.ic_cash_remove_56dp
        holder.ivPaid.setImageResource(image)

        holder.card.setOnClickListener { onClick.viewSale(item) }
    }

    override fun getItemCount(): Int = sales.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val price: TextView = view.findViewById(R.id.tv_price)
        val date: TextView = view.findViewById(R.id.tv_date)
        val ivPaid: ImageView = view.findViewById(R.id.iv_paid)
        val card: MaterialCardView = view.findViewById(R.id.card)
    }

    fun updateList(newList: MutableList<Sale>) {
        sales = newList
        notifyDataSetChanged()
    }
}