package com.smarttourism.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Authentication response returned after successful login/register. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String  token;
    private Long    id;
    private String  fullName;
    private String  email;
    private String  role;
    private String  message;
}
