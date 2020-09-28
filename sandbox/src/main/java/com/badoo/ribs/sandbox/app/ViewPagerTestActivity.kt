package com.badoo.ribs.sandbox.app

import android.os.Bundle
import android.os.Parcelable
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2
import com.badoo.ribs.android.RibActivity
import com.badoo.ribs.android.activitystarter.ActivityStarter
import com.badoo.ribs.android.dialog.DialogLauncher
import com.badoo.ribs.android.permissionrequester.PermissionRequester
import com.badoo.ribs.android.recyclerview.RecyclerViewHost.HostingStrategy
import com.badoo.ribs.android.recyclerview.RecyclerViewHost.HostingStrategy.EAGER
import com.badoo.ribs.android.recyclerview.RecyclerViewHost.Input
import com.badoo.ribs.android.recyclerview.routing.resolution.RecyclerViewItemResolution.Companion.recyclerView
import com.badoo.ribs.android.viewpager.ViewPagerFactory
import com.badoo.ribs.android.viewpager.ViewPagerHost
import com.badoo.ribs.android.viewpager.ViewPagerHostBuilder
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.modality.BuildContext
import com.badoo.ribs.portal.Portal
import com.badoo.ribs.routing.Routing
import com.badoo.ribs.routing.resolution.Resolution
import com.badoo.ribs.routing.resolver.RoutingResolver
import com.badoo.ribs.sandbox.R
import com.badoo.ribs.sandbox.rib.foo_bar.FooBar
import com.badoo.ribs.sandbox.rib.foo_bar.FooBarBuilder
import com.badoo.ribs.sandbox.rib.lorem_ipsum.LoremIpsum
import com.badoo.ribs.sandbox.rib.lorem_ipsum.LoremIpsumBuilder
import com.badoo.ribs.sandbox.rib.switcher.Switcher
import com.badoo.ribs.sandbox.rib.switcher.SwitcherBuilder
import com.badoo.ribs.sandbox.util.CoffeeMachine
import com.badoo.ribs.sandbox.util.StupidCoffeeMachine
import kotlinx.android.parcel.Parcelize

/** The sample app's single activity */
class ViewPagerTestActivity : RibActivity() {

    // We'll put these into the RecyclerView by resolving them to builders (see below)
    sealed class Item : Parcelable {
        @Parcelize object LoremIpsumItem : Item()
        @Parcelize object FooBarItem : Item()
        @Parcelize object Switcher : Item()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_root)
        super.onCreate(savedInstanceState)
    }

    override val rootViewGroup: ViewGroup
        get() = findViewById(R.id.root)

    private val fooBarBuilder = FooBarBuilder(object : FooBar.Dependency {
        override fun permissionRequester(): PermissionRequester = this@ViewPagerTestActivity.permissionRequester
    })

    private val loremIpsumBuilder = LoremIpsumBuilder(object : LoremIpsum.Dependency {})

    private val noopPortal = object : Portal.OtherSide {
        override fun showContent(remoteNode: Node<*>, remoteConfiguration: Parcelable) {
            // Sorry, no-op
        }

        override fun showOverlay(remoteNode: Node<*>, remoteConfiguration: Parcelable) {
            // Sorry, no-op
        }
    }

    private val switcherBuilder = SwitcherBuilder(
        object : Switcher.Dependency {
            override fun activityStarter(): ActivityStarter = activityStarter
            override fun permissionRequester(): PermissionRequester =
                permissionRequester

            override fun dialogLauncher(): DialogLauncher = this@ViewPagerTestActivity
            override fun coffeeMachine(): CoffeeMachine = StupidCoffeeMachine()
            override fun portal(): Portal.OtherSide = noopPortal
        }
    )

    private val ribResolver = object : RoutingResolver<Item> {
        override fun resolve(routing: Routing<Item>): Resolution =
                when (routing.configuration) {
                    Item.LoremIpsumItem -> recyclerView { loremIpsumBuilder.build(it) }
                    Item.FooBarItem -> recyclerView { fooBarBuilder.build(it) }
                    Item.Switcher-> recyclerView { switcherBuilder.build(it) }
                }
    }

    private val initialElements = listOf(
            Item.FooBarItem
    )

    lateinit var viewPagerHost: ViewPagerHost<Item>

    override fun createRib(savedInstanceState: Bundle?): Rib {
        val viewPager = ViewPager2(this)
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        viewPager.isUserInputEnabled = true

        return ViewPagerHostBuilder(
                object : ViewPagerHost.Dependency<Item> {
                    override fun hostingStrategy(): HostingStrategy = EAGER
                    override fun initialElements(): List<Item> = initialElements
                    override fun viewPagerFactory(): ViewPagerFactory = { viewPager }
                    override fun viewHolderLayoutParams(): FrameLayout.LayoutParams =
                            FrameLayout.LayoutParams(
                                    FrameLayout.LayoutParams.MATCH_PARENT,
                                    FrameLayout.LayoutParams.MATCH_PARENT
                            )
                    override fun resolver(): RoutingResolver<Item> = ribResolver
                }
        ).build(BuildContext.root(savedInstanceState)).also {
            viewPagerHost = it
        }
    }

    override fun onResume() {
        super.onResume()
        viewPagerHost.input.accept(Input.Add(Item.LoremIpsumItem))
        viewPagerHost.input.accept(Input.Add(Item.Switcher))
    }
}
