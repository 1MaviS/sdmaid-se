package eu.darken.sdmse.common.forensics.csi.pub

import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import eu.darken.sdmse.common.areas.DataArea
import eu.darken.sdmse.common.areas.DataAreaManager
import eu.darken.sdmse.common.areas.currentAreas
import eu.darken.sdmse.common.debug.logging.logTag
import eu.darken.sdmse.common.files.core.APath
import eu.darken.sdmse.common.forensics.AreaInfo
import eu.darken.sdmse.common.forensics.CSIProcessor
import eu.darken.sdmse.common.forensics.csi.LocalCSIProcessor
import javax.inject.Inject

@Reusable
class PortableCSI @Inject constructor(
    private val areaManager: DataAreaManager,
) : LocalCSIProcessor {

    override suspend fun hasJurisdiction(type: DataArea.Type): Boolean = type == DataArea.Type.PORTABLE

    override suspend fun identifyArea(target: APath): AreaInfo? = areaManager.currentAreas()
        .firstOrNull { area ->
            val base = area.path
            target.path.startsWith(base.path + "/") && target.path != base.path
        }
        ?.let {
            AreaInfo(
                dataArea = it,
                file = target,
                prefix = "${it.path.path}/",
                isBlackListLocation = false,
            )
        }

    override suspend fun findOwners(areaInfo: AreaInfo): CSIProcessor.Result {
        require(hasJurisdiction(areaInfo.type)) { "Wrong jurisdiction: ${areaInfo.type}" }
        return CSIProcessor.Result()
    }

    @Module @InstallIn(SingletonComponent::class)
    abstract class DIM {
        @Binds @IntoSet abstract fun mod(mod: PortableCSI): CSIProcessor
    }

    companion object {
        val TAG: String = logTag("CSI", "Portable")
    }
}