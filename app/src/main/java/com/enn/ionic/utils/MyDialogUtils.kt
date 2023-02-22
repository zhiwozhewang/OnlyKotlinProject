package com.enn.ionic.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import com.enn.base.base.BaseApp
import com.enn.base.base.IUiView
import com.enn.base.data.getValue
import com.enn.base.ktx.getSelfVersionName
import com.enn.base.ktx.startActivity
import com.enn.base.ktx.toCall
import com.enn.ionic.ConstantObject
import com.enn.ionic.R
import com.enn.ionic.bean.UpdateResultBean
import com.enn.ionic.bean.webbean.WebFileBean
import com.enn.ionic.databinding.*
import com.enn.ionic.download.toDownLoad
import com.enn.ionic.ui.activity.WebActivityNew
import com.enn.network.utils.LogUtils
import com.kongzue.dialogx.dialogs.CustomDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import com.kongzue.dialogx.interfaces.OnBindView
import java.io.File

object MyDialogUtils {

    fun showBindDialog(finish: () -> Unit) {


        CustomDialog.show(object : OnBindView<CustomDialog>(R.layout.dialog_company_bind_result) {
            override fun onBind(dialog: CustomDialog, v: View) {
                val dialogCompanyBindResultBinding = DialogCompanyBindResultBinding.bind(v)
                dialogCompanyBindResultBinding.dialogBindSure.setOnClickListener { finish() }
                dialogCompanyBindResultBinding.dialogBindCancle.setOnClickListener { dialog?.dismiss() }
            }
        }).setMaskColor(Color.parseColor("#A6000000"))


    }

    fun showLoginoutDialog(loginout: () -> Unit) {


        CustomDialog.show(object : OnBindView<CustomDialog>(R.layout.dialog_login_out) {
            override fun onBind(dialog: CustomDialog, v: View) {
                val dialogLoginOutBinding = DialogLoginOutBinding.bind(v)
                dialogLoginOutBinding.dialogLoginoutSure.setOnClickListener { loginout() }
                dialogLoginOutBinding.dialogLoginoutCancle.setOnClickListener { dialog?.dismiss() }
            }
        }).setMaskColor(Color.parseColor("#A6000000"))


    }

    fun showBindCheckingDialog() {

        CustomDialog.show(object : OnBindView<CustomDialog>(R.layout.dialog_company_isbinding) {
            override fun onBind(dialog: CustomDialog, v: View) {
                val dialogCompanyBindResultBinding = DialogCompanyIsbindingBinding.bind(v)
                val checkphone = BaseApp.instance.getValue("checkphone", "")
                dialogCompanyBindResultBinding.dialogBindingContent.text =
                    "该企业已绑定主账号，请等待主账号审核：$checkphone"
                dialogCompanyBindResultBinding.dialogBindingSure.setOnClickListener {
                    showPhoneDialog(checkphone)
                    dialog.dismiss()
                }
                dialogCompanyBindResultBinding.dialogBindingCancle.setOnClickListener { dialog?.dismiss() }
            }
        }).setMaskColor(Color.parseColor("#A6000000"))

    }

    fun showPhoneDialog(phone: String) {
        CustomDialog.show(object :
            OnBindView<CustomDialog>(R.layout.dialog_company_isbinding_tophone) {
            override fun onBind(dialog: CustomDialog, v: View) {
                val dialogCompanyBindResultBinding = DialogCompanyIsbindingTophoneBinding.bind(v)

                dialogCompanyBindResultBinding.dialogTophoneContent.text = "呼叫 $phone"
                dialogCompanyBindResultBinding.dialogTophoneView.setOnClickListener {
                    dialog.dismiss()
                }
                dialogCompanyBindResultBinding.dialogTophoneView2.setOnClickListener {
                    //打电话
                    v.context.toCall(phone)
                    dialog.dismiss()
                }
            }
        }).setAlign(CustomDialog.ALIGN.BOTTOM).setMaskColor(Color.parseColor("#A6000000"))

    }

