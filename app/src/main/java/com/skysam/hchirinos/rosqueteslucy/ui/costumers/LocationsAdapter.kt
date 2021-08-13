package com.skysam.hchirinos.rosqueteslucy.ui.costumers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Location

/**
 * Created by Hector Chirinos (Home) on 11/8/2021.
 */
class LocationsAdapter(private var locations: MutableList<Location>):
    RecyclerView.Adapter<LocationsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_location_costumer_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationsAdapter.ViewHolder, position: Int) {
        holder.location.text = locations[position].name
    }

    override fun getItemCount(): Int = locations.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val location: TextView = view.findViewById(R.id.tv_location)
    }

    fun updateList(newList: MutableList<Location>) {
        locations = newList
        notifyDataSetChanged()
    }
}