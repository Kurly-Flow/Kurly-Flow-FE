package com.example.kurlyflow.working.end

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kurlyflow.databinding.ItemEndRecyclerviewBinding
import com.example.kurlyflow.working.end.model.BasketModel

class BasketRecyclerViewAdapter(
    private val context: Context,
    private var list: ArrayList<BasketModel>
) :
    RecyclerView.Adapter<BasketRecyclerViewAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemEndRecyclerviewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            listener?.onItemClick(holder.itemView, list[position])
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: ArrayList<BasketModel>) {
        this.list = list
        notifyDataSetChanged() // 데이터의 변경을 화면에 반영합니다.
    }

    // ClickListener : activity나 fragment에서 반응할 수 있습니다.
    interface OnItemClickListener {
        fun onItemClick(v: View, item: BasketModel)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }


    inner class Holder(private val binding: ItemEndRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: BasketModel) {
            binding.textviewManage0.text = item.basketId.toString() + " - " + item.invoiceId
            binding.textviewManage1.text = item.endAt.toString()
        }
    }
}