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
import site.youtogether.config.property.JwtProperties;
import site.youtogether.exception.jwt.InvalidTokenException;

@Service
@RequiredArgsConstructor
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
		} catch (ExpiredJwtException e) {
			throw new IllegalArgumentException("토큰 시간 만료");
		} catch (UnsupportedJwtException | MalformedJwtException e) {
			throw new InvalidTokenException();
		} catch (SignatureException e) {
			throw new IllegalArgumentException("변조된 토큰");
		}
	}

	public boolean isValidToken(String token) {
		try {
			Jwts.parser()
				.setSigningKey(jwtProperties.getSecretKey())
				.parseClaimsJws(token)
				.getBody();
			return true;
		} catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException e) {
			return false;
		}
	}

}
