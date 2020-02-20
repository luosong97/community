package life.luosong.community.service;

import life.luosong.community.mapper.UserMapper;
import life.luosong.community.model.User;
import life.luosong.community.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public void createOrUpdate(User user) {
        UserExample example = new UserExample();
        example.createCriteria().andAccountIdEqualTo(user.getAccountId());
        List<User> users = userMapper.selectByExample(example);
        if(users.size() == 0){
            //insert
            user.setGmtCreate(System.currentTimeMillis()); //当前时间
            user.setGmtModified(user.getGmtCreate());
            userMapper.insert(user);
        }else {
            //update
            User dbUser = users.get(0);
            User updateUser = new User();
            updateUser.setGmtModified(System.currentTimeMillis());
            updateUser.setAvatarUrl(user.getAvatarUrl());
            updateUser.setName(user.getName());
            updateUser.setToken(user.getToken());

            example.createCriteria().andIdEqualTo(dbUser.getId());

            userMapper.updateByExampleSelective(updateUser,example);

        }
    }
}
