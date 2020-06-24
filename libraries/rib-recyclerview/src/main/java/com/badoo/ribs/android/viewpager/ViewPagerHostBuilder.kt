package com.badoo.ribs.android.viewpager

import android.os.Parcelable
import com.badoo.mvicore.android.AndroidTimeCapsule
import com.badoo.ribs.android.recyclerview.Adapter
import com.badoo.ribs.android.recyclerview.RecyclerViewHostFeature
import com.badoo.ribs.android.recyclerview.RecyclerViewHostInteractor
import com.badoo.ribs.builder.SimpleBuilder
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.routing.router.RouterByDelegate
import com.badoo.ribs.routing.source.impl.Pool

class ViewPagerHostBuilder<T : Parcelable>(
    private val dependency: ViewPagerHost.Dependency<T>
) : SimpleBuilder<ViewPagerHost<T>>() {

    @SuppressWarnings("LongMethod")
    override fun build(buildParams: BuildParams<Nothing?>): ViewPagerHost<T> {
        val timeCapsule = AndroidTimeCapsule(buildParams.savedInstanceState)

        val routingSource = Pool<T>(
                allowRepeatingConfigurations = true
        )

        val feature = RecyclerViewHostFeature(
            timeCapsule = timeCapsule,
            initialElements = dependency.initialElements()
        )

        val adapter = Adapter(
            hostingStrategy = dependency.hostingStrategy(),
            initialEntries = feature.state.items,
            routingSource = routingSource,
            feature = feature,
            viewHolderLayoutParams = dependency.viewHolderLayoutParams()
        )

        val router = RouterByDelegate(
            buildParams = buildParams,
            routingSource = routingSource,
            resolver = dependency.resolver(),
            clientChildActivator = adapter
        )

        val interactor = RecyclerViewHostInteractor(
                buildParams = buildParams,
                feature = feature,
                adapter = adapter
        )

        val viewDeps = object : ViewPagerHostView.Dependency {
            override fun adapter(): Adapter<*> = adapter
            override fun viewPagerFactory(): ViewPagerFactory = dependency.viewPagerFactory()
        }

        return ViewPagerHostNode(
            buildParams = buildParams,
            viewDeps = viewDeps,
            plugins = listOf(
                router,
                interactor
            ),
            timeCapsule = timeCapsule,
            adapter = adapter
        )
    }
}
