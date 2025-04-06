package org.rtsda.android.presentation.events

import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import org.rtsda.android.R
import org.rtsda.android.data.model.Event
import org.rtsda.android.databinding.FragmentEventDetailBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class EventDetailFragment : Fragment() {

    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

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
        setupEventDetails()
    }

    private fun setupEventDetails() {
        val eventId = requireArguments().getString("eventId") ?: return
        val eventTitle = requireArguments().getString("eventTitle") ?: return
        val eventDescription = requireArguments().getString("eventDescription") ?: ""
        val eventStartDate = Date(requireArguments().getLong("eventStartDate"))
        val eventEndDate = Date(requireArguments().getLong("eventEndDate"))
        val eventLocation = requireArguments().getString("eventLocation") ?: ""
        val eventLocationUrl = requireArguments().getString("eventLocationUrl") ?: ""
        val eventImageUrl = requireArguments().getString("eventImageUrl") ?: ""
        val eventCategory = requireArguments().getString("eventCategory") ?: ""
        val eventRecurring = requireArguments().getString("eventRecurring") ?: ""

        val event = Event(
            id = eventId,
            title = eventTitle,
            description = eventDescription,
            startDate = eventStartDate,
            endDate = eventEndDate,
            location = eventLocation,
            locationUrl = eventLocationUrl,
            imageUrl = eventImageUrl,
            thumbnailUrl = "",
            category = eventCategory,
            reoccuring = eventRecurring,
            isFeatured = false
        )

        val imageUrl = if (event.imageUrl.isNotEmpty()) {
            "https://pocketbase.rockvilletollandsda.church/api/files/events/${event.id}/${event.imageUrl}"
        } else {
            ""
        }

        Glide.with(requireView())
            .load(imageUrl)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(binding.eventImage)

        binding.eventTitle.text = event.title
        binding.categoryChip.text = event.category

        val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        binding.eventDateTime.text = getString(
            R.string.event_date_time_format,
            dateFormat.format(event.startDate),
            timeFormat.format(event.startDate)
        )

        // Make location clickable if there's a URL
        if (event.locationUrl.isNotEmpty()) {
            binding.eventLocation.setOnClickListener {
                try {
                    val url = if (event.locationUrl.startsWith("http")) {
                        event.locationUrl
                    } else {
                        "https://${event.locationUrl}"
                    }
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        R.string.error_opening_location,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            binding.eventLocation.paintFlags = binding.eventLocation.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
            binding.eventLocation.setTextColor(requireContext().getColor(android.R.color.holo_blue_dark))
            binding.eventLocation.text = event.location
        } else {
            binding.eventLocation.text = event.location
        }
        
        // Clean and format the description
        val cleanDescription = event.description
            .replace(Regex("<br\\s*/?>"), "\n") // Replace <br> tags with newlines
            .replace(Regex("<[^>]+>"), "") // Remove all other HTML tags
            .trim() // Remove leading/trailing whitespace
        
        // Decode HTML entities
        binding.eventDescription.text = HtmlCompat.fromHtml(cleanDescription, HtmlCompat.FROM_HTML_MODE_LEGACY)

        binding.addToCalendarButton.setOnClickListener {
            addEventToCalendar(event)
        }
    }

    private fun addEventToCalendar(event: Event) {
        val intent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.Events.TITLE, event.title)
            .putExtra(CalendarContract.Events.DESCRIPTION, event.description)
            .putExtra(CalendarContract.Events.EVENT_LOCATION, event.location)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.startDate.time)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.endDate.time)
            .putExtra(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)

        // Set recurrence rule based on the event's reoccuring property
        when (event.reoccuring?.lowercase()) {
            "daily" -> {
                intent.putExtra(
                    CalendarContract.Events.RRULE,
                    "FREQ=DAILY;INTERVAL=1"
                )
            }
            "weekly" -> {
                intent.putExtra(
                    CalendarContract.Events.RRULE,
                    "FREQ=WEEKLY;INTERVAL=1"
                )
            }
            "biweekly" -> {
                intent.putExtra(
                    CalendarContract.Events.RRULE,
                    "FREQ=WEEKLY;INTERVAL=2"
                )
            }
            "monthly" -> {
                intent.putExtra(
                    CalendarContract.Events.RRULE,
                    "FREQ=MONTHLY;INTERVAL=1"
                )
            }
            "yearly" -> {
                intent.putExtra(
                    CalendarContract.Events.RRULE,
                    "FREQ=YEARLY;INTERVAL=1"
                )
            }
            "first_tuesday" -> {
                intent.putExtra(
                    CalendarContract.Events.RRULE,
                    "FREQ=MONTHLY;BYDAY=1TU"
                )
            }
        }

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                R.string.error_adding_to_calendar,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 