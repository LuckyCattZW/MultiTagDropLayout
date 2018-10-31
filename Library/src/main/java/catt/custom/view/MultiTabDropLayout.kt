package catt.custom.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log.e
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat

class MultiTabDropLayout
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayoutCompat(context, attrs, defStyleAttr) {

    private val _TAG: String  by lazy { MultiTabDropLayout::class.java.simpleName }

    private val _metrics : DisplayMetrics by lazy {
        DisplayMetrics().run {
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(this@run)
            this@run
        }
    }

    private val _contentMenuBlockLayer:View by lazy {
        View(context).apply {
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
            elevation = convertPx(TypedValue.COMPLEX_UNIT_DIP, Int.MAX_VALUE)
            translationZ = convertPx(TypedValue.COMPLEX_UNIT_DIP, Int.MAX_VALUE)
            isClickable = true
        }
    }

    private val _tabContainer: LinearLayoutCompat by lazy {
        1
        LinearLayoutCompat(context).apply {
            orientation = LinearLayoutCompat.HORIZONTAL
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_light))
        }
    }

    private val _headerScrollView: HorizontalScrollView by lazy {
        HorizontalScrollView(context).apply {
            isHorizontalScrollBarEnabled = false
            elevation = convertPx(TypedValue.COMPLEX_UNIT_DIP, 8)
            translationZ = convertPx(TypedValue.COMPLEX_UNIT_DIP, 8)
            this@apply.addView(_tabContainer)
        }
    }

    private val _menuContentContainer: FrameLayout by lazy {
        FrameLayout(context).apply {
            descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
            elevation = convertPx(TypedValue.COMPLEX_UNIT_DIP, 4)
            translationZ = convertPx(TypedValue.COMPLEX_UNIT_DIP, 4)
        }
    }

    private val _shadowBg: View by lazy {
        View(context).apply {
            setBackgroundColor(Color.parseColor("#90000000"))
            elevation = convertPx(TypedValue.COMPLEX_UNIT_DIP, 0)
            translationZ = convertPx(TypedValue.COMPLEX_UNIT_DIP, 0)
        }
    }

    var adapter: IMultiTabDropAdapter? = null
        set(value) {
            value ?: throw NullPointerException("Adapter cannot be null!!!")
            field = value
            mountTabViews(value)
        }

    private var beforePosition: Int = -1
    private var currentPosition: Int = -1

    private val displayTabCount: Int = 3

    private var menuContentTranslationY: Float = 0F
        get() = field * 0.75F

    private val whetherHiddenMenuContentContainer: Boolean
        get() = _menuContentContainer.translationY == -menuContentTranslationY

    private val whetherShowMenuContentContainer: Boolean
        get() = _menuContentContainer.translationY == 0F

    private val openAnimatorListener: AnimatorListenerAdapter = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            disableBlockClickLayer(_menuContentContainer)
        }

        override fun onAnimationStart(animation: Animator) {
            setShadowBgVisible(View.VISIBLE)
            enableBlockClickLayer(_menuContentContainer)
        }
    }


    private val closeAnimatorListener: AnimatorListenerAdapter = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            setShadowBgVisible(View.GONE)
            _menuContentContainer.removeAllViews()
            disableBlockClickLayer(_menuContentContainer)
            currentPosition = -1
            beforePosition = -1
        }

        override fun onAnimationStart(animation: Animator) {
            adapter?.onMenuContentClosed(_tabContainer.getChildAt(currentPosition), currentPosition)
            enableBlockClickLayer(_menuContentContainer)
        }
    }


    private val openAnimatorUpdateListener = ValueAnimator.AnimatorUpdateListener {
        _shadowBg.alpha = Math.abs(it.animatedFraction)
        _shadowBg.translationY = it.animatedValue as Float
    }

    private val closeAnimatorUpdateListener = ValueAnimator.AnimatorUpdateListener {
        _shadowBg.alpha = 1 - Math.abs(it.animatedFraction)
        _shadowBg.translationY = it.animatedValue as Float
    }

    init {
        descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        elevation = convertPx(TypedValue.COMPLEX_UNIT_DIP, Int.MAX_VALUE)
        translationZ = convertPx(TypedValue.COMPLEX_UNIT_DIP, Int.MAX_VALUE)
        orientation = LinearLayoutCompat.VERTICAL
        addView(_headerScrollView, 0, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        addView(_menuContentContainer, 1, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        addView(_shadowBg.apply { alpha = 0F
                                  visibility = View.GONE
            setOnClickListener {
                if (!whetherShowMenuContentContainer || currentPosition== -1) {
                    return@setOnClickListener
                }
                closeMenuContentAnimator(_tabContainer.getChildAt(currentPosition), currentPosition)
            }
        }, -1, LayoutParams(LayoutParams.MATCH_PARENT, _metrics.heightPixels))
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (menuContentTranslationY == 0F) {
            _menuContentContainer.apply {
                menuContentTranslationY = MeasureSpec.getSize(heightMeasureSpec).toFloat()
                layoutParams.height = menuContentTranslationY.toInt()
                translationY = -menuContentTranslationY
            }
            _shadowBg.translationY = -menuContentTranslationY
        }
    }

    private fun mountTabViews(adapter: IMultiTabDropAdapter) {
        for (position in 0 until adapter.getCount()) {
            post {
                _tabContainer.addView(adapter.getItemForTab(position, _tabContainer).apply {
                    layoutParams.width = this@MultiTabDropLayout.measuredWidth / displayTabCount
                    setOnItemClick(this, position)
                }, position)
            }
        }
    }

    private fun setOnItemClick(view: View, position: Int) {
        view.setOnClickListener {
            if (!existContentMenuBlockLayer()) {
                when (currentPosition) {
                    position -> {
                        closeMenuContentAnimator(view, position)
                    }
                    else -> {
                        currentPosition = position
                        if (whetherHiddenMenuContentContainer) {
                            openMenuContentAnimator()
                        }
                        adapter?.apply {
                            when{
                                !existContentMenuBlockLayer() && _menuContentContainer.childCount > 0 ->
                                    _menuContentContainer.removeViews(0, _menuContentContainer.childCount)
                                existContentMenuBlockLayer() && _menuContentContainer.childCount > 1 ->
                                    _menuContentContainer.removeViews(1, _menuContentContainer.childCount)
                            }
                            _menuContentContainer.addView(getItemForContent(position, _menuContentContainer))
                            if(beforePosition != -1) {
                                onMenuContentClosed(_tabContainer.getChildAt(beforePosition), beforePosition)
                            }
                            onMenuContentOpening(view, position)
                        }
                        beforePosition = position
                    }
                }
            }
        }
    }

    private fun openMenuContentAnimator() {
        (ObjectAnimator.ofFloat(_menuContentContainer, "translationY", -menuContentTranslationY, 0F) as ValueAnimator).apply {
            duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        }.run {
            addUpdateListener(openAnimatorUpdateListener)
            addListener(openAnimatorListener)
            this@run
        }.start()

    }

    private fun closeMenuContentAnimator(view:View, position: Int) {
        (ObjectAnimator.ofFloat(_menuContentContainer, "translationY", 0F, -menuContentTranslationY) as ValueAnimator).apply {
            duration = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
        }.run {
            addUpdateListener(closeAnimatorUpdateListener)
            addListener(closeAnimatorListener)
            this@run
        }.start()

        adapter?.onMenuContentClosed(view, position)
    }

    private fun existContentMenuBlockLayer():Boolean = _menuContentContainer.indexOfChild(_contentMenuBlockLayer) != -1

    private fun getContentMenuBlockLayerIndex():Int = _menuContentContainer.indexOfChild(_contentMenuBlockLayer)

    private fun disableBlockClickLayer(parent: ViewGroup) {
        parent.apply {
            if (existContentMenuBlockLayer()) {
                parent.removeViewAt(getContentMenuBlockLayerIndex())
            }
        }
    }

    private fun enableBlockClickLayer(parent: ViewGroup) {
        parent.apply {
            if (!existContentMenuBlockLayer()) {
                addView(_contentMenuBlockLayer, 0, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
            }
        }
    }

    private fun setShadowBgVisible(visible : Int) {
        _shadowBg.apply {
            if (visibility != visible) {
                visibility = visible
            }
        }
    }

    private fun convertPx(unit: Int, value: Int) =
        TypedValue.applyDimension(unit, value.toFloat(), resources.displayMetrics)
}