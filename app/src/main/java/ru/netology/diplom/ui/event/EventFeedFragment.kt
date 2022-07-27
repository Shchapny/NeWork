package ru.netology.diplom.ui.event

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.diplom.R
import ru.netology.diplom.adapder.EventActionListener
import ru.netology.diplom.adapder.EventAdapter
import ru.netology.diplom.data.dto.entity.Event
import ru.netology.diplom.databinding.FragmentEventFeedBinding
import ru.netology.diplom.viewmodel.EventViewModel
import ru.netology.diplom.viewmodel.auth.AuthViewModel

@AndroidEntryPoint
class EventFeedFragment : Fragment(R.layout.fragment_event_feed) {

    private var _binding: FragmentEventFeedBinding? = null
    private val binding get() = _binding!!
    private val authViewModel by activityViewModels<AuthViewModel>()
    private val eventViewModel by activityViewModels<EventViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)?.also {
            _binding = FragmentEventFeedBinding.bind(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = EventAdapter(object : EventActionListener {

            override fun onEdit(event: Event) {
                eventViewModel.edit(event)
                val action =
                    EventFeedFragmentDirections.actionEventFeedFragmentToEventNewOrEditFragment(
                        event
                    )
                findNavController().navigate(action)
            }

            override fun onRemove(event: Event) {
                eventViewModel.removeById(event.id)
            }

            override fun onLike(event: Event) {
                if (authViewModel.authenticated) {
                    if (!event.likedByMe) {
                        eventViewModel.likeById(event.id)
                    } else {
                        eventViewModel.dislikeById(event.id)
                    }
                } else {
                    findNavController().navigate(R.id.action_eventFeedFragment_to_authenticationFragment)
                }
            }

            override fun onShare(event: Event) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, event.content)
                    type = "text/plain"
                }
                val share = Intent.createChooser(intent, getString(R.string.chooser_share))
                startActivity(share)
            }

            override fun onAttachImage(event: Event) {
                val action =
                    EventFeedFragmentDirections.actionEventFeedFragmentToShowImageFragment(event.attachment)
                findNavController().navigate(action)
            }

            override fun onParticipate(event: Event) {
                if (authViewModel.authenticated) {
                    if (!event.participatedByMe) {
                        eventViewModel.participateById(event.id)
                    } else {
                        eventViewModel.refuseById(event.id)
                    }
                } else {
                    findNavController().navigate(R.id.action_eventFeedFragment_to_authenticationFragment)
                }
            }
        })
        binding.container.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            eventViewModel.data.collectLatest(adapter::submitData)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collectLatest { state ->
                binding.swipeRefresh.isRefreshing = state.refresh is LoadState.Loading
            }
        }

        eventViewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.apply {
                swipeRefresh.isRefreshing = state.loading
                swipeRefresh.isRefreshing = state.refreshing
                if (state.error) {
                    Snackbar.make(root, R.string.error_loading, Snackbar.LENGTH_INDEFINITE)
                        .setAction(R.string.retry_loading) { adapter.refresh() }
                        .show()
                }
            }
        }

        authViewModel.dataAuth.observe(viewLifecycleOwner) { adapter.refresh() }

        binding.apply {
            swipeRefresh.setOnRefreshListener { adapter.refresh() }
            fab.setOnClickListener {
                if (authViewModel.authenticated) {
                    findNavController().navigate(R.id.action_eventFeedFragment_to_eventNewOrEditFragment)
                } else {
                    findNavController().navigate(R.id.action_eventFeedFragment_to_authenticationFragment)
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}