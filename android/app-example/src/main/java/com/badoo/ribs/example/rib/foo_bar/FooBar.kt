package com.badoo.ribs.example.rib.foo_bar

import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.directory.Directory
import com.badoo.ribs.core.directory.inflateOnDemand
import com.badoo.ribs.example.R
import com.badoo.ribs.core.view.ViewFactory
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface FooBar : Rib {

    interface Dependency {
        fun foobarInput(): ObservableSource<Input>
        fun foobarOutput(): Consumer<Output>
        fun ribCustomisation(): Directory
    }

    sealed class Input

    sealed class Output

    class Customisation(
        val viewFactory: ViewFactory<FooBarView> = inflateOnDemand(
            R.layout.rib_foobar
        )
    )
}