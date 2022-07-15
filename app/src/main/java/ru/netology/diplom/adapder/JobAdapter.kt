package ru.netology.diplom.adapder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.diplom.R
import ru.netology.diplom.data.dto.entity.Job
import ru.netology.diplom.databinding.CardJobBinding
import ru.netology.diplom.util.convertLongToString

interface JobActionListener {
    fun onEdit(job: Job)
    fun onRemove(job: Job)
}

class JobAdapter(private val listener: JobActionListener) :
    ListAdapter<Job, JobViewHolder>(JobDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CardJobBinding.inflate(layoutInflater, parent, false)
        return JobViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = getItem(position)
        holder.bind(job)
    }
}

class JobViewHolder(
    private val binding: CardJobBinding,
    private val listener: JobActionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(job: Job) {
        binding.apply {
            companyLayout.text = root.context.getString(R.string.company_job, job.name)
            positionLayout.text = root.context.getString(R.string.position_job, job.position)

            workExperience.text = if (job.finish == null) {
                root.context.getString(
                    R.string.work_experience_now,
                    job.start.convertLongToString()
                )
            } else {
                root.context.getString(
                    R.string.work_experience,
                    job.start.convertLongToString(),
                    job.finish.convertLongToString()
                )
            }

            linkLayout.apply {
                if (job.link == null) {
                    visibility = View.GONE
                } else {
                    text = root.context.getString(R.string.link_job, job.link)
                }
            }

            menu.visibility = if (job.ownedByMe) View.VISIBLE else View.GONE
            menu.setOnClickListener { view ->
                PopupMenu(view.context, view).apply {
                    inflate(R.menu.menu_entity)
                    menu.setGroupVisible(R.id.owned, job.ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                listener.onRemove(job)
                                true
                            }
                            R.id.edit -> {
                                listener.onEdit(job)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}

object JobDiffCallback : DiffUtil.ItemCallback<Job>() {

    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem == newItem
    }
}

