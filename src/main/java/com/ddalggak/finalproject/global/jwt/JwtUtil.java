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
import com.ddalggak.finalproject.global.jwt.token.entity.Token;
import com.ddalggak.finalproject.global.jwt.token.repository.TokenRepository;
import com.ddalggak.finalproject.global.security.UserDetailsServiceImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
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

	public Token login(String email, UserRole role) {
		if (tokenRepository.existsById(email)) {
			log.info("기존의 존재하는 모든 토큰 삭제");
			tokenRepository.deleteById(email);
		}

		String accessToken = createAccessToken(email, role).getAccessToken();
		String refreshToken = createRefreshToken(email, role).getRefreshToken();

		return Token.builder()
			.email(email)
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	public Token createAccessToken(String email, UserRole role) {
		Date date = new Date();

		String accessToken = BEARER_PREFIX +
			Jwts.builder()
				.setSubject(email)
				.claim(AUTHORIZATION_KEY, role)
				.setExpiration(new Date(date.getTime() + ACCESS_TOKEN_TIME))
				.setIssuedAt(date)
				.signWith(key, signatureAlgorithm)
				.compact();

		Token toSaveAccessToken = Token.builder()
			.email(email)
			.accessToken(accessToken)
			.build();

		tokenRepository.save(toSaveAccessToken);

		return toSaveAccessToken;

	}

	public Token createRefreshToken(String email, UserRole role) {
		Date date = new Date();

		String refreshToken = BEARER_PREFIX +
			Jwts.builder()
				.setSubject(email)
				.claim(AUTHORIZATION_KEY, role)
				.setExpiration(new Date(date.getTime() + REFRESH_TOKEN_TIME))
				.setIssuedAt(date)
				.signWith(key, signatureAlgorithm)
				.compact();

		Token toSaveRefreshToken = Token.builder()
			.email(email)
			.refreshToken(refreshToken)
			.build();

		tokenRepository.save(toSaveRefreshToken);

		return toSaveRefreshToken;

	}

	public void logout(String email) {
		Token token = tokenRepository.findById(email).orElseThrow(()-> new UserException(ErrorCode.INVALID_REQUEST));
		String accessToken = token.getAccessToken();
		String refreshToken = token.getRefreshToken();

		if (accessToken != null || refreshToken != null) {
			log.info("기존의 존재하는 토큰 모두 삭제");
			tokenRepository.deleteById(email);
		}
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
		} catch (Exception e) {
			log.info("Invalid token, d.");
		}
		return false;
	}

	public boolean validateRefreshToken(String refreshToken) {
		Claims claims = getUserInfo(refreshToken);
		String email = claims.getSubject();

		userRepository.findByEmail(email)
			.orElseThrow(() -> new UserException(ErrorCode.MEMBER_NOT_FOUND));
		try {
			Jws<Claims> claim = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(refreshToken);
			if (!claim.getBody().getExpiration().before(new Date())) {
				return true;
			}
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

	public String recreateAccessToken(String accessToken) {
		Claims claims = getUserInfo(accessToken);
		String email = claims.getSubject();
		UserRole role = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserException(ErrorCode.MEMBER_NOT_FOUND))
			.getRole();

		Jws<Claims> claim = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken);

		return recreationAccessToken(claim.getBody().get("sub").toString(), role);

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