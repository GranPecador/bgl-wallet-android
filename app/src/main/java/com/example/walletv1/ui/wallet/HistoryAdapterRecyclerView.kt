package com.example.walletv1.ui.wallet

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView
import com.example.walletv1.R
import com.example.walletv1.model.HistoryItemModel
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapterRecyclerView(val items: MutableList<HistoryItemModel> = mutableListOf()) :
    RecyclerView.Adapter<HistoryAdapterRecyclerView.HistoryViewHolder>() {

    init {
        notifyDataSetChanged()
    }

    fun addItems(newItems: List<HistoryItemModel>) {
        clearData()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun clearData() {
        items.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_item, parent, false)
        view.clipToOutline = true
        return HistoryViewHolder(view)
    }

    private val simpleDateFormat = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.ENGLISH)
    private fun getDateString(time: Int): String = simpleDateFormat.format(time * 1000L)

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        if (items[position].category != "receive") {
            holder.category.text = "Send"
            holder.iconCategoryView.setImageResource(R.drawable.ic_send_icon_item)
        }
        holder.amountView.text = "${items[position].amount} BGL"
        try {
            holder.dateView.text = getDateString(items[position].time)
        } catch (e: Exception) {
            Log.e("tag", e.toString())
        }
        holder.feeView.text = "${items[position].fee}"
        holder.confirmationsView.text = "${items[position].confirmations}"
        holder.txIdView.text = items[position].txid
        holder.itemView.setOnClickListener {
            holder.group.visibility =
                if (holder.group.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
    }

    override fun getItemCount(): Int = items.size

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val category: TextView = itemView.findViewById(R.id.category_history_item_text)
        val iconCategoryView: ImageView =
            itemView.findViewById(R.id.icon_category_history_item_image)
        val amountView: TextView = itemView.findViewById(R.id.amount_history_item_text)
        val dateView: TextView = itemView.findViewById(R.id.date_history_item_text)
        val feeView: TextView = itemView.findViewById(R.id.fee_history_item_text)
        val confirmationsView: TextView =
            itemView.findViewById(R.id.confirmations_history_item_text)
        val txIdView: TextView = itemView.findViewById(R.id.txid_history_item_text)
        val group: Group = itemView.findViewById(R.id.group)
    }

}

