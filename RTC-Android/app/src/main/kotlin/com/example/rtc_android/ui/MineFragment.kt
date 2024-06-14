package com.example.rtc_android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.example.common.AppIntent
import com.example.common.AppViewModel
import com.example.rtc_android.*
import com.example.rtc_android.databinding.FragmentMineBinding
import com.tinet.ticloudrtc.TiCloudRTC
import kotlinx.coroutines.launch

class MineFragment : Fragment() {

    private lateinit var binding: FragmentMineBinding

    private val viewModel by activityViewModels<AppViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvAppVersion.text = resources.getString(R.string.app_version) + BuildConfig.VERSION_NAME
            tvSdkVersion.text = resources.getString(R.string.sdk_version) + TiCloudRTC.getVersion()

            btnLogout.setOnClickListener {
                viewModel.handleIntent(AppIntent.Logout)
            }
        }

    }


}