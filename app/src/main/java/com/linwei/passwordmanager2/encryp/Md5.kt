package com.linwei.passwordmanager2.encryp

import java.security.MessageDigest

/**
 * @Author linwwei
 * @Date  2021/8/6 16:02
 * @Version 1.0
 **/
object Md5 {

    fun MD5(str: String): String? {
        val hexDigits = charArrayOf( // 用来将字节转换成 16 进制表示的字符
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f'
        )
        return try {
            val strTemp = str.toByteArray()
            val mdTemp = MessageDigest.getInstance("MD5")
            mdTemp.update(strTemp)
            val tmp = mdTemp.digest() // MD5 的计算结果是一个 128 位的长整数，
            // 用字节表示就是 16 个字节
            val strs = CharArray(16 * 2) // 每个字节用 16 进制表示的话，使用两个字符，
            // 所以表示成 16 进制需要 32 个字符
            var k = 0 // 表示转换结果中对应的字符位置
            for (i in 0..15) { // 从第一个字节开始，对 MD5 的每一个字节
                // 转换成 16 进制字符的转换
                val byte0 = tmp[i] // 取第 i 个字节
                strs[k++] = hexDigits[byte0.toInt() ushr 4 and 0xf] // 取字节中高 4 位的数字转换,
                // >>> 为逻辑右移，将符号位一起右移
                strs[k++] = hexDigits[byte0.toInt() and 0xf] // 取字节中低 4 位的数字转换
            }
            String(strs).substring(8,24) // 16位换后的结果转换为字符串.toUpperCase()
        } catch (e: Exception) {
            null
        }
    }
}