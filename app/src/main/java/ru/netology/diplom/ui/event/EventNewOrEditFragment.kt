package ru.netology.diplom.ui.event

import android.app.Activity
import android.os.Bundle
import android.view.*
import android.widget.RadioButton
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
import ru.netology.diplom.databinding.FragmentEventNewOrEditBinding
import ru.netology.diplom.util.*
import ru.netology.diplom.viewmodel.EventViewModel

@AndroidEntryPoint
class EventNewOrEditFragment : Fragment(R.layout.fragment_event_new_or_edit) {

    private var _binding: FragmentEventNewOrEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<EventViewModel>()
    private val navArgs by navArgs<EventNewOrEditFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)?.also {
            _binding = FragmentEventNewOrEditBinding.bind(it)
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
                            resetErrorInfo()
                            val eventDate = binding.eventDate.text.toString()
                            val eventTime = binding.eventTime.text.toString()
                            val description = binding.eventDesc.text.toString()
                            val link = binding.eventLink.text.toString()
                            when {
                                eventDate.isBlank() -> binding.eventDateLayout.error =
                                    getString(R.string.error_event_date)
                                eventTime.isBlank() -> binding.eventTimeLayout.error =
                                    getString(R.string.error_event_time)
                                description.isBlank() -> binding.eventDescLayout.error =
                                    getString(R.string.error_description)
                                link.isBlank() -> binding.eventLinkLayout.error =
                                    getString(R.string.error_link)
                                else -> {
                                    viewModel.changeContent(
                                        description,
                                        "${eventDate.sendEventDate()}T${eventTime.sendEventTime()}Z",
                                        resources.getResourceEntryName(binding.type.checkedRadioButtonId),
                                        link
                                    )
                                    viewModel.save()
                                    hideKeyboard(requireView())
                                }
                            }
                        }
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)

        navArgs.eventArgs?.let { event ->
            binding.apply {
                eventDate.setText(event.datetime.substringBefore("T").dateFormatEntity())
                eventTime.setText(event.datetime.substringAfter("T").timeFormatEntity())
                eventDesc.setText(event.content)
                eventLink.setText(event.link)
                resources.getIdentifier(event.type.name.lowercase(), "id", context?.packageName)
                    .let { id ->
                        val type = binding.root.findViewById<RadioButton>(id)
                        type.isChecked = true
                    }
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
                loadFromGallery(this@EventNewOrEditFragment, 2048, photoLauncher::launch)
            }
            includeBottom.takePhoto.setOnClickListener {
                loadFromCamera(this@EventNewOrEditFragment, 2048, photoLauncher::launch)
            }
            includePhoto.removePhoto.setOnClickListener {
                viewModel.changePhoto(null, null)
            }
            eventDate.setOnClickListener {
                selectDateDialog(eventDateLayout.editText, requireParentFragment())
            }
            eventTime.setOnClickListener {
                selectTimeDialog(
                    eventTimeLayout.editText,
                    requireParentFragment(),
                    requireContext()
                )
            }
        }

        viewModel.eventCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }


        viewModel.photo.observe(viewLifecycleOwner) { photo ->
            binding.apply {
                if (photo.uri == null) {
                    includePhoto.photoContainer.visibility = View.GONE
                    return@observe
                }
                includePhoto.photoContainer.visibility = View.VISIBLE
                includePhoto.photo.setImageURI(photo.uri)
            }
        }
    }

    private fun resetErrorInfo() {
        binding.apply {
            eventDateLayout.error = null
            eventTimeLayout.error = null
            eventDescLayout.error = null
            eventLinkLayout.error = null
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}