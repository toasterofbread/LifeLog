package dev.toastbits.lifelog.application.worker.model

import dev.toastbits.lifelog.application.worker.command.WorkerCommandProgress
import org.w3c.dom.ErrorEvent

internal fun ErrorEvent.toPotentialError(): WorkerCommandProgress.PotentialError =
    WorkerCommandProgress.PotentialError("$message ($filename:$lineno:$colno)")
