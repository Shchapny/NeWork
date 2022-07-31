package ru.netology.diplom.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.diplom.R
import ru.netology.diplom.adapder.UserActionListener
import ru.netology.diplom.adapder.UserAdapter
import ru.netology.diplom.data.dto.User
import ru.netology.diplom.databinding.FragmentListUsersBinding
import ru.netology.diplom.viewmodel.EventViewModel
import ru.netology.diplom.viewmodel.auth.AuthViewModel

@AndroidEntryPoint
class ListUsersFragment : Fragment(R.layout.fragment_list_users) {

    private var _binding: FragmentListUsersBinding? = null
    private val binding get() = _binding!!
    private val authViewModel by activityViewModels<AuthViewModel>()
    private val eventViewModel by activityViewModels<EventViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        when (arguments?.getString("showUsers")) {
            "participants" -> titleListUsersFragment(R.string.list_participants)
            "speakers" -> titleListUsersFragment(R.string.list_speakers)
            "selectSpeakers" -> titleListUsersFragment(R.string.select_speaker)
        }
        return super.onCreateView(inflater, container, savedInstanceState)?.also {
            _binding = FragmentListUsersBinding.bind(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = UserAdapter(object : UserActionListener {
            override fun selectUser(user: User) {
                when (arguments?.getString("showUsers")) {
                    "selectSpeakers" -> {
                        eventViewModel.selectSpeaker(user)
                        findNavController().navigateUp()
                    }
                }
            }
        })
        binding.apply {
            container.adapter = adapter
            container.addItemDecoration(
                DividerItemDecoration(
                    container.context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }

        authViewModel.data.observe(viewLifecycleOwner) { users ->
            when(arguments?.getString("showUsers")) {
                "selectSpeakers" -> adapter.submitList(users)
                else -> adapter.submitList(users.filter { user ->
                    authViewModel.userIds.value?.contains(user.id) == true
                })
            }
        }

        authViewModel.dataState.observe(viewLifecycleOwner) { state ->
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun titleListUsersFragment(@StringRes id: Int) {
        (activity as AppCompatActivity).supportActionBar?.title = context?.getString(id)
    }
}