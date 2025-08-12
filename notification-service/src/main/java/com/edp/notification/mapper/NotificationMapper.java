package com.edp.notification.mapper;

import com.edp.notification.data.document.Notification;
import com.edp.notification.model.NotificationSubmissionDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationSubmissionDTO toSubmissionNotificationDTO(Notification notification);

    List<NotificationSubmissionDTO> toSubmissionNotificationDTOs(List<Notification> notifications);
}