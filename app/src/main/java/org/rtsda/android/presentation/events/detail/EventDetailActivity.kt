package org.rtsda.android.presentation.events.detail

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import dagger.hilt.android.AndroidEntryPoint
import org.rtsda.android.R
import org.rtsda.android.databinding.ActivityEventDetailBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class EventDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventDetailBinding
    private val dateFormat = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get event details from intent
        val eventId = intent.getStringExtra("eventId")
        val eventTitle = intent.getStringExtra("eventTitle")
        val eventDescription = intent.getStringExtra("eventDescription")
        val eventStartDate = intent.getLongExtra("eventStartDate", 0)
        val eventEndDate = intent.getLongExtra("eventEndDate", 0)
        val eventLocation = intent.getStringExtra("eventLocation")
        val eventLocationUrl = intent.getStringExtra("eventLocationUrl")
        val eventImageUrl = intent.getStringExtra("eventImageUrl")
        val eventCategory = intent.getStringExtra("eventCategory")
        val eventRecurring = intent.getStringExtra("eventRecurring")

        // Set up the UI with event details
        setupUI(
            eventId = eventId ?: "",
            title = eventTitle,
            description = eventDescription,
            startDate = eventStartDate,
            endDate = eventEndDate,
            location = eventLocation,
            locationUrl = eventLocationUrl,
            imageUrl = eventImageUrl,
            category = eventCategory,
            recurring = eventRecurring
        )

        // Set up calendar button click
        binding.addToCalendarButton.setOnClickListener {
            addToCalendar(
                title = eventTitle ?: "",
                description = eventDescription ?: "",
                location = eventLocation ?: "",
                startTime = eventStartDate,
                endTime = eventEndDate,
                recurring = eventRecurring
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupUI(
        eventId: String,
        title: String?,
        description: String?,
        startDate: Long,
        endDate: Long,
        location: String?,
        locationUrl: String?,
        imageUrl: String?,
        category: String?,
        recurring: String?
    ) {
        // Set title in toolbar
        supportActionBar?.title = cleanHtml(title)

        // Set category
        binding.categoryChip.text = cleanHtml(category)

        // Set date and time
        val startDateObj = Date(startDate)
        val endDateObj = Date(endDate)
        val dateText = "${dateFormat.format(startDateObj)}"
        val timeText = "${timeFormat.format(startDateObj)} - ${timeFormat.format(endDateObj)}"
        binding.eventDateTime.text = "$dateText\n$timeText"

        // Set location with optional URL
        binding.eventLocation.apply {
            text = cleanHtml(location)
            if (!locationUrl.isNullOrBlank()) {
                setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(locationUrl))
                    startActivity(intent)
                }
                paintFlags = paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
                setTextColor(getColor(android.R.color.holo_blue_dark))
                setCompoundDrawableTintList(getColorStateList(android.R.color.holo_blue_dark))
            } else {
                isClickable = false
                setTextColor(getColor(android.R.color.darker_gray))
                setCompoundDrawableTintList(getColorStateList(android.R.color.darker_gray))
            }
        }

        // Set description
        binding.eventDescription.text = cleanHtml(description)

        // Load image if available
        binding.progressBar.visibility = if (imageUrl != null) View.VISIBLE else View.GONE
        binding.eventImage.visibility = if (imageUrl != null) View.VISIBLE else View.GONE

        val fullImageUrl = if (!imageUrl.isNullOrEmpty()) {
            "https://pocketbase.rockvilletollandsda.church/api/files/events/${eventId}/${imageUrl}"
        } else {
            ""
        }

        if (fullImageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(fullImageUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.progressBar.visibility = View.GONE
                        return false
                    }
                })
                .into(binding.eventImage)
        }
    }

    private fun cleanHtml(text: String?): String {
        if (text == null) return ""
        return HtmlCompat.fromHtml(
            text.replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace("<p style=\"text-align: center;\">", "")
                .replace("</p>", ""),
            HtmlCompat.FROM_HTML_MODE_COMPACT
        ).toString().trim()
    }

    private fun addToCalendar(
        title: String,
        description: String,
        location: String,
        startTime: Long,
        endTime: Long,
        recurring: String?
    ) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, cleanHtml(title))
            putExtra(CalendarContract.Events.DESCRIPTION, cleanHtml(description))
            putExtra(CalendarContract.Events.EVENT_LOCATION, cleanHtml(location))
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)
            putExtra(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)

            // Set recurrence rule based on the event's reoccuring property
            when (recurring?.lowercase()) {
                "daily" -> {
                    putExtra(
                        CalendarContract.Events.RRULE,
                        "FREQ=DAILY;INTERVAL=1"
                    )
                }
                "weekly" -> {
                    putExtra(
                        CalendarContract.Events.RRULE,
                        "FREQ=WEEKLY;INTERVAL=1"
                    )
                }
                "biweekly" -> {
                    putExtra(
                        CalendarContract.Events.RRULE,
                        "FREQ=WEEKLY;INTERVAL=2"
                    )
                }
                "monthly" -> {
                    putExtra(
                        CalendarContract.Events.RRULE,
                        "FREQ=MONTHLY;INTERVAL=1"
                    )
                }
                "yearly" -> {
                    putExtra(
                        CalendarContract.Events.RRULE,
                        "FREQ=YEARLY;INTERVAL=1"
                    )
                }
                "first_tuesday" -> {
                    putExtra(
                        CalendarContract.Events.RRULE,
                        "FREQ=MONTHLY;BYDAY=1TU"
                    )
                }
            }
        }
        startActivity(intent)
    }

    companion object {
        fun newIntent(
            context: Context,
            eventId: String,
            eventTitle: String,
            eventDescription: String,
            eventStartDate: Long,
            eventEndDate: Long,
            eventLocation: String,
            eventLocationUrl: String,
            eventImageUrl: String,
            eventCategory: String,
            eventRecurring: String
        ): Intent {
            return Intent(context, EventDetailActivity::class.java).apply {
                putExtra("eventId", eventId)
                putExtra("eventTitle", eventTitle)
                putExtra("eventDescription", eventDescription)
                putExtra("eventStartDate", eventStartDate)
                putExtra("eventEndDate", eventEndDate)
                putExtra("eventLocation", eventLocation)
                putExtra("eventLocationUrl", eventLocationUrl)
                putExtra("eventImageUrl", eventImageUrl)
                putExtra("eventCategory", eventCategory)
                putExtra("eventRecurring", eventRecurring)
            }
        }
    }
} 