package com.bselzer.gw2.manager.common.dependency

import me.tatarka.inject.annotations.Scope

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
@Scope
annotation class Singleton