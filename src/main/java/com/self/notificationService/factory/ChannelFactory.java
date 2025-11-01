package com.self.notificationService.factory;


import com.self.notificationService.channel.NotificationChannelService;
import com.self.notificationService.channel.implementations.EmailChannelService;
import com.self.notificationService.channel.implementations.SmaChannelService;
import com.self.notificationService.channel.implementations.WhatsAppChannelService;
import com.self.notificationService.enums.NotificationChannel;
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
    public ChannelFactory(EmailChannelService emailChannelService, SmaChannelService smsChannelService,
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
