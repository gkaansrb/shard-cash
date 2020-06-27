package com.kakaopay.demo.config

import com.kakaopay.demo.domain.common.DataNotFoundException
import java.io.PrintWriter
import java.io.StringWriter
import javax.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest

@ControllerAdvice(annotations = [RestController::class])
class RestErrorConfig {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(value = [RuntimeException::class])
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleGlobalException(
        e: RuntimeException,
        request: HttpServletRequest,
        webRequest: WebRequest
    ): Map<String, Any> = errorForm(
        webRequest = webRequest,
        statusCode = HttpStatus.INTERNAL_SERVER_ERROR,
        logParams = getLogParams(request, e),
        message = e.message ?: ""
    )

    @ExceptionHandler(value = [DataNotFoundException::class])
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    fun handleDataNotFoundException(
        e: DataNotFoundException,
        request: HttpServletRequest,
        webRequest: WebRequest
    ): Map<String, Any> = errorForm(
        webRequest = webRequest,
        statusCode = HttpStatus.NOT_FOUND,
        logParams = getLogParams(request, e),
        message = e.message ?: ""
    )

    @ExceptionHandler(value = [IllegalStateException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleIllegalStateException(
        e: IllegalStateException,
        request: HttpServletRequest,
        webRequest: WebRequest
    ): Map<String, Any> = errorForm(
        webRequest = webRequest,
        statusCode = HttpStatus.BAD_REQUEST,
        logParams = getLogParams(request, e),
        message = e.message ?: ""
    )

    private fun getLogParams(
        request: HttpServletRequest,
        e: RuntimeException
    ) = mapOf(
        "url" to request.requestURI,
        "exception message:" to "${e.message}\n",
        "stackTrace" to traceToString(e)
    ).toString()

    private fun errorForm(
        webRequest: WebRequest,
        statusCode: HttpStatus,
        logParams: String,
        message: String
    ): MutableMap<String, Any> {
        log.error(logParams)
        val errorAttributes = DefaultErrorAttributes().getErrorAttributes(webRequest, true)
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
