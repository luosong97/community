package life.luosong.community.service;

import life.luosong.community.advice.CustomizeExceptionHandler;
import life.luosong.community.dto.NotificationDTO;
import life.luosong.community.dto.PagenationDTO;
import life.luosong.community.enums.NotificationStatusEnum;
import life.luosong.community.enums.NotificationTypeEnum;
import life.luosong.community.exception.CustomizeErrorCode;
import life.luosong.community.exception.CustomzieException;
import life.luosong.community.mapper.NotificationMapper;
import life.luosong.community.mapper.UserMapper;
import life.luosong.community.model.Notification;
import life.luosong.community.model.NotificationExample;
import life.luosong.community.model.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;
    
    @Autowired
    private UserMapper userMapper;

    public PagenationDTO list(Long userId, Integer page, Integer size) {
        PagenationDTO<NotificationDTO> pagenationDTO = new PagenationDTO<>();
        Integer totalPage;

        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(userId);

        Integer totalCount = (int)notificationMapper.countByExample(notificationExample);


        if(totalCount % size == 0) {
            totalPage = totalCount / size;
        }else {
            totalPage = totalCount / size  + 1;
        }

        if(page < 1){
            page = 1;
        }
        if(page > totalPage){
            page = totalPage;
        }

        Integer offset = size * (page - 1);
        if(offset < 0){
            offset = 0;
        }

        pagenationDTO.setPagenation(totalPage,page);

        NotificationExample example = new NotificationExample();
        example.createCriteria().andReceiverEqualTo(userId);
        example.setOrderByClause("gmt_create desc");
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(example,new RowBounds(offset,size) );

        if(notifications.size() == 0){
            return pagenationDTO;
        }

        List<NotificationDTO> notificationDTOS = new ArrayList<>();

        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification,notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOS.add(notificationDTO);
        }

        pagenationDTO.setData(notificationDTOS);
        return pagenationDTO;
    }

    public Long unreadCount(Long userId) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(userId).andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        return notificationMapper.countByExample(notificationExample);
    }

    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);

        if(notification == null){
            throw new CustomzieException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if(notification.getReceiver() != user.getId()){
            throw new CustomzieException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }

        //修改为已读
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification,notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));

        return notificationDTO;
    }
}
