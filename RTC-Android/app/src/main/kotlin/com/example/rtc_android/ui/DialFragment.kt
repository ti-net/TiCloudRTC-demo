package com.example.rtc_android.ui

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.example.common.AppIntent
import com.example.common.AppViewModel
import com.example.rtc_android.BuildConfig
import com.example.rtc_android.databinding.FragmentDialBinding
import kotlinx.coroutines.launch

class DialFragment:Fragment() {

    private lateinit var binding:FragmentDialBinding

    private val viewModel by activityViewModels<AppViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            dialPanel.numPanel0.setOnClickListener { appendTelNum("0")}
            dialPanel.numPanel1.setOnClickListener { appendTelNum("1") }
            dialPanel.numPanel2.setOnClickListener { appendTelNum("2") }
            dialPanel.numPanel3.setOnClickListener { appendTelNum("3") }
            dialPanel.numPanel4.setOnClickListener { appendTelNum("4") }
            dialPanel.numPanel5.setOnClickListener { appendTelNum("5") }
            dialPanel.numPanel6.setOnClickListener { appendTelNum("6") }
            dialPanel.numPanel7.setOnClickListener { appendTelNum("7") }
            dialPanel.numPanel8.setOnClickListener { appendTelNum("8") }
            dialPanel.numPanel9.setOnClickListener { appendTelNum("9") }

            btnDeleteNum.setOnClickListener { dropLastNum() }
            btnDeleteNum.setOnLongClickListener {
                clearNum()
                true
            }

            btnCall.setOnClickListener { call() }
        }
    }

    private fun call(){
        viewModel.handleIntent(
            AppIntent.Call(
                tel = binding.tvShowTel.text.toString(),
                clid = "",
                userField = BuildConfig.OUT_CALL_USER_FIELD,
                type = 1
            )
        )
    }

    private fun appendTelNum(num:String){
        binding.tvShowTel.editableText.append(num)
    }

    private fun dropLastNum(){
        binding.tvShowTel.apply {
            text = Editable.Factory.getInstance().newEditable(editableText.dropLast(1))
        }
    }

    private fun clearNum(){
        binding.tvShowTel.editableText.clear()
    }
}