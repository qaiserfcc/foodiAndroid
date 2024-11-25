package com.hallyu.style.ui.qrscan

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.navigation.fragment.findNavController
import com.hallyu.style.core.BaseFragment
import com.hallyu.style.databinding.FragmentAllowCameraBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllowCameraFragment : BaseFragment<FragmentAllowCameraBinding>(
    FragmentAllowCameraBinding::inflate
) {
    override fun setUpViews() {
        binding.apply {
            btnClose.setOnClickListener {
                findNavController().navigateUp()
            }
            btnNotNow.setOnClickListener {
                findNavController().navigateUp()
            }

            btnGoToSetting.setOnClickListener {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:" + requireActivity().packageName)
                findNavController().navigateUp()
                startActivity(intent)
            }
        }
    }
}