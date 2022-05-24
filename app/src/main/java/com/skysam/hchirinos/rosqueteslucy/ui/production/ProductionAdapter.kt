package com.skysam.hchirinos.rosqueteslucy.ui.production

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.ClassesCommon
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Production
import java.text.DateFormat
import java.util.*

/**
 * Created by Hector Chirinos on 23/05/2022.
 */

class ProductionAdapter(private var productions: MutableList<Production>):
    RecyclerView.Adapter<ProductionAdapter.ViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_refund_item, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = productions[position]
        holder.name.text = DateFormat.getDateInstance().format(item.date)
        holder.quantity.text = context.getString(R.string.text_quantity_production_item, item.quantity.toString())
        val total = item.price * item.quantity
        val symbol = if (item.isDolar) "$" else "Bs."
        val convert = ClassesCommon.convertDoubleToString(total / item.rate)
        holder.price.text = if (item.isDolar) context.getString(
            R.string.text_price_item, symbol,
            String.format(Locale.GERMANY, "%,.2f", total))
        else context.getString(
            R.string.text_price_convert_item, symbol,
            String.format(Locale.GERMANY, "%,.2f", total), convert)
        holder.date.visibility = View.GONE

        /*holder.card.setOnClickListener {
            val popMenu = PopupMenu(context, holder.card)
            popMenu.inflate(R.menu.menu_refunds_item)
            popMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    *//*R.id.menu_view-> onClick.viewDetails(item)
                    R.id.menu_delete-> onClick.delete(item)*//*
                }
                false
            }
            popMenu.show()
        }*/
    }

    override fun getItemCount(): Int = productions.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val price: TextView = view.findViewById(R.id.tv_price)
        val quantity: TextView = view.findViewById(R.id.tv_quantity)
        val date: TextView = view.findViewById(R.id.tv_date)
        val card: MaterialCardView = view.findViewById(R.id.card)
    }

    fun updateList(newList: MutableList<Production>) {
        productions = newList
        notifyDataSetChanged()
    }
}