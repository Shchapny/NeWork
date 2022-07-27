package ru.netology.diplom.ui.job

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diplom.R
import ru.netology.diplom.adapder.JobActionListener
import ru.netology.diplom.adapder.JobAdapter
import ru.netology.diplom.data.dto.entity.Job
import ru.netology.diplom.databinding.FragmentJobFeedBinding
import ru.netology.diplom.viewmodel.JobViewModel

@AndroidEntryPoint
class JobFeedFragment : Fragment(R.layout.fragment_job_feed) {

    private var _binding: FragmentJobFeedBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<JobViewModel>()

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
                val action =
                    JobFeedFragmentDirections.actionJobFeedFragmentToJobNewOrEditFragment(job)
                findNavController().navigate(action)
                viewModel.edit(job)
            }

            override fun onRemove(job: Job) {
                viewModel.removeById(job.id)
            }
        })
        binding.container.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.apply {
                swipeRefresh.isRefreshing = state.loading
                swipeRefresh.isRefreshing = state.refreshing
                progress.isVisible = state.loading
                if (state.error) {
                    Snackbar.make(root, R.string.error_loading, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry_loading) { viewModel.refresh() }
                        .show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.data.collect { state ->
                adapter.submitList(state.entities)
            }
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.jobNewOrEditFragment)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}