package org.rtsda.android.presentation.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.rtsda.android.domain.model.Message
import org.rtsda.android.databinding.ItemLiveStreamBinding
import org.rtsda.android.databinding.ItemMessageBinding

class MessagesAdapter(
    private val onMessageClick: (Message) -> Unit,
    private val onLiveStreamClick: (() -> Unit)? = null
) : ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_LIVE_STREAM = 0
        private const val VIEW_TYPE_MESSAGE = 1
    }

    private var liveStreamStatus: LiveStreamStatus? = null
    private var currentList: List<Message> = emptyList()

    fun setLiveStreamStatus(status: LiveStreamStatus?) {
        val oldStatus = liveStreamStatus
        liveStreamStatus = status
        
        // Only update if the status actually changed
        if (oldStatus != status) {
            // Create a new list that includes or excludes the live stream card
            val newList = if (status != null) {
                listOf(createLiveStreamMessage(status)) + currentList
            } else {
                currentList
            }
            
            // Submit the new list
            submitList(newList)
        }
    }

    private fun createLiveStreamMessage(status: LiveStreamStatus): Message {
        return Message(
            id = "live",
            title = status.title,
            speaker = "Live",
            videoUrl = "",
            thumbnailUrl = status.thumbnailUrl,
            date = "",
            description = status.description,
            isLiveStream = true
        )
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && liveStreamStatus != null) {
            VIEW_TYPE_LIVE_STREAM
        } else {
            VIEW_TYPE_MESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LIVE_STREAM -> {
                val binding = ItemLiveStreamBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                LiveStreamViewHolder(binding)
            }
            else -> {
                val binding = ItemMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                MessageViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LiveStreamViewHolder -> holder.bind(liveStreamStatus!!)
            is MessageViewHolder -> holder.bind(getItem(position))
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (liveStreamStatus != null) 1 else 0
    }

    override fun getItem(position: Int): Message {
        return if (liveStreamStatus != null && position == 0) {
            createLiveStreamMessage(liveStreamStatus!!)
        } else {
            // Adjust position for messages
            super.getItem(if (liveStreamStatus != null) position - 1 else position)
        }
    }

    override fun submitList(list: List<Message>?) {
        currentList = list ?: emptyList()
        super.submitList(list)
    }

    inner class LiveStreamViewHolder(
        private val binding: ItemLiveStreamBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onLiveStreamClick?.invoke()
            }
        }

        fun bind(status: LiveStreamStatus) {
            binding.apply {
                titleTextView.text = status.title
                descriptionTextView.text = status.description
                
                Glide.with(thumbnailImageView)
                    .load(status.thumbnailUrl)
                    .centerCrop()
                    .into(thumbnailImageView)
            }
        }
    }

    inner class MessageViewHolder(
        private val binding: ItemMessageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onMessageClick(getItem(position))
                }
            }
        }

        fun bind(message: Message) {
            binding.apply {
                titleTextView.text = message.title
                speakerTextView.text = message.speaker
                dateTextView.text = message.date
                liveChip.visibility = if (message.isLiveStream) ViewGroup.VISIBLE else ViewGroup.GONE

                Glide.with(thumbnailImageView)
                    .load(message.thumbnailUrl)
                    .centerCrop()
                    .into(thumbnailImageView)
            }
        }
    }
}

data class LiveStreamStatus(
    val title: String,
    val description: String,
    val thumbnailUrl: String?
)

class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
} 