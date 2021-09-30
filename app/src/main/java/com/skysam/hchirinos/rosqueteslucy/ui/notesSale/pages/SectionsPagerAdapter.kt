package com.skysam.hchirinos.rosqueteslucy.ui.notesSale.pages

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Created by Hector Chirinos (Home) on 29/9/2021.
 */
class SectionsPagerAdapter(fm: FragmentActivity): FragmentStateAdapter(fm) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return NotesSaleFragment.newInstance(position)
    }
}