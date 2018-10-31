package catt.custom.view

import android.view.View
import android.view.ViewGroup

interface IMultiTabDropAdapter {

    fun getCount(): Int

    fun getItemForTab(position: Int, parent: ViewGroup): View

    fun getItemForContent(position: Int, parent: ViewGroup): View

    fun onMenuContentOpening(view:View, position: Int)

    fun onMenuContentClosed(view:View, position: Int)

    fun registerDataSetObserver(observer: BaseMultiTabObserver)

    fun unregisterDataSetObserver(observer: BaseMultiTabObserver)
}