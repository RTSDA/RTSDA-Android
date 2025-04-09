package org.rtsda.android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import org.rtsda.android.databinding.ActivityMainBinding
import org.rtsda.android.presentation.beliefs.BeliefsActivity
import org.rtsda.android.ui.home.HomeFragment
import org.rtsda.android.ui.bulletins.BulletinsFragment
import org.rtsda.android.presentation.events.EventsFragment
import org.rtsda.android.presentation.messages.MessagesFragment
import org.rtsda.android.ui.more.MoreFragment
import org.rtsda.android.BulletinDetailActivity

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var pagerAdapter: MainPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewPager = binding.viewPager
        bottomNavigation = binding.bottomNavigation

        setupViewPager()
        setupBottomNavigation()
    }

    private fun setupViewPager() {
        pagerAdapter = MainPagerAdapter(this)
        viewPager.adapter = pagerAdapter
        
        // Set offscreen page limit to keep fragments in memory
        viewPager.offscreenPageLimit = 3
        
        // Disable swipe gesture
        viewPager.isUserInputEnabled = false
        
        // Add page change callback to handle fragment visibility
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Update bottom navigation to match current page
                bottomNavigation.selectedItemId = when (position) {
                    0 -> R.id.navigation_home
                    1 -> R.id.navigation_bulletins
                    2 -> R.id.navigation_events
                    3 -> R.id.navigation_messages
                    4 -> R.id.navigation_more
                    else -> R.id.navigation_home
                }
            }
        })

        // Set initial position
        viewPager.setCurrentItem(0, false)
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> viewPager.setCurrentItem(0, false)
                R.id.navigation_bulletins -> viewPager.setCurrentItem(1, false)
                R.id.navigation_events -> viewPager.setCurrentItem(2, false)
                R.id.navigation_messages -> viewPager.setCurrentItem(3, false)
                R.id.navigation_more -> viewPager.setCurrentItem(4, false)
            }
            true
        }

        // Set initial selection
        bottomNavigation.selectedItemId = R.id.navigation_home
    }

    private inner class MainPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = 5

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> HomeFragment()
                1 -> BulletinsFragment()
                2 -> EventsFragment()
                3 -> MessagesFragment()
                4 -> MoreFragment()
                else -> throw IllegalStateException("Invalid position: $position")
            }
        }
    }

    fun navigateToBulletinDetail(bulletinId: String) {
        val intent = BulletinDetailActivity.newIntent(this, bulletinId)
        startActivity(intent)
    }

    fun navigateToBeliefs() {
        val intent = BeliefsActivity.newIntent(this)
        startActivity(intent)
    }
} 