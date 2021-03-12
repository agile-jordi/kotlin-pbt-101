package com.lifullconnect.listings.training.pbt.example

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll


class StringConcatenationSpec : StringSpec({

    "String size" {
        checkAll(Arb.string(), Arb.string()) { a, b ->
            (a + b).length.shouldBe(a.length + b.length)
        }
    }


})
