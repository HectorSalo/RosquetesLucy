package com.skysam.hchirinos.rosqueteslucy.ui.costumers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer

/**
 * Created by Hector Chirinos (Home) on 2/8/2021.
 */
class CostumersAdapter(private var costumers: MutableList<Costumer>, private val onClick: OnClick):
    RecyclerView.Adapter<CostumersAdapter.ViewHolder>() {
    private lateinit var context: Context
    private lateinit var adapterLocations: LocationsAdapter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CostumersAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_costumer_item, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CostumersAdapter.ViewHolder, position: Int) {
        val item = costumers[position]
        adapterLocations = LocationsAdapter(item.locations)
        holder.recyclerView.apply {
            setHasFixedSize(true)
            adapter = adapterLocations
        }
        holder.name.text = item.name
        holder.identifier.text = item.identifier

        if (item.isExpanded) {
            holder.expandable.visibility = View.VISIBLE
            holder.locationTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up_24, 0)
        } else {
            holder.expandable.visibility = View.GONE
            holder.locationTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_24, 0)
        }

        holder.locations.setOnClickListener {
            adapterLocations.updateList(item.locations)
            item.isExpanded = !item.isExpanded
            notifyItemChanged(position)
        }

        holder.menu.setOnClickListener {
            val popMenu = PopupMenu(context, holder.menu)
            popMenu.inflate(R.menu.menu_costumers)
            popMenu.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.menu_add_location-> onClick.addLocation(item)
                    R.id.menu_delete_location-> onClick.deleteLocation(item)
                    R.id.menu_edit-> onClick.edit(item)
                    R.id.menu_delete-> onClick.delete(item)
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
        val locationTitle: TextView = view.findViewById(R.id.tv_location)
        val locations: LinearLayout = view.findViewById(R.id.ll_locations)
        val expandable: ConstraintLayout = view.findViewById(R.id.expandable)
        val recyclerView: RecyclerView = view.findViewById(R.id.rv_locations)
        val menu: TextView = view.findViewById(R.id.tv_menu)
    }

    fun updateList(newList: MutableList<Costumer>) {
        costumers = newList
        notifyDataSetChanged()
    }
}