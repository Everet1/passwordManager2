package com.linwei.passwordmanager2.ui.activty


import android.content.Context
import android.content.SharedPreferences
import android.hardware.biometrics.BiometricPrompt
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.databinding.DataBindingUtil
import com.linwei.passwordmanager2.Constant
import com.linwei.passwordmanager2.R
import com.linwei.passwordmanager2.base.BaseActivity
import com.linwei.passwordmanager2.bean.TestData
import com.linwei.passwordmanager2.databinding.PsactivityTestBinding
import com.linwei.passwordmanager2.encryp.Aes
import com.linwei.passwordmanager2.encryp.FingerAuthen
import com.linwei.passwordmanager2.encryp.Md5
import kotlinx.android.synthetic.main.psactivity_test.*
import java.lang.Exception
import java.util.*

/**
 * @Author linwwei
 * @Date  2021/8/25 14:03
 * @Version 1.0
 **/
class TestActivity : BaseActivity() {


    val TAG = "helloactivity"
    var skipCount = 5
    var isencrip = true
    var miwen = ""
    var plainPassword = ""
    var Md5Password = ""
    lateinit var perf: SharedPreferences
    lateinit var data: TestData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.psactivity_test)
        val binding =
            DataBindingUtil.setContentView<PsactivityTestBinding>(this, R.layout.psactivity_test)
        data = TestData("1", "like you")
        binding.testdata = data



        perf = getSharedPreferences("data", Context.MODE_PRIVATE)
        initView()
        val password = "123456"

        switch1.isChecked = perf.getBoolean("switch", false)
        switch1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (editPlain.text.toString().isNotEmpty())
                    perf.edit {
                        putBoolean("switch", isChecked)
                    }
                else {

                }

            } else {
                Log.e(TAG, "onCreate: ???")
                perf.edit {
                    putBoolean("switch", isChecked)
                    putString("iv", "")//??????????????????
                    putString("se", "")
                }
            }
        }

    }

    override fun initView() {


        val fingerAuthen = FingerAuthen(this)
        fingerAuthen.setAuthenticationCallback(authenticationCallback)
        encodebtn.setOnClickListener {
            isencrip = true
            plainPassword = editPlain.text.toString()

            if (plainPassword.isNotEmpty()) {
                showText("?????? $plainPassword")
                Md5.MD5(plainPassword)?.let {
                    Md5Password = it
                    showText("???????????????$it")
                    fingerAuthen.fingerPrint_encryption()
                }
            }
        }
        decodebtn.setOnClickListener {
            isencrip = false
            fingerAuthen.fingerPrint_Decryption()
        }

        innerbtn.setOnClickListener {
            isencrip = false
            val AESMD5Key = perf.getString("AESMD5Key", "")
            if (!AESMD5Key.isNullOrEmpty()) {
                showText(AESMD5Key)

                try {
                    val s = Md5.MD5("123456")?.let { it1 -> Aes(it1).decrypt(AESMD5Key) }
                    showText(s!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

//            fingerAuthen.getCipher()?.let {
//                val encodestr = perf.getString("se", "")
//                if (!encodestr.isNullOrEmpty()) {
//                    val decode = it.doFinal(Base64.getDecoder().decode(encodestr))
//                    showText("?????????????????????${String(decode)}")
//                }
//            }
        }

        password_encripbtn.setOnClickListener {

            val customizeDialog = AlertDialog.Builder(this)
            val dialogview = LayoutInflater.from(this).inflate(R.layout.psdialog_check, null)
            customizeDialog.setTitle("???????????????:")
            customizeDialog.setView(dialogview)
            customizeDialog.setCancelable(false)
            val edit = dialogview.findViewById<EditText>(R.id.passedit)
            customizeDialog.setNegativeButton("??????") { dialog, which ->
                dialog.dismiss()
            }
            customizeDialog.setPositiveButton("??????") { dialog, which ->
                Log.e(TAG, "initView: ?????? " + which)
                val pass = edit.text.toString().trim()
                if (!pass.isEmpty())
                    passencription(pass)
                else
                    showText("????????????")

            }

            edit.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {


                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                }

                override fun afterTextChanged(s: Editable?) {
                    showText(s.toString().trim())
                }

            })

            customizeDialog.show()

        }

        button.setOnClickListener {
            Log.e(TAG, "initView: button??????")
            data.id = "555"
            data.name = "4552"
        }
    }

    fun passencription(pass: String) {
        val first_level_pass = Md5.MD5(pass)
        first_level_pass?.let {
            val cipher = FingerAuthen(this).getCipher()
            val encode = cipher.doFinal(it.toByteArray())

            val se = Base64.getEncoder().encodeToString(encode)
            val siv = Base64.getEncoder().encodeToString(cipher.iv)

            perf.edit {
                putString("iv", siv)
                putString("se", se)
                putString("AESMD5Key", Aes(first_level_pass).encryp(first_level_pass))
                showText("onAuthenticationSucceeded: ????????????")
                showText("?????????se:" + perf.getString("se", ""))
            }
        }


    }

    fun showText(str: String) {
        Constant.logStr += "$str\n"
        Logtext.text = Constant.logStr

    }

    override fun onResume() {
        super.onResume()


    }


    private val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            super.onAuthenticationError(errorCode, errString)
            Log.d("TAG", "onAuthenticationError errorCode: $errorCode errString: $errString")
        }

        override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
            super.onAuthenticationHelp(helpCode, helpString)
            Log.d(
                "TAG",
                "onAuthenticationHelp helpCode:" + helpCode + "helpString: " + helpString
            )
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)
            Log.e(TAG, "onAuthenticationSucceeded: ????????????")
            if (isencrip) {
                val cipher = result!!.cryptoObject.cipher
                val encode = cipher.doFinal(Md5Password.toByteArray())

                val se = Base64.getEncoder().encodeToString(encode)
                val siv = Base64.getEncoder().encodeToString(cipher.iv)

                perf.edit {
                    putString("iv", siv)
                    putString("se", se)
                    showText("onAuthenticationSucceeded: ????????????")
                    showText("?????????se:" + perf.getString("se", ""))
                }
            } else {
                val encodestr = perf.getString("se", "")
                if (!encodestr.isNullOrEmpty()) {
                    val cipher = result!!.cryptoObject.cipher
                    val decode = cipher.doFinal(Base64.getDecoder().decode(encodestr))
                    showText("???????????????${String(decode)}")
                }

            }


        }

        override fun onAuthenticationFailed() {
            super.onAuthenticationFailed()
            Log.d("TAG", "????????????")

        }
    }
}