    fun showLoginBottomDialog(toAgree: () -> Unit, toWeb: () -> Unit) {
        CustomDialog.show(object :
            OnBindView<CustomDialog>(R.layout.layout_login_agreement) {
            override fun onBind(dialog: CustomDialog, v: View) {
                val layoutLoginAgreementBinding = LayoutLoginAgreementBinding.bind(v)

                layoutLoginAgreementBinding.loginAgreementClose.setOnClickListener { dialog?.dismiss() }
                layoutLoginAgreementBinding.loginAgreementBtn.setOnClickListener {
                    //   同意
                    toAgree()
                    dialog.dismiss()
                }
                layoutLoginAgreementBinding.loginAgreementTip.setOnClickListener {
                    toWeb()
                }
            }
        }).setAlign(CustomDialog.ALIGN.BOTTOM).setMaskColor(Color.parseColor("#A6000000"))

    }

    inline fun showCancleBindDialog(crossinline toCancleBind: () -> Unit) {

        CustomDialog.show(object : OnBindView<CustomDialog>(R.layout.dialog_company_cancle_bind) {
            override fun onBind(dialog: CustomDialog, v: View) {
                val dialogCompanyBindResultBinding = DialogCompanyCancleBindBinding.bind(v)
                dialogCompanyBindResultBinding.dialogCancleBindSure.setOnClickListener {
                    toCancleBind()
                    dialog.dismiss()
                }
                dialogCompanyBindResultBinding.dialogCancleBindCancle.setOnClickListener { dialog.dismiss() }
            }
        }).setMaskColor(Color.parseColor("#A6000000"))

    }

    fun showPicDialog(toTakePic: (Int) -> Unit) {

        CustomDialog.show(object : OnBindView<CustomDialog>(R.layout.dialog_pic) {
            override fun onBind(dialog: CustomDialog, v: View) {
                val dialogPicBinding = DialogPicBinding.bind(v)
                dialogPicBinding.dialogPicTakepic.setOnClickListener {
                    toTakePic(0)
                    dialog.dismiss()
                }
                dialogPicBinding.dialogPicLocal.setOnClickListener {
                    toTakePic(1)
                    dialog.dismiss()
                }
                dialogPicBinding.dialogPicCancle.setOnClickListener { dialog.dismiss() }

            }
        }).setMaskColor(Color.parseColor("#A6000000"))

    }

    fun showTransferCheckDialog(tocheck: () -> Unit) {

        CustomDialog.show(object : OnBindView<CustomDialog>(R.layout.dialog_transfer_check) {
            override fun onBind(dialog: CustomDialog, v: View) {
                val dialogCompanyBindResultBinding = DialogTransferCheckBinding.bind(v)
                dialogCompanyBindResultBinding.transferCheckSure.setOnClickListener {
                    tocheck()
                    dialog.dismiss()
                }
                dialogCompanyBindResultBinding.transferCheckCancle.setOnClickListener { dialog.dismiss() }
            }
        }).setMaskColor(Color.parseColor("#A6000000"))

    }

    fun showUnbingCheckDialog(
        tocheck: () -> Unit,
        iscantounbind: Boolean,
        username: String? = null
    ) {
        CustomDialog.show(object : OnBindView<CustomDialog>(R.layout.dialog_unbind_check) {
            override fun onBind(dialog: CustomDialog, v: View) {
                val dialogUnbindCheckBinding = DialogUnbindCheckBinding.bind(v)
                dialogUnbindCheckBinding.unbindCheckContent.text =
                    if (iscantounbind) "您的注册账号:$username 解除绑定，将无法使用。" else "账号有待审核的申请,\n" +
                            "请审核通过后，并转让主账号身份方可解除绑定"
                dialogUnbindCheckBinding.unbindCheckSure.text = if (iscantounbind) "确定" else "去审核"
                dialogUnbindCheckBinding.unbindCheckSure.setOnClickListener {
                    tocheck()
                    dialog.dismiss()
                }
                dialogUnbindCheckBinding.unbindCheckCancle.setOnClickListener { dialog.dismiss() }
            }
        }).setMaskColor(Color.parseColor("#A6000000"))

    }

