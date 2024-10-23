package com.example.rtc_android.ui

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.commom.BuildConfig
import com.example.common.AppIntent
import com.example.common.AppViewModel
import com.example.rtc_android.databinding.FragmentOutCallBinding

class OutCallFragment : Fragment() {

    private lateinit var binding: FragmentOutCallBinding

    private val viewModel by activityViewModels<AppViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOutCallBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnOutCall.setOnClickListener {
                viewModel.handleIntent(
                    AppIntent.Call(
                        tel = edtOutCallTel.text.toString(),
                        clid = edtOutCallClid.text.toString(),
                        userField = if (BuildConfig.DEBUG) edtOutCallUserField.text.toString() else String.format(
                            BuildConfig.OUT_CALL_USER_FIELD,
                            edtOutCallUserField.text.toString()
                        ),
                        type = 6 // 6 为外呼场景
                    ).apply {
                        callerNumber = binding.edtCallerNumber.text.toString()
                        obClidAreaCode = binding.edtObClidAreaCode.text.toString()
                        obClidGroup = binding.edtObClidGroup.text.toString()
                    }
                )
            }
            edtOutCallUserField.setOnLongClickListener {
                if (BuildConfig.DEBUG) resetUserField()
                true
            }
        }
    }

    private fun resetUserField() {
        binding.edtOutCallUserField.text =
            Editable.Factory.getInstance()
                .newEditable(String.format(BuildConfig.OUT_CALL_USER_FIELD, ""))
    }
}