package com.origindev.bglwallet.ui.wallet

import com.origindev.bglwallet.net.UsdRetrofitClientInstance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BglUsdCourseRepository {

    val usdCourseFlow: Flow<Double> = flow {
        val response = UsdRetrofitClientInstance.instance.getCoinBglInDollars()
        if (response.isSuccessful) {
            emit(response.body()?.course?.usd ?: doubleZero)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: BglUsdCourseRepository? = null

        fun getInstance(): BglUsdCourseRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = BglUsdCourseRepository()
                INSTANCE = instance
                instance
            }
        }
    }
}