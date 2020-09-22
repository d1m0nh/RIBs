package com.badoo.ribs.android.viewpager

import android.content.Context
import android.os.Parcelable
import androidx.viewpager2.widget.ViewPager2
import com.badoo.ribs.android.recyclerview.RecyclerViewHost

/**
 * Considered experimental. Handle with care.
 */
interface ViewPagerHost<T : Parcelable> : RecyclerViewHost<T> {

    interface Dependency<T : Parcelable> : RecyclerViewHost.BaseDependency<T> {
        fun viewPagerFactory(): ViewPagerFactory
    }
}

typealias ViewPagerFactory = (Context) -> ViewPager2
