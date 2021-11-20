package com.zf1976.mayi.auth.service;

import com.zf1976.mayi.auth.oauth2.repository.Page;
import com.zf1976.mayi.auth.pojo.dto.RegisteredClientDTO;
import com.zf1976.mayi.auth.pojo.vo.RegisteredClientVO;

import java.util.Set;

public interface OAuth2RegisteredClientService {

    Void sava(RegisteredClientDTO registeredClientDTO);

    RegisteredClientVO findById(String id);

    RegisteredClientVO findByClientId(String clientId);

    Page<RegisteredClientVO> findList(Page<?> page);

    Void removeById(String id);

    Void removeByClientId(String clientId);

    Void removeByIdList(Set<String> idList);

    Void removeByClientIdList(Set<String> clientIdList);

}
