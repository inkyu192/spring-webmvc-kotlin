package spring.webmvc.infrastructure.crypto

import org.springframework.stereotype.Component
import spring.webmvc.infrastructure.properties.AppProperties
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

@Component
class AesBase64CryptoService(
    appProperties: AppProperties,
) : CryptoService {
    private val secretKey = SecretKeySpec(appProperties.crypto.secretKey.toByteArray(StandardCharsets.UTF_8), "AES")
    private val ivParameter = IvParameterSpec(appProperties.crypto.ivParameter.toByteArray(StandardCharsets.UTF_8))

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
