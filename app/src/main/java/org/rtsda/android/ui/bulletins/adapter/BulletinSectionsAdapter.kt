package org.rtsda.android.ui.bulletins.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.rtsda.android.databinding.ItemBulletinSectionBinding
import org.rtsda.android.domain.model.BulletinSection

class BulletinSectionsAdapter : ListAdapter<BulletinSection, BulletinSectionsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBulletinSectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val isLastItem = position == itemCount - 1
        holder.bind(getItem(position), isLastItem)
    }

    class ViewHolder(
        private val binding: ItemBulletinSectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(section: BulletinSection, isLastItem: Boolean) {
            binding.sectionTitleText.text = section.title
            
            // Simply replace newlines with HTML breaks
            val content = section.content.replace("\n", "<br>")
            binding.sectionContentText.text = HtmlCompat.fromHtml(
                content,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )
            
            // Hide divider for the last item
            binding.divider.visibility = if (isLastItem) View.GONE else View.VISIBLE
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<BulletinSection>() {
        override fun areItemsTheSame(oldItem: BulletinSection, newItem: BulletinSection): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: BulletinSection, newItem: BulletinSection): Boolean {
            return oldItem == newItem
        }
    }
} 