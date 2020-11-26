package com.origindev.bglwallet

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.origindev.bglwallet.repositories.FlagsPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class IntroWalletActivity : AppCompatActivity() {

    lateinit var introViewPager: ViewPager2
    private lateinit var dotsLayout: LinearLayout
    private lateinit var dots: Array<TextView?>
    private lateinit var layouts: IntArray
    private lateinit var viewModel: FlagsViewModel
    private lateinit var leverageIntroPanelLayout: LinearLayout
    private lateinit var letsStartButton: Button
    private lateinit var skipTextButton: Button
    private lateinit var nextTextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            FlagsViewModelFactory(FlagsPreferencesRepository.getInstance(this))
        ).get(FlagsViewModel::class.java)

        lifecycleScope.launch {
            val flags = viewModel.flagsPreferencesFlow.first()
            if (flags.logged) {
                val intent = Intent(applicationContext, WalletActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else
                if (flags.isFirstTimeLaunched) {
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
        }

        setContentView(R.layout.activity_intro_wallet)
        layouts = intArrayOf(
            R.layout.first_intro_layout,
            R.layout.two_intro_layout,
            R.layout.three_intro_layout,
            R.layout.four_intro_wallet
        )
        letsStartButton = findViewById<Button>(R.id.let_start_wallet_button)
        letsStartButton.setOnClickListener {
            launchHomeScreen()
        }
        skipTextButton = findViewById(R.id.skip_intro_textbutton)
        skipTextButton.setOnClickListener {
            launchHomeScreen()
        }
        nextTextButton = findViewById(R.id.next_intro_page_textbutton)
        nextTextButton.setOnClickListener {
            nextPage()
        }
        leverageIntroPanelLayout = findViewById<LinearLayout>(R.id.leverage_intro_panel_layout)
        introViewPager = findViewById<ViewPager2>(R.id.intro_view_pager).apply {
            offscreenPageLimit = 1
            adapter = object : FragmentStateAdapter(this@IntroWalletActivity) {
                override fun createFragment(position: Int): Fragment {
                    return Fragment(layouts[position])
                }

                override fun getItemCount(): Int {
                    return layouts.size
                }
            }
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    addBottomDots(position)
                    updateBottomButtons(position)
                }
            })
        }

        dotsLayout = findViewById(R.id.layoutDots)
        findViewById<Button>(R.id.skip_intro_button).setOnClickListener {
            launchHomeScreen()
        }
        findViewById<Button>(R.id.next_intro_page_button).setOnClickListener {
            nextPage()
        }
        addBottomDots(0)
    }

    fun nextPage() {
        val current: Int = getItem(+1)
        if (current < layouts.size) {
            // move to next screen
            introViewPager.currentItem = current
            updateBottomButtons(current)
        }
    }

    fun updateBottomButtons(position: Int) {
        if (position == layouts.size - 1) {
            leverageIntroPanelLayout.visibility = View.GONE
            skipTextButton.visibility = View.GONE
            nextTextButton.visibility = View.GONE
            letsStartButton.visibility = View.VISIBLE
        } else {
            leverageIntroPanelLayout.visibility = View.VISIBLE
            skipTextButton.visibility = View.VISIBLE
            nextTextButton.visibility = View.VISIBLE
            letsStartButton.visibility = View.GONE
        }
    }

    private fun addBottomDots(currentPage: Int) {
        dots = arrayOfNulls(size = layouts.size)
        val colorActive = resources.getColor(R.color.dot_active, theme)
        val colorInactive = resources.getColor(R.color.dot_inactive, theme)
        dotsLayout.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]?.text = (Html.fromHtml("&#8226;"))
            dots[i]?.textSize = 35f
            dots[i]?.setTextColor(colorInactive)
            dotsLayout.addView(dots.get(i))
        }
        if (dots.isNotEmpty()) dots.get(currentPage)?.setTextColor(colorActive)
    }

    private fun getItem(i: Int): Int {
        return introViewPager.currentItem + i
    }

    private fun launchHomeScreen() {
        viewModel.setFirstTimeLaunched()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}