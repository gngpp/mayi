/*
 *
 *  * Copyright (c) 2021 zf1976
 *  *
 *  * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  * of this software and associated documentation files (the "Software"), to deal
 *  * in the Software without restriction, including without limitation the rights
 *  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  * copies of the Software, and to permit persons to whom the Software is
 *  * furnished to do so, subject to the following conditions:
 *  *
 *  * The above copyright notice and this permission notice shall be included in all
 *  * copies or substantial portions of the Software.
 *  *
 *  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING COMMUNICATION_AUTHORIZATION,
 *  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  * SOFTWARE.
 *  *
 *
 */

package com.zf1976.mayi.upms.biz.security.convert;

import com.zf1976.mayi.upms.biz.pojo.dto.PermissionDTO;
import com.zf1976.mayi.upms.biz.pojo.po.SysPermission;
import com.zf1976.mayi.upms.biz.pojo.vo.PermissionVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * @author mac
 * @date 2021/5/11
 */
@SuppressWarnings("all")
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SecurityConvert {

    SecurityConvert INSTANCE = Mappers.getMapper(SecurityConvert.class);

    /**
     * 转权限vo
     *
     * @param sysPermission 系统权限实体对象
     * @return {@link PermissionVO}
     * @date 2021-05-11 23:56:07
     */
    PermissionVO toPermissionVO(SysPermission sysPermission);

    /**
     * permission DTO 转实体
     * @param permissionDTO DTO
     * @return {@link SysPermission}
     */
    SysPermission toPermissionEntity(PermissionDTO permissionDTO);

    /**
     * 复制属性
     *
     * @param permissionDTO DTO
     * @param sysPermission 实体
     * @throws
     * @date 2021-05-12 09:13:09
     */
    void copyProperties(PermissionDTO permissionDTO, @MappingTarget SysPermission sysPermission);


}
