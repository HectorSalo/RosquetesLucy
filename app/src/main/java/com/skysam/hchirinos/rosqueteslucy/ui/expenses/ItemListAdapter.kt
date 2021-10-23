package com.skysam.hchirinos.rosqueteslucy.ui.expenses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.ClassesCommon
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.PrimaryProducts

/**
 * Created by Hector Chirinos (Home) on 1/10/2021.
 */
class ItemListAdapter(
    private var products: MutableList<PrimaryProducts>, private val listener: OnClickList
) : RecyclerView.Adapter<ItemListAdapter.ViewHolder>() {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_product_list, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = products[position]
        holder.name.text = item.name
        holder.price.text = ClassesCommon.convertDoubleToString(item.price)
        holder.unit.text = context.getString(R.string.text_price_item,
            ClassesCommon.convertDoubleToString(item.quantity), item.unit)
        holder.priceQuantity.text = ClassesCommon.convertDoubleToString(item.price * item.quantity)
        holder.buttonDelete.setOnClickListener { listener.deleteItem(item) }
        holder.buttonEdit.setOnClickListener { listener.editItem(item) }
    }

    override fun getItemCount(): Int = products.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name_item)
        val unit: TextView = view.findViewById(R.id.tv_unit)
        val price: TextView = view.findViewById(R.id.tv_price)
        val priceQuantity: TextView = view.findViewById(R.id.tv_price_quantity)
        val buttonDelete: ImageButton = view.findViewById(R.id.ib_delete_item)
        val buttonEdit: ConstraintLayout = view.findViewById(R.id.constraint)
    }

    fun updateList(newList: MutableList<PrimaryProducts>) {
        products = newList
        notifyDataSetChanged()
    }
}