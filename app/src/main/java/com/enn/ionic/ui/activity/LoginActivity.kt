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

//        验证码登录
        mBinding.loginCodeIc.loginBtn.setOnClickListener {
            login(
                phone = mBinding.loginCodeIc.loginPhone.text.toString(),
                code = mBinding.loginCodeIc.loginCode.text.toString()
            )
        }
//        获取验证码
        mBinding.loginCodeIc.loginPhoneCodeBtn.setOnClickListener {
//            发送验证码
            sendCode(mBinding.loginCodeIc.loginPhone.text.toString(), it as TextView)

        }
        mBinding.loginCodeIc.loginPhone.seConfiguration(mBinding.loginCodeIc.loginPhoneDelete)

/*        //      过滤空格
        mBinding.loginCodeIc.loginPhone.apply {
            addTextChangedListener(FilterSpaceTextWatcher(this) {
//                隐藏显示清除按钮
                mBinding.loginCodeIc.loginPhoneCodeDelete.visibility =
                    if (it?.isEmpty() == true) View.GONE else View.VISIBLE
            })
        }
        //        清除手机号输入
        mBinding.loginCodeIc.loginPhoneCodeDelete.setOnClickListener {
            mBinding.loginCodeIc.loginPhone.text = null
            it.visibility = View.GONE
        }*/

        mBinding.loginCodeIc.loginCode.seConfiguration(mBinding.loginCodeIc.loginPhoneCodeDelete)
        /*       //        过滤空格
               mBinding.loginCodeIc.loginCode.apply {
                   addTextChangedListener(FilterSpaceTextWatcher(this))
               }*/
