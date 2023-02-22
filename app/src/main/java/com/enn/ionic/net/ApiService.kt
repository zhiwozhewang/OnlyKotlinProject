package com.enn.ionic.net

import com.enn.ionic.BuildConfig
import com.enn.ionic.bean.*
import com.enn.ionic.bean.webbean.CheckBean
import com.enn.ionic.js.HttpResponse
import com.enn.ionic.js.HttpTokenResponse
import com.enn.ionic.js.TokenBody
import com.enn.ionic.js.UpLoadFile
import com.enn.network.entity.ApiResponse
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import retrofit2.http.Body
import retrofit2.http.Header

interface ApiService {

    @GET("xxx/xxx/xxx")
    suspend fun getData(): ApiResponse<List<DataBean>>

    @GET("xx/xxx/xxx")
    suspend fun getDataError(): ApiResponse<List<DataBean>>

    //首頁获取  roleType 默认3（游客）
    @GET("$BASE_DATA_URL/test/docloudSale/hqw/authority/getHqwModels")
    suspend fun getFirstUtilsData(
        @Query("phone") phone: String,
        @Query("roleType") roleType: String
    ): ApiResponse<List<UtilsBean>>

    //应用中心 全量获取
    @GET("$BASE_DATA_URL/test/docloudSale/hqw/authority/getHqwAllModelsByGroupBy")
    suspend fun getUtilsData(@Query("phone") phone: String): ApiResponse<List<UtilsBean>>

    //密码登录
    @POST("/crmgas/portal/login/icLogin")
    suspend fun login(@Body toLoginBean: ToLoginBean): LoginBean<Data>

    //验证码登录
    @POST("/crmgas/portal/login/icLoginSms")
    suspend fun loginByCode(@Body toLoginBean: ToLoginBean): LoginBean<Data>

    //找回密码
    @POST("/crmgas/portal/login/resetPassword")
    suspend fun findPw(@Body toFindPwBean: ToFindPwBean): LoginBean<Data>

    //发送验证码
    @POST("/crmgas/portal/sms/sendSms")
    suspend fun sendCode(@Body phone: RequestBody): LoginBean<String>

    //上传移动后的模块
    @POST("$BASE_DATA_URL/test/docloudSale/hqw/authority/saveModel")
    suspend fun setUtilsOrder(@Body postChangeBean: RequestBody): ApiResponse<String>

    //首页获取banner图
    @GET("$BASE_DATA_URL/test/docloudSale/hqw/authority/getImages")
    suspend fun getBannerData(
        @Query("sysFlag") sysFlag: String,
        @Query("type") type: String
    ): ApiResponse<List<FirstBannerBean>>

    //判断用户是否是东莞
    @GET("$BASE_DATA_URL/test/docloudSale/small/gasplan/getNewCustomer")
    suspend fun isDongGuan(
        @Query("bp") bpstr: String
    ): ApiResponse<List<Boolean>>

    //获取文件服务器token
    @POST("https://se-fastdfs.enn.cn/getToken")
    suspend fun getToken(@Body tokenBody: TokenBody): HttpTokenResponse<String>

    //文件上传
    @POST("https://se-fastdfs.enn.cn/fdfs/upload")
    @Multipart
    suspend fun uploadFile(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): HttpResponse<UpLoadFile>

    //城燃 获取code
    @GET("$BASE_CITY_FIRE_URL/customer/project/latest/project")
    suspend fun getCfCode(@Query("customerBp") customerBp: String): ApiResponse<CfCodeBean>

    // 获取用户类型
    @GET("/crmgas/portal/user/get-user-type")
    suspend fun getUserType(@Header("Authorization") token: String): LoginBean<String>

    // 分页获取燃气表列表
    @POST("/crmgas/get-meter-list")
    suspend fun getGasList(
        @Header("Authorization") token: String,
        @Body toGetGasList: ToGetGasList
    ): LoginBean<GasListBean>

