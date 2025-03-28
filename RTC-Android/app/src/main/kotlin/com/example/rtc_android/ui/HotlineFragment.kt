package com.example.rtc_android.ui

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.example.commom.BuildConfig
import com.example.common.AppIntent
import com.example.common.AppViewModel
import com.example.rtc_android.databinding.FragmentHotlineBinding
import kotlinx.coroutines.launch

class HotlineFragment : Fragment() {

    private lateinit var binding: FragmentHotlineBinding

    private val viewModel by activityViewModels<AppViewModel>()

    private val nodeIvrMap = mutableMapOf<View, String>()

    private val nodeIvrUserFieldMap = mutableMapOf<View, String>()

    private lateinit var nodeList: List<View>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHotlineBinding.inflate(inflater, container, false)
        nodeList = listOf(
            binding.btnNode1,
            binding.btnNode2,
            binding.btnNode3,
            binding.btnNode4
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnNode1.isSelected = true
            nodeIvrMap[btnNode1] = ""
            nodeIvrMap[btnNode2] = ""
            nodeIvrMap[btnNode3] = ""
            nodeIvrMap[btnNode4] = ""
            nodeIvrUserFieldMap[btnNode1] = BuildConfig.NODE_1_USER_FIELD
            nodeIvrUserFieldMap[btnNode2] = BuildConfig.NODE_2_USER_FIELD
            nodeIvrUserFieldMap[btnNode3] = BuildConfig.NODE_3_USER_FIELD
            nodeIvrUserFieldMap[btnNode4] = BuildConfig.NODE_4_USER_FIELD
            edtNodeUserField.text = Editable.Factory.getInstance().newEditable(nodeIvrMap[btnNode1])

        }.apply {
            btnNode1.setOnClickListener { selectNode(it) }
            btnNode2.setOnClickListener { selectNode(it) }
            btnNode3.setOnClickListener { selectNode(it) }
            btnNode4.setOnClickListener { selectNode(it) }
            btnHotlineCall.setOnClickListener {
                viewModel.handleIntent(
                    AppIntent.Call(
                        tel = edtAgentTel2.text.toString(),
                        clid = "",
                        userField = String.format(
                            nodeIvrUserFieldMap[currentSelectedNode()!!]!!,
                            binding.edtNodeUserField.text.toString()
                        ),
                        type = 1 // 1 为客服场景
                    )
                )
            }
            btnCallAgent.setOnClickListener {
                viewModel.viewModelScope.launch {
                    viewModel.handleIntent(
                        AppIntent.Call(
                            tel = edtAgentTel1.text.toString(),
                            clid = "",
                            userField = String.format(
                                BuildConfig.CALL_AGENT_USER_FIELD,
                                edtAgentUserField.text.toString()
                            ),
                            type = 1 // 1 为客服场景
                        )
                    )
                }
            }
        }
    }

    private fun selectNode(node: View) {
        clearNodeSelectState()
        updateUserField(node)
        node.isSelected = true
    }

    private fun updateUserField(node: View) {
        binding.edtNodeUserField.text = Editable.Factory.getInstance().newEditable(nodeIvrMap[node])
    }

    private fun clearNodeSelectState() {
        nodeList.forEach { it.isSelected = false }
    }

    private fun currentSelectedNode(): View? {
        return nodeList.find { it.isSelected }
    }
}