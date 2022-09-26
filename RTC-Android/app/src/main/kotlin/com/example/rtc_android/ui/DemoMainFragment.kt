package com.example.rtc_android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.rtc_android.R
import com.example.rtc_android.databinding.FragmentDemoMainBinding

class DemoMainFragment: Fragment() {

    private lateinit var binding: FragmentDemoMainBinding

    private var isPhoneNumRight = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDemoMainBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            includeDataItem.btnOpenDial.setOnClickListener {
                if(isPhoneNumRight){
                    findNavController().navigate(R.id.action_demoMainFragment_to_demoDialFragment,bundleOf(
                        "phoneNum" to edtPhoneNum.text.toString()
                    ))
                }
            }
            edtPhoneNum.addTextChangedListener {
                isPhoneNumRight = it?.matches(Regex("^1(3\\d|4[5-9]|5[0-35-9]|6[567]|7[0-8]|8\\d|9[0-35-9])\\d{8}\$"))?:false
                tvPhoneNumError.visibility = if(isPhoneNumRight || it?.length == 0) View.INVISIBLE else View.VISIBLE
            }
        }
    }
}