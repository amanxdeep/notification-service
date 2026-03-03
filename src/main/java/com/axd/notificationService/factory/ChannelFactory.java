package com.axd.notificationService.factory;


import com.axd.notificationService.channel.NotificationChannelService;
import com.axd.notificationService.channel.implementations.EmailChannelService;
import com.axd.notificationService.channel.implementations.SmsChannelService;
import com.axd.notificationService.channel.implementations.WhatsAppChannelService;
import com.axd.notificationService.enums.NotificationChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ChannelFactory {

    private final Map<NotificationChannel, NotificationChannelService> channelMap;

    @Autowired
    public ChannelFactory(EmailChannelService emailChannelService, SmsChannelService smsChannelService,
                          WhatsAppChannelService whatsAppChannelService) {
        channelMap = new  HashMap<>();
        channelMap.put(NotificationChannel.EMAIL, emailChannelService);
        channelMap.put(NotificationChannel.SMS, smsChannelService);
        channelMap.put(NotificationChannel.WHATSAPP, whatsAppChannelService);
    }

    public NotificationChannelService getChannel(NotificationChannel channel) {
        return channelMap.get(channel);
    }
}
