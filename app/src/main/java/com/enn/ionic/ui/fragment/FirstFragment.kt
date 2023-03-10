package com.enn.ionic.ui.fragment

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.graphics.ColorUtils
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.listener.OnItemDragListener
import com.dylanc.viewbinding.binding
import com.enn.base.base.BaseFragment
import com.enn.base.data.getValue
import com.enn.base.eventbus.Event
import com.enn.base.eventbus.FlowEventBus
import com.enn.base.ktx.startActivity
import com.enn.base.util.statusbar.StatusBarCompat
import com.enn.base.util.launchAndCollect
import com.enn.base.util.launchAndCollectIn
import com.enn.base.util.launchWithLoading
import com.enn.ionic.ConstantObject
import com.enn.ionic.ConstantObject.Companion.ACS_URL
import com.enn.ionic.ConstantObject.Companion.DEFAULT_BIAOJI_URL
import com.enn.ionic.ConstantObject.Companion.DEFAULT_NICK_NAME
import com.enn.ionic.ConstantObject.Companion.DEFAULT_TODO_URL
import com.enn.ionic.ConstantObject.Companion.GAS_PLAN_ID
import com.enn.ionic.ConstantObject.Companion.HAS_DO
import com.enn.ionic.ConstantObject.Companion.MESSAGE_URL
import com.enn.ionic.ConstantObject.Companion.MORE_ID
import com.enn.ionic.ConstantObject.Companion.PAY_ID
import com.enn.ionic.ConstantObject.Companion.PROJECT_PROGRESS_ID
import com.enn.ionic.ConstantObject.Companion.Result_Code_Sucess
import com.enn.ionic.ConstantObject.Companion.TO_DO
import com.enn.ionic.ConstantObject.Companion.UNITLS_MORE_NAME
import com.enn.ionic.ConstantObject.Companion.USER_TYPE0
import com.enn.ionic.ConstantObject.Companion.USER_TYPE1
import com.enn.ionic.ConstantObject.Companion.USER_TYPE2
import com.enn.ionic.ConstantObject.Companion.USER_TYPE3
import com.enn.ionic.R
import com.enn.ionic.adapter.FirstBannerGasAdapter
import com.enn.ionic.adapter.FirstBannerImAdapter
import com.enn.ionic.adapter.FirstGridAdapter
import com.enn.ionic.bean.*
import com.enn.ionic.databinding.*
import com.enn.ionic.js.FirstJsCall
import com.enn.ionic.ui.activity.CompanyBindActivity
import com.enn.ionic.ui.activity.SwitchCompanyActivity
import com.enn.ionic.ui.activity.WebActivityNew
import com.enn.ionic.utils.*
import com.enn.ionic.vm.FirstViewModel
import com.enn.network.toast
import com.enn.network.utils.LogUtils
import com.permissionx.guolindev.PermissionX
import com.youth.banner.indicator.CircleIndicator
import com.youth.banner.listener.OnPageChangeListener
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FirstFragment : BaseFragment(R.layout.fragment_first), OnItemDragListener {

    private val mViewModel by viewModels<FirstViewModel>()
    private val mBinding: FragmentFirstBinding by binding()
    private lateinit var recyclerViewRecommend: RecyclerView
    private lateinit var firstGridAdapter: FirstGridAdapter
    private lateinit var fragmentFirstInTodoBinding: FragmentFirstInTodoBinding
    private lateinit var fragmentFirstInRecommendBinding: FragmentFirstInRecommendBinding
    private lateinit var fragmentFirstInActivityBinding: FragmentFirstInActivityBinding
    private lateinit var fragmentFirstInWebBinding: FragmentFirstInWebBinding
    private lateinit var bannerImageAdapter: FirstBannerImAdapter<FirstBannerBean>
    private lateinit var firstBannerGasAdapter: FirstBannerGasAdapter
    private lateinit var token: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        checkUpdate()
    }

    override fun onResume() {
        super.onResume()
        StatusBarCompat.nosteepStatusBarText(activity, false, "#0473FF")
    }

    private fun initView() {
        //        ????????????
        //        mBinding.firstRefresh.setRefreshHeader(ClassicsHeader(this))
        //        mBinding.firstRefresh.setRefreshFooter(ClassicsFooter(this))
        mBinding.firstRefresh.setOnRefreshListener {
            launchNoLoadingNoBase({
                mViewModel.toRefreshInfo()
            }) {

            }
        }
//        banner??????????????????
        mBinding.firstInInclude.banner.apply {
            addBannerLifecycleObserver(this@FirstFragment)
//                .indicator =
//                CircleIndicator(activity)
            setIndicator(mBinding.firstInInclude.firstIndicator, false)
            isAutoLoop(false)
        }

        //????????????
        firstGridAdapter = FirstGridAdapter(R.layout.item_first_grid, null)
        mBinding.firstInInclude.firstGrid.adapter = firstGridAdapter.apply {
            setOnItemClickListener { adapter, view, position ->
//                ??????????????????
                activity?.clickState {
                    val item: HqwModelVO = adapter.getItem(position) as HqwModelVO

                    val linker: String? =
                        when (item.modelId) {
//                                ????????????
                            GAS_PLAN_ID -> ConstantObject.GAS_PLAN_URL
//                                ????????????
                            PROJECT_PROGRESS_ID -> item.linkUrl + "?bp=${
                                activity?.getValue(
                                    "bp",
                                    ""
                                )
                            }"
//                            GAS_CHECK_ID -> ConstantObject.GAS_CHECK_URL
                            else -> item.linkUrl
                        }
                    when (item.modelId) {
                        PAY_ID -> {
//                            ????????????
                            getSysTimeIsLimit {
                                toWeb(linker, item)
                            }
                        }
                        MORE_ID -> {
                            //                  ??????
                            Bundle().apply {
                                putParcelableArrayList(
                                    "firstUtils",
                                    firstGridAdapter.data as ArrayList
                                )
                                startActivity<UtilsActivity>(this)
                            }
                        }
                        else -> {
                            toWeb(linker, item)
                        }
                    }

                }

            }

        }
        firstGridAdapter.draggableModule.apply {
            isDragEnabled = true
            itemTouchHelperCallback.setDragMoveFlags(ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.UP or ItemTouchHelper.DOWN)
            setOnItemDragListener(this@FirstFragment)
        }
