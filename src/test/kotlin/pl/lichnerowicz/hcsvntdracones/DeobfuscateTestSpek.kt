package pl.lichnerowicz.hcsvntdracones

import com.winterbe.expekt.should
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.data_driven.data
import org.jetbrains.spek.data_driven.on
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
class DeobfuscateTestSpek : Spek({
    describe("a deobfuscator") {
        val deobfuscator = Deobfuscator(2587540296, 25214903917, "DESede", "CBC/PKCS5Padding", "__")

        on(
                "deobfuscate",
                data("__-001-12-3IfCh6U2YQs=HXjDrSiIZ3+oUAEBoUSxDw==", "now you see me"),
                data("__-001-12-jga66nLaAm4=nM6grkyRJnKGDkCQgXO3nw==", "now you see me"),
                data("__-001-12-TgFjB4oXd4U=Pz7dtISlOn4lVemm2yTmHQ==", "now you see me"),
                data("__-001-12-Re6QvVs5d8o=Oq/b7jPmp1Ev76a2s7FmMA==", "now you see me"),
                data("__-001-10-srF4GBEKl+o=TW+iILQQ2jUCvAK4t4iDpQ==", "now you see me")
        ) { param, expected ->
            val actual = deobfuscator.execute(param)
            it("returns correct plain text") {
                actual.should.be.equal(expected)
            }
        }
    }
})