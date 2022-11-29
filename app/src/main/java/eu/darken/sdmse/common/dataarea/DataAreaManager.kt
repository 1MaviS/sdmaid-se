package eu.darken.sdmse.common.dataarea

import eu.darken.sdmse.common.coroutine.AppScope
import eu.darken.sdmse.common.dataarea.modules.DataAreaFactory
import eu.darken.sdmse.common.debug.logging.logTag
import eu.darken.sdmse.common.flow.replayingShare
import eu.darken.sdmse.common.flow.setupCommonEventHandlers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.mapLatest
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DataAreaManager @Inject constructor(
    @AppScope private val appScope: CoroutineScope,
    private val dataAreaFactory: DataAreaFactory,
) {

    private val refreshTrigger = MutableStateFlow(UUID.randomUUID())

    val areas: Flow<Set<DataArea>> = refreshTrigger
        .mapLatest {
            dataAreaFactory.build().toSet()
        }
        .setupCommonEventHandlers(TAG) { "areas" }
        .replayingShare(appScope)

    companion object {
        val TAG: String = logTag("DataArea", "Manager")
    }
}