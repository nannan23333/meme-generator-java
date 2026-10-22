package meme.server;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;

/** Web 配置：CORS 全开 + 请求体大小限制（对齐 Rust CorsLayer::very_permissive + DefaultBodyLimit）。 */
@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .exposedHeaders("*")
                        .allowCredentials(false);
            }
        };
    }

    @Bean
    public OncePerRequestFilter bodyLimitFilter(MemeServerProperties props) {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain) throws ServletException, IOException {
                if ("POST".equalsIgnoreCase(request.getMethod())
                        || "PUT".equalsIgnoreCase(request.getMethod())) {
                    String contentLength = request.getHeader("Content-Length");
                    if (contentLength != null
                            && Long.parseLong(contentLength) > props.bodyLimit()) {
                        response.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE,
                                "Request body too large");
                        return;
                    }
                }
                chain.doFilter(request, response);
            }
        };
    }
}
