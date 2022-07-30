package ru.netology.diplom.adapder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.diplom.data.dto.User
import ru.netology.diplom.databinding.CardUserBinding
import ru.netology.diplom.util.loadImage

interface UserActionListener {
    fun selectUser(user: User)
}

class UserAdapter(private val listener: UserActionListener) :
    ListAdapter<User, UserViewHolder>(UserDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CardUserBinding.inflate(layoutInflater, parent, false)
        return UserViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }
}

class UserViewHolder(
    private val binding: CardUserBinding,
    private val listener: UserActionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User) {
        binding.apply {
            avatar.loadImage(user.avatar.toString(), "avatars")
            name.text = user.name

            selectUser.setOnClickListener {
                listener.selectUser(user)
            }
        }
    }
}

object UserDiffCallback : DiffUtil.ItemCallback<User>() {

    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}