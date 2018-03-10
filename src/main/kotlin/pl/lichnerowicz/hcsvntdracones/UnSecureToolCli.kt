package pl.lichnerowicz.hcsvntdracones

import com.natpryce.konfig.*
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default

@Suppress("ClassName")
object crypto : PropertyGroup() {
    val base by longType
    val seed by longType
    val algorithm by stringType
    val cipher by stringType
    val prefix by stringType
}

class ParsedArgs(parser: ArgParser) {
    val base by parser.storing("-b", "--base", help="randomizer seed").default("1212121212")
    val seed by parser.storing("-s", "--seed", help="seed").default("34343434343")
    val algorithm by parser.storing("-a", "--algorithm", help="key").default("DESede")
    val cipher by parser.storing("-c", "--cipher", help="cipher").default("CBC/PKCS5Padding")
    val prefix by parser.storing("-p", "--prefix", help="prefix").default( "__")
    val sources by parser.positionalList("SOURCE", help = "source filename", sizeRange = 1..Int.MAX_VALUE)
}

fun main(args : Array<String>) {
    ArgParser(args).parseInto(::ParsedArgs).run {
        val config = systemProperties() overriding
                EnvironmentVariables() overriding
                ConfigurationMap(mapOf(
                        "crypto.base" to base,
                        "crypto.seed" to seed,
                        "crypto.algorithm" to algorithm,
                        "crypto.cipher" to cipher,
                        "crypto.prefix" to prefix))

        val deobfuscator = Deobfuscator(
                config[crypto.base].toLong(),
                config[crypto.seed].toLong(),
                config[crypto.algorithm],
                config[crypto.cipher],
                config[crypto.prefix])

        sources.forEach {
            try {
                val plain = deobfuscator.execute(it)
                println("$it: $plain")
            } catch (t: Throwable) {
                println("$it: FAILED.")
            }
        }
    }
}