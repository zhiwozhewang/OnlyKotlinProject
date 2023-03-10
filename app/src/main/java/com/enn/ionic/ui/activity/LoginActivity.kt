package com.enn.ionic.ui.activity

import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import androidx.lifecycle.lifecycleScope
import com.dylanc.viewbinding.binding
import com.enn.base.base.BaseActivity
import com.enn.base.eventbus.Event
import com.enn.base.eventbus.FlowEventBus
import com.enn.base.ktx.startActivity
import com.enn.base.util.*
import com.enn.base.util.statusbar.StatusBarCompat
import com.enn.ionic.ConstantObject
import com.enn.ionic.ConstantObject.Companion.Result_Code_Need_Bind
import com.enn.ionic.ConstantObject.Companion.Result_Code_Sucess
import com.enn.ionic.R
import com.enn.ionic.bean.ToLoginBean
import com.enn.ionic.databinding.FragmentLoginBinding
import com.enn.ionic.databinding.LayoutLoginAgreementBinding
import com.enn.ionic.databinding.VsLoginPwBinding
import com.enn.ionic.utils.*
import com.enn.ionic.vm.LoginViewModel
import com.enn.network.toast
import com.kongzue.dialogx.dialogs.BottomDialog
import com.kongzue.dialogx.interfaces.OnBindView
import com.loc.et
import kotlinx.coroutines.launch
import java.util.regex.Matcher
import java.util.regex.Pattern


class LoginActivity : BaseActivity(R.layout.fragment_login) {

    private val mViewModel by viewModels<LoginViewModel>()
    private val mBinding: FragmentLoginBinding by binding()
    private lateinit var vsLoginPwBinding: VsLoginPwBinding
    private var vsLoginPwView: View? = null
    private lateinit var layoutLoginAgreementBinding: LayoutLoginAgreementBinding
//    private lateinit var bottomDialog: BottomDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        StatusBarCompat.steepStatusBarText(this)

//        ???????????????
        mBinding.loginCodeIc.loginBtn.setOnClickListener {
            login(
                phone = mBinding.loginCodeIc.loginPhone.text.toString(),
                code = mBinding.loginCodeIc.loginCode.text.toString()
            )
        }
//        ???????????????
        mBinding.loginCodeIc.loginPhoneCodeBtn.setOnClickListener {
//            ???????????????
            sendCode(mBinding.loginCodeIc.loginPhone.text.toString(), it as TextView)

        }
        mBinding.loginCodeIc.loginPhone.seConfiguration(mBinding.loginCodeIc.loginPhoneDelete)

/*        //      ????????????
        mBinding.loginCodeIc.loginPhone.apply {
            addTextChangedListener(FilterSpaceTextWatcher(this) {
//                ????????????????????????
                mBinding.loginCodeIc.loginPhoneCodeDelete.visibility =
                    if (it?.isEmpty() == true) View.GONE else View.VISIBLE
            })
        }
        //        ?????????????????????
        mBinding.loginCodeIc.loginPhoneCodeDelete.setOnClickListener {
            mBinding.loginCodeIc.loginPhone.text = null
            it.visibility = View.GONE
        }*/

        mBinding.loginCodeIc.loginCode.seConfiguration(mBinding.loginCodeIc.loginPhoneCodeDelete)
        /*       //        ????????????
               mBinding.loginCodeIc.loginCode.apply {
                   addTextChangedListener(FilterSpaceTextWatcher(this))
               }*/
//        ??????????????????
        mBinding.loginCodeIc.loginOther.setOnClickListener { toPwlogin() }
        //????????????
        mBinding.loginAgreement.setOnClickListener {
            startActivity<WebActivityNew>(Bundle().also {
                it.putString(
                    "url",
                    ConstantObject.AgreementUrl
                )
            })
        }
        mBinding.loginBack.setOnClickListener { finish() }
        mBinding.loginWechatIm.setOnClickListener {
//            ????????????
            loginWx()
        }

        showBottomDialog()

        initAgreeText()