    fun showTransferDialog(toname: String?, totransfer: () -> Unit) {

        CustomDialog.show(object : OnBindView<CustomDialog>(R.layout.dialog_transfer_to) {
            override fun onBind(dialog: CustomDialog, v: View) {
                val dialogCompanyBindResultBinding = DialogTransferToBinding.bind(v)
                dialogCompanyBindResultBinding.transferToContent.text =
                    "确认选择 ${if (toname.isNullOrEmpty()) ConstantObject.DEFAULT_NICK_NAME2 else toname} 为新主账号\n" +
                            "你将自动放弃主账号身份"
                dialogCompanyBindResultBinding.transferToSure.setOnClickListener {
                    totransfer()
                    dialog.dismiss()
                }
                dialogCompanyBindResultBinding.transferToCancle.setOnClickListener { dialog.dismiss() }
            }
        }).setMaskColor(Color.parseColor("#A6000000"))

    }

    fun showDeleteDialog(cancleDelete: (() -> Unit?)? = null, toDelete: () -> Unit) {

        CustomDialog.show(object : OnBindView<CustomDialog>(R.layout.dialog_is_delete) {
            override fun onBind(dialog: CustomDialog, v: View) {
                val dialogIsDeleteBinding = DialogIsDeleteBinding.bind(v)

                dialogIsDeleteBinding.deleteSure.setOnClickListener {
                    toDelete()
                    dialog.dismiss()
                }
                dialogIsDeleteBinding.deleteCancle.setOnClickListener {
                    cancleDelete?.invoke()
                    dialog.dismiss()
                }
            }
        }).setMaskColor(Color.parseColor("#A6000000"))

    }

    fun showUpdateDialog(context: Context, updateResultBean: UpdateResultBean?) {
        updateResultBean?.let {
            if ("00" == updateResultBean.code) {
                updateResultBean.result?.let { result ->
                    if (context.getSelfVersionName().replace(".", "").toLong() <
                        (result.num?.replace(".", "")?.toLong() ?: -1)
                    ) {
                        val isForce = "1" == updateResultBean.result?.forcedUpdate
                        CustomDialog.build()
                            .setCustomView(object :
                                OnBindView<CustomDialog>(R.layout.layout_update_dialog) {
                                override fun onBind(dialog: CustomDialog, v: View) {
                                    val updateBinding = LayoutUpdateDialogBinding.bind(v)
                                    updateBinding.imgClose.visibility = View.GONE
                                    updateBinding.tvVersion.text =
                                        "V${updateResultBean.result?.num}"
                                    updateBinding.tvRemark.text = updateResultBean.result?.remark
                                    updateBinding.imgClose.setOnClickListener {
                                        dialog.dismiss()
                                    }
                                    updateBinding.tvUpdate.setOnClickListener {
                                        //下载
                                        result.address?.let { url ->
                                            (context as IUiView).toDownLoad<WebFileBean>(
                                                url,
                                                context.cacheDir,
                                                "${File(url).name}.apk"
                                            ) {
                                                onError = {
                                                    LogUtils.d("下载失败")
                                                }
                                                onSuccess = { file ->
                                                    if (!isForce) {
                                                        WaitDialog.dismiss()
                                                    } else {
                                                        WaitDialog.show("正在安装...")
                                                    }
                                                    CommonUtil.installApk(file, context as Activity)
                                                }
                                                onInProgress = { it ->
                                                    WaitDialog.show(
                                                        "正在下载${it}%",
                                                        it.toFloat()
                                                    )
                                                    LogUtils.d("下载进度:$it")
                                                }
                                            }
                                        }
                                        dialog.dismiss()
                                    }
                                }
                            }).setMaskColor(context.resources.getColor(R.color.black30))
                            .setCancelable(!isForce).show()
                    }
                }
            }
        }
    }

}
