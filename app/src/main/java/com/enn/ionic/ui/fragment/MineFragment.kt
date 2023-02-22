package com.enn.ionic.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.dylanc.viewbinding.binding
import com.enn.base.base.BaseFragment
import com.enn.base.data.getValue
import com.enn.base.eventbus.Event
import com.enn.base.eventbus.FlowEventBus
import com.enn.base.ktx.startActivity
import com.enn.base.util.statusbar.StatusBarCompat
import com.enn.ionic.ConstantObject
import com.enn.ionic.ConstantObject.Companion.DEFAULT_NICK_NAME
import com.enn.ionic.ConstantObject.Companion.USER_BIND_STATUS_LOGIN_BIND
import com.enn.ionic.ConstantObject.Companion.USER_BIND_STATUS_LOGIN_BINDING
import com.enn.ionic.ConstantObject.Companion.USER_BIND_STATUS_LOGIN_NOBIND
import com.enn.ionic.ConstantObject.Companion.USER_TYPE0
import com.enn.ionic.ConstantObject.Companion.USER_TYPE1
import com.enn.ionic.ConstantObject.Companion.USER_TYPE2
import com.enn.ionic.ConstantObject.Companion.USER_TYPE3
import com.enn.ionic.R
import com.enn.ionic.bean.ToUpdateInfoBean
import com.enn.ionic.databinding.FragmentMineBinding
import com.enn.ionic.ui.activity.*
import com.enn.ionic.utils.CommonUtil
import com.enn.ionic.utils.CommonUtil.subingJob
import com.enn.ionic.utils.MyDialogUtils
import com.enn.ionic.utils.clickBindState
import com.enn.ionic.utils.launchWithLoadingNoBase
import com.enn.ionic.vm.AccountViewModel
import com.enn.ionic.vm.InfoViewModel
import com.enn.network.toast
import com.youth.banner.util.LogUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MineFragment : BaseFragment(R.layout.fragment_mine), View.OnClickListener {

    private var bindstate: Int = USER_BIND_STATUS_LOGIN_NOBIND
    private val mBinding: FragmentMineBinding by binding()
    private var usertype: String = USER_TYPE0
    private val mViewModel by viewModels<AccountViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        FlowEventBus.observe<Event.AfterLoginOrOut>(this) { it ->
            LogUtils.e("登录状态刷新")
            initView()
        }
        FlowEventBus.observe<Event.SaveInfoBean<ToUpdateInfoBean>>(this) { it ->
            LogUtils.e("修改个人信息后刷新")
            initNameAndPhone(
                nickname = it.bean.nickname,
                job = it.bean.position,
                headim = it.bean.headImageUrl
            )
        }

    }

    override fun onResume() {
        super.onResume()
        StatusBarCompat.steepStatusBarText(activity, statusColor = "#00000000")
    }

    private fun initView() {
        //用户类型

        usertype = activity?.getValue("usertype", USER_TYPE0).toString()
        mBinding.mineSwitch.visibility = View.GONE

        when (usertype) {
            USER_TYPE0 -> {
//                未登录
                mBinding.mineName.text = "请登录"
                mBinding.mineTip.visibility = View.GONE
                mBinding.mineTouristStatus.visibility = View.GONE
                mBinding.mineScan.visibility = View.GONE
                mBinding.mineHeadimRim.visibility = View.GONE
                mBinding.mineCompanyLi.visibility = View.GONE
                mBinding.mineHeadim.setImageResource(R.mipmap.im_mine_head)
                initBindView(View.VISIBLE, true, "绑定", "", R.mipmap.im_mine_binding)

            }
            USER_TYPE1 -> {
//                1：用气客户，
                mBinding.mineScan.visibility = View.VISIBLE
                //        扫码登录
                mBinding.mineScan.setOnClickListener {
                    startActivity<QRCodeScanningActivity>()
                }
                initUserNotTourist()
            }
            USER_TYPE2 -> {
//               2：工程客户
                mBinding.mineScan.visibility = View.GONE
                initUserNotTourist()
            }
            USER_TYPE3 -> {
//                3：游客
                mBinding.mineScan.visibility = View.GONE
                initUserTourist()
            }
            else -> {}
        }
        mBinding.mineHeadim.setOnClickListener(this)
        mBinding.mineSet.setOnClickListener(this)
        mBinding.mineSwitch.setOnClickListener(this)
        mBinding.minePay.setOnClickListener(this)
        mBinding.mineTelLayer.setOnClickListener(this)
        mBinding.mineHousekeeper.setOnClickListener(this)
        mBinding.mineCompanyChange.setOnClickListener(this)
    }

    private fun initUserTourist() {
        //游客登录判断绑定状态
        bindstate = activity?.getValue("bindstate", USER_BIND_STATUS_LOGIN_NOBIND)!!
        when (bindstate) {
            USER_BIND_STATUS_LOGIN_NOBIND -> {
                initNameAndPhone()
                initTouristStatus(bindstate)
                initBindView(
                    View.VISIBLE,
                    true,
                    "绑定",
                    "3秒认证，立享专属服务",
                    R.mipmap.im_mine_binding,
                    R.color.FF9200
                )

            }
            USER_BIND_STATUS_LOGIN_BINDING -> {
                initNameAndPhone()
                initTouristStatus(bindstate)
                initBindView(
                    View.VISIBLE,
                    false,
                    "认证审核中",
                    "取消认证",
                    R.mipmap.im_mine_audit,
                    R.color.color_0473FF
                )
            }
            USER_BIND_STATUS_LOGIN_BIND -> {
                initUserNotTourist()
            }
            else -> {}
        }
    }

    private fun initTouristStatus(bindstate: Int) {
        mBinding.mineTouristStatus.visibility = View.VISIBLE
        mBinding.mineCompanyLi.visibility = View.GONE
        when (bindstate) {
            USER_BIND_STATUS_LOGIN_NOBIND -> {
                mBinding.mineTouristStatus.text = "去认证"
                mBinding.mineTouristStatus.setOnClickListener {
                    startActivity<CompanyBindActivity>()
                }
            }
            USER_BIND_STATUS_LOGIN_BINDING -> {
                mBinding.mineTouristStatus.text = "认证审核中"
                mBinding.mineTouristStatus.setOnClickListener {
                    //认证审核中弹框
                    MyDialogUtils.showBindCheckingDialog()
                }
            }
            else -> {}
        }
        initSwitchStatus()

    }

    private fun initUserNotTourist() {
        initNameAndPhone()
//
        mBinding.mineTouristStatus.visibility = View.GONE
        mBinding.mineCompanyLi.visibility = View.VISIBLE
        initSwitchStatus()
        mBinding.mineCompany.text = activity?.getValue("company", "")
        initBindView(View.GONE)
    }

    /**
     * 切换公司按钮状态刷新
     */
    private fun initSwitchStatus() {
        val viewstate =
            if (activity?.getValue("hascompanys", false) == true) View.VISIBLE else View.GONE
        mBinding.mineCompanyChange.visibility = viewstate
        mBinding.mineSwitch.visibility = viewstate
    }

    private fun initNameAndPhone(
        nickname: String? = activity?.getValue("nickname", DEFAULT_NICK_NAME),
        job: String? = activity?.getValue("job", ""),
        phone: String? = activity?.getValue("phone", ""),
        headim: String? = activity?.getValue("headim", "")
    ) {

        mBinding.mineName.text = nickname
        mBinding.mineTip.visibility = View.VISIBLE
//        职位与电话
        val job = job?.let { subingJob(it) }
        val phone = phone?.let { CommonUtil.hidePhone(it) }
        mBinding.mineTip.text = if (!TextUtils.isEmpty(job)) "$job | $phone" else "$phone"
//  头像
        mBinding.mineHeadimRim.visibility = View.VISIBLE
        mBinding.mineHeadim.load(headim) {
            transformations(CircleCropTransformation())
            error(R.mipmap.im_mine_head)
            placeholder(R.mipmap.im_mine_head)
        }
    }

    private fun initBindView(
        visible: Int,
        isshow: Boolean = true,
        text: String = "",
        righttext: String = "",
        leftim: Int = R.mipmap.im_mine_binding,
        righttextcolor: Int = R.color.color_0473FF
    ) {

        mBinding.mineBind.visibility = visible
        mBinding.mineBind.setRightImageIsShow(isshow)
        mBinding.mineBind.setText(text)
        mBinding.mineBind.setRightText(righttext, righttextcolor)
        mBinding.mineBind.setLeftImageResource(leftim)
        if (visible == View.VISIBLE) {
            mBinding.mineItemLi.setBackgroundResource(R.mipmap.im_bg_mine_item_2line)
            if (TextUtils.equals("取消认证", righttext)) {
                mBinding.mineBind.setOnClickListener {
                    MyDialogUtils.showCancleBindDialog {
//                    取消认证
                        toUnBind()
                    }
                }
            } else {
                mBinding.mineBind.setOnClickListener(this)

            }
        } else
            mBinding.mineItemLi.setBackgroundResource(R.mipmap.im_bg_mine_item)

    }

    override fun onClick(view: View?) {
        when (usertype) {
            USER_TYPE0 -> {
                startActivity<LoginActivity>()
                return
            }
            else -> {}
        }

        when (view?.id) {
            R.id.mine_housekeeper -> {
//                专属管家
                activity?.clickBindState {
                    Bundle().let {
                        it.putCharSequence("url", ConstantObject.HOUSE_KEEPER_URL)
                        it.putCharSequence("token", activity?.getValue("token", ""))
                        it.putCharSequence("title", "专属管家")
                        startActivity<WebActivityNew>(it)
                    }
                }

            }
            R.id.mine_set -> {
                startActivity<MySetActivity>()
            }
            R.id.mine_headim -> {
                startActivity<MyInfoActivity>()
            }
            R.id.mine_company_change, R.id.mine_switch -> {
//                切换公司
                startActivity<SwitchCompanyActivity>()
            }
            R.id.mine_bind -> {
                startActivity<CompanyBindActivity>()
            }
            R.id.mine_pay -> {
                //                交易记录
                activity?.clickBindState {
                    Bundle().let {
                        it.putCharSequence("url", ConstantObject.PAY_URL)
                        it.putCharSequence("token", activity?.getValue("token", ""))
                        it.putCharSequence("title", "交易记录")
                        startActivity<WebActivityNew>(it)
                    }
                }
            }
            R.id.mine_tel_layer -> {
                MyDialogUtils.showPhoneDialog(ConstantObject.Customer_service_PHONE)
            }
            else -> {}
        }

    }

    private fun toUnBind() {

        launchWithLoadingNoBase({
            activity?.let {
                mViewModel.toUnBind(
                    it.getValue("token", ""),
                    it.getValue("bp", ""),
                    it.getValue("username", "")
                )
            }
        }) {
            onSuccess = {
                when (it?.body?.resultCode) {
                    ConstantObject.Result_Code_Sucess -> {
                        lifecycleScope.launch {
                            mViewModel.toRefreshInfo()
                        }
                    }
                    else -> {
                        toast("取消认证失败:${it?.body?.resultDesc}")
                    }
                }
            }
        }
    }


}