package com.skysam.hchirinos.rosqueteslucy.ui.expenses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.ClassesCommon
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.PrimaryProducts

/**
 * Created by Hector Chirinos (Home) on 3/10/2021.
 */
class ViewExpenseAdapter (private val products: MutableList<PrimaryProducts>):
    RecyclerView.Adapter<ViewExpenseAdapter.ViewHolder>() {
    private lateinit var context: Context
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewExpenseAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_product_list, parent, false)
        context = parent.context
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewExpenseAdapter.ViewHolder, position: Int) {
        val item = products[position]
        holder.name.text = item.name
        holder.price.text = ClassesCommon.convertDoubleToString(item.price)
        holder.unit.text =  context.getString(R.string.text_price_item,
            ClassesCommon.convertDoubleToString(item.quantity), item.unit)
        holder.buttonDelete.visibility = View.GONE
    }

    override fun getItemCount(): Int = products.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_name_item)
        val unit: TextView = view.findViewById(R.id.tv_unit)
        val price: TextView = view.findViewById(R.id.tv_price)
        val buttonDelete: ImageButton = view.findViewById(R.id.ib_delete_item)
    }
}