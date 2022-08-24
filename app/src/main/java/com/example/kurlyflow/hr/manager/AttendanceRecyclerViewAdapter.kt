package com.example.kurlyflow.hr.manager

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kurlyflow.databinding.ItemManageRecyclerviewBinding
import com.example.kurlyflow.hr.manager.model.AttendanceModel

class AttendanceRecyclerViewAdapter(
    private val context: Context,
    private var list: ArrayList<AttendanceModel>
) :
    RecyclerView.Adapter<AttendanceRecyclerViewAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemManageRecyclerviewBinding.inflate(
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
    fun setData(list: ArrayList<AttendanceModel>) {
        this.list = list
        notifyDataSetChanged() // 데이터의 변경을 화면에 반영합니다.
    }

    // ClickListener : activity나 fragment에서 반응할 수 있습니다.
    interface OnItemClickListener {
        fun onItemClick(v: View, item: AttendanceModel)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }


    inner class Holder(private val binding: ItemManageRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AttendanceModel) {
            binding.textviewManage0.text = item.name
            binding.textviewManage1.text = item.employeeNumber
            if (item.isAttended) {
                binding.textviewManage2.text = "O"
            } else {
                binding.textviewManage2.text = "X"
            }

        }
    }
}