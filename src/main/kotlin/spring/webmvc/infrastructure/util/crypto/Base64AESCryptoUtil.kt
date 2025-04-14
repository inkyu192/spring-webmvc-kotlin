package spring.webmvc.infrastructure.util.crypto

import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Component
class Base64AESCryptoUtil(
    cryptoProperties: CryptoProperties,
) : CryptoUtil {
    private val secretKey = SecretKeySpec(cryptoProperties.secretKey.toByteArray(StandardCharsets.UTF_8), "AES")
    private val ivParameter = IvParameterSpec(cryptoProperties.ivParameter.toByteArray(StandardCharsets.UTF_8))

    override fun encrypt(plainText: String): String = runCatching {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            .apply { init(Cipher.ENCRYPT_MODE, secretKey, ivParameter) }

        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        Base64.getEncoder().encodeToString(encryptedBytes)
    }.getOrElse { throw RuntimeException(it) }

    override fun decrypt(encryptedText: String): String = runCatching {
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            .apply { init(Cipher.DECRYPT_MODE, secretKey, ivParameter) }

        val decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText))

        String(decryptedBytes, StandardCharsets.UTF_8)
    }.getOrElse { throw RuntimeException(it) }
}
