package org.rtsda.android.presentation.events.adapter

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.rtsda.android.R
import org.rtsda.android.data.model.Event
import org.rtsda.android.databinding.ItemEventBinding
import java.text.SimpleDateFormat
import java.util.Locale

class EventsAdapter(
    private val onEventClick: (Event) -> Unit
) : ListAdapter<Event, EventsAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EventViewHolder(
        private val binding: ItemEventBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            binding.root.setOnClickListener {
                onEventClick(event)
            }

            val imageUrl = if (event.imageUrl.isNotEmpty()) {
                "https://pocketbase.rockvilletollandsda.church/api/files/events/${event.id}/${event.imageUrl}"
            } else {
                ""
            }

            Glide.with(binding.root)
                .load(imageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(binding.eventImage)

            binding.eventTitle.text = event.title
            binding.categoryChip.text = event.category

            // Set chip background color based on category
            val chipBackgroundColor = when (event.category.lowercase()) {
                "worship" -> R.color.worship_chip_background
                "fellowship" -> R.color.fellowship_chip_background
                "education" -> R.color.education_chip_background
                "outreach" -> R.color.outreach_chip_background
                else -> R.color.other_chip_background
            }
            binding.categoryChip.setChipBackgroundColorResource(chipBackgroundColor)
            binding.categoryChip.setTextColor(binding.root.context.getColor(R.color.on_surface))

            val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            binding.eventDateTime.text = binding.root.context.getString(
                R.string.event_date_time_format,
                dateFormat.format(event.startDate),
                timeFormat.format(event.startDate),
                timeFormat.format(event.endDate)
            )
        }
    }

    private class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }
} 