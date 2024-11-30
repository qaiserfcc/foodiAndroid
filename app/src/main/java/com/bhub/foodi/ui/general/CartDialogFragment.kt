package com.bhub.foodi.ui.general

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bhub.foodi.R

class CartDialogFragment(
    private val onDismissClick: () -> Unit,
    private val onGoToCart: () -> Unit
) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the custom layout for the dialog
        val view: View = inflater.inflate(R.layout.dialog_add_to_cart, container, false)

        // Handle button clicks
        view.findViewById<View>(R.id.btnDismiss).setOnClickListener { v: View? ->
            onDismissClick()
            dismiss()
        }

        view.findViewById<View>(R.id.btnGoToCart).setOnClickListener { v: View? ->
            onGoToCart()
            dismiss()
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomDialogTheme) // Apply custom theme
    }
}
