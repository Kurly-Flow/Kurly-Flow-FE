package com.example.kurlyflow.working.end

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.kurlyflow.R
import com.example.kurlyflow.working.WorkingLoginSharedPreference
import com.example.kurlyflow.working.picking.model.ToteModel
import com.example.kurlyflow.working.picking.service.PickingService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PrintDialog(context: Context, name:String, address:String, invoiceId:String, products:String) {
    private val dialog = Dialog(context)
    private lateinit var onClickListener: OnDialogClickListener
    private val context = context
    private val name = name
    private val address = address
    private val invoiceId = invoiceId
    private val products = products
    fun setOnClickListener(listener: OnDialogClickListener) {
        onClickListener = listener
    }

    fun showDialog() {
        dialog.setContentView(R.layout.dialog_print)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        dialog.findViewById<TextView>(R.id.textview_print_name).text = "받는 사람: $name"
        dialog.findViewById<TextView>(R.id.textview_print_address).text = "주소:  $address"
        dialog.findViewById<TextView>(R.id.textview_print_invoiceid).text = "송장 번호: $invoiceId"
        dialog.findViewById<TextView>(R.id.textview_print_products).text = products

        dialog.findViewById<Button>(R.id.button_print).setOnClickListener {
            dialog.dismiss()
        }

    }

    interface OnDialogClickListener {
        fun onClicked()
    }

}