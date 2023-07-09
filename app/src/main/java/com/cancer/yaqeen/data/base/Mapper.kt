package com.cancer.yaqeen.data.base

interface Mapper<in I, out O> {
    fun map(input: I): O
}
