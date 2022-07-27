package ru.netology.diplom.ui.post

import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diplom.R
import ru.netology.diplom.databinding.FragmentPostNewOrEditBinding
import ru.netology.diplom.util.hideKeyboard
import ru.netology.diplom.util.loadFromCamera
import ru.netology.diplom.util.loadFromGallery
import ru.netology.diplom.viewmodel.PostViewModel

@AndroidEntryPoint
class PostNewOrEditFragment : Fragment(R.layout.fragment_post_new_or_edit) {

    private var _binding: FragmentPostNewOrEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<PostViewModel>()
    private val navArgs by navArgs<PostNewOrEditFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)?.also {
            _binding = FragmentPostNewOrEditBinding.bind(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_save, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.save -> {
                        _binding?.let { binding ->
                            if (binding.textPost.text.toString().isEmpty()) {
                                findNavController().navigateUp()
                            } else {
                                viewModel.changeContent(binding.textPost.text.toString())
                                viewModel.save()
                                hideKeyboard(requireView())
                            }
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)

        navArgs.postArgs?.let { post ->
            binding.apply {
                textPost.setText(post.content)
            }
        }

        val photoLauncher =
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
                        val uri = result.data?.data
                        viewModel.changePhoto(uri, uri?.toFile())
                    }
                }
            }

        binding.apply {
            includeBottom.pickPhoto.setOnClickListener {
                loadFromGallery(this@PostNewOrEditFragment, 2048, photoLauncher::launch)
            }
            includeBottom.takePhoto.setOnClickListener {
                loadFromCamera(this@PostNewOrEditFragment, 2048, photoLauncher::launch)
            }
            includePhoto.removePhoto.setOnClickListener {
                viewModel.changePhoto(null, null)
            }
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        binding.apply {
            viewModel.photo.observe(viewLifecycleOwner) { photo ->
                if (photo.uri == null) {
                    includePhoto.photoContainer.visibility = View.GONE
                    return@observe
                }
                includePhoto.photoContainer.visibility = View.VISIBLE
                includePhoto.photo.setImageURI(photo.uri)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}