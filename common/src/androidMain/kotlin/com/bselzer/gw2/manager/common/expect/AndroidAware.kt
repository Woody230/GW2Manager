package com.bselzer.gw2.manager.common.expect

import coil.ImageLoader

interface AndroidAware : Gw2Aware<AndroidApp> {
    val imageLoader: ImageLoader
}