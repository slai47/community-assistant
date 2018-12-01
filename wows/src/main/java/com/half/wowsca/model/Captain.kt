package com.half.wowsca.model

import com.half.wowsca.model.enums.Server
import com.half.wowsca.model.ranked.RankedInfo

/**
 * Created by slai4 on 9/15/2015.
 */
class Captain {

    var id: Long = 0
    var name: String? = null
    var server: Server? = null
    var clanName: String? = null

    var details: CaptainDetails? = null

    var teamBattleDetails: Statistics? = null

    var pveDetails: Statistics? = null

    var pvpDiv3Details: Statistics? = null
    var pvpDiv2Details: Statistics? = null
    var pvpSoloDetails: Statistics? = null

    var ships: List<Ship>? = null

    var achievements: List<Achievement>? = null

    var rankedSeasons: List<RankedInfo>? = null

    var information: CaptainPrivateInformation? = null

    fun copy(): Captain {
        val c = Captain()
        c.id = id
        c.name = name
        c.server = server
        return c
    }

    override fun toString(): String {
        return "Captain{" +
                "id=" + id +
                ", name='" + name + '\''.toString() +
                ", server=" + server +
                ", details=" + details +
                ", achievements=" + achievements +
                ", ships=" + ships +
                '}'.toString()
    }
}
