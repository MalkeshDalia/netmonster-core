package cz.mroczis.netmonster.core.telephony

import android.Manifest
import android.content.Context
import android.os.Build
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import androidx.annotation.RequiresPermission
import androidx.annotation.WorkerThread
import cz.mroczis.netmonster.core.callback.CellCallbackError
import cz.mroczis.netmonster.core.callback.CellCallbackSuccess
import cz.mroczis.netmonster.core.model.cell.ICell
import cz.mroczis.netmonster.core.model.model.CellError
import cz.mroczis.netmonster.core.telephony.mapper.CellInfoCallbackMapper
import cz.mroczis.netmonster.core.telephony.mapper.CellInfoMapper
import cz.mroczis.netmonster.core.telephony.mapper.CellLocationMapper
import cz.mroczis.netmonster.core.util.DirectExecutor

/**
 * Modifies some functionalities of [TelephonyManager] and unifies access to
 * methods across all platform versions.
 */
internal open class TelephonyManagerCompat14(
    private val context: Context,
    private val subId: Int = Integer.MAX_VALUE
) : ITelephonyManagerCompat {

    protected val telephony: TelephonyManager
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !Build.MANUFACTURER.equals("huawei", ignoreCase = true)) {
            (context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).createForSubscriptionId(subId)
        } else {
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        }

    protected val cellInfoMapper = CellInfoMapper()
    private val cellLocationMapper = CellLocationMapper(telephony, subId)

    @WorkerThread
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun getAllCellInfo(
        onSuccess: CellCallbackSuccess
    ) = getAllCellInfo(onSuccess, null)

    @WorkerThread
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun getAllCellInfo(
        onSuccess: CellCallbackSuccess,
        onError: CellCallbackError?
    ) {
        if (onError != null) {
            onError.invoke(CellError.UNSUPPORTED_AOSP_VERSION)
        } else {
            onSuccess.invoke(emptyList())
        }
    }


    @Suppress("DEPRECATION")
    @WorkerThread
    @RequiresPermission(
        allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE]
    )
    override fun getCellLocation() : List<ICell> {
        return cellLocationMapper.map(telephony.cellLocation)
    }

}