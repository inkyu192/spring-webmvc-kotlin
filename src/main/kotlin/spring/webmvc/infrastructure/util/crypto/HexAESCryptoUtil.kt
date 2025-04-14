package spring.webmvc.infrastructure.util.crypto

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import spring.webmvc.infrastructure.util.hexToBytes
import spring.webmvc.infrastructure.util.toHex
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Component
class HexAESCryptoUtil(
    @Value("\${crypto.secret-key}")
    private val secretKey: String,
    @Value("\${crypto.iv-parameter}")
    private val ivParameter: String,
) : CryptoUtil {
    override fun encrypt(plainText: String): String = runCatching {
        val secretKeySpec = SecretKeySpec(secretKey.toByteArray(StandardCharsets.UTF_8), "AES")
        val ivParameterSpec = IvParameterSpec(ivParameter.toByteArray(StandardCharsets.UTF_8))

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            .apply { init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec) }

        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        encryptedBytes.toHex()
    }.getOrElse { throw RuntimeException(it) }

    override fun decrypt(encryptedText: String): String = runCatching {
        val secretKeySpec = SecretKeySpec(secretKey.toByteArray(StandardCharsets.UTF_8), "AES")
        val ivParameterSpec = IvParameterSpec(ivParameter.toByteArray(StandardCharsets.UTF_8))

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            .apply { init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec) }

        val decryptedBytes = cipher.doFinal(encryptedText.hexToBytes())

        String(decryptedBytes, StandardCharsets.UTF_8)
    }.getOrElse { throw RuntimeException(it) }
}
