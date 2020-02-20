package life.luosong.community.interceptor;

import life.luosong.community.mapper.UserMapper;
import life.luosong.community.model.User;
import life.luosong.community.model.UserExample;
import life.luosong.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies =  request.getCookies();
        if(cookies == null){
            return true;
        }

        for(Cookie cookie : cookies){
            if(cookie.getName().equals("token")){
                String token = cookie.getValue();
                UserExample example = new UserExample();
                example.createCriteria().andTokenEqualTo(token);
                List<User> users = userMapper.selectByExample(example);
//                User user = userMapper.findByToken(token);
                if(users.size() != 0){
                    request.getSession().setAttribute("user",users.get(0));   //设置session
                    Long unreadCount = notificationService.unreadCount(users.get(0).getId());
                    request.getSession().setAttribute("unreadCount",unreadCount);
                }
                break;
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