//        跳转密码登录
        mBinding.loginCodeIc.loginOther.setOnClickListener { toPwlogin() }
        //隐私协议
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
//            微信登录
            loginWx()
        }

        showBottomDialog()

        initAgreeText()

        FlowEventBus.observe<Event.ShowWxLoginCode>(this) { it ->
            //   微信登录
            toLogin(wxcode = it.code, type = 2)
        }
    }

    private fun initAgreeText() {
        val agreetext = "我已阅读并同意<font color = '#0473FF'>《新奥慧用能APP用户使用协议》、《隐私政策》、《认证服务条款》</font>";
        val charSequence: CharSequence
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            charSequence = Html.fromHtml(agreetext, Html.FROM_HTML_MODE_LEGACY)
        } else {
            charSequence = Html.fromHtml(agreetext)
        }
        mBinding.loginAgreement.text = charSequence
    }

    /**
     * 跳转密码登录
     */
    private fun toPwlogin() {

        if (vsLoginPwView == null) {
            mBinding.loginPwVs.apply {
                vsLoginPwView = inflate()
                vsLoginPwView?.let { initVsPwBind(it) }
            }
        }
        mBinding.loginTitle.text = "账号密码登录"
        mBinding.loginTitleTip.text = "使用已有账号手机号登录"
        vsLoginPwBinding.loginPhonePwVsLayout.visibility = View.VISIBLE
        mBinding.loginCodeIc.icLoginCodeLayout.visibility = View.GONE
    }

    /**
     * 初始化密码登录页面
     */
    private fun initVsPwBind(vsLoginPwView: View) {
        vsLoginPwBinding = VsLoginPwBinding.bind(vsLoginPwView)
/*        //        监听手机号输入      过滤空格
        vsLoginPwBinding.loginPhoneVs.apply {
            addTextChangedListener(FilterSpaceTextWatcher(this) {
                //            隐藏显示清除按钮
                vsLoginPwBinding.loginPhonePwDelete.visibility =
                    if (text?.isEmpty() == true) View.GONE else View.VISIBLE
            })
        }
        //        清除手机号输入
        vsLoginPwBinding.loginPhonePwDelete.setOnClickListener {
            vsLoginPwBinding.loginPhoneVs.text = null
            it.visibility = View.GONE
        }*/
        vsLoginPwBinding.loginPhoneVs.seConfiguration(vsLoginPwBinding.loginPhonePwDelete)

//        密码登录
        vsLoginPwBinding.loginBtnPw.setOnClickListener {
            login(
                phone = vsLoginPwBinding.loginPhoneVs.text.toString(),
                pw = vsLoginPwBinding.loginPw.text.toString(), type = 1
            )
        }
//        跳转验证码登录
        vsLoginPwBinding.loginOtherPwTocode.setOnClickListener {
            mBinding.loginTitle.text = "短信登录"
            mBinding.loginTitleTip.text = "未注册的手机号登录后将自动创建新账号"
            vsLoginPwBinding.loginPhonePwVsLayout.visibility = View.GONE
            mBinding.loginCodeIc.icLoginCodeLayout.visibility = View.VISIBLE
        }
//        跳转忘记密码
        vsLoginPwBinding.loginOtherPwForget.setOnClickListener { startActivity<FindPwActivity>() }


        vsLoginPwBinding.loginPw.seConfiguration(
            vsLoginPwBinding.loginPwDelete,
            vsLoginPwBinding.loginPwIshide
        )
/*//        过滤空格
        vsLoginPwBinding.loginPw.apply {
            addTextChangedListener(FilterSpaceTextWatcher(this))
        }*/
/*//        监听密码输入
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
//                密码可见
                vsLoginPwBinding.loginPw.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else
//                密码不可见
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
     * 验证码 密码 登录
     */
    private fun login(phone: String, code: String? = null, pw: String? = null, type: Int = 0) {

        if (type == 0 && (TextUtils.isEmpty(phone) || TextUtils.isEmpty(code))) {
            toast("手机号/验证码不能为空")
            return
        }
//        if (type == 0) {
//
//            toast("验证码不匹配")
//            return
//        }
        if (type == 1 && (TextUtils.isEmpty(phone) || TextUtils.isEmpty(pw))) {
            toast("手机号/密码不能为空")
            return
        }
        if (!mBinding.loginAgreementCheck.isChecked) {
//            toast("请勾选同意隐私政策")
            showBottomDialog()
            return
        }
        if (!checkPhoneNum(phone)) {
            toast("请输入正确格式手机号")
            return
        }
        code?.let {
            if (!checkPwd(it, 6, 6, 1)) {
                toast("请输入正确格式验证码")
                return@login
            }
        }
        pw?.let {
            if (!checkPwd(it, 6, 20, 1)) {
                toast("请输入正确格式密码")
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
//                    验证码登录
                    mViewModel.loginByCode(ToLoginBean(phone, null, code))
                }
                1 -> {
//                    密码登录
                    mViewModel.login(ToLoginBean(phone, pw, null))
                }
                else -> {
//                    微信登录
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
                            toast("登录成功")
                            finish()
                        }
//                    fixme   旧有逻辑    获取用户类型

//                        it?.body?.data?.token?.let { it1 -> toGetUserType(it1) }
                    }
                    Result_Code_Need_Bind -> {
                        //去绑定
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
                    toast("登录成功")
                    mViewModel.saveUserType(it?.body?.data)
                    finish()
                } else
                    toast("${it?.body?.resultDesc}")
            }
        }

    }

    private fun sendCode(phone: String, view: TextView) {
        if (TextUtils.isEmpty(phone)) {
            toast("请输入手机号")
            return
        }
        if (!checkPhoneNum(phone)) {
            toast("请输入正确格式手机号")
            return
        }
        view.isClickable = false
        launchWithLoadingNoBase({ mViewModel.sendCode(phone) }) {
            onSuccess = {
                if (TextUtils.equals(Result_Code_Sucess, it?.body?.resultCode)) {
                    toast("发送成功")
                    countDownCoroutines(60, { i: Int ->
                        view.text = "重新发送($i)"
                    }, {
                        view.isClickable = true
                        view.text = "获取验证码"
                    })
                } else {
                    view.isClickable = true
                    toast("发送失败:${it?.body?.resultDesc}")
                }
            }

        }
    }
}