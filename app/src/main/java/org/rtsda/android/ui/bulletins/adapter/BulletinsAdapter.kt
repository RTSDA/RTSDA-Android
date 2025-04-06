package org.rtsda.android.ui.bulletins.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.rtsda.android.databinding.ItemBulletinBinding
import org.rtsda.android.domain.model.Bulletin
import java.text.SimpleDateFormat
import java.util.Locale

class BulletinsAdapter(
    private val onBulletinClicked: (Bulletin) -> Unit
) : ListAdapter<Bulletin, BulletinsAdapter.BulletinViewHolder>(BulletinDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BulletinViewHolder {
        val binding = ItemBulletinBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BulletinViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BulletinViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BulletinViewHolder(
        private val binding: ItemBulletinBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())

        fun bind(bulletin: Bulletin) {
            binding.bulletinTitle.text = bulletin.title
            binding.bulletinDate.text = dateFormat.format(bulletin.date)
            
            binding.root.setOnClickListener {
                onBulletinClicked(bulletin)
            }
        }
    }

    private class BulletinDiffCallback : DiffUtil.ItemCallback<Bulletin>() {
        override fun areItemsTheSame(oldItem: Bulletin, newItem: Bulletin): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Bulletin, newItem: Bulletin): Boolean {
            return oldItem == newItem
        }
    }
} 