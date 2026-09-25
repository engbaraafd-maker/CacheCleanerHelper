package com.example.cachecleaner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class AppListAdapter(
    private var items: List<AppInfoItem>,
    private val onOpenSettings: (AppInfoItem) -> Unit
) : RecyclerView.Adapter<AppListAdapter.AppViewHolder>() {

    inner class AppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.ivIcon)
        val name: TextView = view.findViewById(R.id.tvName)
        val btn: MaterialButton = view.findViewById(R.id.btnOpenSettings)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
        return AppViewHolder(v)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val item = items[position]
        holder.icon.setImageDrawable(item.icon)
        holder.name.text = item.label
        holder.btn.setOnClickListener { onOpenSettings(item) }
        holder.itemView.setOnClickListener { onOpenSettings(item) }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<AppInfoItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
