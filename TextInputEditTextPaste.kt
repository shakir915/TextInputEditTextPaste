package com.ems.ourz.views

import android.R
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import androidx.annotation.NonNull
import androidx.core.view.children
import com.ems.ourz.base.SingleLiveEvent
import com.google.android.material.textfield.TextInputEditText


class TextInputEditTextPaste : TextInputEditText, OnTFACodePastedListener {

    var onPasteDone = {  }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
        val ic = super.onCreateInputConnection(outAttrs)
        return TFAInputConnectionWrapper(this, ic, false)
    }

    override fun onTFACodePasted(code: String?) {
        println("code $code")
        try {
            (parent as ViewGroup).children.toList().forEachIndexed { index, view ->
                (view as TextInputEditTextPaste).setText(code!![index].toString())
            }
            onPasteDone.invoke()
        } catch (e: Exception) {
           e.printStackTrace()
        }

    }

    override fun onTextContextMenuItem(id: Int): Boolean {
        val consumed = super.onTextContextMenuItem(id)
        when (id) {
            R.id.cut -> {}
            R.id.paste -> {
                try {
                    val clipBoardManager = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val code=clipBoardManager.primaryClip?.getItemAt(0)?.text?.toString()
                    if (code?.toIntOrNull()?.toString()?.length==6){
                        (parent as ViewGroup).children.toList().forEachIndexed { index, view ->
                            (view as TextInputEditTextPaste).setText(code!![index].toString())
                        }
                    }
                    onPasteDone.invoke()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            R.id.copy -> {


            }
        }
        return consumed
    }
}



class TFAInputConnectionWrapper(
    @NonNull pListener: OnTFACodePastedListener,
    target: InputConnection?, mutable: Boolean
) : InputConnectionWrapper(target, mutable) {
    private val mCodePastedListener: OnTFACodePastedListener

    init {
        mCodePastedListener = pListener
    }

    override fun commitText(text: CharSequence, newCursorPosition: Int): Boolean {
        val tfaCode = text.toString()
        // Just a regex to avoid dispatching incorrect 2FA code for me
        if (tfaCode.toIntOrNull()?.toString()?.length==6) {
            mCodePastedListener.onTFACodePasted(tfaCode)
            // Returning false here avoid the wrapped InputConnection
            // to dispatch it further has we already handled it.
            return false
        }
        // On my side, I return false here too to only handle pasting the 2FA code my way
        // but it could be useful to keep it in your case
        return super.commitText(text, newCursorPosition)
    }
}

interface OnTFACodePastedListener {
    fun onTFACodePasted( code: String?)
}
