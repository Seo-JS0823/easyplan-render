package com.easyplan._01_web.security.filter;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.easyplan._01_web.webutil.CookieProp;
import com.easyplan._01_web.webutil.CookieProvider;
import com.easyplan._03_domain.auth.service.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
	private final UserDetailsService userService;
	
	private final CookieProvider cookie;
	
	private final TokenService tokenService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String aToken = cookie.getCookieValue(CookieProp.ACCESS, request);
		String rToken = cookie.getCookieValue(CookieProp.REFRESH, request);
		
		if(aToken == null) {
			if(rToken != null) {
				// TODO reissue
			}
			
			String requestURI = request.getRequestURI();
			
			if(isRedirectUri(requestURI)) {
				response.sendRedirect("/");
				return;
			}
			
			filterChain.doFilter(request, response);
			return;
		}
		
		try {
			String publicId = tokenService.extractTokenClaims(aToken).getPublicId();
			
			UsernamePasswordAuthenticationToken authentication = authentication(publicId);
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (Exception e) {
			
		}
		
		
		filterChain.doFilter(request, response);
	}

	private boolean isRedirectUri(String uri) {
    return uri.equals("/user/my");
	}
	
	private UsernamePasswordAuthenticationToken authentication(String publicId) {
		UserDetails details = userService.loadUserByUsername(publicId);
		
		return new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
	}
	
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getRequestURI();
    AntPathMatcher pathMatcher = new AntPathMatcher();
		
		String[] excludePath = {
			"/css/**", "/js/**", "/img/**", "/favicon.ico", "/error", "/.well-known/**",
			"/",
    };
		
		return Arrays.stream(excludePath)
        .anyMatch(pattern -> pathMatcher.match(pattern, path));
	}
}
