package com.studyhere.studyhere.handlerInterceptor;

import com.studyhere.studyhere.domain.entity.Account;
import com.studyhere.studyhere.domain.userdetail.UserAccount;
import com.studyhere.studyhere.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@RequiredArgsConstructor
public class NotificationInterceptor implements HandlerInterceptor {

    private final NotificationRepository notificationRepository;

    /**postHandle()은 뷰 랜더링 전 핸들러 처리이후**/
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //인증이 된 회원만
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (modelAndView != null && !isRedirectView(modelAndView) && authentication != null
                && authentication.getPrincipal() instanceof UserAccount) {

            Account account = ((UserAccount)authentication.getPrincipal()).getAccount();
            //읽지 않은 알림 갯수
            long count = notificationRepository.countByAccountAndChecked(account, false);
            //true이면 읽지 않은 알람 존재
            modelAndView.addObject("hasNotification", count > 0);
        }
    }

    /**리다이렉트 view가 아닌 경우**/
    private boolean isRedirectView(ModelAndView modelAndView) {
        return modelAndView.getViewName().startsWith("redirect:") || modelAndView.getView() instanceof RedirectView;
    }
}
