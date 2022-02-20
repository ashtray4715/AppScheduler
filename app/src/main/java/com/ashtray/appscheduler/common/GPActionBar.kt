package com.ashtray.appscheduler.common

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

import com.ashtray.appscheduler.R

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat

class GPActionBar(
    context: Context?,
    attrs: AttributeSet?
) : RelativeLayout(
    context,
    attrs
) {

    companion object {
        private const val TAG = "GPActionBar"
    }

    private var backListener: OnClickListener? = null
    private var menuListener1:OnClickListener? = null
    private var menuListener2:OnClickListener? = null
    private var textOptionListener:OnClickListener? = null

    private var ivBack: ImageView? = null
    private var ivMenu1:ImageView? = null
    private var ivMenu2:ImageView? = null
    private var tvTitle: TextView? = null
    private var tvTextOptionMenu:TextView? = null
    private var actionBarDivider: View? = null

    init {
        setViewAndInitializeComponents(context)
        addHandlerAndListenersWhereNecessary()
        drawComponentsForTheFirstTime(context, attrs)
    }

    fun setBackListener(backListener: OnClickListener?) {
        this.backListener = backListener
    }

    fun setMenuListener1(menuListener1: OnClickListener?) {
        this.menuListener1 = menuListener1
    }

    fun setMenuListener2(menuListener2: OnClickListener?) {
        this.menuListener2 = menuListener2
    }

    fun setTextOptionListener(textOptionListener: OnClickListener?) {
        this.textOptionListener = textOptionListener
    }

    private fun setActionBarTitle(title: String?) {
        tvTitle?.text = title
    }

    private fun addHandlerAndListenersWhereNecessary() {
        ivBack?.setOnClickListener { v: View? ->
            backListener?.onClick(v)
        }
        ivMenu1?.setOnClickListener { v: View? ->
            menuListener1?.onClick(v)
        }
        ivMenu2?.setOnClickListener { v: View? ->
            menuListener2?.onClick(v)
        }
        tvTextOptionMenu?.setOnClickListener { v: View? ->
            textOptionListener?.onClick(v)
        }
    }

    private fun setViewAndInitializeComponents(context: Context?) {
        val view = inflate(context, R.layout.gp_custom_action_bar, this)
        ivBack = view.findViewById(R.id.iv_back_button)
        ivMenu1 = view.findViewById(R.id.iv_menu_item1)
        ivMenu2 = view.findViewById(R.id.iv_menu_item2)
        tvTitle = view.findViewById(R.id.tv_title)
        tvTextOptionMenu = view.findViewById(R.id.tv_text_option_menu)
        actionBarDivider = view.findViewById(R.id.action_bar_divider)
        GPLog.d(TAG, "back is initialized [${ivBack == null}]")
    }

    @SuppressLint("NonConstantResourceId")
    private fun drawComponentsForTheFirstTime(context: Context?, attrs: AttributeSet?) {
        val typedArray = context?.obtainStyledAttributes(attrs, R.styleable.GPActionBar)
        try {
            val paramCount = typedArray?.indexCount ?: 0
            for (i in 0 until paramCount) {
                when (val currentAttribute = typedArray?.getIndex(i)) {
                    R.styleable.GPActionBar_gp_actionbar_title -> {
                        setActionBarTitle(typedArray.getString(currentAttribute))
                    }
                    R.styleable.GPActionBar_gp_actionbar_back_btn_visibility -> {
                        val status = typedArray.getBoolean(currentAttribute, false)
                        ivBack?.visibility = if(status) View.VISIBLE else View.GONE
                        GPLog.d(TAG, "found value = $status")
                    }
                    R.styleable.GPActionBar_gp_actionbar_back_btn_icon -> {
                        val resId0 = typedArray.getResourceId(currentAttribute, -1)
                        val drawable0 = ContextCompat.getDrawable(context, resId0)
                        ivBack?.visibility = VISIBLE
                        ivBack?.setImageDrawable(drawable0)
                    }
                    R.styleable.GPActionBar_gp_menu_item_one_icon -> {
                        val resId1 = typedArray.getResourceId(currentAttribute, -1)
                        val drawable1 = ContextCompat.getDrawable(context, resId1)
                        ivMenu1?.visibility = VISIBLE
                        ivMenu1?.setImageDrawable(drawable1)
                    }
                    R.styleable.GPActionBar_gp_menu_item_two_icon -> {
                        val resId2 = typedArray.getResourceId(currentAttribute, -1)
                        val drawable2 = ContextCompat.getDrawable(context, resId2)
                        ivMenu2?.visibility = VISIBLE
                        ivMenu2?.setImageDrawable(drawable2)
                    }
                    R.styleable.GPActionBar_gp_menu_item_one_visibility -> {
                        val visibility1 = typedArray.getBoolean(currentAttribute, false)
                        ivMenu1?.visibility = if (visibility1) View.VISIBLE else View.GONE

                    }
                    R.styleable.GPActionBar_gp_menu_item_two_visibility -> {
                        val vis2 = typedArray.getBoolean(currentAttribute, false)
                        ivMenu2?.visibility = if (vis2) View.VISIBLE else View.GONE
                    }
                    R.styleable.GPActionBar_gp_show_action_bar_divider -> {
                        val vis3 = typedArray.getBoolean(currentAttribute, false)
                        actionBarDivider?.visibility = if (vis3) View.VISIBLE else View.GONE
                    }
                    R.styleable.GPActionBar_gp_menu_text_option_text -> {
                        tvTextOptionMenu?.text = typedArray.getString(currentAttribute)
                    }
                    R.styleable.GPActionBar_gp_menu_text_option_visibility -> {
                        val visNow = typedArray.getBoolean(currentAttribute, false)
                        tvTextOptionMenu?.visibility = if (visNow) View.VISIBLE else View.GONE
                    }
                }
            }
        } catch (e: Exception) {
            GPLog.e(TAG, "drawComponentsForTheFirstTime: problem occurs")
            e.printStackTrace()
        } finally {
            typedArray?.recycle()
        }
    }

}