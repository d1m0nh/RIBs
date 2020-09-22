package com.badoo.ribs.android.viewpager

import android.os.Bundle
import android.os.Parcelable
import com.badoo.mvicore.android.AndroidTimeCapsule
import com.badoo.ribs.android.recyclerview.*
import com.badoo.ribs.android.recyclerview.Adapter
import com.badoo.ribs.clienthelper.connector.Connectable
import com.badoo.ribs.clienthelper.connector.NodeConnector
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.modality.BuildParams
import com.badoo.ribs.core.plugin.Plugin
import com.badoo.ribs.core.view.RibView

class ViewPagerHostNode<T : Parcelable> internal constructor(
        buildParams: BuildParams<Nothing?>,
        plugins: List<Plugin>,
        private val viewDeps: ViewPagerHostView.Dependency,
        private val timeCapsule: AndroidTimeCapsule,
        private val adapter: Adapter<T>,
        private val connector: NodeConnector<RecyclerViewHost.Input<T>, Nothing> = NodeConnector()
) : Node<RibView>(
        viewFactory = { ViewPagerHostViewImpl.Factory().invoke(viewDeps).invoke(it) },
        plugins = plugins,
        buildParams = buildParams
), ViewPagerHost<T>, Connectable<RecyclerViewHost.Input<T>, Nothing> by connector {
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        timeCapsule.saveState(outState)
    }

    override fun onDestroy() {
        adapter.onDestroy()
        super.onDestroy()
    }
}
