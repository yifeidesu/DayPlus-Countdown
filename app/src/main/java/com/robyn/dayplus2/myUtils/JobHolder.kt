package com.robyn.dayplus2.myUtils

import kotlinx.coroutines.experimental.Job
import kotlin.coroutines.experimental.CoroutineContext

/**
 * An activity by implementing this interface, assigns all coroutines with its job inst,
 * and finally release the job on activity's destroy.
 */
interface JobHolder {
    var job: Job
}