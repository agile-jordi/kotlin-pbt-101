# kotlin_arb_puzzle

This is the repository of a workshop I'm facilitating at Lifull. We are going to create a minimal library for property based testing in Kotlin.

Initially we have a backbone implementation with `TODO()` placeholders and a set of tests telling us what to implement. To make things easier, the tests use a 
source of randomness that is totally deterministic, so assertions can be written in terms of expected "random" values to be generated.

The design of the library is losely inspired by [kotest property](https://kotest.io/docs/proptest/property-based-testing.html).

This repo is intended as support material for the workshop. It is not intended to be stand alone material for other to use.
