package com.enn.ionic.bean
class UpdateResultBean {
    var code: String? = null
    var message: String? = null
    var result: ResultDTO? = null

    class ResultDTO {
        var id: Any? = null
        var createId: Any? = null
        var createTime: Any? = null
        var updateId: Any? = null
        var updateTime: Any? = null
        var address: String? = null
        var forcedUpdate: String? = null
        var imgAddress: Any? = null
        var info: Any? = null
        var iosHttpsAddress: String? = null
        var num: String? = null
        var remark: String? = null
        var totalDowns: Any? = null
        var type: Any? = null
        var versionSize: Any? = null
        var workStatus: Any? = null
        var appId: Any? = null
    }
}