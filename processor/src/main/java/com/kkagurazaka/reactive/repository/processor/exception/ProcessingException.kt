package com.kkagurazaka.reactive.repository.processor.exception

import com.kkagurazaka.reactive.repository.processor.ReactiveRepositoryProcessor
import javax.lang.model.element.Element

class ProcessingException(message: String, val element: Element?, cause: Throwable? = null) :
    RuntimeException("[${ReactiveRepositoryProcessor.TAG}] $message", cause)
