package com.enn.ionic

import com.enn.ionic.bean.FirstGridBean

class ConstantObject {

    companion object {

        const val ACS_URL =
            "http://great-gas-h5.ipaas.enncloud.cn/subPages/nfc/intro/intro"
        const val NEWS_URL =
            "http://great-gas-h5.ipaas.enncloud.cn/pages/top-news/news-list/news-list"
        const val MARKET_URL = "http://great-gas-h5.ipaas.enncloud.cn/pages/mall/mall"
        const val HOUSE_KEEPER_URL = "http://great-gas-h5.ipaas.enncloud.cn/subPages/butler/butler"
        const val PAY_URL = "http://great-gas-h5.ipaas.enncloud.cn/pages/fee/fee/fee"
        const val WECHAT_LOGIN_STATE = ""//微信登录自定义字段
        const val WECHAT_APP_ID = "wxdacde0b5969f4641"//微信开发后台申请的app_id
        const val WECHAT_APP_SECRET = "wxdacde0b5969f4641"

        const val PHONE = ""
        const val Customer_service_PHONE = "95158"

        const val UNITLS_MORE_NAME = "更多"
        const val channelType = "Z"
        const val CITY_FIRE_CARD_URL_DEV = "https://customer-workbench-h5.dev.ennew.com/"
        const val CITY_FIRE_CARD_URL_FAT = "https://customer-workbench-h5.fat.ennew.com/"
        const val CITY_FIRE_CARD_URL_PRO = "https://customer-workbench-h5.ennew.com/"
        const val Result_Code_Sucess = "1000"
        const val Result_Code_Need_Bind = "3008"

        const val AgreementUrl =
            "http://great-gas-h5.ipaas.enncloud.cn/pages/login/agreement/agreement"
        const val JSOBJECT = "crmapp"

        //        * 0 未登录 1：用气客户，2：工程客户 3：游客
        const val USER_TYPE0 = "0"
        const val USER_TYPE1 = "1"
        const val USER_TYPE2 = "2"
        const val USER_TYPE3 = "3"
        const val MESSAGE_URL =
            "http://great-gas-h5.ipaas.enncloud.cn/subPages/my-message/list/list"


        lateinit var GAS_PLAN_URL: String
        lateinit var GAS_CHECK_URL: String
        lateinit var SUCESS_CALLBACK_METHOD: String
        lateinit var FAILD_CALLBACK_METHOD: String

        //        用气计划id
        const val GAS_PLAN_ID = 10799

        //        更多模块id
        const val MORE_ID = -1

        //       充值缴费id
        const val PAY_ID = 20287

        //        工程进度id
        const val PROJECT_PROGRESS_ID = 10798

        //        用气分析id  不再区分东莞用户
        const val GAS_CHECK_ID = 20288

        //       游客的绑定状态
//        const val USER_BIND_STATUS_NOLOGIN = 0
        const val USER_BIND_STATUS_LOGIN_NOBIND = 1
        const val USER_BIND_STATUS_LOGIN_BINDING = 2
        const val USER_BIND_STATUS_LOGIN_BIND = 3

        //        默认昵称
        const val DEFAULT_NICK_NAME = "游客"
        const val DEFAULT_BIAOJI_URL =
            "http://great-gas-h5.ipaas.enncloud.cn/pages/home/meter-list/meter-list"
        const val DEFAULT_TODO_URL =
            "http://great-gas-h5.ipaas.enncloud.cn/pages/need-dealt-with/need-dealt-with-list?bp="


        //
        const val DEFAULT_NICK_NAME2 = "未录昵称"
        const val TO_DO = "待处理"
        const val HAS_DO = "已完成"

        //        表记背景
        const val GAS_BLUE_BG =
            "https://lfrz1.stor.enncloud.cn/enn-oss/account2688/blue95299.png"
        const val GAS_ORANGE_BG =
            "https://lfrz1.stor.enncloud.cn/enn-oss/account2688/orange35864.png"

        const val GAS_RED_BG =
            "https://lfrz1.stor.enncloud.cn/enn-oss/account2688/red75011.png"

        const val PAY_TIME_LIMIT = "每天23:00-24:00系统维护时间，无法缴费"


        private val arrayList = arrayListOf<FirstGridBean>()

        fun getGridList(): ArrayList<FirstGridBean> {

            if (arrayList.isNotEmpty()) {
                return arrayList
            }

//            val namearray: Array<String> =
//                arrayOf("用气分析", "用气计划", "用气账单", "充值消费", "用气跟踪", "安全报告", "碳排计算器", "更多")

            val names: Array<String> =
                arrayOf("工程进度", "用气计划", "电子发票", "充值缴费", "东莞用能", "其他用能", "客户服务")
            val urls: Array<String> =
                arrayOf(
                    "https://customer-workbench-h5.dev.ennew.com/",
                    "http://great-gas-h5.ipaas.enncloud.cn/",
                    "http://great-gas-h5.ipaas.enncloud.cn/pages/predict-gas/predict-gas-report/predict-gas-report",
                    "http://great-gas-h5.ipaas.enncloud.cn/pages/invoice/trade-list/trade-list",
                    "http://great-gas-h5.ipaas.enncloud.cn/pages/pay/meters/meters",
                    "http://great-gas-h5.ipaas.enncloud.cn/pages/dg-manager-energy/dg-manager-energy",
                    "http://great-gas-h5.ipaas.enncloud.cn/pages/manager-energy/manager-energy-info/manager-energy-info?id=0002364661",
                    "http://great-gas-h5.ipaas.enncloud.cn/subPages/customer-service/index/index"
                )

            for (index in 0..6) {
                arrayList.add(FirstGridBean(names[index], urls[index], ""))
            }

            return arrayList
        }
    }
}