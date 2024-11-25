package com.hallyu.style.ui.qrscan

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.*
import com.hallyu.style.R
import com.hallyu.style.core.BaseFragment
import com.hallyu.style.databinding.FragmentQrScanBinding
import com.hallyu.style.utilities.NULL
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class QrScanFragment : BaseFragment<FragmentQrScanBinding>(
    FragmentQrScanBinding::inflate
) {
    override val viewModel: QrScanViewModel by viewModels()

    private lateinit var codeScanner: CodeScanner
    private lateinit var requestCamera: ActivityResultLauncher<String>
    private val handlerFragment = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestCamera =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    Toast.makeText(context, "Camera permission granted", Toast.LENGTH_SHORT).show()
                    startScanning()
                } else {
                    Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                startScanning()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                val action = QrScanFragmentDirections.actionQrScanFragmentToAllowCameraFragment()
                findNavController().navigate(action)
            }
            else -> {
                requestCamera.launch(Manifest.permission.CAMERA)
            }
        }
    }

    override fun setUpViews() {
        binding.apply {
            codeScanner = CodeScanner(requireContext(), scannerView)
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    override fun setUpObserve() {
        viewModel.apply {
            statusCheckProduct.observe(viewLifecycleOwner) {
                if (it.isNotBlank()) {
                    if (it != NULL) {
                        val action =
                            QrScanFragmentDirections.actionQrScanFragmentToProductDetailFragment(
                                idProduct = it
                            )
                        findNavController().navigate(action)
                    } else {
                        incorrectProductPopup()
                    }
                }
            }
        }
    }

    private fun startScanning() {
        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            requireActivity().runOnUiThread {
                if (it.text.isNotBlank()) {
                    handlerFragment.removeMessages(0)
                    val newText = it.text.filter { it.isLetterOrDigit() }
                    if (newText.isNotBlank()){
                        viewModel.checkProduct(newText)
                    }
                    else{
                        viewModel.statusCheckProduct.postValue(NULL)
                    }
                }
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            requireActivity().runOnUiThread {
                Toast.makeText(
                    requireContext(), "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun timeOutDetectedQR() {
        handlerFragment.removeMessages(0)
        handlerFragment.postDelayed({
            popUpTimeOutPopup()
        }, TIME_OUT.toLong())
    }

    private fun popUpTimeOutPopup() {
        pauseCamera()
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.title_time_out))
            .setMessage(getString(R.string.message_time_out))
            .setPositiveButton(getString(R.string.message_continue)) { dialog, _ ->
                resumeCamera()
                timeOutDetectedQR()
                dialog.dismiss()
            }
            .setNegativeButton(
                getString(R.string.close)
            ) { dialog, _ ->
                findNavController().navigateUp()
                dialog.dismiss()
            }
            .setCancelable(false)
            .create().show()
    }

    private fun incorrectProductPopup() {
        pauseCamera()
        AlertDialog.Builder(context)
            .setTitle(getString(R.string.title_incorrect))
            .setMessage(getString(R.string.message_incorrect))
            .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                resumeCamera()
                timeOutDetectedQR()
                dialog.dismiss()
            }
            .setCancelable(false)
            .create().show()
    }

    private fun resumeCamera() {
        if (::codeScanner.isInitialized) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                codeScanner.startPreview()
            }
        }
    }

    private fun pauseCamera() {
        if (::codeScanner.isInitialized) {
            codeScanner.releaseResources()
        }
    }

    override fun onResume() {
        super.onResume()
        timeOutDetectedQR()
        resumeCamera()
    }

    override fun onPause() {
        super.onPause()
        handlerFragment.removeMessages(0)
        pauseCamera()
    }

    override fun onDestroy() {
        handlerFragment.removeMessages(0)
        codeScanner.isFlashEnabled = false
        super.onDestroy()
    }

    companion object {
        const val TIME_OUT = 1000 * 60 * 2
    }
}