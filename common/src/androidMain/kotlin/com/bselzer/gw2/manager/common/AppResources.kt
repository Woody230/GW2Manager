package com.bselzer.gw2.manager.common

import com.bselzer.gw2.manager.common.R
import dev.icerock.moko.resources.AssetResource
import dev.icerock.moko.resources.ColorResource
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.ResourceContainer
import dev.icerock.moko.resources.ResourcePlatformDetails
import dev.icerock.moko.resources.StringResource
import kotlin.String
import kotlin.collections.List

public actual object AppResources {
  private val contentHash: String = "1e35dd9a9385def10fac5c7fbbec4cc0"

  public actual object strings : ResourceContainer<StringResource> {
    actual override val __platformDetails: ResourcePlatformDetails = ResourcePlatformDetails()

    public actual val app_name: StringResource = StringResource(R.string.app_name)

    public actual val bloodlust_for: StringResource = StringResource(R.string.bloodlust_for)

    public actual val borderlands_color: StringResource = StringResource(R.string.borderlands_color)

    public actual val claimed: StringResource = StringResource(R.string.claimed)

    public actual val claimed_at: StringResource = StringResource(R.string.claimed_at)

    public actual val claimed_by: StringResource = StringResource(R.string.claimed_by)

    public actual val color_failure: StringResource = StringResource(R.string.color_failure)

    public actual val continents_description: StringResource =
        StringResource(R.string.continents_description)

    public actual val default_zoom_level: StringResource =
        StringResource(R.string.default_zoom_level)

    public actual val flipped_at: StringResource = StringResource(R.string.flipped_at)

    public actual val guild_emblem: StringResource = StringResource(R.string.guild_emblem)

    public actual val guilds_description: StringResource =
        StringResource(R.string.guilds_description)

    public actual val held_for: StringResource = StringResource(R.string.held_for)

    public actual val hexadecimal_color: StringResource = StringResource(R.string.hexadecimal_color)

    public actual val hold_for: StringResource = StringResource(R.string.hold_for)

    public actual val home_world: StringResource = StringResource(R.string.home_world)

    public actual val images_description: StringResource =
        StringResource(R.string.images_description)

    public actual val images: StringResource = StringResource(R.string.images)

    public actual val neutral_slice: StringResource = StringResource(R.string.neutral_slice)

    public actual val no_claim: StringResource = StringResource(R.string.no_claim)

    public actual val no_upgrade: StringResource = StringResource(R.string.no_upgrade)

    public actual val no_worlds: StringResource = StringResource(R.string.no_worlds)

    public actual val overview_name: StringResource = StringResource(R.string.overview_name)

    public actual val owned_slice: StringResource = StringResource(R.string.owned_slice)

    public actual val permanent_waypoint: StringResource =
        StringResource(R.string.permanent_waypoint)

    public actual val selected_objective: StringResource =
        StringResource(R.string.selected_objective)

    public actual val status_available_description: StringResource =
        StringResource(R.string.status_available_description)

    public actual val status_unavailable_description: StringResource =
        StringResource(R.string.status_unavailable_description)

    public actual val team_label: StringResource = StringResource(R.string.team_label)

    public actual val temporary_waypoint: StringResource =
        StringResource(R.string.temporary_waypoint)

    public actual val token_description: StringResource = StringResource(R.string.token_description)

    public actual val token_failure: StringResource = StringResource(R.string.token_failure)

    public actual val token_hyperlink: StringResource = StringResource(R.string.token_hyperlink)

    public actual val translations: StringResource = StringResource(R.string.translations)

    public actual val upgrade_level: StringResource = StringResource(R.string.upgrade_level)

    public actual val upgrade_tier: StringResource = StringResource(R.string.upgrade_tier)

    public actual val upgrade_tier_level: StringResource =
        StringResource(R.string.upgrade_tier_level)

    public actual val upgrade_tier_yaks: StringResource = StringResource(R.string.upgrade_tier_yaks)

    public actual val wvw_description: StringResource = StringResource(R.string.wvw_description)

    public actual val wvw_tile: StringResource = StringResource(R.string.wvw_tile)

    public actual val yaks_delivered: StringResource = StringResource(R.string.yaks_delivered)

    public actual val yaks_delivered_ratio: StringResource =
        StringResource(R.string.yaks_delivered_ratio)

    actual override fun values(): List<StringResource> = listOf(app_name, bloodlust_for,
        borderlands_color, claimed, claimed_at, claimed_by, color_failure, continents_description,
        default_zoom_level, flipped_at, guild_emblem, guilds_description, held_for,
        hexadecimal_color, hold_for, home_world, images_description, images, neutral_slice,
        no_claim, no_upgrade, no_worlds, overview_name, owned_slice, permanent_waypoint,
        selected_objective, status_available_description, status_unavailable_description,
        team_label, temporary_waypoint, token_description, token_failure, token_hyperlink,
        translations, upgrade_level, upgrade_tier, upgrade_tier_level, upgrade_tier_yaks,
        wvw_description, wvw_tile, yaks_delivered, yaks_delivered_ratio)
  }

  public actual object images : ResourceContainer<ImageResource> {
    actual override val __platformDetails: ResourcePlatformDetails = ResourcePlatformDetails()

    public actual val bloodstone_night: ImageResource = ImageResource(R.drawable.bloodstone_night)

    public actual val ice: ImageResource = ImageResource(R.drawable.ice)

    public actual val two_sylvari: ImageResource = ImageResource(R.drawable.two_sylvari)

    actual override fun values(): List<ImageResource> = listOf(bloodstone_night, ice, two_sylvari)
  }

  public actual object colors : ResourceContainer<ColorResource> {
    actual override val __platformDetails: ResourcePlatformDetails = ResourcePlatformDetails()

    public actual val purple_200: ColorResource = ColorResource(R.color.purple_200)

    public actual val purple_500: ColorResource = ColorResource(R.color.purple_500)

    public actual val purple_700: ColorResource = ColorResource(R.color.purple_700)

    public actual val teal_200: ColorResource = ColorResource(R.color.teal_200)

    public actual val teal_700: ColorResource = ColorResource(R.color.teal_700)

    public actual val black: ColorResource = ColorResource(R.color.black)

    public actual val white: ColorResource = ColorResource(R.color.white)

    actual override fun values(): List<ColorResource> = listOf(purple_200, purple_500, purple_700,
        teal_200, teal_700, black, white)
  }

  public actual object assets : ResourceContainer<AssetResource> {
    actual override val __platformDetails: ResourcePlatformDetails = ResourcePlatformDetails()

    public actual val aboutlibraries_json: AssetResource = AssetResource(path =
        "aboutlibraries.json")

    public actual val Configuration_xml: AssetResource = AssetResource(path = "Configuration.xml")

    actual override fun values(): List<AssetResource> = listOf(aboutlibraries_json,
        Configuration_xml)
  }
}
