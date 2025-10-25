package com.self.notificationService.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenericResponse<T> {
    private T data;
    private GenericError error;

    public static <T> GenericResponse<T> buildSuccess(T data) {
        return new GenericResponse<>(data, null);
    }

    public static <T> GenericResponse<T> buildFailure(GenericError error) {
        return new GenericResponse<>(null, error);
    }

}
