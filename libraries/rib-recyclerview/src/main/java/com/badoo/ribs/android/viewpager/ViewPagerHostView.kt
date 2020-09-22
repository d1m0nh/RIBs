package com.badoo.ribs.android.viewpager

import androidx.viewpager2.widget.ViewPager2
import com.badoo.ribs.android.recyclerview.Adapter
import com.badoo.ribs.android.viewpager.ViewPagerHostView.Dependency
import com.badoo.ribs.core.view.AndroidRibView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.core.view.ViewFactory


internal interface ViewPagerHostView : RibView {

    interface Factory : ViewFactory<Dependency, ViewPagerHostView>

    interface Dependency {
        fun adapter(): Adapter<*>
        fun viewPagerFactory(): ViewPagerFactory
    }
}

internal class ViewPagerHostViewImpl private constructor(
    override val androidView: ViewPager2
) : AndroidRibView(),
    ViewPagerHostView {

    class Factory: ViewPagerHostView.Factory {
        override fun invoke(deps: Dependency): (RibView) -> ViewPagerHostView = {
            ViewPagerHostViewImpl(
                androidView = deps.viewPagerFactory().invoke(it.context).apply {
                    adapter = deps.adapter()
                }
            )
        }
    }
}
