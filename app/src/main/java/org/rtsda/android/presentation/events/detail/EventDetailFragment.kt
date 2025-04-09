package org.rtsda.android.presentation.events.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import org.rtsda.android.R
import org.rtsda.android.data.model.Event
import org.rtsda.android.databinding.FragmentEventDetailBinding
import java.text.SimpleDateFormat
import java.util.Locale

class EventDetailFragment : Fragment() {
    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!
    
    private var event: Event? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Get event from arguments
        arguments?.let { args ->
            event = args.getParcelable(ARG_EVENT)
            event?.let { updateUI(it) }
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.apply {
            addToCalendarButton.setOnClickListener {
                event?.let { addEventToCalendar(it) }
            }

            eventLocation.setOnClickListener {
                event?.let { openLocationInMaps(it) }
            }
        }
    }

    private fun updateUI(event: Event) {
        binding.apply {
            toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }
            
            eventTitle.text = event.title
            categoryChip.text = event.category

            // Set chip background color based on category
            val chipBackgroundColor = when (event.category.lowercase()) {
                "worship" -> R.color.worship_chip_background
                "fellowship" -> R.color.fellowship_chip_background
                "education" -> R.color.education_chip_background
                "outreach" -> R.color.outreach_chip_background
                "ministry" -> R.color.ministry_chip_background
                else -> R.color.other_chip_background
            }
            categoryChip.setChipBackgroundColorResource(chipBackgroundColor)
            categoryChip.setTextColor(requireContext().getColor(R.color.on_surface))

            val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            eventDateTime.text = getString(
                R.string.event_date_time_format,
                dateFormat.format(event.startDate),
                timeFormat.format(event.startDate),
                timeFormat.format(event.endDate)
            )

            // Handle HTML content in description
            val descriptionHtml = HtmlCompat.fromHtml(
                event.description ?: "",
                HtmlCompat.FROM_HTML_MODE_COMPACT
            )
            eventDescription.text = descriptionHtml
            eventDescription.movementMethod = LinkMovementMethod.getInstance()

            // Handle location
            if (event.location.isNullOrBlank()) {
                eventLocation.visibility = View.GONE
            } else {
                eventLocation.visibility = View.VISIBLE
                eventLocation.text = event.location
            }

            // Handle image
            if (event.imageUrl.isNullOrBlank()) {
                eventImage.visibility = View.GONE
            } else {
                eventImage.visibility = View.VISIBLE
                // TODO: Load image using Glide or Coil
            }
        }
    }

    private fun addEventToCalendar(event: Event) {
        try {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, event.title)
                putExtra(CalendarContract.Events.DESCRIPTION, event.description?.let {
                    Html.fromHtml(it, Html.FROM_HTML_MODE_COMPACT).toString()
                })
                putExtra(CalendarContract.Events.EVENT_LOCATION, event.location)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.startDate.time)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.endDate.time)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), R.string.error_adding_to_calendar, Toast.LENGTH_SHORT).show()
        }
    }

    private fun openLocationInMaps(event: Event) {
        try {
            val location = event.location ?: return
            val encodedLocation = Uri.encode(location)
            val uri = Uri.parse("geo:0,0?q=$encodedLocation")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), R.string.error_opening_location, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_EVENT = "arg_event"

        fun newInstance(event: Event): EventDetailFragment {
            return EventDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_EVENT, event)
                }
            }
        }
    }
} 