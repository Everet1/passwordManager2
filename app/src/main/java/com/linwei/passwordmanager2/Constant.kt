package com.linwei.passwordmanager2

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import kotlinx.android.synthetic.main.psactivity_hello.*
import javax.crypto.Cipher
import javax.crypto.SecretKey

object Constant {
    const val BASE_URL = "https://ccdcapi.alipay.com"//支付宝查询银行卡所属银行
    const val DEFAULT_KEY_NAME = "test_key"
    const val PASSWORD_ERRO = "密码错误"

    var logStr = ""

    fun ToastUtil(activity: Activity, msg: String, time: Int) {
        Toast.makeText(activity, msg, time).show()
    }
}