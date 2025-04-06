package org.rtsda.android.ui.bulletins.adapter

import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.rtsda.android.databinding.ItemBulletinSectionBinding
import org.rtsda.android.domain.model.BulletinSection

class BulletinSectionAdapter : ListAdapter<BulletinSection, BulletinSectionAdapter.BulletinSectionViewHolder>(BulletinSectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BulletinSectionViewHolder {
        val binding = ItemBulletinSectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BulletinSectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BulletinSectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BulletinSectionViewHolder(
        private val binding: ItemBulletinSectionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(section: BulletinSection) {
            binding.sectionTitle.text = section.title
            binding.sectionContent.apply {
                movementMethod = LinkMovementMethod.getInstance()
                text = formatContent(section.content)
            }
        }

        private fun formatContent(content: String): Spanned {
            // First clean the HTML entities
            val cleanedContent = content
                .replace("&amp;", "&")
                .replace("&nbsp;", " ")
                .replace("&quot;", "\"")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&apos;", "'")
                .replace("&ldquo;", "\"")
                .replace("&rdquo;", "\"")
                .replace("&lsquo;", "'")
                .replace("&rsquo;", "'")
                .replace("&mdash;", "—")
                .replace("&ndash;", "–")
                .replace("&hellip;", "...")
                .replace("&copy;", "©")
                .replace("&reg;", "®")
                .replace("&trade;", "™")
                .replace("&euro;", "€")
                .replace("&pound;", "£")
                .replace("&yen;", "¥")
                .replace("&cent;", "¢")
                .replace("&sect;", "§")
                .replace("&para;", "¶")
                .replace("&dagger;", "†")
                .replace("&Dagger;", "‡")
                .replace("&permil;", "‰")
                .replace("&micro;", "µ")
                .replace("&middot;", "·")
                .replace("&bull;", "•")
                .replace("&hellip;", "...")
                .replace("&prime;", "′")
                .replace("&Prime;", "″")
                .replace("&lsaquo;", "‹")
                .replace("&rsaquo;", "›")
                .replace("&laquo;", "«")
                .replace("&raquo;", "»")
                .replace("&oline;", "‾")
                .replace("&frasl;", "/")
                .replace("&weierp;", "℘")
                .replace("&image;", "ℑ")
                .replace("&real;", "ℜ")
                .replace("&trade;", "™")
                .replace("&alefsym;", "ℵ")
                .replace("&larr;", "←")
                .replace("&uarr;", "↑")
                .replace("&rarr;", "→")
                .replace("&darr;", "↓")
                .replace("&harr;", "↔")
                .replace("&crarr;", "↵")
                .replace("&lArr;", "⇐")
                .replace("&uArr;", "⇑")
                .replace("&rArr;", "⇒")
                .replace("&dArr;", "⇓")
                .replace("&hArr;", "⇔")
                .replace("&forall;", "∀")
                .replace("&part;", "∂")
                .replace("&exist;", "∃")
                .replace("&empty;", "∅")
                .replace("&nabla;", "∇")
                .replace("&isin;", "∈")
                .replace("&notin;", "∉")
                .replace("&ni;", "∋")
                .replace("&prod;", "∏")
                .replace("&sum;", "∑")
                .replace("&minus;", "−")
                .replace("&lowast;", "∗")
                .replace("&radic;", "√")
                .replace("&prop;", "∝")
                .replace("&infin;", "∞")
                .replace("&ang;", "∠")
                .replace("&and;", "∧")
                .replace("&or;", "∨")
                .replace("&cap;", "∩")
                .replace("&cup;", "∪")
                .replace("&int;", "∫")
                .replace("&there4;", "∴")
                .replace("&sim;", "∼")
                .replace("&cong;", "≅")
                .replace("&asymp;", "≈")
                .replace("&ne;", "≠")
                .replace("&equiv;", "≡")
                .replace("&le;", "≤")
                .replace("&ge;", "≥")
                .replace("&sub;", "⊂")
                .replace("&sup;", "⊃")
                .replace("&nsub;", "⊄")
                .replace("&sube;", "⊆")
                .replace("&supe;", "⊇")
                .replace("&oplus;", "⊕")
                .replace("&otimes;", "⊗")
                .replace("&perp;", "⊥")
                .replace("&sdot;", "⋅")
                .replace("&lceil;", "⌈")
                .replace("&rceil;", "⌉")
                .replace("&lfloor;", "⌊")
                .replace("&rfloor;", "⌋")
                .replace("&lang;", "〈")
                .replace("&rang;", "〉")
                .replace("&loz;", "◊")
                .replace("&spades;", "♠")
                .replace("&clubs;", "♣")
                .replace("&hearts;", "♥")
                .replace("&diams;", "♦")
                .trim()

            // Add proper spacing and formatting
            val formattedContent = cleanedContent
                .replace("\n", "<br>") // Preserve line breaks
                .replace("<br><br>", "<br><br>") // Ensure double line breaks
                .replace("<p>", "<p style='margin-bottom: 16dp;'>") // Add spacing between paragraphs
                .replace("<ul>", "<ul style='margin-bottom: 16dp;'>") // Add spacing for lists
                .replace("<ol>", "<ol style='margin-bottom: 16dp;'>") // Add spacing for ordered lists
                .replace("<li>", "<li style='margin-bottom: 8dp;'>") // Add spacing for list items

            return HtmlCompat.fromHtml(formattedContent, HtmlCompat.FROM_HTML_MODE_COMPACT)
        }
    }

    private class BulletinSectionDiffCallback : DiffUtil.ItemCallback<BulletinSection>() {
        override fun areItemsTheSame(oldItem: BulletinSection, newItem: BulletinSection): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: BulletinSection, newItem: BulletinSection): Boolean {
            return oldItem == newItem
        }
    }
} 