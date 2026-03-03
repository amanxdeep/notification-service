package com.axd.notificationService.factory;

import com.axd.notificationService.enums.NotificationChannel;
import com.axd.notificationService.enums.NotificationProvider;
import com.axd.notificationService.provider.NotificationProviderService;
import com.axd.notificationService.provider.emailProvider.AwsEmailProviderService;
import com.axd.notificationService.provider.smsProvider.AwsSmsProviderService;
import com.axd.notificationService.provider.smsProvider.TwilioSmsProviderService;
import com.axd.notificationService.provider.whatsAppProvider.TwilioWhatsAppProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ProviderFactory {
    private final Map<NotificationChannel, Map<NotificationProvider, NotificationProviderService>> notificationChannelProviderMap;

    @Autowired
    public ProviderFactory(AwsEmailProviderService awsEmailProviderService, AwsSmsProviderService awsSmsProviderService,
                           TwilioSmsProviderService twilioSmsProviderService, TwilioWhatsAppProviderService twilioWhatsAppProviderService) {
        notificationChannelProviderMap = new HashMap<>();

        Map<NotificationProvider, NotificationProviderService> emailProviderMap = new HashMap<>();
        emailProviderMap.put(NotificationProvider.AWS_SES, awsEmailProviderService);
        notificationChannelProviderMap.put(NotificationChannel.EMAIL, emailProviderMap);

        Map<NotificationProvider, NotificationProviderService> smsProviderMap = new HashMap<>();
        smsProviderMap.put(NotificationProvider.AWS_SES, awsSmsProviderService);
        smsProviderMap.put(NotificationProvider.TWILIO_WHATSAPP, twilioSmsProviderService);
        notificationChannelProviderMap.put(NotificationChannel.SMS, smsProviderMap);

        Map<NotificationProvider, NotificationProviderService> whatsAppProviderMap = new HashMap<>();
        whatsAppProviderMap.put(NotificationProvider.TWILIO_WHATSAPP, twilioWhatsAppProviderService);
        notificationChannelProviderMap.put(NotificationChannel.WHATSAPP, whatsAppProviderMap);
    }

    public NotificationProviderService getProviderService(NotificationChannel notificationChannel, NotificationProvider notificationProvider) {
        Map<NotificationProvider, NotificationProviderService> providerMap = notificationChannelProviderMap.get(notificationChannel);
        return providerMap.get(notificationProvider);
    }

}
