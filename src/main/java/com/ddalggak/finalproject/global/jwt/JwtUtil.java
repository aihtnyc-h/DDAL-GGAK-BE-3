package com.ddalggak.finalproject.global.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ddalggak.finalproject.domain.user.exception.UserException;
import com.ddalggak.finalproject.domain.user.repository.UserRepository;
import com.ddalggak.finalproject.domain.user.role.UserRole;
import com.ddalggak.finalproject.global.error.ErrorCode;
import com.ddalggak.finalproject.global.jwt.token.entity.AccessToken;
import com.ddalggak.finalproject.global.jwt.token.entity.RefreshToken;
import com.ddalggak.finalproject.global.jwt.token.repository.AccessTokenRepository;
import com.ddalggak.finalproject.global.jwt.token.repository.RefreshTokenRepository;
import com.ddalggak.finalproject.global.security.UserDetailsImpl;
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
	private final AccessTokenRepository accessTokenRepository;
	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserDetailsServiceImpl userDetailsService;
	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String REFRESH_TOKEN_HEADER = "RefreshToken";
	public static final String AUTHORIZATION_KEY = "auth";
	private static final String BEARER_PREFIX = "Bearer ";

	@Value("${app.auth.accessTokenTime}")
	private Long accessTokenTime;
	@Value("${app.auth.refreshTokenTime}")
	private Long refreshTokenTime;
	@Value("${jwt.secret.key}")
	private String secretKey;
	private Key key;
	private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

	@PostConstruct
	public void init() {
		byte[] bytes = Base64.getDecoder().decode(secretKey);
		key = Keys.hmacShaKeyFor(bytes);
	}

	public String login(String email, UserRole role) {
		if (accessTokenRepository.existsById(email)) {
			log.info("기존의 존재하는 액세스 토큰 삭제");
			accessTokenRepository.deleteById(email);
		}
		if (refreshTokenRepository.existsById(email)) {
			log.info("기존의 존재하는 리프레시 토큰 삭제");
			refreshTokenRepository.deleteById(email);
		}

		String accessToken = createAccessToken(email, role);
		String refreshToken = createRefreshToken(email, role).getRefreshToken();

		AccessToken newAccessToken = AccessToken.builder()
			.email(email)
			.accessToken(accessToken)
			.build();
		accessTokenRepository.save(newAccessToken);

		RefreshToken newRefreshToken = RefreshToken.builder()
			.email(email)
			.refreshToken(refreshToken)
			.build();
		refreshTokenRepository.save(newRefreshToken);

		return accessToken;
	}

	public String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
			return bearerToken.substring(7);
		}
		return null;
	}

	public String resolveRefreshToken(String refreshToken) {
		if (StringUtils.hasText(refreshToken) && refreshToken.startsWith(BEARER_PREFIX)) {
			return refreshToken.substring(7);
		}
		return null;
	}

	public String createAccessToken(String email, UserRole role) {
		Date date = new Date();

		return BEARER_PREFIX +
			Jwts.builder()
				.setSubject(email)
				.claim(AUTHORIZATION_KEY, role)
				.setExpiration(new Date(date.getTime() + accessTokenTime))
				.setIssuedAt(date)
				.signWith(key, signatureAlgorithm)
				.compact();

	}

	public String createAccessToken(Authentication authentication) {
		Date date = new Date();

		UserDetailsImpl user = (UserDetailsImpl)authentication.getPrincipal();

		String email = user.getEmail();
		String role = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		String accessToken = BEARER_PREFIX +
			Jwts.builder()
				.setSubject(email)
				.claim(AUTHORIZATION_KEY, role)
				.setExpiration(new Date(date.getTime() + accessTokenTime))
				.setIssuedAt(date)
				.signWith(key, signatureAlgorithm)
				.compact();

		AccessToken toSaveAccessToken = AccessToken.builder()
			.email(email)
			.accessToken(accessToken)
			.build();

		return toSaveAccessToken.getAccessToken();
	}

	public RefreshToken createRefreshToken(String email, UserRole role) {
		Date date = new Date();

		String refreshToken = BEARER_PREFIX +
			Jwts.builder()
				.setSubject(email)
				.claim(AUTHORIZATION_KEY, role)
				.setExpiration(new Date(date.getTime() + refreshTokenTime))
				.setIssuedAt(date)
				.signWith(key, signatureAlgorithm)
				.compact();

		RefreshToken toSaveRefreshToken = RefreshToken.builder()
			.email(email)
			.refreshToken(refreshToken)
			.build();

		refreshTokenRepository.save(toSaveRefreshToken);

		return toSaveRefreshToken;

	}

	public String createRefreshToken(Authentication authentication) {
		Date date = new Date();

		UserDetailsImpl user = (UserDetailsImpl)authentication.getPrincipal();

		String email = user.getEmail();
		String role = authentication.getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.collect(Collectors.joining(","));

		return BEARER_PREFIX +
			Jwts.builder()
				.setSubject(email)
				.claim(AUTHORIZATION_KEY, role)
				.setExpiration(new Date(date.getTime() + refreshTokenTime))
				.setIssuedAt(date)
				.signWith(key, signatureAlgorithm)
				.compact();
	}

	public void logout(String email) {
		RefreshToken token = refreshTokenRepository.findById(email)
			.orElseThrow(() -> new UserException(ErrorCode.INVALID_REQUEST));
		String refreshToken = token.getRefreshToken();

		if (refreshToken != null) {
			log.info("기존의 존재하는 토큰 모두 삭제");
			refreshTokenRepository.deleteById(email);
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

		Date date = new Date();

		return BEARER_PREFIX +
			Jwts.builder()
				.setSubject(email)
				.claim(AUTHORIZATION_KEY, role)
				.setExpiration(new Date(date.getTime() + accessTokenTime))
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

	public boolean isExpired(String token) {
		try {
			Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getExpiration().before(new Date());
		} catch (ExpiredJwtException e) {
			log.info("Expired JWT token, 만료된 JWT token 입니다.");
			return true;
		}
		return false;
	}

	public boolean isAccessTokenAboutToExpire(String accessToken) {
		Long now = new Date().getTime();
		if (now - getExpirationTime(accessToken).getTime() <= 120000) {
			return true;
		}
		return false;
	}

	public Date getExpirationTime(String token) {
		return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getExpiration();
	}

}