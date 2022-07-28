package ru.netology.diplom.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diplom.R
import ru.netology.diplom.databinding.FragmentAuthenticationBinding
import ru.netology.diplom.util.hideKeyboard
import ru.netology.diplom.viewmodel.auth.AuthenticationViewModel

@AndroidEntryPoint
class AuthenticationFragment : Fragment(R.layout.fragment_authentication) {

    private var _binding: FragmentAuthenticationBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<AuthenticationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)?.also {
            _binding = FragmentAuthenticationBinding.bind(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btSignin.setOnClickListener {
                viewModel.reset()
                resetErrorInfo()
                val login = login.text?.trim().toString()
                val password = password.text?.trim().toString()
                when {
                    login.isBlank() -> loginLayout.error = getString(R.string.error_login)
                    password.isBlank() -> passwordLayout.error = getString(R.string.error_password)
                    else -> viewModel.authentication(login, password)
                }
                binding.warning.visibility = View.INVISIBLE
            }

            btSignup.setOnClickListener {
                findNavController().navigate(R.id.registrationFragment)
            }
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            when {
                state.error -> binding.warning.visibility = View.VISIBLE
                state.network -> onShowSnackbar(R.string.error_network)
                state.server -> onShowSnackbar(R.string.error_server)
                state.authState -> {
                    hideKeyboard(requireView())
                    findNavController().navigateUp()
                    binding.warning.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun resetErrorInfo() {
        binding.loginLayout.error = null
        binding.passwordLayout.error = null
    }

    private fun onShowSnackbar(res: Int) {
        Snackbar.make(binding.root, res, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        viewModel.reset()
        _binding = null
        super.onDestroyView()
    }
}