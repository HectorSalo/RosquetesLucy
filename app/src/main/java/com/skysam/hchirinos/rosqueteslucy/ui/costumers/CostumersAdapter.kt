package com.skysam.hchirinos.rosqueteslucy.ui.costumers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer

/**
 * Created by Hector Chirinos (Home) on 2/8/2021.
 */
class CostumersAdapter(private var costumers: MutableList<Costumer>, private val onClick: OnClick):
    RecyclerView.Adapter<CostumersAdapter.ViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CostumersAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_costumer_item, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CostumersAdapter.ViewHolder, position: Int) {
        val item = costumers[position]
        holder.name.text = item.name
        holder.identifier.text = item.identifier
        holder.location.text = item.location

        holder.menu.setOnClickListener {
            val popMenu = PopupMenu(context, holder.menu)
            popMenu.inflate(R.menu.menu_costumers)
            popMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.menu_edit-> onClick.edit(item)
                    R.id.menu_delete-> onClick.delete(item.id)
                }
               false
            }
            popMenu.show()
        }
    }

    override fun getItemCount(): Int = costumers.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name)
        val identifier: TextView = view.findViewById(R.id.tv_identifier)
        val location: TextView = view.findViewById(R.id.tv_location)
        val menu: TextView = view.findViewById(R.id.tv_menu)
    }

    fun updateList(newList: MutableList<Costumer>) {
        costumers = newList
        notifyDataSetChanged()
    }
}