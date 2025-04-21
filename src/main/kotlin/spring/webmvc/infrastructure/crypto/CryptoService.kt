package spring.webmvc.infrastructure.crypto

interface CryptoService {
    fun encrypt(plainText: String): String
    fun decrypt(encryptedText: String): String
}