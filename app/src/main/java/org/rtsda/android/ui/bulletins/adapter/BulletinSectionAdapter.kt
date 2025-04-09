package org.rtsda.android.ui.bulletins.adapter

import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.rtsda.android.databinding.ItemBulletinSectionBinding
import org.rtsda.android.domain.model.BulletinSection

class BulletinSectionAdapter : ListAdapter<BulletinSection, BulletinSectionAdapter.ViewHolder>(DiffCallback()) {

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
            
            // Format content as two columns
            val lines = section.content.split("\n")
            val formattedContent = SpannableStringBuilder()
            var isFirstLine = true
            
            for (line in lines) {
                if (line.isBlank()) continue
                
                if (!isFirstLine) {
                    formattedContent.append("\n")
                }
                
                // Split line into role and name if it contains a colon
                if (line.contains(":")) {
                    val parts = line.split(":", limit = 2)
                    val role = parts[0].trim()
                    val name = parts[1].trim()
                    
                    // Add role and name with proper spacing
                    formattedContent.append(role)
                    formattedContent.append("\t\t\t") // Reduced tab spacing
                    formattedContent.append(name)
                } else {
                    // If no colon, just add the line as is
                    formattedContent.append(line)
                }
                
                isFirstLine = false
            }
            
            binding.sectionContentText.text = formattedContent
            
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