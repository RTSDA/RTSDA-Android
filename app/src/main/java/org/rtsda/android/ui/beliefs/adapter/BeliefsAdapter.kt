package org.rtsda.android.ui.beliefs.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.rtsda.android.databinding.ItemBeliefBinding
import org.rtsda.android.ui.beliefs.Belief

class BeliefsAdapter(
    private val onItemClick: (Belief) -> Unit
) : ListAdapter<Belief, BeliefsAdapter.BeliefViewHolder>(BeliefDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeliefViewHolder {
        val binding = ItemBeliefBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BeliefViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BeliefViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BeliefViewHolder(
        private val binding: ItemBeliefBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(belief: Belief) {
            binding.beliefNumberText.text = "${belief.id}."
            binding.beliefTitleText.text = belief.title
            binding.beliefSummaryText.text = belief.summary

            binding.root.setOnClickListener {
                onItemClick(belief)
            }
        }
    }

    private class BeliefDiffCallback : DiffUtil.ItemCallback<Belief>() {
        override fun areItemsTheSame(oldItem: Belief, newItem: Belief): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Belief, newItem: Belief): Boolean {
            return oldItem == newItem
        }
    }
} 