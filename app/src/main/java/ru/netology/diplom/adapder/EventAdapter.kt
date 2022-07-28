package ru.netology.diplom.adapder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.diplom.R
import ru.netology.diplom.data.dto.entity.Event
import ru.netology.diplom.databinding.CardEventBinding
import ru.netology.diplom.enumeration.AttachmentType
import ru.netology.diplom.util.count
import ru.netology.diplom.util.dateFormat
import ru.netology.diplom.util.loadImage
import ru.netology.diplom.util.timeFormat

interface EventActionListener {
    fun onEdit(event: Event)
    fun onRemove(event: Event)
    fun onLike(event: Event)
    fun onShare(event: Event)
    fun onAttachImage(event: Event)
    fun onParticipate(event: Event)
}

class EventAdapter(private val listener: EventActionListener) :
    PagingDataAdapter<Event, EventViewHolder>(EventDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CardEventBinding.inflate(layoutInflater, parent, false)
        return EventViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position) ?: return
        holder.bind(event)
    }
}

class EventViewHolder(
    private val binding: CardEventBinding,
    private val listener: EventActionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(event: Event) {
        binding.apply {
            avatar.loadImage(event.authorAvatar.toString(), "avatars")
            author.text = event.author
            content.text = event.content
            published.text = event.published.dateFormat()

            val date = event.datetime.substringBefore("T").dateFormat()
            val time = event.datetime.substringAfter("T").timeFormat()
            eventDate.text = root.context.getString(R.string.event_datetime, date, time)

            eventType.text = event.type.format
            eventLink.text = event.link

            likes.text = count(event.likeOwnerIds.size.toLong())
            likes.isChecked = event.likedByMe

            participants.text = count(event.participantsIds.size.toLong())
            participate.apply {
                text = if (event.participatedByMe) {
                    root.context.getString(R.string.refuse)
                } else {
                    root.context.getString(R.string.participate)
                }
            }

            menu.visibility = if (event.ownedByMe) View.VISIBLE else View.INVISIBLE
            menu.setOnClickListener { view ->
                PopupMenu(view.context, view).apply {
                    inflate(R.menu.menu_entity)
                    menu.setGroupVisible(R.id.owned, event.ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                listener.onRemove(event)
                                true
                            }
                            R.id.edit -> {
                                listener.onEdit(event)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            likes.setOnClickListener { listener.onLike(event) }
            share.setOnClickListener { listener.onShare(event) }
            participate.setOnClickListener { listener.onParticipate(event) }
            imageEvent.setOnClickListener { listener.onAttachImage(event) }

            if (event.attachment != null && event.attachment.type == AttachmentType.IMAGE) {
                imageEvent.visibility = View.VISIBLE
                imageEvent.loadImage(event.attachment.url, "media")
            } else {
                imageEvent.visibility = View.GONE
            }
        }
    }
}

object EventDiffCallback : DiffUtil.ItemCallback<Event>() {

    override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem == newItem
    }
}