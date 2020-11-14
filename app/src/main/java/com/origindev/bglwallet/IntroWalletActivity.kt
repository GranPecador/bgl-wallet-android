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
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2


class IntroWalletActivity : AppCompatActivity() {

    lateinit var introViewPager: ViewPager2
    private lateinit var dotsLayout: LinearLayout
    private lateinit var dots: Array<TextView?>
    private lateinit var layouts: IntArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_wallet)
        layouts = intArrayOf(
            R.layout.first_intro_layout,
            R.layout.two_intro_layout,
            R.layout.three_intro_layout,
            R.layout.four_intro_wallet
        )
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
                }
            })
        }

        dotsLayout = findViewById(R.id.layoutDots)
        val letsStartButton = findViewById<Button>(R.id.let_start_wallet_button)
        letsStartButton.setOnClickListener {
            launchHomeScreen()
        }
        val leverageIntroPanelLayout = findViewById<LinearLayout>(R.id.leverage_intro_panel_layout)
        findViewById<Button>(R.id.skip_intro_button).setOnClickListener {
            launchHomeScreen()
        }
        findViewById<Button>(R.id.next_intro_page_button).setOnClickListener {
            val current: Int = getItem(+1)
            if (current < layouts.size) {
                // move to next screen
                introViewPager.currentItem = current
                if (current == layouts.size-1) {
                    leverageIntroPanelLayout.visibility = View.GONE
                    letsStartButton.visibility = View.VISIBLE
                }
            }
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
        return introViewPager.getCurrentItem() + i
    }

    private fun launchHomeScreen() {
        //prefManager.setFirstTimeLaunch(false)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}