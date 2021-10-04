package com.skysam.hchirinos.rosqueteslucy.ui.expenses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.ClassesCommon
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Expense
import java.text.DateFormat

/**
 * Created by Hector Chirinos on 27/08/2021.
 */
class ExpensesAdapter(private var expens: MutableList<Expense>, private val onClick: OnClick):
    RecyclerView.Adapter<ExpensesAdapter.ViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpensesAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_expense_item, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpensesAdapter.ViewHolder, position: Int) {
        val item = expens[position]
        holder.name.text = item.nameSupplier
        holder.price.text = context.getString(R.string.text_total_dolar_expense,
            ClassesCommon.convertDoubleToString(item.total))
        holder.date.text = DateFormat.getDateInstance().format(item.dateCreated)

        holder.card.setOnClickListener {
            val popMenu = PopupMenu(context, holder.card)
            popMenu.inflate(R.menu.menu_expenses)
            popMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.menu_view -> onClick.viewExpense(item)
                    R.id.menu_edit-> onClick.edit(item)
                    R.id.menu_delete-> onClick.delete(item)
                }
                false
            }
            popMenu.show()
        }
    }

    override fun getItemCount(): Int = expens.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val price: TextView = view.findViewById(R.id.tv_price)
        val date: TextView = view.findViewById(R.id.tv_date)
        val card: MaterialCardView = view.findViewById(R.id.card)
    }

    fun updateList(newList: MutableList<Expense>) {
        expens = newList
        notifyDataSetChanged()
    }
}