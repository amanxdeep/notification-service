package com.self.notificationService.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.ObjectUtils;

@UtilityClass
public final class CommonUtils {

    public static boolean isNotEmpty(final Object input ){
        return ObjectUtils.isEmpty(input);
    }
}