//        ????????????
        mBinding.imMsg.setOnClickListener {
            //                ??????????????????
            activity?.clickState {
                startActivity<WebActivityNew>(Bundle().also {
                    it.putString("url", MESSAGE_URL)
                    it.putString("token", token)
                    it.putString("title", "????????????")
                })
            }
        }
        //        ????????????
        mBinding.imCall.setOnClickListener {
            toast("???????????????,????????????...")
        }
        //        ????????????
        mBinding.companyChange.setOnClickListener {
            startActivity<SwitchCompanyActivity>()
        }
//        eventbus
        FlowEventBus.observe<Event.AfterLoginOrOut>(this) { it ->
            LogUtils.e("???????????????????????????")
            initData()
        }
        FlowEventBus.observe<Event.SaveInfoBean<ToUpdateInfoBean>>(this) { it ->
            LogUtils.e("???????????????????????????")
            //        title??????
            mBinding.firstName.text = it.bean.nickname
            val job = it.bean.position
            mBinding.firstId.text = job
            mBinding.firstId.visibility = if (job.isNullOrEmpty()) View.GONE else View.VISIBLE
        }
        FlowEventBus.observe<Event.AfterChangeUtils>(this) { it ->
            LogUtils.e("????????????????????????????????????")
            //
            getFirstUtils()
        }
        FlowEventBus.observe<Event.AfterDoneToDo>(this) { it ->
            LogUtils.e("?????????????????????")
            //        ????????????
            getToDoList()
        }

    }

    private fun toWeb(linker: String?, item: HqwModelVO) {
        Bundle().apply {
            putCharSequence("url", linker)
            putCharSequence("token", token)
            putCharSequence("title", item.modelName?.toString())
            startActivity<WebActivityNew>(this)
        }
    }


    private fun initData() {
//        ?????????token
        token = activity?.getValue("token", "").toString()
        getFirstUtils()
        isDongGuan()
        //       ?????????????????????
        mBinding.firstInInclude.fragmentFirstInAc.apply {
            if (parent != null) {
                inflate()?.let {
                    fragmentFirstInActivityBinding = FragmentFirstInActivityBinding.bind(it)
                    fragmentFirstInActivityBinding.acIm.setOnClickListener {
                        Bundle().apply {
                            putCharSequence("url", ACS_URL)
                            putCharSequence("token", token)
                            putCharSequence("title", "????????????")
                            startActivity<WebActivityNew>(this)
                        }
                    }
                }
            }
        }
        initViewByUser(activity?.getValue("usertype", USER_TYPE0))
    }

    /**
     *  ?????????????????????
     */
    private fun getFirstUtils() {
        launchAndCollect({
            mViewModel.requestFirstUtilsData()
        }) {
            onSuccess = { result: List<UtilsBean>? ->
                firstGridAdapter.setList(result?.get(0)?.hqwModelVOList)
            }
            onFailed = { _, msg -> toast(msg ?: "") }
        }
    }


    //????????????type????????????
    private fun initViewByUser(usertype: String?) {

//        title??????
        mBinding.firstName.text = activity?.getValue("nickname", DEFAULT_NICK_NAME)
        val job = activity?.getValue("job", "")
        mBinding.firstId.text = job
        mBinding.firstId.visibility = if (job.isNullOrEmpty()) View.GONE else View.VISIBLE
        mBinding.company.text = activity?.getValue("company", "")
//        ????????????
        when (usertype) {
            USER_TYPE0 -> {
//                ?????????
                initUser0()
                getBanners()
                mBinding.companyLi.visibility = View.GONE
                mBinding.touristStatus.visibility = View.GONE
            }
            USER_TYPE1 -> {
//                ????????????
                initUser1()
                getBannersGas(token, ToGetGasList(0, 3))
                mBinding.companyLi.visibility = View.VISIBLE
                mBinding.touristStatus.visibility = View.GONE
                mBinding.companyChange.visibility = if (activity?.getValue(
                        "hascompanys",
                        false
                    ) == true
                ) View.VISIBLE else View.GONE
            }
            USER_TYPE2 -> {
//                ????????????
                initUser2()
                getBanners()
                mBinding.companyLi.visibility = View.VISIBLE
                mBinding.touristStatus.visibility = View.GONE
                mBinding.companyChange.visibility = if (activity?.getValue(
                        "hascompanys",
                        false
                    ) == true
                ) View.VISIBLE else View.GONE
            }
            USER_TYPE3 -> {
//                ??????
                initUser0()
                getBanners()
                mBinding.companyLi.visibility = View.GONE
                mBinding.touristStatus.visibility = View.VISIBLE
                refershTouristBindStatus()
            }
            else -> {}
        }
        //        ????????????
        requestWeather(token)
        //??????????????????
        activity?.let { getMessageNum(it.getValue("bp", ""), it.getValue("icLoginId", 0)) }
        //        ????????????
        mBinding.firstRefresh.finishRefresh()


    }

    /**
     * ????????????????????????
     */
    private fun refershTouristBindStatus() {

        when (activity?.getValue(
            "bindstate",
            ConstantObject.USER_BIND_STATUS_LOGIN_NOBIND
        )) {
            ConstantObject.USER_BIND_STATUS_LOGIN_NOBIND -> {
                mBinding.touristStatus.text = "?????????"
                mBinding.touristStatus.setOnClickListener {
                    startActivity<CompanyBindActivity>()
                }
            }
            ConstantObject.USER_BIND_STATUS_LOGIN_BINDING -> {
                mBinding.touristStatus.text = "???????????????"
                mBinding.touristStatus.setOnClickListener {
                    //?????????????????????
                    MyDialogUtils.showBindCheckingDialog()
                }
            }
            ConstantObject.USER_BIND_STATUS_LOGIN_BIND -> {
                mBinding.touristStatus.visibility = View.GONE
                mBinding.companyLi.visibility = View.VISIBLE
            }
            else -> {}
        }
    }

    /**
     *  ??????  ?????????
     */
    private fun initUser0() {
//       ?????? ??????
        mBinding.firstInInclude.fragmentFirstInTodoVs.apply {
            if (parent == null) {
                visibility = View.GONE
            }
        }
//        ??????????????????
        mBinding.firstInInclude.fragmentFirstInRe.apply {
            if (parent == null) {
                visibility = View.GONE
            }
        }
//        ?????????????????? webview
        mBinding.firstInInclude.fragmentFirstInWeb.apply {
            if (parent == null) {
                visibility = View.GONE
            }
        }
        //       ??????????????????
        mBinding.firstInInclude.fragmentFirstInAc.apply {
            if (parent == null) {
                visibility = View.VISIBLE
            }
        }
    }

    /**
     *  ????????????
     */
    private fun initUser1() {
//        ??????
        mBinding.firstInInclude.fragmentFirstInTodoVs.apply {
            if (parent != null) {
                inflate()?.let {
                    fragmentFirstInTodoBinding = FragmentFirstInTodoBinding.bind(it)
                    fragmentFirstInTodoBinding.firstTodo.setOnClickListener {
                        Bundle().apply {
                            putCharSequence("url", DEFAULT_TODO_URL)
                            putCharSequence("token", token)
                            putCharSequence("title", "????????????")
                            startActivity<WebActivityNew>(this)
                        }
                    }
                }
            } else
                visibility = View.VISIBLE

        }
//        fixme ????????????  ????????????
        /*     mBinding.firstInInclude.fragmentFirstInRe.apply {
                 if (parent != null) {
                     inflate()?.let {
                         fragmentFirstInRecommendBinding = FragmentFirstInRecommendBinding.bind(it)
                         fragmentFirstInRecommendBinding.reRecy.adapter = FirstRecommendAdapter(
                             R.layout.item_first_recommend, ArrayList<FirstRecommendBean>()
                         )
                     }

                 } else
                     visibility = View.VISIBLE

             }*/
//        ?????????????????? webview
        mBinding.firstInInclude.fragmentFirstInWeb.apply {
//            viewstub???????????????
            if (parent == null) {
                visibility = View.GONE
            }
        }
        //       ??????????????????
        mBinding.firstInInclude.fragmentFirstInAc.apply {
            if (parent == null) {
                visibility = View.VISIBLE
            }
        }
//        ????????????
        getToDoList()
    }

    /**
     *  ????????????
     */
    private fun initUser2() {
        //       ?????? ??????
        mBinding.firstInInclude.fragmentFirstInTodoVs.apply {
            if (parent == null) {
                visibility = View.GONE
            }
        }
//       ?????? ????????????
        mBinding.firstInInclude.fragmentFirstInRe.apply {
            if (parent == null) {
                visibility = View.GONE
            }
        }
        //???????????????webview
        mBinding.firstInInclude.fragmentFirstInWeb.apply {
            if (parent != null) {
                inflate()?.let {
                    fragmentFirstInWebBinding = FragmentFirstInWebBinding.bind(it)
                    activity?.let { it2 ->
                        fragmentFirstInWebBinding.webWrapper.addJavascriptInterface(
                            FirstJsCall(it2),
                            ConstantObject.JSOBJECT
                        )
                    }
                }
            } else
                visibility = View.VISIBLE
        }
        //       ??????????????????
        mBinding.firstInInclude.fragmentFirstInAc.apply {
            if (parent == null) {
                visibility = View.GONE
            }
        }
        //??????????????????code
        getCfCode()

    }

    override fun onItemDragStart(viewHolder: RecyclerView.ViewHolder?, pos: Int) {

    }

    override fun onItemDragMoving(
        source: RecyclerView.ViewHolder?,
        from: Int,
        target: RecyclerView.ViewHolder?,
        to: Int
    ) {

    }

    override fun onItemDragEnd(viewHolder: RecyclerView.ViewHolder?, pos: Int) {
        mViewModel.changeUtilsOrder(
            firstGridAdapter.data,
            activity?.getValue("phone", ConstantObject.PHONE) ?: ""
        )
    }

    /**
     *??????  ??????  ??????  ????????????banner
     */
    private fun getBanners() {
        launchAndCollect({
            mViewModel.getBanners(
                "hqw",
                "banner"
            )
        }) {
            onSuccess = {
                //??????????????????
                bannerImageAdapter = FirstBannerImAdapter(ArrayList())
//                    ???????????????
                mBinding.firstInInclude.banner.background = if (it.isNullOrEmpty()) {
                    resources.getDrawable(R.mipmap.im_bg_banner, null);
                } else null

                bannerImageAdapter.setDatas(it)
                bannerImageAdapter.setOnBannerListener { data, position ->
                    Bundle().apply {
                        putCharSequence("url", data.linkUrl)
                        putCharSequence("token", token)
//                        putCharSequence("title", "????????????")
                        startActivity<WebActivityNew>(this)
                    }
                }
                mBinding.firstInInclude.banner.setAdapter(bannerImageAdapter)
            }
            onFailed = { _, errorMsg ->
                toast(errorMsg ?: "")
            }
        }
    }

    private fun isDongGuan() {
        launchAndCollect({
            mViewModel.isDongGuan(
                activity?.getValue("bp", "0002092964") ?: ""
            )
        }) {
            onSuccess = {

                ConstantObject.GAS_PLAN_URL = if (it?.get(0) == true) {
                    "http://great-gas-h5.ipaas.enncloud.cn/pages/dg-predice-gas/predict-gas-report/predict-gas-report"
                } else {
                    "http://great-gas-h5.ipaas.enncloud.cn/pages/predict-gas/predict-gas-report/predict-gas-report"
                }
                ConstantObject.GAS_CHECK_URL = if (it?.get(0) == true) {
                    "http://great-gas-h5.ipaas.enncloud.cn/pages/dg-manager-energy/dg-manager-energy"
                } else {
                    "http://great-gas-h5.ipaas.enncloud.cn/pages/manager-energy/manager-energy-info/manager-energy-info"
                }
            }

        }
    }

    /**
     * ????????????????????????
     */
    private fun getToDoList() {

        launchAndCollect({
            mViewModel.getToDoList(
                bp = activity?.getValue("bp", "").toString(),
                icLoginId = activity?.getValue("icLoginId", 0)
            )
        }) {
            onSuccess = {
                it?.let {

                    initToDoView(it[0])

                }
            }
        }
    }

    /**
     * ????????????
     */
    private fun getMessageNum(bp: String, icLoginId: Int) {

        launchAndCollect({
            mViewModel.getMessageNum(bp, icLoginId)
        }) {
            onSuccess = {
                it?.let {
                    mBinding.imMsgRed.visibility = if (it[0] > 0) View.VISIBLE else View.GONE
                }
            }
        }
    }

    /**
     * ??????????????????
     */
    private fun initToDoView(toDoBean: ToDoBean) {
        fragmentFirstInTodoBinding.todoRightIm.visibility =
            if (toDoBean.isShow) View.VISIBLE else View.GONE
        mBinding.firstInInclude.fragmentFirstInTodoVs.apply {
            visibility = if (toDoBean.list.isNullOrEmpty()) View.GONE else View.VISIBLE
        }
        if (!toDoBean.list.isNullOrEmpty()) {
            fragmentFirstInTodoBinding.todoText1.text = toDoBean.list[0].title
            fragmentFirstInTodoBinding.todoText1Rtext.text =
                if (toDoBean.list[0].isDeal == 0) TO_DO else HAS_DO

            when (toDoBean.list.size > 1) {
                true -> {
                    fragmentFirstInTodoBinding.todoText2Re.visibility = View.VISIBLE
                    fragmentFirstInTodoBinding.todoText2.text = toDoBean.list[1].title
                    fragmentFirstInTodoBinding.todoText2Rtext.text =
                        if (toDoBean.list[1].isDeal == 0) TO_DO else HAS_DO
                }
                else -> {
                    fragmentFirstInTodoBinding.todoText2Re.visibility = View.GONE
                }
            }
        }
    }

    /**
     * ??????????????????code
     */
    private fun getCfCode() {
        val bp = activity?.getValue("bp", "") ?: ""
        val icLoginId = activity?.getValue("icLoginId", 0) ?: 0

        launchAndCollect({
            mViewModel.getCfCode(bp)
        }) {
            onSuccess = {
                it?.let {
                    fragmentFirstInWebBinding.webWrapper.loadUrl(ConstantObject.CITY_FIRE_CARD_URL_FAT + "card?id=${it.id}&code=${it.projectCode}&bp=$bp&icLoginId=$icLoginId")
// FIXME: ????????????  ???????????????
                    //                    fragmentFirstInWebBinding.webBtn1.setOnClickListener {
////????????????
//                        Bundle().apply {
//                            putCharSequence(
//                                "url",
//                                ConstantObject.CITY_FIRE_CARD_URL_FAT + "?bp=$bp&icLoginId=$icLoginId"
//                            )
//                            putCharSequence("title", "????????????")
//                            startActivity<WebActivityNew>(this)
//                        }
//                    }
//                    fragmentFirstInWebBinding.webBtn2.setOnClickListener { _ ->
////??????
//                        Bundle().apply {
//                            putCharSequence(
//                                "url",
//                                ConstantObject.CITY_FIRE_CARD_URL_FAT + "h5?id=${it.id}&code=${it.projectCode}&bp=$bp&icLoginId=$icLoginId&pageCode=CUSTOMER_PAYMENT"
//                            )
//                            putCharSequence("title", "????????????")
//                            startActivity<WebActivityNew>(this)
//                        }
//                    }
                }

            }

        }
    }

    /**
     * ????????????????????????banner
     */
    private fun getBannersGas(token: String, toGetGasList: ToGetGasList) {
        launchNoLoadingNoBase({
            mViewModel.getGasList(token, toGetGasList)
        }) {
            onSuccess = {
                when (it?.body?.resultCode) {
                    Result_Code_Sucess -> {
                        if (it.body.data.list.isNullOrEmpty()) {
//                     ??????banner???
                            getBanners()
                        } else {
//                    ??????????????????
                            firstBannerGasAdapter = FirstBannerGasAdapter(it?.body?.data?.list)
                            mBinding.firstInInclude.banner.run {
                                setAdapter(firstBannerGasAdapter)
//                        ????????????????????????????????????????????????
                                addOnPageChangeListener(object : OnPageChangeListener {
                                    override fun onPageScrolled(
                                        position: Int,
                                        positionOffset: Float,
                                        positionOffsetPixels: Int
                                    ) {
                                        if (position == 2) {
                                            Bundle().apply {
                                                putCharSequence("url", DEFAULT_BIAOJI_URL)
                                                putCharSequence("token", token)
                                                putCharSequence("title", "????????????")
                                                startActivity<WebActivityNew>(this)
                                            }
                                        }
                                    }

                                    override fun onPageSelected(position: Int) {
                                    }

                                    override fun onPageScrollStateChanged(state: Int) {
                                    }

                                })
                            }
                        }
                    }
                    else -> {}
                }
            }
        }

    }

    private fun requestWeather(token: String) {
        PermissionX.init(activity)
            .permissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .request { allGranted: Boolean, _: List<String?>?, _: List<String?>? ->
                if (allGranted) {
                    lifecycleScope.launch {
                        val location = activity?.let { getCurrentLocation(it) }
                        location?.let {
                            requestWeatherDetail(token, it.city)
                        }
                    }
                } else {
                    activity?.getValue("bp", "")?.let {
                        launchAndCollect({
                            mViewModel.getCityByCustomerBp(it)
                        }) {
                            onSuccess = {
                                LogUtils.d("????????????:$it")
                                if (!it.isNullOrEmpty()) {
                                    requestWeatherDetail(token, it[0].city ?: "")
                                }
                            }
                        }
                    }
                }
            }
    }

    private fun requestWeatherDetail(token: String, city: String) {
        lifecycleScope.launch {
            var greetings = ""
            var weatherDes = ""

            val weatherRepo = async { mViewModel.getWeather(city) }
            val timeRepo = async { mViewModel.getTime() }

            val weather = weatherRepo.await()
            val time = timeRepo.await()

//            val weather = mViewModel.getWeather(city)
//            val time = mViewModel.getTime()

            time?.body?.data?.let {

                val calendar = Calendar.getInstance()
                calendar.time =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).parse(it) as Date
                val hour = calendar.get(Calendar.HOUR_OF_DAY)

                if (hour < 12) {
                    greetings = "????????????"
                }
                if (hour in 12..17) {
                    greetings = "????????????"
                }
                if (hour >= 18) {
                    greetings = "????????????"
                }
            }

            weather?.body?.data?.temperature?.let {
                weatherDes = "${it}???${weather.body.data.weatherDes}"
            }
            lifecycleScope.launchWhenResumed {
                mBinding.firstWeather.apply {
                    isSelected = true
                    text =
                        "${greetings}${weatherDes}"
                }
            }


        }
    }


    private fun checkUpdate() {
        launchNoLoadingNoBase({
            mViewModel.checkUpdate()
        }) {
            onSuccess = {
                LogUtils.d("???????????????:$it")
                it?.let {
                    MyDialogUtils.showUpdateDialog(requireActivity(), it)
                }
            }
        }
    }

    /**
     *??????????????????????????????
     */
    private fun getSysTimeIsLimit(toDo: () -> Unit) {
        launchNoLoadingNoBase({
            mViewModel.getSysTimeIsLimit()
        }) {
            onSuccess = {
                when (it) {
                    false -> {
                        toDo()
                    }
                    else -> {
                        toast(ConstantObject.PAY_TIME_LIMIT)
                    }
                }
            }

        }
    }
}