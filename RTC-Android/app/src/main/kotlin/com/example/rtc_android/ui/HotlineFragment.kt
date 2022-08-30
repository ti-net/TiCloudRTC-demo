package com.example.rtc_android.ui

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.example.rtc_android.AppIntent
import com.example.rtc_android.BuildConfig
import com.example.rtc_android.MainActivityViewModel
import com.example.rtc_android.databinding.FragmentHotlineBinding
import kotlinx.coroutines.launch

class HotlineFragment : Fragment() {

    private lateinit var binding: FragmentHotlineBinding

    private val viewModel by activityViewModels<MainActivityViewModel>()

    private val nodeIvrMap = mutableMapOf<View, String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHotlineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnNode1.isSelected = true
            nodeIvrMap[btnNode1] = BuildConfig.NODE_1_USER_FIELD
            nodeIvrMap[btnNode2] = BuildConfig.NODE_2_USER_FIELD
            nodeIvrMap[btnNode3] = BuildConfig.NODE_3_USER_FIELD
            nodeIvrMap[btnNode4] = BuildConfig.NODE_4_USER_FIELD
            edtHotlineUserField.text = Editable.Factory.getInstance().newEditable(nodeIvrMap[btnNode1])

        }.apply {
            btnNode1.setOnClickListener { selectNode(it) }
            btnNode2.setOnClickListener { selectNode(it) }
            btnNode3.setOnClickListener { selectNode(it) }
            btnNode4.setOnClickListener { selectNode(it) }
            btnHotlineCall.setOnClickListener {
                viewModel.viewModelScope.launch {
                    viewModel.intentChannel.send(AppIntent.Call(
                        tel = "",
                        clid = "",
                        userField = edtHotlineUserField.text.toString(),
                        type = 1 // 1 为客服场景
                    ))
                }
            }
        }
    }

    private fun selectNode(node: View) {
        clearNodeSelectState()
        updateUserField(node)
        node.isSelected = true
    }

    private fun updateUserField(node:View){
        binding.edtHotlineUserField.text = Editable.Factory.getInstance().newEditable(nodeIvrMap[node])
    }

    private fun clearNodeSelectState() {
        binding.apply {
            btnNode1.isSelected = false
            btnNode2.isSelected = false
            btnNode3.isSelected = false
            btnNode4.isSelected = false
        }
    }
}