    // 微信登陆
    @POST("/crmgas/portal/login/icLoginWxForApp")
    suspend fun toWechatLogin(@Body toWechatLoginBean: ToWechatLoginBean): LoginBean<Data>

    // 微信登陆绑定手机
    @POST("/crmgas/portal/login/bindWxForApp")
    suspend fun toWechatLoginBind(@Body toWechatLoginBean: ToWechatLoginBean): LoginBean<Data>


    // pc扫码  获取临时token
    @POST("/crmgas/scan/login/tempToken")
    suspend fun getScanToken(
        @Header("Authorization") token: String,
        @Query("id") id: String
    ): LoginBean<String>

    // pc扫码  更新登录状态并绑定
    @POST("/crmgas/scan/login/bind")
    suspend fun toScanBind(
        @Header("Authorization") token: String,
        @Header("tempToken") tempToken: String,
        @Query("id") id: String,
        @Query("companyId") companyId: String, @Query("username") username: String
    ): LoginBean<Boolean>

    // 认证- 查询客户信息
    @POST("/crmgas/portal/user/getNewCustomerInfo")
    suspend fun toQueryCustomerInfo(
        @Header("Authorization") token: String,
        @Body code: RequestBody
    ): LoginBean<List<BindCompanyQuryBean>>

    // 认证-绑定客户信息
    @POST("/crmgas/portal/user/toBindUserAndCustomer")
    suspend fun toBindCustomer(
        @Header("Authorization") token: String,
        @Body bindCustomerDto: RequestBody
    ): LoginBean<BindCustomerBean>

    // 修改头像 昵称  职位
    @POST("$BASE_DATA_URL/test/docloudSale/small/smart-wx-info/update")
    suspend fun updateInfo(@Body toUpdateInfoBean: ToUpdateInfoBean): ApiResponse<List<Boolean>>

    // 获取个人信息
    @GET("$BASE_DATA_URL/test/docloudSale/small/smart-wx-info/get")
    suspend fun getInfo(@Query("loginId") loginId: String): ApiResponse<List<ToUpdateInfoBean>>

    // 获取个人信息  id  为空  调用  添加个人信息
    @POST("$BASE_DATA_URL/test/docloudSale/small/smart-wx-info/add")
    suspend fun addInfo(@Body smartWxInfoSaveDTO: ToAddInfoBean): ApiResponse<List<Boolean>>

    // 账号与安全
    @GET("/crmgas/getOrdinaryCountAndWaitingReviewCount")
    suspend fun getAccountInfo(@Header("Authorization") token: String): LoginBean<AccountBean>

    // 获取天气
    @POST("/crmgas/portal/weather/getWeather")
    suspend fun getWeather(
//        @Header("Authorization") token: String,
        @Query("city") city: String
    ): LoginBean<WeatherBean>

    // 获取时间
    @GET("/crmgas/getSysTime")
    suspend fun getTime(): LoginBean<String>

    // 根据bp分页查询非主账号列表
    @POST("/crmgas/manage/pagelist/ordinary/{pageNum}/{pageSize}")
    suspend fun queryAccount(
        @Header("authorization") token: String,
        @Path("pageNum") pageNum: String,
        @Path("pageSize") pageSize: String,
        @Body icLoginDto: ToQueryAccountBean
    ): LoginBean<TransferBean>

    // 转让主账号身份
    @GET("/crmgas/transfer")
    suspend fun transferAccount(
        @Header("Authorization") token: String,
        @Query("fromId") fromId: Int,
        @Query("toId") toId: Int
    ): LoginBean<Any>

    // 公司列表查询
    @POST("/crmgas/portal/user/customerList")
    suspend fun queryCompanys(
        @Header("Authorization") token: String,
        @Body changingParamString: ToSwitchCompanyBean
    ): LoginBean<List<QueryCompanyBackBean>>

    // 切换公司
    @POST("/crmgas/portal/user/changeCompany")
    suspend fun changeCompany(
        @Header("Authorization") token: String,
        @Body changingParamString: ToSwitchCompanyBean
    ): LoginBean<Data>

