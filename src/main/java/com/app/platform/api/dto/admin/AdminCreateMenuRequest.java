package com.app.platform.api.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AdminCreateMenuRequest(
		@NotBlank @Size(max = 128) String name,
		Long parentId,
		@Size(max = 256) String path,
		@Size(max = 128) String icon,
		@Pattern(regexp = "[0123]") String menuType,
		@Size(max = 16) String clientType,
		Integer sequ,
		@Size(max = 16) String controlType
) {
}
