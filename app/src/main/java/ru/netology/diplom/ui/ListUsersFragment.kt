package ru.netology.diplom.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        return super.onCreateView(inflater, container, savedInstanceState)?.also {
            _binding = FragmentListUsersBinding.bind(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = UserAdapter(object : UserActionListener {
            override fun selectUser(user: User) {
                eventViewModel.selectSpeaker(user)
                findNavController().navigateUp()
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
            adapter.submitList(users)
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
}