package com.kostlin.fragment.ui.logic

import com.kostlin.fragment.ui.main.MainFragment


class DataTransferClass {

    interface OnClick {
        fun getData(count: String)
    }

    private var listener: OnClick? = null

    fun setListener(listener: MainFragment) {
        this.listener = listener
    }

    fun setData(data: String) {
        if (listener == null) {
            listener?.getData(data)
        }
    }

    companion object {
        private var mInstance: DataTransferClass? = null
        val instance: DataTransferClass?
            get() {
                if (mInstance == null) {
                    mInstance = DataTransferClass()
                }
                return mInstance
            }
    }
}