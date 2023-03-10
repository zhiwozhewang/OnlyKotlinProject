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
            LogUtils.e("??????????????????")
            initView()
        }
        FlowEventBus.observe<Event.SaveInfoBean<ToUpdateInfoBean>>(this) { it ->
            LogUtils.e("???????????????????????????")
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
        //????????????

        usertype = activity?.getValue("usertype", USER_TYPE0).toString()
        mBinding.mineSwitch.visibility = View.GONE

        when (usertype) {
            USER_TYPE0 -> {
//                ?????????
                mBinding.mineName.text = "?????????"
                mBinding.mineTip.visibility = View.GONE
                mBinding.mineTouristStatus.visibility = View.GONE
                mBinding.mineScan.visibility = View.GONE
                mBinding.mineHeadimRim.visibility = View.GONE
                mBinding.mineCompanyLi.visibility = View.GONE
                mBinding.mineHeadim.setImageResource(R.mipmap.im_mine_head)
                initBindView(View.VISIBLE, true, "??????", "", R.mipmap.im_mine_binding)

            }
            USER_TYPE1 -> {
//                1??????????????????
                mBinding.mineScan.visibility = View.VISIBLE
                //        ????????????
                mBinding.mineScan.setOnClickListener {
                    startActivity<QRCodeScanningActivity>()
                }
                initUserNotTourist()
            }
            USER_TYPE2 -> {
//               2???????????????
                mBinding.mineScan.visibility = View.GONE
                initUserNotTourist()
            }
            USER_TYPE3 -> {
//                3?????????
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
        //??????????????????????????????
        bindstate = activity?.getValue("bindstate", USER_BIND_STATUS_LOGIN_NOBIND)!!
        when (bindstate) {
            USER_BIND_STATUS_LOGIN_NOBIND -> {
                initNameAndPhone()
                initTouristStatus(bindstate)
                initBindView(
                    View.VISIBLE,
                    true,
                    "??????",
                    "3??????????????????????????????",
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
                    "???????????????",
                    "????????????",
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
                mBinding.mineTouristStatus.text = "?????????"
                mBinding.mineTouristStatus.setOnClickListener {
                    startActivity<CompanyBindActivity>()
                }
            }
            USER_BIND_STATUS_LOGIN_BINDING -> {
                mBinding.mineTouristStatus.text = "???????????????"
                mBinding.mineTouristStatus.setOnClickListener {
                    //?????????????????????
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
     * ??????????????????????????????
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
//        ???????????????
        val job = job?.let { subingJob(it) }
        val phone = phone?.let { CommonUtil.hidePhone(it) }
        mBinding.mineTip.text = if (!TextUtils.isEmpty(job)) "$job | $phone" else "$phone"
//  ??????
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
            if (TextUtils.equals("????????????", righttext)) {
                mBinding.mineBind.setOnClickListener {
                    MyDialogUtils.showCancleBindDialog {
//                    ????????????
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
//                ????????????
                activity?.clickBindState {
                    Bundle().let {
                        it.putCharSequence("url", ConstantObject.HOUSE_KEEPER_URL)
                        it.putCharSequence("token", activity?.getValue("token", ""))
                        it.putCharSequence("title", "????????????")
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
//                ????????????
                startActivity<SwitchCompanyActivity>()
            }
            R.id.mine_bind -> {
                startActivity<CompanyBindActivity>()
            }
            R.id.mine_pay -> {
                //                ????????????
                activity?.clickBindState {
                    Bundle().let {
                        it.putCharSequence("url", ConstantObject.PAY_URL)
                        it.putCharSequence("token", activity?.getValue("token", ""))
                        it.putCharSequence("title", "????????????")
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
                        toast("??????????????????:${it?.body?.resultDesc}")
                    }
                }
            }
        }
    }


}