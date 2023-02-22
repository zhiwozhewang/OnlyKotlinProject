package com.enn.ionic.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class FilterSpaceTextWatcher(editText: EditText, myafterTextChanged: (p0: Editable?) -> Unit = {}) :
    TextWatcher {
    private val editText = editText
    private val myafterTextChanged = myafterTextChanged
    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        // 禁止EditText输入空格
        if (p0.toString().contains(" ")) {
            val str = p0?.toString()?.split(" ")
            val sb = StringBuffer()
            for (i in 0 until str?.size!!) {
                sb.append(str[i])
            }
            editText.setText(sb.toString())
            editText.setSelection(p1)
        }
    }

    override fun afterTextChanged(p0: Editable?) {
        myafterTextChanged(p0)
    }
}