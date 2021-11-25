package com.bselzer.gw2.manager.configuration.wvw

import com.bselzer.gw2.manager.configuration.common.Size
import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
class WvwChart(
    @XmlSerialName(value = "background", namespace = "", prefix = "")
    val backgroundLink: String = "",

    @XmlSerialName(value = "divider", namespace = "", prefix = "")
    val dividerLink: String = "",

    @XmlSerialName(value = "blue", namespace = "", prefix = "")
    val blueLink: String = "",

    @XmlSerialName(value = "green", namespace = "", prefix = "")
    val greenLink: String = "",

    @XmlSerialName(value = "red", namespace = "", prefix = "")
    val redLink: String = "",

    @XmlSerialName(value = "neutral", namespace = "", prefix = "")
    val neutralLink: String = "",

    @XmlSerialName(value = "Title", namespace = "", prefix = "")
    val title: WvwChartTitle = WvwChartTitle(),

    @XmlSerialName(value = "Data", namespace = "", prefix = "")
    val data: WvwChartData = WvwChartData(),

    @XmlSerialName(value = "Size", namespace = "", prefix = "")
    val size: Size = Size()
)