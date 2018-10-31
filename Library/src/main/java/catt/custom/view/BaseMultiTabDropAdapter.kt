package catt.custom.view

abstract class BaseMultiTabDropAdapter : IMultiTabDropAdapter {
    private val _multiTabObserver by lazy { BaseMultiTabObserver() }

    override fun registerDataSetObserver(observer: BaseMultiTabObserver) {
        _multiTabObserver.registerObserver(observer)
    }

    override fun unregisterDataSetObserver(observer: BaseMultiTabObserver) {
        _multiTabObserver.unregisterObserver(observer)
    }

    fun notifyChangedMenuContentClose(){
        _multiTabObserver.notifyChangedMenuContentClose()
    }
}

