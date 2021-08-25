package com.skysam.hchirinos.rosqueteslucy.ui.sales.pages

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
import java.util.*

/**
 * Created by Hector Chirinos (Home) on 13/8/2021.
 */
class SalesAdapter(private var sales: MutableList<Sale>, private val onClick: OnClick):
    RecyclerView.Adapter<SalesAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_sale_item, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = sales[position]
        holder.name.text = context.getString(R.string.text_costumer_location,
            item.nameCostumer, item.location)
        val total = item.quantity * item.price
        val symbol = if (item.isDolar) "$" else "Bs."
        holder.price.text = context.getString(R.string.text_price_item, symbol,
            String.format(Locale.GERMANY, "%,.2f", total))
        val daysBetween = getTimeDistance(Date(item.date), Date())
        holder.date.text = if (item.isPaid) {
            DateFormat.getDateInstance()
                .format(item.date)
        } else {
            context.getString(R.string.text_date_days_between,
                DateFormat.getDateInstance()
                    .format(item.date), daysBetween.toString())
        }
        val image = if (item.isPaid) R.drawable.ic_cash_check_56dp else R.drawable.ic_cash_remove_56dp
        holder.ivPaid.setImageResource(image)
        holder.invoice.text = context.getString(R.string.text_invoice_item, item.invoice.toString())

        holder.card.setOnClickListener { onClick.viewSale(item) }
    }

    override fun getItemCount(): Int = sales.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val price: TextView = view.findViewById(R.id.tv_price)
        val date: TextView = view.findViewById(R.id.tv_date)
        val invoice: TextView = view.findViewById(R.id.tv_invoice)
        val ivPaid: ImageView = view.findViewById(R.id.iv_paid)
        val card: MaterialCardView = view.findViewById(R.id.card)
    }

    fun updateList(newList: MutableList<Sale>) {
        sales = newList
        notifyDataSetChanged()
    }

    fun getTimeDistance(beginDate: Date, endDate: Date): Int {
        val fromCalendar = Calendar.getInstance()
        fromCalendar.time = beginDate
        fromCalendar[Calendar.HOUR_OF_DAY] = fromCalendar.getMinimum(Calendar.HOUR_OF_DAY)
        fromCalendar[Calendar.MINUTE] = fromCalendar.getMinimum(Calendar.MINUTE)
        fromCalendar[Calendar.SECOND] = fromCalendar.getMinimum(Calendar.SECOND)
        fromCalendar[Calendar.MILLISECOND] = fromCalendar.getMinimum(Calendar.MILLISECOND)
        val toCalendar = Calendar.getInstance()
        toCalendar.time = endDate
        toCalendar[Calendar.HOUR_OF_DAY] = fromCalendar.getMinimum(Calendar.HOUR_OF_DAY)
        toCalendar[Calendar.MINUTE] = fromCalendar.getMinimum(Calendar.MINUTE)
        toCalendar[Calendar.SECOND] = fromCalendar.getMinimum(Calendar.SECOND)
        toCalendar[Calendar.MILLISECOND] = fromCalendar.getMinimum(Calendar.MILLISECOND)

        var daysBetween = 0
        while (fromCalendar.before(toCalendar)) {
            fromCalendar.add(Calendar.DAY_OF_MONTH, 1)
            daysBetween++
        }
        return daysBetween
    }
}