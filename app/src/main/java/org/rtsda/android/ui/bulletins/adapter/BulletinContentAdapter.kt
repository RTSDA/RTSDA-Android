package org.rtsda.android.ui.bulletins.adapter

import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.rtsda.android.R
import org.rtsda.android.databinding.ItemBulletinContentBinding

class BulletinContentAdapter : ListAdapter<BulletinContentItem, BulletinContentAdapter.BulletinContentViewHolder>(BulletinContentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BulletinContentViewHolder {
        val binding = ItemBulletinContentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BulletinContentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BulletinContentViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("BulletinContentAdapter", "Binding item at position $position: $item")
        holder.bind(item)
    }

    class BulletinContentViewHolder(
        private val binding: ItemBulletinContentBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: BulletinContentItem) {
            Log.d("BulletinContentAdapter", "Binding content: title=${item.title}, content=${item.content}")
            binding.apply {
                contentTitle.text = item.title
                
                // Process HTML content and style subheadings
                val htmlContent = Html.fromHtml(item.content, Html.FROM_HTML_MODE_COMPACT)
                val spannable = SpannableString(htmlContent)
                
                // Find and style all subheadings (text between <b> tags)
                var startIndex = 0
                
                while (true) {
                    val boldStart = htmlContent.toString().indexOf("<b>", startIndex)
                    val boldEnd = htmlContent.toString().indexOf("</b>", startIndex)
                    
                    if (boldStart == -1 || boldEnd == -1) break
                    
                    val subheading = htmlContent.toString().substring(boldStart + 3, boldEnd)
                    val subheadingStart = htmlContent.toString().indexOf(subheading, startIndex)
                    val subheadingEnd = subheadingStart + subheading.length
                    
                    // Apply bold style to subheadings
                    spannable.setSpan(
                        StyleSpan(android.graphics.Typeface.BOLD),
                        subheadingStart,
                        subheadingEnd,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    
                    // Apply primary color to subheadings
                    spannable.setSpan(
                        ForegroundColorSpan(binding.root.context.getColor(R.color.primary)),
                        subheadingStart,
                        subheadingEnd,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    
                    startIndex = boldEnd + 4
                }
                
                contentText.text = spannable

                // Remove PDF button handling since it's now in the fragment
            }
        }
    }

    private class BulletinContentDiffCallback : DiffUtil.ItemCallback<BulletinContentItem>() {
        override fun areItemsTheSame(oldItem: BulletinContentItem, newItem: BulletinContentItem): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: BulletinContentItem, newItem: BulletinContentItem): Boolean {
            return oldItem == newItem
        }
    }
} 