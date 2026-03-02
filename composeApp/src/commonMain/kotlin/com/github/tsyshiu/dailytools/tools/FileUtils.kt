package com.github.tsyshiu.dailytools.tools

import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.math.BigInteger
import java.security.MessageDigest

private const val TAG = "FileUtils"

suspend fun calculateMD5(file: File): String = withContext(Dispatchers.IO) {
    Logger.i(TAG){"calculateMD5: $file"}
    if (!file.exists()) return@withContext ""
    try {
        val md = MessageDigest.getInstance("MD5")
        FileInputStream(file).use { fis ->
            val buffer = ByteArray(8192)
            var read: Int
            while (fis.read(buffer).also { read = it } != -1) {
                md.update(buffer, 0, read)
            }
        }
        val md5sum = md.digest()
        val bigInt = BigInteger(1, md5sum)
        val output = bigInt.toString(16)
        Logger.i(TAG){"calculateMD5: $file done"}
        // Fill to 32 chars
        output.padStart(32, '0')
    } catch (e: Exception) {
        e.printStackTrace()
        "Error calculating MD5"
    }
}

suspend fun calculateHash(file: File, algorithm: String): String = withContext(Dispatchers.IO) {
    Logger.i(TAG){"calculate: $algorithm $file"}
    if (!file.exists()) return@withContext ""
    try {
        val md = MessageDigest.getInstance(algorithm)
        FileInputStream(file).use { fis ->
            val buffer = ByteArray(8192)
            var read: Int
            while (fis.read(buffer).also { read = it } != -1) {
                md.update(buffer, 0, read)
            }
        }
        val digest = md.digest()

        // Calculate the expected hex string length (1 byte = 2 hex chars)
        val hexLength = md.digestLength * 2

        val bigInt = BigInteger(1, digest)
        val output = bigInt.toString(16)
        Logger.i(TAG){"calculate: $algorithm $file done"}

        // Pad with leading zeros to the correct length
        output.padStart(hexLength, '0')
    } catch (e: Exception) {
        e.printStackTrace()
        "Error calculating hash ($algorithm)"
    }
}