        FlowEventBus.observe<Event.ShowWxLoginCode>(this) { it ->
            //   ????????????
            toLogin(wxcode = it.code, type = 2)
        }
    }

    private fun initAgreeText() {
        val agreetext = "?????????????????????<font color = '#0473FF'>??????????????????APP?????????????????????????????????????????????????????????????????????</font>";
        val charSequence: CharSequence
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            charSequence = Html.fromHtml(agreetext, Html.FROM_HTML_MODE_LEGACY)
        } else {
            charSequence = Html.fromHtml(agreetext)
        }
        mBinding.loginAgreement.text = charSequence
    }

    /**
     * ??????????????????
     */
    private fun toPwlogin() {

        if (vsLoginPwView == null) {
            mBinding.loginPwVs.apply {
                vsLoginPwView = inflate()
                vsLoginPwView?.let { initVsPwBind(it) }
            }
        }
        mBinding.loginTitle.text = "??????????????????"
        mBinding.loginTitleTip.text = "?????????????????????????????????"
        vsLoginPwBinding.loginPhonePwVsLayout.visibility = View.VISIBLE
        mBinding.loginCodeIc.icLoginCodeLayout.visibility = View.GONE
    }

    /**
     * ???????????????????????????
     */
    private fun initVsPwBind(vsLoginPwView: View) {
        vsLoginPwBinding = VsLoginPwBinding.bind(vsLoginPwView)
/*        //        ?????????????????????      ????????????
        vsLoginPwBinding.loginPhoneVs.apply {
            addTextChangedListener(FilterSpaceTextWatcher(this) {
                //            ????????????????????????
                vsLoginPwBinding.loginPhonePwDelete.visibility =
                    if (text?.isEmpty() == true) View.GONE else View.VISIBLE
            })
        }
        //        ?????????????????????
        vsLoginPwBinding.loginPhonePwDelete.setOnClickListener {
            vsLoginPwBinding.loginPhoneVs.text = null
            it.visibility = View.GONE
        }*/
        vsLoginPwBinding.loginPhoneVs.seConfiguration(vsLoginPwBinding.loginPhonePwDelete)

//        ????????????
        vsLoginPwBinding.loginBtnPw.setOnClickListener {
            login(
                phone = vsLoginPwBinding.loginPhoneVs.text.toString(),
                pw = vsLoginPwBinding.loginPw.text.toString(), type = 1
            )
        }
//        ?????????????????????
        vsLoginPwBinding.loginOtherPwTocode.setOnClickListener {
            mBinding.loginTitle.text = "????????????"
            mBinding.loginTitleTip.text = "??????????????????????????????????????????????????????"
            vsLoginPwBinding.loginPhonePwVsLayout.visibility = View.GONE
            mBinding.loginCodeIc.icLoginCodeLayout.visibility = View.VISIBLE
        }
//        ??????????????????
        vsLoginPwBinding.loginOtherPwForget.setOnClickListener { startActivity<FindPwActivity>() }


        vsLoginPwBinding.loginPw.seConfiguration(
            vsLoginPwBinding.loginPwDelete,
            vsLoginPwBinding.loginPwIshide
        )
/*//        ????????????
        vsLoginPwBinding.loginPw.apply {
            addTextChangedListener(FilterSpaceTextWatcher(this))
        }*/
/*//        ??????????????????
        vsLoginPwBinding.loginPw.addTextChangedListener { text: Editable? ->


            vsLoginPwBinding.loginPwDelete.visibility =
                if (text?.isEmpty() == true) View.GONE else View.VISIBLE
            vsLoginPwBinding.loginPwIshide.visibility =
                if (text?.isEmpty() == true) View.GONE else View.VISIBLE
        }
        vsLoginPwBinding.loginPwDelete.setOnClickListener {
            vsLoginPwBinding.loginPw.text = null
            it.visibility = View.GONE
        }
        vsLoginPwBinding.loginPwIshide.setOnCheckedChangeListener { compoundButton, b ->
            if (b) {
//                ????????????
                vsLoginPwBinding.loginPw.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else
//                ???????????????
                vsLoginPwBinding.loginPw.transformationMethod =
                    PasswordTransformationMethod.getInstance()
        }*/

    }

    private fun showBottomDialog() {

        MyDialogUtils.showLoginBottomDialog({
            mBinding.loginAgreementCheck.isChecked = true
        }, {
            startActivity<WebActivityNew>(
                Bundle().also { it.putString("url", ConstantObject.AgreementUrl) })
        })
    }

    /**
     * ????????? ?????? ??????
     */
    private fun login(phone: String, code: String? = null, pw: String? = null, type: Int = 0) {

        if (type == 0 && (TextUtils.isEmpty(phone) || TextUtils.isEmpty(code))) {
            toast("?????????/?????????????????????")
            return
        }
//        if (type == 0) {
//
//            toast("??????????????????")
//            return
//        }
        if (type == 1 && (TextUtils.isEmpty(phone) || TextUtils.isEmpty(pw))) {
            toast("?????????/??????????????????")
            return
        }
        if (!mBinding.loginAgreementCheck.isChecked) {
//            toast("???????????????????????????")
            showBottomDialog()
            return
        }
        if (!checkPhoneNum(phone)) {
            toast("??????????????????????????????")
            return
        }
        code?.let {
            if (!checkPwd(it, 6, 6, 1)) {
                toast("??????????????????????????????")
                return@login
            }
        }
        pw?.let {
            if (!checkPwd(it, 6, 20, 1)) {
                toast("???????????????????????????")
                return@login
            }
        }


        toLogin(phone, pw, code, null, type)

    }

    private fun toLogin(
        phone: String? = null,
        pw: String? = null,
        code: String? = null,
        wxcode: String? = null,
        type: Int
    ) {
        launchWithLoadingNoBase({
            when (type) {
                0 -> {
//                    ???????????????
                    mViewModel.loginByCode(ToLoginBean(phone, null, code))
                }
                1 -> {
//                    ????????????
                    mViewModel.login(ToLoginBean(phone, pw, null))
                }
                else -> {
//                    ????????????
                    mViewModel.toWechatLogin(wxcode)
                }
            }
        }) {
            onSuccess = {

                when (it?.body?.resultCode) {
                    Result_Code_Sucess -> {
                        mViewModel.saveLoginData(it)
                        lifecycleScope.launch {
                            mViewModel.refreshInfo()
                            toast("????????????")
                            finish()
                        }
//                    fixme   ????????????    ??????????????????

//                        it?.body?.data?.token?.let { it1 -> toGetUserType(it1) }
                    }
                    Result_Code_Need_Bind -> {
                        //?????????
                        startActivity<BindPhoneActivity>()
                        finish()
                    }
                    else -> {
                        toast("${it?.body?.resultDesc}")
                    }
                }
            }
        }


    }

    private fun toGetUserType(token: String) {
        launchNoLoadingNoBase({
            mViewModel.getUserType(token)
        }) {
            onSuccess = {
                if (TextUtils.equals(Result_Code_Sucess, it?.body?.resultCode)) {
                    toast("????????????")
                    mViewModel.saveUserType(it?.body?.data)
                    finish()
                } else
                    toast("${it?.body?.resultDesc}")
            }
        }

    }

    private fun sendCode(phone: String, view: TextView) {
        if (TextUtils.isEmpty(phone)) {
            toast("??????????????????")
            return
        }
        if (!checkPhoneNum(phone)) {
            toast("??????????????????????????????")
            return
        }
        view.isClickable = false
        launchWithLoadingNoBase({ mViewModel.sendCode(phone) }) {
            onSuccess = {
                if (TextUtils.equals(Result_Code_Sucess, it?.body?.resultCode)) {
                    toast("????????????")
                    countDownCoroutines(60, { i: Int ->
                        view.text = "????????????($i)"
                    }, {
                        view.isClickable = true
                        view.text = "???????????????"
                    })
                } else {
                    view.isClickable = true
                    toast("????????????:${it?.body?.resultDesc}")
                }
            }

        }
    }
}