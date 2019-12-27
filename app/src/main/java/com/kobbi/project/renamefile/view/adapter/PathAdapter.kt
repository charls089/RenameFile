package com.kobbi.project.renamefile.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kobbi.project.renamefile.BR
import com.kobbi.project.renamefile.R
import com.kobbi.project.renamefile.databinding.ItemPathBinding
import com.kobbi.project.renamefile.listener.ClickListener
import com.kobbi.project.renamefile.listener.LongClickListener

class PathAdapter(items: List<String>) : RecyclerView.Adapter<PathAdapter.ViewHolder>() {
    private val mItems = mutableListOf<String>()
    var clickListener: ClickListener? = null
    var longClickListener: LongClickListener? = null

    init {
        setItems(items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemPathBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_path, parent, false
            )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    fun setItems(items: List<String>) {
        mItems.clear()
        mItems.addAll(items)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemPathBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener,
        View.OnLongClickListener {
        init {
            val view = binding.root
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            v?.let { clickListener?.onItemClick(layoutPosition, v) }
        }

        override fun onLongClick(v: View?): Boolean {
            v?.let { longClickListener?.onItemLongClick(layoutPosition, v) }
            return true
        }

        fun bind(position: Int) {
            val path = mItems[position]
            binding.setVariable(BR.path, path)
        }
    }
}