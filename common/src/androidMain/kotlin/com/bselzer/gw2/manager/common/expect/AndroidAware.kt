package com.bselzer.gw2.manager.common.expect

import coil.ImageLoader
import okhttp3.OkHttpClient

interface AndroidAware : Gw2Aware<AndroidApp> {
    val imageLoader: ImageLoader
    val okHttpClient: OkHttpClient
}