package com.kobbi.project.renamefile.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.kobbi.project.renamefile.BR
import com.kobbi.project.renamefile.R
import com.kobbi.project.renamefile.databinding.ItemFileBinding
import com.kobbi.project.renamefile.listener.ClickListener
import com.kobbi.project.renamefile.listener.LongClickListener
import com.kobbi.project.renamefile.utils.Utils
import com.kobbi.project.renamefile.view.model.DirViewModel
import java.io.File
import java.util.concurrent.Executors

class DirAdapter(items: List<File>) : RecyclerView.Adapter<DirAdapter.ViewHolder>() {
    private val mItems = mutableListOf<File>()
    private val mSelectedPositions = mutableListOf<Int>()
    private var mSelectMode: DirViewModel.SelectMode = DirViewModel.SelectMode.NORMAL
    var clickListener: ClickListener? = null
    var longClickListener: LongClickListener? = null

    init {
        setItems(items)
    }


    fun setItems(items: List<File>): Boolean {
        if (mItems.isNotEmpty() && items.isNotEmpty() && mItems[0].parent == items[0].parent && mItems.size == items.size) {
            return false
        }
        mItems.clear()
        mItems.addAll(items)
        return true
    }

    fun setSelectMode(mode: DirViewModel.SelectMode): Boolean {
        if (mSelectMode != mode) {
            mSelectMode = mode
            return true
        }
        return false
    }

    fun setSelectedPositions(positions: List<Int>) {
        Log.e("####", "positions : $positions")
        Log.e("####", "mSelectedPositions : $mSelectedPositions")
        if (positions.size - mSelectedPositions.size < 0) {
            mSelectedPositions.filter {
                !positions.contains(it)
            }
        } else {
            positions.filter {
                !mSelectedPositions.contains(it)
            }
        }.run {
            mSelectedPositions.clear()
            mSelectedPositions.addAll(positions)
            Log.e("####", "change : $this")
            forEach {
                notifyItemChanged(it)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemFileBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.item_file, parent, false
            )
        return ViewHolder(binding)
    }

    override fun getItemCount() = mItems.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(private val binding: ItemFileBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener,
        View.OnLongClickListener {
        init {
            binding.root.let { view ->
                view.setOnClickListener(this)
                view.setOnLongClickListener(this)
            }
        }

        override fun onClick(v: View?) {
            v?.let { clickListener?.onItemClick(layoutPosition, v) }
        }

        override fun onLongClick(v: View?): Boolean {
            v?.let { longClickListener?.onItemLongClick(layoutPosition, v) }
            return true
        }

        fun bind(position: Int) {
            val file = mItems[position]
            binding.setVariable(BR.file, file)
            binding.setVariable(BR.is_selected, mSelectedPositions.contains(position))
            getImage(file)
            binding.setVariable(BR.select_mode, mSelectMode)
        }

        private fun getImage(file: File) {
            Executors.newFixedThreadPool(30).execute {
                val bitmap = Utils.getBitmap(file, 480, 480)
                binding.setVariable(BR.img, bitmap)
            }
        }
    }
}