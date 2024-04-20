package site.youtogether.jwt;

import static site.youtogether.util.AppConstants.*;

import java.time.Duration;
import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.youtogether.config.property.JwtProperties;
import site.youtogether.exception.jwt.AuthorizationHeaderNoExistenceException;
import site.youtogether.exception.jwt.InvalidTokenException;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

	private final JwtProperties jwtProperties;

	public String issue(Long userId, Duration expiry) {
		Date now = new Date();
		Date expiredAt = new Date(now.getTime() + expiry.toMillis());

		return Jwts.builder()
			.setHeaderParam(Header.TYPE, Header.JWT_TYPE)
			.setIssuer(jwtProperties.getIssuer())
			.setIssuedAt(now)
			.setExpiration(expiredAt)
			.claim("userId", userId)
			.signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
			.compact();
	}

	public Claims parse(String authorizationHeader) {
		log.info("입력으로 들어온 문자열: {}", authorizationHeader);
		validationAuthorizationHeader(authorizationHeader);
		String token = extract(authorizationHeader);
		log.info("추출된 들어온 문자열: {}", token);
		try {
			return Jwts.parser()
				.setSigningKey(jwtProperties.getSecretKey())
				.parseClaimsJws(token)
				.getBody();
		} catch (ExpiredJwtException e) {
			throw new IllegalArgumentException("토큰 시간 만료");
		} catch (UnsupportedJwtException | MalformedJwtException e) {
			throw new InvalidTokenException();
		} catch (SignatureException e) {
			throw new IllegalArgumentException("변조된 토큰");
		}
	}

	private void validationAuthorizationHeader(String header) {
		if (header == null || !header.startsWith(BEARER)) {
			throw new AuthorizationHeaderNoExistenceException();
		}
	}

	private String extract(String authorizationHeader) {
		return authorizationHeader.substring(BEARER.length());
	}

}
