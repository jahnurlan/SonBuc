package com.example.userms.model.dto.response;

import com.example.userms.model.enums.RoleType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponseDto {
    String username;
    String email;
    LocalDateTime joinDate;
    RoleType role;
    boolean status;

}
