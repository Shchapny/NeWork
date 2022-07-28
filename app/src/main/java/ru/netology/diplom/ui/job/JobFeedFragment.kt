package ru.netology.diplom.ui.job

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diplom.R
import ru.netology.diplom.adapder.JobActionListener
import ru.netology.diplom.adapder.JobAdapter
import ru.netology.diplom.data.dto.entity.Job
import ru.netology.diplom.databinding.FragmentJobFeedBinding
import ru.netology.diplom.viewmodel.JobViewModel
import ru.netology.diplom.viewmodel.auth.AuthViewModel

@AndroidEntryPoint
class JobFeedFragment : Fragment(R.layout.fragment_job_feed) {

    private var _binding: FragmentJobFeedBinding? = null
    private val binding get() = _binding!!
    private val authViewModel by activityViewModels<AuthViewModel>()
    private val jobViewModel by activityViewModels<JobViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)?.also {
            _binding = FragmentJobFeedBinding.bind(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = JobAdapter(object : JobActionListener {

            override fun onEdit(job: Job) {
                jobViewModel.edit(job)
                val action =
                    JobFeedFragmentDirections.actionJobFeedFragmentToJobNewOrEditFragment(job)
                findNavController().navigate(action)
            }

            override fun onRemove(job: Job) {
                jobViewModel.removeById(job.id)
            }
        })
        binding.container.adapter = adapter

        jobViewModel.data.observe(viewLifecycleOwner) { jobs ->
            adapter.submitList(jobs.list)
            if (jobs.empty) {
                binding.infoText.visibility = View.VISIBLE
                binding.infoText.text = getString(R.string.empty_entity, "Jobs")
            } else {
                binding.infoText.visibility = View.GONE
            }
        }

        authViewModel.dataAuth.observe(viewLifecycleOwner) { auth ->
            if (auth.id == 0L) {
                binding.infoText.visibility = View.VISIBLE
                binding.infoText.setText(R.string.notauthorized)
            } else {
                binding.infoText.visibility = View.GONE
                jobViewModel.loadJobs(auth.id)
            }
        }

        jobViewModel.dataState.observe(viewLifecycleOwner) { state ->
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.fab.setOnClickListener {
            if (authViewModel.authenticated) {
                findNavController().navigate(R.id.jobNewOrEditFragment)
            } else {
                findNavController().navigate(R.id.authenticationFragment)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}