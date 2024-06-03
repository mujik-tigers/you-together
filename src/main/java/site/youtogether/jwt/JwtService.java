package site.youtogether.jwt;

import static site.youtogether.util.AppConstants.*;

import java.time.Duration;
import java.util.Date;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import site.youtogether.config.property.JwtProperties;

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
			.claim(USER_ID, userId)
			.signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
			.compact();
	}

	public Long parse(String token) {
		try {
			Claims claims = Jwts.parser()
				.setSigningKey(jwtProperties.getSecretKey())
				.parseClaimsJws(token)
				.getBody();
			return claims.get(USER_ID, Long.class);
		} catch (Exception e) {
			log.error("JwtService parse error! ", e);
			return null;
		}
	}

}
