package catt.custom.view

import android.database.Observable
open class BaseMultiTabObserver : Observable<BaseMultiTabObserver>() {

     open fun onChangedMenuContentClose(){

     }

    fun notifyChangedMenuContentClose(){
         synchronized (mObservers)
         {
             for (index in mObservers.indices.reversed()) {
                 mObservers[index].onChangedMenuContentClose()
             }
         }
     }
}