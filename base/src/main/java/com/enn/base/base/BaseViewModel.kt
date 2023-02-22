package com.enn.base.base

import androidx.lifecycle.ViewModel
abstract class BaseViewModel : ViewModel()

// TODO MVI
sealed class ViewEffect {
    data class ShowLoading(val isShow: Boolean) : ViewEffect()
    data class ShowToast(val message: String) : ViewEffect()
    data class Success<T>(val data: T) : ViewEffect()
    object Empty : ViewEffect()
}