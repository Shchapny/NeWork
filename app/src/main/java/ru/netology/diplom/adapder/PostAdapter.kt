package ru.netology.diplom.adapder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.diplom.BuildConfig
import ru.netology.diplom.R
import ru.netology.diplom.data.dto.entity.Post
import ru.netology.diplom.databinding.CardPostBinding
import ru.netology.diplom.enumeration.AttachmentType
import ru.netology.diplom.util.count
import ru.netology.diplom.util.loadImage

interface PostActionListener {
    fun onEdit(post: Post)
    fun onRemove(post: Post)
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onAttachImage(post: Post)
}

class PostAdapter(private val listener: PostActionListener) :
    PagingDataAdapter<Post, PostViewHolder>(PostDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CardPostBinding.inflate(layoutInflater, parent, false)
        return PostViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position) ?: return
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val listener: PostActionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            avatar.loadImage(BuildConfig.BASE_URL, "avatars", post.authorAvatar)
            author.text = post.author
            content.text = post.content
            published.text = post.published

            likes.text = count(post.likeOwnerIds.size.toLong())
            likes.isChecked = post.likedByMe

            menu.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE
            menu.setOnClickListener { view ->
                PopupMenu(view.context, view).apply {
                    inflate(R.menu.menu_entity)
                    menu.setGroupVisible(R.id.owned, post.ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                listener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                listener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            likes.setOnClickListener { listener.onLike(post) }
            share.setOnClickListener { listener.onShare(post) }
            imagePost.setOnClickListener { listener.onAttachImage(post) }

            if (post.attachment != null && post.attachment.type == AttachmentType.IMAGE) {
                imagePost.visibility = View.VISIBLE
                imagePost.loadImage(BuildConfig.BASE_URL, "media", post.attachment.url)
            } else {
                imagePost.visibility = View.GONE
            }
        }
    }
}

object PostDiffCallback : DiffUtil.ItemCallback<Post>() {

    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}