package org.rtsda.android.ui.bulletins.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.rtsda.android.data.model.BulletinSection
import org.rtsda.android.databinding.ItemBulletinSectionBinding

class BulletinSectionsAdapter : ListAdapter<BulletinSection, BulletinSectionsAdapter.SectionViewHolder>(SectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val binding = ItemBulletinSectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SectionViewHolder(
        private val binding: ItemBulletinSectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(section: BulletinSection) {
            binding.sectionTitle.text = section.title
            binding.sectionContent.text = section.content
        }
    }

    private class SectionDiffCallback : DiffUtil.ItemCallback<BulletinSection>() {
        override fun areItemsTheSame(oldItem: BulletinSection, newItem: BulletinSection): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: BulletinSection, newItem: BulletinSection): Boolean {
            return oldItem == newItem
        }
    }
} 