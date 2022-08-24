package com.example.kurlyflow.hr.manager

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.kurlyflow.R
import com.example.kurlyflow.databinding.FragmentToBinding
import com.example.kurlyflow.hr.manager.request.SaveToRequest
import com.example.kurlyflow.hr.manager.service.ManagerService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class ToFragment : Fragment(R.layout.fragment_to) {
    private lateinit var binding: FragmentToBinding
    private lateinit var localDate: LocalDate
    private var selectedSpinner = "선택"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentToBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
        initCalendar()
        initSpinner()
        initListener()
        initToolbar()
    }

    private fun loadData() {
        binding.textviewToName.text = ManagerLoginSharedPreference.getUserName(requireContext())
        binding.textviewToRegion.text =
            ManagerLoginSharedPreference.getUserRegion(requireContext()) + " 관리자"
        binding.textviewToRegion2.text =
            ManagerLoginSharedPreference.getUserRegion(requireContext())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initListener() {
        binding.buttonToSubmit.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("정말 제출 하시겠습니까?")
                .setPositiveButton(
                    "확인",
                    DialogInterface.OnClickListener { dialog, _ ->
                        saveTo()
                        dialog.dismiss()
                    })
                .setNegativeButton(
                    "취소",
                    DialogInterface.OnClickListener { dialog, _ ->
                        dialog.dismiss()
                    })
            builder.show()
        }

        binding.buttonToSetregion.setOnClickListener {
            ManagerService.tmpppp(
                ManagerLoginSharedPreference.getUserAccessToken(requireContext())
            ).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d("TAGr", response.body()+response.code().toString()+response.errorBody())
                    if (response.code() == 200) {
                        Toast.makeText(
                            requireContext(),
                            "작업자의 근무지 배정이 완료되었습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "모든 작업자가 준비되지 않았습니다.\n잠시 후 다시 시도해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("TAGr", t.localizedMessage)
                    Toast.makeText(
                        requireContext(),
                        "근무지 배정에 실패했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initCalendar() {
        val currentTime: Long = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        binding.textviewToDate.text = dateFormat.format(currentTime)
        localDate = LocalDate.parse(binding.textviewToDate.text, DateTimeFormatter.ISO_LOCAL_DATE)
        Log.d("TAGG", localDate.toString())
        val cal = Calendar.getInstance()
        binding.textviewToDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                    cal.set(Calendar.YEAR, y)
                    cal.set(Calendar.MONTH, m)
                    cal.set(Calendar.DAY_OF_MONTH, d)
                    binding.textviewToDate.text = dateFormat.format(cal.time)
                    localDate = LocalDate.parse(
                        binding.textviewToDate.text,
                        DateTimeFormatter.ISO_LOCAL_DATE
                    )
                    Log.d("TAGc", localDate.toString())
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun initSpinner() {
        val spinnerList = listOf(
            "선택",
            "주간조 (10:00~19:00)",
            "점심조 (13:00~22:00)",
            "풀타임 (15:30~12:50)",
            "미들 (17:00~12:50)",
            "파트 (19:30~12:50)"
        )
        val adapter = ArrayAdapter(
            requireContext(),
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            spinnerList
        )
        binding.spinnerToTime.adapter = adapter
        binding.spinnerToTime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selected =
                    binding.spinnerToTime.getItemAtPosition(position).toString()
                if (selected != "선택"){
                    selectedSpinner = selected.split(" ")[0]
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveTo() {
        if (selectedSpinner != "선택" && binding.edittextToTo.text.isNotBlank()) {
            val request =
                SaveToRequest(localDate.toString(), selectedSpinner, Integer(binding.edittextToTo.text.toString()))
            ManagerService.saveTo(
                ManagerLoginSharedPreference.getUserAccessToken(requireContext()),
                request
            ).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d("TAGt", response.body() + response.code())
                    if (response.code() == 200) {
                        Toast.makeText(requireContext(), "제출했습니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "제출을 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("TAGe", t.localizedMessage)
                    Toast.makeText(requireContext(), "제출을 실패했습니다.", Toast.LENGTH_SHORT).show()
                }

            })
        } else {
            if (binding.edittextToTo.text.isBlank()) {
                Toast.makeText(requireContext(), "TO을 정확히 입력해주세요", Toast.LENGTH_SHORT).show()
            } else if (selectedSpinner == "선택") {
                Toast.makeText(requireContext(), "근무조를 선택해주세요", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun initToolbar() {
        binding.toolbarTo.menu.findItem(R.id.refresh).isVisible = false
        binding.toolbarTo.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setMessage("로그아웃 하시겠습니까?")
                        .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, _ ->
                            Toast.makeText(requireContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT)
                                .show()
                            ManagerLoginSharedPreference.clearUserInfo(requireContext())
                            startActivity(Intent(requireContext(), ManagerLoginActivity::class.java))
                            requireActivity().finish()
                        })
                        .setNegativeButton("취소", DialogInterface.OnClickListener { dialog, _ ->
                            dialog.cancel()
                        })
                    builder.show()
                    true
                }
                else -> false
            }
        }
    }
}