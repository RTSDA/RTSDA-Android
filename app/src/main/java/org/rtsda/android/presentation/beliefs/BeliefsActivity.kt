package org.rtsda.android.presentation.beliefs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.rtsda.android.R
import org.rtsda.android.databinding.ActivityBeliefsBinding
import org.rtsda.android.databinding.DialogVersesBinding
import org.rtsda.android.databinding.ItemBeliefBinding
import org.rtsda.android.ui.beliefs.BeliefsFragment
import org.rtsda.android.ui.beliefs.Belief

class BeliefsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBeliefsBinding

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, BeliefsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBeliefsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Our Beliefs"
        
        // Set navigation icon color
        binding.toolbar.setNavigationIconTint(getColor(R.color.secondary))

        binding.beliefsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.beliefsRecyclerView.adapter = BeliefsAdapter(BeliefsFragment.beliefs) { belief ->
            showVersesDialog(belief)
        }
    }

    private fun showVersesDialog(belief: Belief) {
        val dialogBinding = DialogVersesBinding.inflate(LayoutInflater.from(this))
        
        dialogBinding.dialogTitleText.text = belief.title
        
        val versesText = belief.verses.joinToString("\n\n") { verse ->
            "${verse.reference}\n${verse.text}"
        }
        dialogBinding.dialogVersesText.text = versesText

        MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_Rounded)
            .setView(dialogBinding.root)
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

class BeliefsAdapter(
    private val beliefs: List<Belief>,
    private val onItemClick: (Belief) -> Unit
) : RecyclerView.Adapter<BeliefsAdapter.BeliefViewHolder>() {

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): BeliefViewHolder {
        val binding = ItemBeliefBinding.inflate(
            android.view.LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BeliefViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BeliefViewHolder, position: Int) {
        val belief = beliefs[position]
        holder.bind(belief)
        holder.itemView.setOnClickListener { onItemClick(belief) }
    }

    override fun getItemCount() = beliefs.size

    class BeliefViewHolder(
        private val binding: ItemBeliefBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(belief: Belief) {
            binding.beliefNumberText.text = "${belief.id}."
            binding.beliefTitleText.text = belief.title
            binding.beliefSummaryText.text = belief.summary
        }
    }
} 