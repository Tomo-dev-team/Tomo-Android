package com.markoala.tomoandroid.data.model

interface PromiseBase {
    val promiseName: String
    val promiseDate: String
    val promiseTime: String
    val place: String
}

data class PromiseResponseDTO(
    override val promiseName: String,
    override val promiseDate: String,
    override val promiseTime: String,
    override val place: String
) : PromiseBase

data class PromiseDTO(
    val title: String,
    override val promiseName: String,
    override val promiseDate: String,
    override val promiseTime: String,
    override val place: String
) : PromiseBase
