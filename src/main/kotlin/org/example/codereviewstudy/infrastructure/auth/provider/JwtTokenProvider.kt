package org.example.codereviewstudy.infrastructure.auth.provider

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.example.codereviewstudy.infrastructure.auth.exception.InvalidTokenException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}")
    key: String,
    @Value("\${jwt.issuer}")
    private val issuer: String,
    @Value("\${jwt.expiration}")
    private val expiration: Long
) {
    private val log: Logger = LoggerFactory.getLogger(JwtTokenProvider::class.java)
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(key))

    fun create(userId: Long): String {
        val now = Date().time
        val expirationDate = Date(now + expiration)

        return Jwts.builder()
            .issuer(issuer)
            .subject(userId.toString())
            .signWith(secretKey)
            .expiration(expirationDate)
            .compact()
    }

    fun getClaims(token: String): Claims {
        try {
            return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: JwtException) {
            log.warn("Invalid JWT token: $token $e")
            throw InvalidTokenException(token)
        }
    }

}