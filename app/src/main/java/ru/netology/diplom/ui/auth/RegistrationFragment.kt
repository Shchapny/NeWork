package ru.netology.diplom.ui.auth

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diplom.R
import ru.netology.diplom.databinding.FragmentRegistrationBinding
import ru.netology.diplom.util.hideKeyboard
import ru.netology.diplom.util.loadFromCamera
import ru.netology.diplom.util.loadFromGallery
import ru.netology.diplom.viewmodel.auth.RegistrationViewModel

@AndroidEntryPoint
class RegistrationFragment : Fragment(R.layout.fragment_registration) {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<RegistrationViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)?.also {
            _binding = FragmentRegistrationBinding.bind(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            when {
                state.network -> onShowSnackbar(R.string.error_network)
                state.server -> onShowSnackbar(R.string.error_server)
                state.error -> onShowSnackbar(R.string.error_unknown)
                state.authState -> {
                    viewModel.reset()
                    hideKeyboard(requireView())
                    findNavController().navigate(R.id.action_registrationFragment_to_postFeedFragment)
                }
            }
        }

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when (result.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(result.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Activity.RESULT_OK -> {
                        val uri: Uri? = result.data?.data
                        viewModel.changePhoto(uri, uri?.toFile())
                    }
                }
            }

        val items = arrayOf(getString(R.string.gallery), getString(R.string.camera))

        binding.avatar.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setItems(items) { _, which ->
                    when (which) {
                        0 -> loadFromGallery(this, 200, pickPhotoLauncher::launch)
                        1 -> loadFromCamera(this, 200, pickPhotoLauncher::launch)
                    }
                }
                .show()
        }

        binding.apply {
            btSignup.setOnClickListener {
                resetErrorInfo()
                val login = login.text?.trim().toString()
                val password = password.text?.trim().toString()
                val name = userName.text?.trim().toString()
                val avatar = viewModel.photo.value?.file
                when {
                    login.isBlank() -> loginLayout.error = getString(R.string.error_login)
                    password.isBlank() -> passwordLayout.error = getString(R.string.error_password)
                    name.isBlank() -> userNameLayout.error = getString(R.string.error_name)
                    passwordConf.text?.trim().toString().isBlank() -> passwordConfLayout.error =
                        getString(R.string.error_password)
                    passwordConf.text?.trim().toString() != password -> passwordConfLayout.error =
                        getString(R.string.error_conf_password)
                    else -> {
                        if (avatar == null) {
                            viewModel.registration(login, password, name)
                        } else {
                            viewModel.registration(login, password, name, avatar)
                        }
                    }
                }
            }
        }

        viewModel.photo.observe(viewLifecycleOwner) { photo ->
            if (photo.uri == null) {
                return@observe
            }
            binding.avatar.setImageURI(photo.uri)
        }
    }

    private fun resetErrorInfo() {
        binding.loginLayout.error = null
        binding.userNameLayout.error = null
        binding.passwordConfLayout.error = null
        binding.passwordLayout.error = null
    }

    private fun onShowSnackbar(res: Int) {
        Snackbar.make(binding.root, res, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}