package com.tare.mechat.messages

import java.lang.Exception
import java.security.spec.KeySpec
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class EncryptMessages {
    fun encrypt(text: ByteArray, key: SecretKey, IV: ByteArray): ByteArray {
        val keySpec = SecretKeySpec(key.encoded, "AES")
        val ivSpec = IvParameterSpec(IV)
        Cipher.ENCRYPT_MODE
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
        return cipher.doFinal(text)
    }

    fun decrypt(cipherText : ByteArray, key :SecretKey, IV:ByteArray) : String{
        try {
            val cipher = Cipher.getInstance("AES")
            val keySpec = SecretKeySpec(key.encoded, "AES")
            val ivSpec = IvParameterSpec(IV)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
            val decrypted = cipher.doFinal(cipherText)
            return String(decrypted)
        }catch (e : Exception)
        {
            e.printStackTrace()
        }
        return ""
    }
}