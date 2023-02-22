package com.enn.ionic.net

import com.enn.base.base.BaseApp
import com.enn.base.data.putValue
import com.enn.base.eventbus.Event
import com.enn.base.eventbus.FlowEventBus
import com.enn.ionic.ConstantObject
import com.enn.ionic.ConstantObject.Companion.channelType
import com.enn.ionic.bean.*
import com.enn.network.base.BaseRepository
import com.enn.network.entity.ApiResponse
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.http.Query


class SwitchCompanyRepository : RefreshInfoRepository() {


}