package com.example.kurlyflow.working.picking

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.kurlyflow.R
import com.example.kurlyflow.databinding.ItemMultipickingRecyclerviewBinding
import com.example.kurlyflow.working.picking.model.PickingProductModel

class MultiPickingRecyclerViewAdapter(
    private val context: Context,
    private var list: ArrayList<PickingProductModel>
) :
    RecyclerView.Adapter<MultiPickingRecyclerViewAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemMultipickingRecyclerviewBinding.inflate(
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
    fun setData(list: ArrayList<PickingProductModel>) {
        this.list = list
        notifyDataSetChanged() // 데이터의 변경을 화면에 반영합니다.
    }

    // ClickListener : activity나 fragment에서 반응할 수 있습니다.
    interface OnItemClickListener {
        fun onItemClick(v: View, item: PickingProductModel)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }


    inner class Holder(private val binding: ItemMultipickingRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PickingProductModel) {
            if (!item.imageUrl.equals(""))
                Glide.with(context).load(item.imageUrl)
                    .into(binding.imageviewMultipickingSmallthumb)
            else
                binding.imageviewMultipickingSmallthumb.setImageResource(R.drawable.all_image_kurlylogo)
            binding.textviewMultipickingName.text = item.name
        }
    }
}