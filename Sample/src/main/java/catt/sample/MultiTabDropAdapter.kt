package catt.sample

import android.content.Context
import android.util.Log.e
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import catt.custom.view.BaseMultiTabDropAdapter
import es.dmoral.toasty.Toasty

class MultiTabDropAdapter constructor(private val _context: Context) : BaseMultiTabDropAdapter() {
    private val _TAG:String by lazy { MultiTabDropAdapter::class.java.simpleName }
    private var tabList: List<String> = mutableListOf(
        "要闻", "视频", "推荐", "北京", "军事", "股票", "动漫", "科技", "法制", "国际", "汽车", "电影", "游戏", "房产"
    )

    override fun getCount(): Int = tabList.size

    override fun getItemForTab(position: Int, parent: ViewGroup): View {
        return (LayoutInflater.from(_context).inflate(R.layout.tab, parent, false) as TextView).apply {
            text = tabList[position]
            textSize = 36F
        }
    }

    override fun getItemForContent(position: Int, parent: ViewGroup): View {
        return (LayoutInflater.from(_context).inflate(R.layout.content, parent, false) as TextView).apply {
            text = "position = $position, Content = ${tabList[position]}"
            textSize = 36F
            setOnClickListener {
                Toasty.info(context, "onClick: position = $position", Toast.LENGTH_SHORT, true).show()
                e(_TAG, "onClick: position = $position, Content = ${tabList[position]}")

                this@MultiTabDropAdapter.notifyChangedMenuContentClose()
            }
        }
    }

    override fun onMenuContentOpening(view: View, position: Int) {
        e(_TAG, "onMenuContentOpening: position = $position")
        view.setBackgroundColor(ContextCompat.getColor(_context, android.R.color.holo_red_light))
    }

    override fun onMenuContentClosed(view: View, position: Int) {
        e(_TAG, "onMenuContentClosed: position = $position")
        view.setBackgroundColor(ContextCompat.getColor(_context, android.R.color.holo_green_light))
    }
}