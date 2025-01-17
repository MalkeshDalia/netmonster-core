package cz.mroczis.netmonster.core.factory

import android.content.Context
import android.os.Build
import cz.mroczis.netmonster.core.INetMonster
import cz.mroczis.netmonster.core.NetMonster
import cz.mroczis.netmonster.core.telephony.ITelephonyManagerCompat
import cz.mroczis.netmonster.core.telephony.TelephonyManagerCompat14
import cz.mroczis.netmonster.core.telephony.TelephonyManagerCompat17
import cz.mroczis.netmonster.core.telephony.TelephonyManagerCompat29

/**
 * Factory that produces new instances.
 */
object NetMonsterFactory {

    /**
     * Creates new instance of [ITelephonyManagerCompat] that is bound to specified
     * subscription id ([subId]) if applicable for current Android version.
     */
    fun getTelephony(context: Context, subId: Int = Integer.MAX_VALUE): ITelephonyManagerCompat =
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> TelephonyManagerCompat29(context, subId)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 -> TelephonyManagerCompat17(context, subId)
            else -> TelephonyManagerCompat14(context, subId)
        }

    /**
     * Creates new instance of [INetMonster].
     */
    fun get(context: Context, subId: Int = Integer.MAX_VALUE) : INetMonster =
        NetMonster(context, getTelephony(context, subId))

}