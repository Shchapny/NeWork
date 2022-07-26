package ru.netology.diplom.ui.post

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.diplom.R
import ru.netology.diplom.adapder.PostActionListener
import ru.netology.diplom.adapder.PostAdapter
import ru.netology.diplom.data.dto.entity.Post
import ru.netology.diplom.databinding.FragmentPostFeedBinding
import ru.netology.diplom.viewmodel.PostViewModel
import ru.netology.diplom.viewmodel.auth.AuthViewModel

@AndroidEntryPoint
class PostFeedFragment : Fragment(R.layout.fragment_post_feed) {

    private var _binding: FragmentPostFeedBinding? = null
    private val binding get() = _binding!!
    private val authViewModel by viewModels<AuthViewModel>()
    private val postViewModel by viewModels<PostViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)?.also {
            _binding = FragmentPostFeedBinding.bind(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PostAdapter(object : PostActionListener {

            override fun onEdit(post: Post) {
                val action =
                    PostFeedFragmentDirections.actionPostFeedFragmentToPostNewOrEditFragment(post)
                findNavController().navigate(action)
                postViewModel.edit(post)
            }

            override fun onRemove(post: Post) {
                postViewModel.removeById(post.id)
            }

            override fun onLike(post: Post) {
                if (authViewModel.authenticated) {
                    if (!post.likedByMe) {
                        postViewModel.likeById(post.id)
                    } else {
                        postViewModel.dislikeById(post.id)
                    }
                } else {
                    findNavController().navigate(R.id.action_postFeedFragment_to_authenticationFragment)
                }
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val share = Intent.createChooser(intent, getString(R.string.chooser_share))
                startActivity(share)
            }

            override fun onAttachImage(post: Post) {
                val action =
                    PostFeedFragmentDirections.actionPostFeedFragmentToShowImageFragment(post.attachment)
                findNavController().navigate(action)
            }
        })
        binding.container.adapter = adapter

        lifecycleScope.launchWhenCreated {
            postViewModel.data.collectLatest(adapter::submitData)
        }

        lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collectLatest { state ->
                binding.swipeRefresh.isRefreshing = state.refresh is LoadState.Loading
            }
        }

        postViewModel.dataState.observe(viewLifecycleOwner) { state ->
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
                    findNavController().navigate(R.id.action_postFeedFragment_to_postNewOrEditFragment)
                } else {
                    findNavController().navigate(R.id.action_postFeedFragment_to_authenticationFragment)
                }
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}