package pl.lichnerowicz.hcsvntdracones

import org.apache.commons.codec.binary.Base64
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESedeKeySpec
import javax.crypto.spec.IvParameterSpec

class Deobfuscator(private val base: Long, private val seed: Long, private val algorithm: String, private val cipher: String, private val prefix: String) {
    fun execute(encryptedText: String): String {
        /**
         * encrypted text follows format
         * <prefix>-<constant>-<ivLength>-<iv><payload>
         */
        val parts = encryptedText.split("-")
        if (4 != parts.size) {
            throw EncryptedTextFormatException("Encrypted text malformed.")
        }

        if (parts[0] != prefix) {
            throw EncryptedTextFormatException("Encrypted text malformed.")
        }
        if (parts[1] != "001") {
            throw EncryptedTextFormatException("Encrypted text malformed.")
        }

        val ivLength = Integer.parseInt(parts[2])
        if (ivLength < 1 || ivLength > parts[3].length) {
            throw EncryptedTextFormatException("Encrypted text malformed.")
        }

        val iv = Base64.decodeBase64(parts[3].substring(0, ivLength).toByteArray(charset("UTF-8")))
        val payload =  Base64.decodeBase64(parts[3].substring(ivLength).toByteArray(charset("UTF-8")))

        val randomNoGenerator = Random(seed xor base)
        val randomBytes = ByteArray(32)
        randomNoGenerator.nextBytes(randomBytes)

        val keyFactory = SecretKeyFactory.getInstance(algorithm)
        val secretKey = keyFactory.generateSecret(DESedeKeySpec(randomBytes))

        val dps = IvParameterSpec(iv)
        val cipher = Cipher.getInstance("$algorithm/$cipher")
        cipher.init(2, secretKey, dps)

        try {
            return cipher.doFinal(payload).toString(Charset.forName("UTF-8"))
        } catch (unsupportedEncodingException: UnsupportedEncodingException) {
            throw EncryptedTextFormatException("Unsupported encoding.")
        }
    }


}
