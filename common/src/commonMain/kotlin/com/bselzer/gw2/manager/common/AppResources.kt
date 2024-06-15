package com.bselzer.gw2.manager.common

import dev.icerock.moko.resources.AssetResource
import dev.icerock.moko.resources.ColorResource
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.ResourceContainer
import dev.icerock.moko.resources.ResourcePlatformDetails
import dev.icerock.moko.resources.StringResource
import kotlin.collections.List

public expect object AppResources {
  public object strings : ResourceContainer<StringResource> {
    override val __platformDetails: ResourcePlatformDetails

    public val app_name: StringResource

    public val bloodlust_for: StringResource

    public val borderlands_color: StringResource

    public val claimed: StringResource

    public val claimed_at: StringResource

    public val claimed_by: StringResource

    public val color_failure: StringResource

    public val continents_description: StringResource

    public val default_zoom_level: StringResource

    public val flipped_at: StringResource

    public val guild_emblem: StringResource

    public val guilds_description: StringResource

    public val held_for: StringResource

    public val hexadecimal_color: StringResource

    public val hold_for: StringResource

    public val home_world: StringResource

    public val images_description: StringResource

    public val images: StringResource

    public val neutral_slice: StringResource

    public val no_claim: StringResource

    public val no_upgrade: StringResource

    public val no_worlds: StringResource

    public val overview_name: StringResource

    public val owned_slice: StringResource

    public val permanent_waypoint: StringResource

    public val selected_objective: StringResource

    public val status_available_description: StringResource

    public val status_unavailable_description: StringResource

    public val team_label: StringResource

    public val temporary_waypoint: StringResource

    public val token_description: StringResource

    public val token_failure: StringResource

    public val token_hyperlink: StringResource

    public val translations: StringResource

    public val upgrade_level: StringResource

    public val upgrade_tier: StringResource

    public val upgrade_tier_level: StringResource

    public val upgrade_tier_yaks: StringResource

    public val wvw_description: StringResource

    public val wvw_tile: StringResource

    public val yaks_delivered: StringResource

    public val yaks_delivered_ratio: StringResource

    override fun values(): List<StringResource>
  }

  public object images : ResourceContainer<ImageResource> {
    override val __platformDetails: ResourcePlatformDetails

    public val bloodstone_night: ImageResource

    public val ice: ImageResource

    public val two_sylvari: ImageResource

    override fun values(): List<ImageResource>
  }

  public object colors : ResourceContainer<ColorResource> {
    override val __platformDetails: ResourcePlatformDetails

    public val purple_200: ColorResource

    public val purple_500: ColorResource

    public val purple_700: ColorResource

    public val teal_200: ColorResource

    public val teal_700: ColorResource

    public val black: ColorResource

    public val white: ColorResource

    override fun values(): List<ColorResource>
  }

  public object assets : ResourceContainer<AssetResource> {
    override val __platformDetails: ResourcePlatformDetails

    public val aboutlibraries_json: AssetResource

    public val Configuration_xml: AssetResource

    override fun values(): List<AssetResource>
  }
}
