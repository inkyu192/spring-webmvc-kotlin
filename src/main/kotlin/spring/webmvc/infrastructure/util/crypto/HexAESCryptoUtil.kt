package spring.webmvc.infrastructure.util.crypto

import org.springframework.stereotype.Component
import spring.webmvc.infrastructure.util.hexToBytes
import spring.webmvc.infrastructure.util.toHex
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Component
class HexAESCryptoUtil(
    cryptoProperties: CryptoProperties,
) : CryptoUtil {
    private val secretKey = SecretKeySpec(cryptoProperties.secretKey.toByteArray(StandardCharsets.UTF_8), "AES")
    private val ivParameter = IvParameterSpec(cryptoProperties.ivParameter.toByteArray(StandardCharsets.UTF_8))

    override fun encrypt(plainText: String): String = runCatching {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            .apply { init(Cipher.ENCRYPT_MODE, secretKey, ivParameter) }

        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        encryptedBytes.toHex()
    }.getOrElse { throw RuntimeException(it) }

    override fun decrypt(encryptedText: String): String = runCatching {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            .apply { init(Cipher.DECRYPT_MODE, secretKey, ivParameter) }

        val decryptedBytes = cipher.doFinal(encryptedText.hexToBytes())

        String(decryptedBytes, StandardCharsets.UTF_8)
    }.getOrElse { throw RuntimeException(it) }
}
