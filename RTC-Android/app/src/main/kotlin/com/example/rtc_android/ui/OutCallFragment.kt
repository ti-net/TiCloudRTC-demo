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
import com.example.rtc_android.databinding.FragmentOutCallBinding
import kotlinx.coroutines.launch

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
                viewModel.viewModelScope.launch {
                    viewModel.intentChannel.send(
                        AppIntent.Call(
                            tel = edtOutCallTel.text.toString(),
                            clid = edtOutCallClid.text.toString(),
                            userField = if (BuildConfig.DEBUG) edtOutCallUserField.text.toString() else String.format(
                                BuildConfig.OUT_CALL_USER_FIELD,
                                edtOutCallUserField.text.toString()
                            ),
                            type = 6 // 6 为外呼场景
                        )
                    )
                }
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