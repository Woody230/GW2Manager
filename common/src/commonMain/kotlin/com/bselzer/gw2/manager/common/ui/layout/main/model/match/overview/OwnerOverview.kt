package com.bselzer.gw2.manager.common.ui.layout.main.model.match.overview

data class OwnerOverview(
    val victoryPoints: Data,
    val pointsPerTick: Data,
    val skirmishWarScore: Data,
    val totalWarScore: Data,
    val owner: Owner,
    val home: Home?,
    val bloodlusts: List<Bloodlust>
)