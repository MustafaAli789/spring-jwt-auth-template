package com.jwtAuth.JWTAuthTemplate.utils

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import javax.servlet.http.HttpServletResponse

class ResponseUtil {

    companion object {
        fun sendMapResponse(mapData: Map<String, String>, res: HttpServletResponse) {
            res.contentType = MediaType.APPLICATION_JSON_VALUE
            ObjectMapper().writeValue(res.outputStream, mapData)
        }

        fun sendResponseErr(errMsg: String?, errCode: Int, res: HttpServletResponse) {
            res.setHeader("error", errMsg)
            res.status = errCode
            //res.sendError(403)

            sendMapResponse(mapOf("error_msg" to (errMsg?: "Error")), res)
        }
    }
}