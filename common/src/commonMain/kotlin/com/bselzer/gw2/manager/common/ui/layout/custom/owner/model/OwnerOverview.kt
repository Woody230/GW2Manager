package com.bselzer.gw2.manager.common.ui.layout.custom.owner.model

import com.bselzer.gw2.manager.common.ui.layout.common.Image

data class OwnerOverview(
    val victoryPoints: Data,
    val pointsPerTick: Data,
    val skirmishWarScore: Data,
    val totalWarScore: Data,
    val owner: Owner,
    val home: Image?,
    val bloodlusts: List<Image>,
)