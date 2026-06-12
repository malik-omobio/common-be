package com.omobio.springbase.dto.permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponsePermissionDTO {
    private UUID id;
    private String displayName;
    private String key;
    @Builder.Default
    private Boolean hasPermission = false;
    /** True when permission is inherited from the user's role (not editable at user level). */
    @Builder.Default
    private Boolean fromRole = false;
    /** True when permission is stored as an additional user-level grant. */
    @Builder.Default
    private Boolean hasAdditional = false;
    /** False when permission is inherited from role and cannot be changed on the user. */
    @Builder.Default
    private Boolean editable = true;
}
