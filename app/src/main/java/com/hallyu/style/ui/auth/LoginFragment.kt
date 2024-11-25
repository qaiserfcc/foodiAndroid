package com.hallyu.style.ui.auth

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hallyu.style.MainActivity
import com.hallyu.style.R
import com.hallyu.style.core.BaseFragment
import com.hallyu.style.core.OnSignInStartedListener
import com.hallyu.style.databinding.FragmentLoginBinding
import com.hallyu.style.utilities.REQUEST_SIGN_IN
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(
    FragmentLoginBinding::inflate
) {
    override val viewModel: AuthViewModel by viewModels()

    override val color = R.color.grey2

    override fun setUpViews() {
        binding.apply {
            appBarLayout.topAppBar.title = getString(R.string.login)
            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
            }

            btnLogin.setOnClickListener {
                editTextEmail.clearFocus()
                editTextPassword.clearFocus()
                (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    editTextPassword.windowToken,
                    0
                )
                viewModel.logIn(
                    editTextEmail.text.toString(),
                    editTextPassword.text.toString()
                )
            }

            btnGoogle.setOnClickListener {
                viewModel.signInWithGoogle(object :
                    OnSignInStartedListener {
                    override fun onSignInStarted(client: GoogleSignInClient?) {
                        startActivityForResult(client?.signInIntent, REQUEST_SIGN_IN)
                    }
                })
            }

            btnForgetPassword.setOnClickListener {
                findNavController().navigate(R.id.forgotPasswordFragment)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    fun copyTextToClipboard( text: String) {
        // Get the ClipboardManager
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // Create a ClipData object with the text
        val clip = ClipData.newPlainText("label", text)

        // Set the ClipData to the clipboard
        clipboard.setPrimaryClip(clip)
        Log.d("TAG", "authWithAPI: copyTextToClipboard:: 2")

        toastMessage("Text copied to clipboard")
        // Optional: Show a toast message to indicate the text has been copied
        Log.d("TAG", "authWithAPI: copyTextToClipboard:: 3")
    }
    override fun setUpObserve() {
        viewModel.initLogin()
        viewModel.apply {

            toastMessage.observe(viewLifecycleOwner) { str ->
                toastMessage(str)
                
            }
            onAuthFinishes.observe(viewLifecycleOwner) {
                if (it != null) {
//                    onLoginDone(it)
                    startActivity(Intent(activity, MainActivity::class.java))
                    activity?.finish()

                }
            }

            validEmailLiveData.observe(viewLifecycleOwner) {
                alertEmail(it)
            }
            errorLiveData.observe(viewLifecycleOwner) { str ->
                val code = str.toIntOrNull()?:0
                if (code== 401){
                    toastMessage(getString(R.string.invalid_credentials))
                } else{
                    toastMessage(getString(R.string.failed))
                }
            }
            isLoading.observe(viewLifecycleOwner) {
                setLoading(it)
            }

        }
    }

    private fun onLoginDone(user: FirebaseUser?) {
        user?.getIdToken(false)?.addOnCompleteListener {
            if (it.isSuccessful) {
                val token = it.result.token ?: return@addOnCompleteListener
                Log.d("TAG", "authWithAPI: token :::: $token")
                copyTextToClipboard(token)
                startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
            }
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SIGN_IN && resultCode == Activity.RESULT_OK && data != null) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                account.idToken?.let { viewModel.firebaseAuthWithGoogle(it) }

            } catch (e: ApiException) {
                Toast.makeText(this.context, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private fun alertEmail(alert: String?) {
        alertEditText(alert,binding.txtLayoutEmail)
    }
}