    // 账户与安全-解绑
    @POST("/crmgas/portal/user/untyingUserAndCustomer")
    suspend fun toUnBind(
        @Header("Authorization") token: String,
        @Body untyingParamString: ToUnbindBean
    ): LoginBean<Any>

    // 账户与安全-拒绝或是通过审核
    @POST("/crmgas/review")
    suspend fun toCheck(
        @Header("Authorization") token: String,
//        @Query("fromId") fromId: Int,
        @Query("id") id: Int, @Query("reviewStatus") reviewStatus: Int
    ): LoginBean<Any>

    // 账户与安全-审核列表
    @GET("/crmgas/manageListForMaster")
    suspend fun getCheckList(
        @Header("Authorization") token: String,
        @Query("pageNum") pageNum: Int,
        @Query("pageSize") pageSize: Int
    ): LoginBean<CheckBean>

    //     账户与安全-审核删除
//    @POST("/crmgas/manage/deleteReviewData")
//    suspend fun deleteCheck(
//        @Header("authorization") token: String,
//        @Query("bp") bp: String,
//        @Query("username") username: String
//    ): LoginBean<CheckBean>
    @POST("/crmgas/manage/deleteReviewData")
    suspend fun deleteCheck(
        @Header("authorization") token: String,
        @Body icLoginDto: ToDeleteCheckBean,
    ): LoginBean<CheckBean>

    // 退出登录
    @POST("/crmgas/portal/user/logout")
    suspend fun toLogout(
        @Header("Authorization") token: String,
        @Query("username") username: String
    ): LoginBean<Data>

    // 根据bp获取城市
    @GET("${BuildConfig.BASE_ZHFX_URL}docloudSale/customer/expand/getCityByCustomerBp")
    suspend fun getCityByCustomerBp(
        @Query("bp") bp: String
    ): ApiResponse<List<BpCityBean>>

    // 检测升级
    @GET("${BuildConfig.BASE_UPDATE_URL}api/systemMgr/newVersion?type=ANDROID")
    suspend fun checkUpdate(): UpdateResultBean

    // 首页-待办
    @GET("$BASE_DATA_URL/test/docloudSale/message/todo/clientList")
    suspend fun getToDoList(
        @Query("pageNum") pageNum: Int,
        @Query("pageSize") pageSize: Int,
        @Query("bp") bp: String
    ): ApiResponse<List<ToDoBean>>

    // 首页-待办-是否展示多余数据
    @GET("$BASE_DATA_URL/test/docloudSale/message/dayGasPlan")
    suspend fun getToDoListHead(
        @Query("bp") bp: String,
        @Query("icLoginId") icLoginId: Int?
    ): ApiResponse<List<ToDoHeadBean>>

    // 首页-待办-是否展示小红点
    @GET("$BASE_DATA_URL/test/docloudSale/message/todo/clientNoDealCount")
    suspend fun getToDoCount(
        @Query("bp") bp: String
    ): ApiResponse<List<Int>>

    //    查询用户信息接口
    @POST("/crmgas/portal/user/userInfo")
    suspend fun refreshInfo(
        @Header("Authorization") token: String,
    ): LoginBean<RefershInfoBean>

    // 首页-获取消息量
    @GET("$BASE_DATA_URL/test/docloudSale/message/notice/clientNoReadCount")
    suspend fun getMessageNum(
        @Query("bp") bp: String,
        @Query("icLoginId") icLoginId: Int
    ): ApiResponse<List<Int>>

    //    查询系统时间
    @GET("/crmgas/getSysTime")
    suspend fun getSysTime(): LoginBean<String>

    companion object {
        const val BASE_URL = "https://cmwx-test.enncloud.cn:443"
        const val BASE_DATA_URL = "https://sg.enn.cn/htapi/api/v1"

        //        const val BASE_BANNER_URL = "http://api-gateway.opaas.enncloud.cn/api/v1/test/docloudSale"
        const val BASE_CITY_FIRE_URL = "https://crm-project.fat.ennew.com"
    }
}