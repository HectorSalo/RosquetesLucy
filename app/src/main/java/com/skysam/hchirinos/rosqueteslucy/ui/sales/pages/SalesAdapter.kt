package com.skysam.hchirinos.rosqueteslucy.ui.sales.pages

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.ClassesCommon
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.database.SharedPref
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
        val convert = ClassesCommon.convertDoubleToString(total / item.ratePaid)
        holder.price.text = if (item.isDolar) context.getString(R.string.text_price_item, symbol,
            String.format(Locale.GERMANY, "%,.2f", total))
        else context.getString(R.string.text_price_convert_item, symbol,
            String.format(Locale.GERMANY, "%,.2f", total), convert)
        val daysBetween = getTimeDistance(Date(item.dateDelivery), Date())
        holder.date.text = if (item.isPaid) {
            context.getString(R.string.text_date_paid,
                DateFormat.getDateInstance()
                .format(item.datePaid))
        } else {
            context.getString(R.string.text_date_days_between,
                DateFormat.getDateInstance()
                    .format(item.dateDelivery), daysBetween.toString())
        }

        var image = if (item.isPaid) R.drawable.ic_sale_paid_56dp else R.drawable.ic_sale_not_paid_56
        if (item.isAnnuled) {
            image = R.drawable.ic_sale_annulled_56dp
            holder.date.text = context.getString(R.string.text_date_annul,
                DateFormat.getDateInstance()
                    .format(item.datePaid))
        }
        holder.ivPaid.setImageResource(image)
        holder.invoice.text = context.getString(R.string.text_invoice_item, item.invoice.toString())

        if (!item.isAnnuled) {
            if (!item.isPaid && daysBetween >= SharedPref.getDaysExpired()) {
                holder.date.setTextColor(ContextCompat.getColor(context, R.color.red))
            } else {
                holder.date.setTextColor(context.resolveColorAttr(android.R.attr.textColorSecondary))
            }
        } else {
            holder.date.setTextColor(context.resolveColorAttr(android.R.attr.textColorSecondary))
        }

        holder.card.setOnClickListener { onClick.viewSale(item) }
        holder.card.setOnLongClickListener {
            onClick.deleteSale(item)
            false
        }
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

    @ColorInt
    fun Context.resolveColorAttr(@AttrRes colorAttr: Int): Int {
        val resolvedAttr = resolveThemeAttr(colorAttr)
        // resourceId is used if it's a ColorStateList, and data if it's a color reference or a hex color
        val colorRes = if (resolvedAttr.resourceId != 0) resolvedAttr.resourceId else resolvedAttr.data
        return ContextCompat.getColor(context, colorRes)
    }

    private fun Context.resolveThemeAttr(@AttrRes attrRes: Int): TypedValue {
        val typedValue = TypedValue()
        theme.resolveAttribute(attrRes, typedValue, true)
        return typedValue
    }
}