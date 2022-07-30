package ru.netology.diplom.ui.job

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diplom.R
import ru.netology.diplom.databinding.FragmentJobNewOrEditBinding
import ru.netology.diplom.util.*
import ru.netology.diplom.viewmodel.JobViewModel

@AndroidEntryPoint
class JobNewOrEditFragment : Fragment(R.layout.fragment_job_new_or_edit) {

    private var _binding: FragmentJobNewOrEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<JobViewModel>()
    private val navArgs by navArgs<JobNewOrEditFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (navArgs.jobArgs != null) {
            (activity as AppCompatActivity).supportActionBar?.title =
                context?.getString(R.string.edit_job)
        }
        return super.onCreateView(inflater, container, savedInstanceState)?.also {
            _binding = FragmentJobNewOrEditBinding.bind(it)
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
                        resetErrorInfo()
                        _binding?.let { binding ->
                            val company = binding.company.text.toString()
                            val position = binding.position.text.toString()
                            val startDate = binding.startDate.text.toString()
                            val finishDate = if (binding.finishDate.text.isNullOrBlank()) {
                                null
                            } else {
                                binding.finishDate.text.toString()
                            }
                            val link = if (binding.link.text.isNullOrBlank()) {
                                null
                            } else {
                                binding.link.text.toString()
                            }
                            when {
                                startDate.isBlank() -> binding.startDateLayout.error =
                                    getString(R.string.error_startdate)
                                company.isBlank() -> binding.companyLayout.error =
                                    getString(R.string.error_company)
                                position.isBlank() -> binding.positionLayout.error =
                                    getString(R.string.error_position)
                                else -> {
                                    viewModel.change(
                                        company = company,
                                        position = position,
                                        startDate = startDate.sendDate(),
                                        finishDate = finishDate?.sendDate(),
                                        link = link
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

        navArgs.jobArgs?.let { job ->
            binding.apply {
                company.setText(job.name)
                position.setText(job.position)
                startDate.setText(job.start.dateFormat())
                if (job.finish.isNullOrEmpty()) {
                    finishDate.setText("")
                } else {
                    finishDate.setText(job.finish.dateFormat())
                }
                link.setText(job.link)
            }
        }

        binding.apply {
            startDate.setOnClickListener {
                selectDateDialog(startDateLayout.editText, requireParentFragment())
            }
            finishDate.setOnClickListener {
                selectDateDialog(finishDateLayout.editText, requireParentFragment())
            }
        }

        viewModel.jobCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_unknown, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun resetErrorInfo() {
        binding.apply {
            startDateLayout.error = null
            companyLayout.error = null
            positionLayout.error = null
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}