package com.kakaopay.demo.config

import com.kakaopay.demo.domain.common.DataNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.web.ErrorProperties
import org.springframework.boot.autoconfigure.web.ResourceProperties
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.reactive.function.server.ServerRequest
import java.io.PrintWriter
import java.io.StringWriter

class RestErrorConfig(
    errorAttribute: ErrorAttributes,
    resourceProperties: ResourceProperties,
    errorProperties: ErrorProperties,
    applicationContext: ApplicationContext,
    private val logger: Logger
) : DefaultErrorWebExceptionHandler(
    errorAttribute,
    resourceProperties,
    errorProperties,
    applicationContext
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(value = [RuntimeException::class])
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleGlobalException(
        e: RuntimeException,
        webRequest: ServerRequest
    ): Map<String, Any> = errorForm(
        webRequest = webRequest,
        statusCode = HttpStatus.INTERNAL_SERVER_ERROR,
        logParams = getLogParams(webRequest, e),
        message = e.message ?: ""
    )

    @ExceptionHandler(value = [DataNotFoundException::class])
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun handleDataNotFoundException(
        e: DataNotFoundException,
        webRequest: ServerRequest
    ): Map<String, Any> = errorForm(
        webRequest = webRequest,
        statusCode = HttpStatus.NOT_FOUND,
        logParams = getLogParams(webRequest, e),
        message = e.message ?: ""
    )

    @ExceptionHandler(value = [IllegalStateException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleIllegalStateException(
        e: IllegalStateException,
        webRequest: ServerRequest
    ): Map<String, Any> = errorForm(
        webRequest = webRequest,
        statusCode = HttpStatus.BAD_REQUEST,
        logParams = getLogParams(webRequest, e),
        message = e.message ?: ""
    )

    private fun getLogParams(
        request: ServerRequest,
        e: RuntimeException
    ) = mapOf(
        "url" to request.uri().toString(),
        "method" to request.method().toString(),
        "exception message:" to "${e.message}\n",
        "stackTrace" to traceToString(e)
    ).toString()

    private fun errorForm(
        webRequest: ServerRequest,
        statusCode: HttpStatus,
        logParams: String,
        message: String
    ): MutableMap<String, Any> {
        log.error(logParams)
        val errorAttributes = getErrorAttributes(webRequest, true)
        errorAttributes["status"] = statusCode.value()
        errorAttributes["logParams"] = logParams
        errorAttributes["message"] = message
        return errorAttributes
    }

    private fun traceToString(e: Exception): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw, true)

        e.printStackTrace(pw)
        return sw.buffer.toString().substring(IntRange(0, 1000)) + "\n... cut!"
    }
}
