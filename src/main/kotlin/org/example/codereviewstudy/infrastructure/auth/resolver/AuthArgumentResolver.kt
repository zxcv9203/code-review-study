package org.example.codereviewstudy.infrastructure.auth.resolver

import jakarta.servlet.http.HttpServletRequest
import org.example.codereviewstudy.infrastructure.auth.exception.InvalidTokenException
import org.example.codereviewstudy.infrastructure.auth.model.AuthUser
import org.example.codereviewstudy.infrastructure.auth.provider.JwtTokenProvider
import org.springframework.core.MethodParameter
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class AuthArgumentResolver(
    private val jwtTokenProvider: JwtTokenProvider,
) : HandlerMethodArgumentResolver {

    private val tokenPrefix = "Bearer "

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == AuthUser::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any? {
        val authorizationHeader = webRequest.getNativeRequest(HttpServletRequest::class.java)
            ?.getHeader(HttpHeaders.AUTHORIZATION)

        if (authorizationHeader.isNullOrBlank() ||
            authorizationHeader.startsWith(tokenPrefix).not()
        ) {
            throw InvalidTokenException(authorizationHeader)
        }

        val token = authorizationHeader.removePrefix(tokenPrefix)
        if (jwtTokenProvider.validate(token).not()) {
            throw InvalidTokenException(token)
        }
        val claims = jwtTokenProvider.getClaims(token)
        val userId = claims.subject.toLong()

        return AuthUser(userId)
    }
}