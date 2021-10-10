package com.skysam.hchirinos.rosqueteslucy.ui.refunds

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Refund
import java.text.DateFormat
import java.util.*

/**
 * Created by Hector Chirinos (Home) on 30/9/2021.
 */
class RefundsAdapter(private var refunds: MutableList<Refund>, private val onClick: OnClick):
        RecyclerView.Adapter<RefundsAdapter.ViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_refund_item, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = refunds[position]
        holder.name.text = context.getString(R.string.text_costumer_location,
            item.nameCostumer, item.location)
        holder.quantity.text = context.getString(R.string.text_quantity_refund_item, item.quantity.toString())
        val total = item.price * item.quantity
        val symbol = if (item.isDolar) "$" else "Bs."
        holder.price.text = context.getString(R.string.text_price_item, symbol,
            String.format(Locale.GERMANY, "%,.2f", total))
        holder.date.text = DateFormat.getDateInstance().format(item.date)

        holder.card.setOnClickListener {
            val popMenu = PopupMenu(context, holder.card)
            popMenu.inflate(R.menu.menu_refunds_item)
            popMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.menu_view-> onClick.viewDetails(item)
                    R.id.menu_delete-> onClick.delete(item)
                }
                false
            }
            popMenu.show()
        }
    }

    override fun getItemCount(): Int = refunds.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val price: TextView = view.findViewById(R.id.tv_price)
        val quantity: TextView = view.findViewById(R.id.tv_quantity)
        val date: TextView = view.findViewById(R.id.tv_date)
        val card: MaterialCardView = view.findViewById(R.id.card)
    }

    fun updateList(newList: MutableList<Refund>) {
        refunds = newList
        notifyDataSetChanged()
    }
}