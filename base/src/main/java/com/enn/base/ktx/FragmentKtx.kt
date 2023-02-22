package com.enn.base.ktx

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment

inline fun <reified T : Activity> Fragment.startActivity(bundle: Bundle? = null) {
    val intent = Intent(activity, T::class.java)
    if (bundle != null) {
        intent.putExtras(bundle)
    }
    startActivity(intent)
}
