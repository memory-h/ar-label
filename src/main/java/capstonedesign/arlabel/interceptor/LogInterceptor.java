package capstonedesign.arlabel.interceptor;

import capstonedesign.arlabel.logtrace.LogTrace;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogInterceptor implements HandlerInterceptor {

    private final LogTrace logTrace;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 요청자의 IP 주소와 User-Agent 정보를 가져온다.
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String requestUrl = request.getRequestURL().toString(); // 요청된 전체 URL

        // 크롤러가 모든 페이지에 접근하는 것을 차단
        if (request.getRequestURI().equals("/robots.txt")) {
            log.warn("[URL] : {}, [User IP] : {}, [User-Agent] : {}", requestUrl, ipAddress, userAgent);

            return true;
        }

        /*
         * 요청 URL이 "/arlabel"이 아니거나, "/arlabel"이지만 "product-name" 파라미터가 없는 경우,
         * 사용자의 IP와 User-Agent와 함께 로그를 남기고 HTTP Status Code 404를 반환
         */
        else if (!request.getRequestURI().equals("/arlabel") || request.getParameter("product-name") == null) {

            log.warn("[잘못된 요청] [URL] : {}, [User IP] : {}, [User-Agent] : {}", requestUrl, ipAddress, userAgent);

            // HTTP Status Code 404
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

            // 요청 처리 중단
            return false;
        }

        // 사용자 IP와 User-Agent를 포함하여 로그 메시지를 생성하고 추적 시작
        logTrace.requestInfo(ipAddress, userAgent);

        return true;
    }

}