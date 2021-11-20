package com.zf1976.mayi.auth.endpoint;

import com.zf1976.mayi.auth.pojo.dto.RegisteredClientDTO;
import com.zf1976.mayi.auth.service.OAuth2RegisteredClientService;
import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.common.core.validate.ValidationInsertGroup;
import com.zf1976.mayi.common.core.validate.ValidationUpdateGroup;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/security/clients")
public class RegisteredClientController {

    private final OAuth2RegisteredClientService oAuth2RegisteredClientService;


    public RegisteredClientController(OAuth2RegisteredClientService oAuth2RegisteredClientService) {
        this.oAuth2RegisteredClientService = oAuth2RegisteredClientService;
    }

    @PostMapping("/save")
    public DataResult<Void> sava(@RequestBody @Validated(ValidationInsertGroup.class) RegisteredClientDTO dto) {
        return DataResult.success(this.oAuth2RegisteredClientService.sava(dto));
    }

    @PutMapping("/update")
    public DataResult<Void> update(@RequestBody @Validated(ValidationUpdateGroup.class) RegisteredClientDTO dto) {
        return DataResult.success(this.oAuth2RegisteredClientService.sava(dto));
    }


}
