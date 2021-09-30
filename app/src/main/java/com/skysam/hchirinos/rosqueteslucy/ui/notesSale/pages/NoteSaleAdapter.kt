package com.skysam.hchirinos.rosqueteslucy.ui.notesSale.pages

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.NoteSale
import java.text.DateFormat
import java.util.*

/**
 * Created by Hector Chirinos on 14/09/2021.
 */
class NoteSaleAdapter(private var notesSale: MutableList<NoteSale>, private val onClick: OnClick):
    RecyclerView.Adapter<NoteSaleAdapter.ViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_sale_item, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = notesSale[position]
        holder.name.text = context.getString(
            R.string.text_costumer_location,
            item.nameCostumer, item.location)
        val total = item.quantity * item.price
        val symbol = if (item.isDolar) "$" else "Bs."
        holder.price.text = context.getString(R.string.text_price_item, symbol,
            String.format(Locale.GERMANY, "%,.2f", total))
        holder.date.text = if (item.isPaid) {
            context.getString(R.string.text_date_note_sale_paid,
                DateFormat.getDateInstance()
                    .format(item.datePaid))
        } else {
            context.getString(R.string.text_date_note_sale_not_paid,
                DateFormat.getDateInstance().format(item.datePaid))
        }
        holder.invoice.text = context.getString(R.string.text_note_sale_item, item.noteNumber.toString())
        val image = if (item.isPaid) R.drawable.ic_cash_check_56dp else R.drawable.ic_cash_remove_56dp
        holder.ivPaid.setImageResource(image)

        holder.card.setOnClickListener { onClick.viewNoteSale(item) }
        holder.card.setOnLongClickListener {
            onClick.deleteNoteSale(item)
            false
        }
    }

    override fun getItemCount(): Int = notesSale.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val price: TextView = view.findViewById(R.id.tv_price)
        val date: TextView = view.findViewById(R.id.tv_date)
        val invoice: TextView = view.findViewById(R.id.tv_invoice)
        val ivPaid: ImageView = view.findViewById(R.id.iv_paid)
        val card: MaterialCardView = view.findViewById(R.id.card)
    }

    fun updateList(newList: MutableList<NoteSale>) {
        notesSale = newList
        notifyDataSetChanged()
    }
}