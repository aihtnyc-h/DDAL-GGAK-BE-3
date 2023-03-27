package com.ddalggak.finalproject.global.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ddalggak.finalproject.domain.user.exception.UserException;
import com.ddalggak.finalproject.domain.user.repository.UserRepository;
import com.ddalggak.finalproject.domain.user.role.UserRole;
import com.ddalggak.finalproject.global.error.ErrorCode;
import com.ddalggak.finalproject.global.jwt.token.dto.Token;
import com.ddalggak.finalproject.global.jwt.token.entity.RefreshToken;
import com.ddalggak.finalproject.global.jwt.token.repository.TokenRepository;
import com.ddalggak.finalproject.global.security.UserDetailsServiceImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {
	private final UserRepository userRepository;
	private final TokenRepository tokenRepository;
	// private final RefreshTokenRepository refreshTokenRepository;
	private final UserDetailsServiceImpl userDetailsService;
	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String REFRESH_TOKEN_HEADER = "RefreshToken";
	public static final String AUTHORIZATION_KEY = "auth";
	private static final String BEARER_PREFIX = "Bearer ";
	private static final long ACCESS_TOKEN_TIME = 60 * 60 * 24 * 1000L;
	private static final long REFRESH_TOKEN_TIME = 60 * 60 * 24 * 30 * 1000L;

	@Value("${jwt.secret.key}")
	private String secretKey;
	private Key key;
	private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

	@PostConstruct
	public void init() {
		byte[] bytes = Base64.getDecoder().decode(secretKey);
		key = Keys.hmacShaKeyFor(bytes);
	}

	public String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(7);
		}
		return null;
	}

	public String resolveRefreshToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(REFRESH_TOKEN_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(7);
		}
		return null;
	}

	public String login(String email, UserRole role) {
		return createToken(email, role).getAccessToken();
	}

	private Token createToken(String email, UserRole role) {
		Date date = new Date();

		String accessToken = BEARER_PREFIX +
			Jwts.builder()
				.setSubject(email)
				.claim(AUTHORIZATION_KEY, role)
				.setExpiration(new Date(date.getTime() + ACCESS_TOKEN_TIME))
				.setIssuedAt(date)
				.signWith(key, signatureAlgorithm)
				.compact();

		String refreshToken = BEARER_PREFIX +
			Jwts.builder()
				.setSubject(email)
				.claim(AUTHORIZATION_KEY, role)
				.setExpiration(new Date(date.getTime() + REFRESH_TOKEN_TIME))
				.setIssuedAt(date)
				.signWith(key, signatureAlgorithm)
				.compact();

		Token token = Token.builder()
			.email(email)
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();

		tokenRepository.save(token);

		return token;

	}

	public void logoutToken(Long userId) {
		Long now = new Date().getTime();
		// refreshTokenRepository.deleteById(userId);
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
		} catch (ExpiredJwtException e) {
			log.info("Expired JWT token, 만료된 JWT token 입니다.");
		} catch (UnsupportedJwtException e) {
			log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
		} catch (IllegalArgumentException e) {
			log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
		}
		return false;
	}

	public String validateRefreshToken(RefreshToken refreshTokenObj) {
		String token = refreshTokenObj.getRefreshToken();
		String email = refreshTokenObj.getEmail();
		UserRole role = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserException(ErrorCode.MEMBER_NOT_FOUND))
			.getRole();
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return recreationAccessToken(email, role);
		} catch (SecurityException | MalformedJwtException e) {
			log.info("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
		} catch (ExpiredJwtException e) {
			log.info("Expired JWT token, 만료된 JWT token 입니다.");
		} catch (UnsupportedJwtException e) {
			log.info("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
		} catch (IllegalArgumentException e) {
			log.info("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
		}
		return null;
	}

	private String recreationAccessToken(String email, UserRole role) {
		Date date = new Date();

		return BEARER_PREFIX +
			Jwts.builder()
				.setSubject(email)
				.claim(AUTHORIZATION_KEY, role)
				.setExpiration(new Date(date.getTime() + ACCESS_TOKEN_TIME))
				.setIssuedAt(date)
				.signWith(key, signatureAlgorithm)
				.compact();

	}

	public Authentication createAuthentication(String email) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(email);
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

	public Claims getUserInfo(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}

}