package com.skysam.hchirinos.rosqueteslucy.ui.suppliers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Supplier

/**
 * Created by Hector Chirinos (Home) on 30/9/2021.
 */
class SuppliersAdapter (private var suppliers: MutableList<Supplier>, private val onClick: OnClick):
    RecyclerView.Adapter<SuppliersAdapter.ViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_costumer_item, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = suppliers[position]
        holder.name.text = item.name

        holder.card.setOnClickListener {
            val popMenu = PopupMenu(context, holder.menu)
            popMenu.inflate(R.menu.menu_suppliers_item)
            popMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.menu_add_expense-> onClick.addExpense(item)
                    R.id.menu_edit -> onClick.editSupplier(item)
                    R.id.menu_delete-> onClick.deleteSupplier(item)
                }
                false
            }
            popMenu.show()
        }

        holder.menu.visibility = View.GONE
    }

    override fun getItemCount(): Int = suppliers.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val card: MaterialCardView = view.findViewById(R.id.card)
        val menu: TextView = view.findViewById(R.id.tv_menu)
    }

    fun updateList(newList: MutableList<Supplier>) {
        suppliers = newList
        notifyDataSetChanged()
    }
}