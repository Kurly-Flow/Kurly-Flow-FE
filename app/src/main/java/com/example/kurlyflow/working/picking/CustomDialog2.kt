package com.example.kurlyflow.working.picking

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.kurlyflow.R
import com.example.kurlyflow.working.WorkingLoginSharedPreference
import com.example.kurlyflow.working.picking.model.ToteModel
import com.example.kurlyflow.working.picking.service.PickingService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomDialog2(context: Context) {
    private val dialog = Dialog(context)
    private lateinit var onClickListener: OnDialogClickListener
    private val context = context
    fun setOnClickListener(listener: OnDialogClickListener) {
        onClickListener = listener
    }

    fun showDialog() {
        dialog.setContentView(R.layout.dialog_multipicking)
        dialog.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.setCanceledOnTouchOutside(true)
        dialog.setCancelable(true)
        dialog.show()

        val editText = dialog.findViewById<EditText>(R.id.edittext_dialogmultipicking)

        dialog.findViewById<Button>(R.id.button_dialogmultipicking_submit).setOnClickListener {
            val toteId = editText.text.toString()
            if (toteId == "") {
                dialog.findViewById<EditText>(R.id.edittext_dialogmultipicking).hint =
                    "토트를 스캔하지 않았습니다."
            } else {
                onClickListener.onClicked(editText.text.toString())
                dialog.dismiss()
            }
        }

        dialog.findViewById<Button>(R.id.button_dialogmultipicking_cancel).setOnClickListener {
            dialog.dismiss()
        }

    }

    interface OnDialogClickListener {
        fun onClicked(toteId: String)
    }

}