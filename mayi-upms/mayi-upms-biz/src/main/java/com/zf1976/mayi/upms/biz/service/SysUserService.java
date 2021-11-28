/*
 * Copyright (c) 2021 zf1976
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING COMMUNICATION_AUTHORIZATION,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.zf1976.mayi.upms.biz.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.zf1976.mayi.commom.cache.annotation.CacheConfig;
import com.zf1976.mayi.commom.cache.annotation.CacheEvict;
import com.zf1976.mayi.commom.cache.annotation.CachePut;
import com.zf1976.mayi.commom.cache.constants.Namespace;
import com.zf1976.mayi.common.core.foundation.exception.BusinessException;
import com.zf1976.mayi.common.core.foundation.exception.BusinessMsgState;
import com.zf1976.mayi.common.core.util.UUIDUtil;
import com.zf1976.mayi.common.core.util.ValidateUtil;
import com.zf1976.mayi.common.core.validate.Validator;
import com.zf1976.mayi.common.encrypt.MD5Encoder;
import com.zf1976.mayi.common.encrypt.RsaUtil;
import com.zf1976.mayi.common.encrypt.property.RsaProperties;
import com.zf1976.mayi.common.security.constants.SecurityConstants;
import com.zf1976.mayi.common.security.property.SecurityProperties;
import com.zf1976.mayi.upms.biz.convert.DepartmentConvert;
import com.zf1976.mayi.upms.biz.convert.UserConvert;
import com.zf1976.mayi.upms.biz.dao.*;
import com.zf1976.mayi.upms.biz.mail.ValidateEmailService;
import com.zf1976.mayi.upms.biz.pojo.Permission;
import com.zf1976.mayi.upms.biz.pojo.Role;
import com.zf1976.mayi.upms.biz.pojo.User;
import com.zf1976.mayi.upms.biz.pojo.dto.user.UpdateEmailDTO;
import com.zf1976.mayi.upms.biz.pojo.dto.user.UpdateInfoDTO;
import com.zf1976.mayi.upms.biz.pojo.dto.user.UpdatePasswordDTO;
import com.zf1976.mayi.upms.biz.pojo.dto.user.UserDTO;
import com.zf1976.mayi.upms.biz.pojo.po.SysDepartment;
import com.zf1976.mayi.upms.biz.pojo.po.SysPosition;
import com.zf1976.mayi.upms.biz.pojo.po.SysRole;
import com.zf1976.mayi.upms.biz.pojo.po.SysUser;
import com.zf1976.mayi.upms.biz.pojo.query.Query;
import com.zf1976.mayi.upms.biz.pojo.query.UserQueryParam;
import com.zf1976.mayi.upms.biz.pojo.vo.dept.DepartmentVO;
import com.zf1976.mayi.upms.biz.pojo.vo.user.UserVO;
import com.zf1976.mayi.upms.biz.property.FileProperties;
import com.zf1976.mayi.upms.biz.security.Context;
import com.zf1976.mayi.upms.biz.service.base.AbstractService;
import com.zf1976.mayi.upms.biz.service.exception.UserException;
import com.zf1976.mayi.upms.biz.service.exception.enums.UserState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统用户(SysUser)表Service接口
 *
 * @author makejava
 * @since 2020-08-31 11:46:01
 */
@Service
@CacheConfig(namespace = Namespace.USER, dependsOn = {Namespace.DEPARTMENT, Namespace.POSITION, Namespace.ROLE})
@Transactional(readOnly = true, rollbackFor = Throwable.class)
public class SysUserService extends AbstractService<SysUserDao, SysUser> {

    private final Logger log = LoggerFactory.getLogger("[SysUserService]");
    private final byte[] DEFAULT_PASSWORD_BYTE = "123456".getBytes(StandardCharsets.UTF_8);
    private final SysPositionDao positionDao;
    private final SysDepartmentDao departmentDao;
    private final SysRoleDao roleDao;
    private final SysPermissionDao permissionDao;
    private final UserConvert userConvert = UserConvert.INSTANCE;
    private final DepartmentConvert departmentConvert = DepartmentConvert.INSTANCE;
    private final SecurityProperties securityProperties;
    private final MD5Encoder md5Encoder = new MD5Encoder();


    public SysUserService(SysPositionDao sysPositionDao,
                          SysDepartmentDao sysDepartmentDao,
                          SysRoleDao sysRoleDao,
                          SysPermissionDao permissionDao,
                          SecurityProperties securityProperties) {
        this.positionDao = sysPositionDao;
        this.departmentDao = sysDepartmentDao;
        this.roleDao = sysRoleDao;
        this.permissionDao = permissionDao;
        this.securityProperties = securityProperties;
    }

    @CachePut(key = "#username")
    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        // 初步验证用户是否存在
        SysUser sysUser = super.baseMapper.selectOneByUsername(username);
        if (sysUser == null) {
            throw new UserException(UserState.USER_NOT_FOUND, username);
        }
        User user = this.userConvert.convert(sysUser);
        // 权限值
        Set<String> grantedAuthorities = this.grantedAuthorities(user.getUsername(), user.getRoleList());
        // 数据权限
        Set<Long> grantedDataPermission = this.grantedDataPermission(user.getUsername(), user.getDepartmentId(), user.getRoleList());
        user.setDataPermissions(grantedDataPermission);
        user.setPermissions(grantedAuthorities);
        return user;
    }

    /**
     * 获取用户数据权限
     * 实际上根据部门id作为数据范围值
     *
     * @param username     用户名
     * @param departmentId 部门id
     * @return 数据权限
     */
    private Set<Long> grantedDataPermission(String username, Long departmentId, List<Role> roleList) {

        // 超级管理员
        if (ObjectUtils.nullSafeEquals(username, this.securityProperties.getOwner())) {
            return this.departmentDao.selectList(Wrappers.emptyWrapper())
                                     .stream()
                                     .map(SysDepartment::getId)
                                     .collect(Collectors.toSet());
        }

        // 用户级别角色排序
        final List<Role> roles = roleList.stream()
                                         .filter(Role::getEnabled)
                                         .sorted(Comparator.comparingInt(Role::getLevel))
                                         .collect(Collectors.toList());
        // 数据权限范围
        final Set<Long> dataPermission = new HashSet<>();
        for (Role role : roles) {
            switch (Objects.requireNonNull(role.getDataScope())) {
                case LEVEL:
                    // 本级数据权限 用户部门
                    dataPermission.add(departmentId);
                    break;

                case CUSTOMIZE:
                    // 自定义用户/角色所在部门的数据权限 - 只能是启用状态部门
                    departmentDao.selectListByRoleId(role.getId())
                                 .stream()
                                 .filter(SysDepartment::getEnabled)
                                 .map(SysDepartment::getId)
                                 .forEach(id -> {
                                     this.collectDepartmentTreeIds(id, dataPermission);
                                 });
                    dataPermission.add(departmentId);
                    break;

                default:
                    // 所有数据权限
                    return ChainWrappers.lambdaQueryChain(this.departmentDao)
                                        .list()
                                        .stream().filter(SysDepartment::getEnabled)
                                        .map(SysDepartment::getId)
                                        .collect(Collectors.toSet());
            }
        }
        return dataPermission;
    }

    /**
     * 获取用户权限
     *
     * @param username 用户名
     * @return 返回用户权限信息
     */
    private Set<String> grantedAuthorities(String username, List<Role> roles) {
        Set<String> authorities = new HashSet<>();
        String markerRoot = securityProperties.getOwner();
        if (username.equals(markerRoot)) {
            // 分配认证超级管理员角色
            authorities.add(SecurityConstants.ROLE.concat(markerRoot));
            return authorities;
        }
        return roles.stream()
                    .flatMap(role -> permissionDao.selectPermissionsByRoleId(role.getId())
                                                  .stream()
                                                  .map(Permission::getValue)
                                                  .filter(StringUtils::hasLength)
                            )
                    .collect(Collectors.toSet());
    }

    /**
     * 收集部门树id
     *
     * @param departmentId id
     * @param handler      collect
     */
    private void collectDepartmentTreeIds(Long departmentId, Set<Long> handler) {
        Assert.notNull(departmentId, "department id can not been null");
        handler.add(departmentId);
        // 角色所有部门
        departmentDao.selectChildrenById(departmentId)
                     .stream()
                     .map(SysDepartment::getId)
                     .forEachOrdered(id -> {
                         this.collectDepartmentTreeIds(id, handler);
                     });
    }

    /**
     * 按条件分页查询用户
     *
     * @param query request page
     * @return /
     */
    @CachePut(key = "#query", dynamicsKey = "#username")
    @Transactional(readOnly = true)
    public IPage<UserVO> findByQuery(Query<UserQueryParam> query, String username) {
        final IPage<SysUser> sourcePage;
        // 非super admin 过滤数据权限
        if (Context.isOwner()) {
            sourcePage = super.queryWrapper()
                              .chainQuery(query)
                              .selectPage();

        } else {
            // 用户可观察数据范围
            Set<Long> dataPermission = this.findByUsername(username).getDataPermissions();
            List<Long> userIds = super.baseMapper.selectIdsByDepartmentIds(dataPermission);
            sourcePage = super.queryWrapper()
                              .chainQuery(query, () -> {
                                  // 自定义条件
                                  return ChainWrappers.queryChain(super.baseMapper)
                                                      .in(!CollectionUtils.isEmpty(userIds), getColumn(SysUser::getId), userIds);
                              })
                              .selectPage();
        }
        // 根据部门分页
        Optional.ofNullable(query.getQuery())
                .ifPresent(queryParam -> {
                    if (queryParam.getDepartmentId() != null) {
                        // 当前查询部门
                        Long departmentId = queryParam.getDepartmentId();
                        Set<Long> collectIds = new HashSet<>();
                        this.departmentDao.selectChildrenById(departmentId)
                                          .stream()
                                          .map(SysDepartment::getId)
                                          .forEach(id -> this.selectDepartmentTreeIds(id, collectIds));
                        collectIds.add(departmentId);
                        List<SysUser> collectUser = sourcePage.getRecords()
                                                              .stream()
                                                              .filter(sysUser -> collectIds.contains(sysUser.getDepartmentId()))
                                                              .collect(Collectors.toList());
                        sourcePage.setRecords(collectUser);
                    }
                });
        return super.mapPageToTarget(sourcePage, this.userConvert::toVo);
    }

        /**
         * 获取当前部门树下所有id
         *
         * @param departmentId id
         * @param supplier supplier
         */
        @Transactional(readOnly = true)
        public void selectDepartmentTreeIds (Long departmentId, Collection < Long > supplier){
            Assert.notNull(departmentId, "department id can not been null");
            supplier.add(departmentId);
            this.departmentDao.selectChildrenById(departmentId)
                              .stream()
                              .map(SysDepartment::getId)
                              .forEach(id -> {
                                  // 继续下一级子节点
                                  this.selectDepartmentTreeIds(id, supplier);
                              });
        }

        /**
         * 获取用户所有角色id
         *
         * @param username 用户名
         * @return role id
         */
        @Transactional(readOnly = true)
        public Set<Long> findRoleByUsername(String username){
            return this.roleDao.selectListByUsername(username)
                               .stream()
                               .map(SysRole::getId)
                               .collect(Collectors.toSet());
        }

    /**
     * 获取用户所有角色id
     *
     * @param id id
     * @return role id
     */
    @Transactional(readOnly = true)
    public Set<Long> findRoleById(Long id){
        return this.roleDao.selectListByUserId(id)
                           .stream()
                           .map(SysRole::getId)
                           .collect(Collectors.toSet());
    }

    /**
     * 获取用户级别部门
     *
     * @param id 部门id
     * @return role id
     */
    @Transactional(readOnly = true)
    public DepartmentVO findDepartmentById(Long id){
        final var sysDepartment = ChainWrappers.lambdaQueryChain(this.departmentDao)
                                               .select(SysDepartment::getId, SysDepartment::getName)
                                               .eq(SysDepartment::getId, id)
                                               .one();
        return this.departmentConvert.toVo(sysDepartment);
    }

    /**
     * 获取用户职位id
     *
     * @param id id
     * @return position id
     */
    @Transactional(readOnly = true)
    public Set<Long> findPositionById(Long id){
        return this.positionDao.selectListByUserId(id)
                               .stream()
                               .filter(SysPosition::getEnabled)
                               .map(SysPosition::getId)
                               .collect(Collectors.toSet());
    }

        /**
         * 更新用户状态
         *
         * @param id      id
         * @param enabled enabled
         * @return /
         */
        @CacheEvict
        @Transactional
        public Void updateByIdAndEnabled(Long id, Boolean enabled){
            SysUser sysUser = super.lambdaQuery()
                                   .eq(SysUser::getId, id)
                                   .oneOpt()
                                   .orElseThrow(() -> new UserException(UserState.USER_NOT_FOUND));

            String currentUsername = Context.username();
            // 禁止操作oneself,当前session ID与操作ID相等，说明操作到是当前用户
            Validator.of(currentUsername)
                     .withValidated(username -> !username.equals(sysUser.getUsername()),
                             () -> new UserException(UserState.USER_OPT_DISABLE_ONESELF_ERROR));
            // 禁止禁用管理员
            Validator.of(sysUser)
                     .withValidated(user -> !user.getUsername().equals(this.securityProperties.getOwner()),
                             () -> new UserException(UserState.USER_OPT_DISABLE_ONESELF_ERROR));
            // 设置状态
            sysUser.setEnabled(enabled);
            // 更新
            super.savaOrUpdate(sysUser);
            return null;
        }

        /**
         * 修改头像
         *
         * @param multipartFile 上传头像
         * @return /
         */
        @CacheEvict
        @Transactional
        public Void updateAvatar (MultipartFile multipartFile){
            final SysUser sysUser = super.lambdaQuery()
                                         .select(SysUser::getId, SysUser::getAvatarName)
                                         .eq(SysUser::getUsername, Context.username())
                                         .one();
            String filename = null;
            try {
                // 原文件名
                final String avatarName = sysUser.getAvatarName();
                // 上传文件名
                String originalFilename = multipartFile.getOriginalFilename();
                Assert.notNull(originalFilename, "filename cannot been null!");
                // 新文件名
                filename = UUIDUtil.getUpperCaseUuid() + originalFilename.substring(originalFilename.lastIndexOf("."));
                // 写入新头像文件
                multipartFile.transferTo(Paths.get(FileProperties.getAvatarRealPath(), filename));
                // 实体保存新文件名
                sysUser.setAvatarName(filename);
                // 进行更新
                super.savaOrUpdate(sysUser);
                // 删除旧头像文件
                Files.deleteIfExists(Paths.get(FileProperties.getAvatarRealPath(), avatarName));
            } catch (Exception e) {
                this.log.error(e.getMessage(), e.getCause());
                // 防止文件已经创建，但写入不完全，进行删除处理
                if (filename != null) {
                    try {
                        Files.deleteIfExists(Paths.get(FileProperties.getAvatarRealPath(), filename));
                    } catch (IOException ioException) {
                        log.error(ioException.getMessage(), ioException.getCause());
                    }
                }
                throw new BusinessException(BusinessMsgState.UPLOAD_ERROR);
            }
            return null;
        }

        /**
         * change password
         *
         * @param dto dto
         */
        @CacheEvict
        @Transactional
        public Void updatePassword (UpdatePasswordDTO dto){
            final SysUser sysUser = super.lambdaQuery()
                                         .select(SysUser::getId, SysUser::getPassword)
                                         .eq(SysUser::getUsername, Context.username())
                                         .oneOpt()
                                         .orElseThrow(() -> new BusinessException(BusinessMsgState.CODE_NOT_FOUNT));
            String rawPassword;
            String freshPassword;
            try {
                rawPassword = RsaUtil.decryptByPrivateKey(RsaProperties.PRIVATE_KEY, dto.getOldPass());
                freshPassword = RsaUtil.decryptByPrivateKey(RsaProperties.PRIVATE_KEY, dto.getNewPass());
            } catch (Exception e) {
                throw new BusinessException(BusinessMsgState.OPT_ERROR);
            }

            // Verify the original password, whether the new password is empty
            if (StringUtils.isEmpty(rawPassword) || StringUtils.isEmpty(freshPassword)) {
                throw new BusinessException(BusinessMsgState.NULL_PASSWORD);
            }

            Validator.of(freshPassword)
                     // verify if the password is repeated
                     .withValidated(value -> ObjectUtils.nullSafeEquals(value, rawPassword),
                             () -> new BusinessException(BusinessMsgState.PASSWORD_REPEAT))
                     // verify password eligibility
                     .withValidated(ValidateUtil::isPassword,
                             () -> new BusinessException(BusinessMsgState.PASSWORD_LOW));

            // password matching verification
            Validator.of(rawPassword)
                     .withValidated(value -> md5Encoder.matches(value, sysUser.getPassword()),
                             () -> new BusinessException(BusinessMsgState.PASSWORD_REPEAT));

            // set new password
            sysUser.setPassword(md5Encoder.encode(freshPassword));
            // update entity
            super.savaOrUpdate(sysUser);
            // force users to log in again
            Context.revokeAuthentication();
            return null;
        }

        /**
         * modify email
         *
         * @param code verification code
         * @param dto dto
         * @return /
         */
        @CacheEvict
        @Transactional
        public Void updateEmail (String code, UpdateEmailDTO dto){

            // check whether the verification code is empty
            if (!StringUtils.hasLength(code) || ObjectUtils.isEmpty(dto)) {
                throw new BusinessException(BusinessMsgState.PARAM_ILLEGAL);
            }

            ValidateEmailService validateService = ValidateEmailService.validateEmailService();
            // query user
            var sysUser = super.lambdaQuery()
                               .select(SysUser::getId, SysUser::getPassword, SysUser::getEmail)
                               .eq(SysUser::getUsername, Context.username())
                               .oneOpt()
                               .orElseThrow(() -> new UserException(UserState.USER_NOT_FOUND));
            if (validateService.validateVerifyCode(dto.getEmail(), code)) {
                try {
                    String rawPassword = RsaUtil.decryptByPrivateKey(RsaProperties.PRIVATE_KEY, dto.getPassword());
                    if (md5Encoder.matches(rawPassword, sysUser.getPassword())) {
                        // verify whether the mailbox is duplicate
                        if (ObjectUtils.nullSafeEquals(sysUser.getEmail(), dto.getEmail())) {
                            throw new BusinessException(BusinessMsgState.EMAIL_EXISTING);
                        }
                        // set up a new mailbox
                        sysUser.setEmail(dto.getEmail());
                        // update entity
                        super.savaOrUpdate(sysUser);
                    }
                } catch (BusinessException e) {
                    throw e;
                } catch (Exception e) {
                    throw new BusinessException(BusinessMsgState.OPT_ERROR);
                } finally {
                    validateService.clearVerifyCode(dto.getEmail());
                }
            }
            return null;
        }

        /**
         * personal center information modification
         *
         * @param dto dto
         * @return /
         */
        @CacheEvict
        @Transactional
        public Void updateInfo(UpdateInfoDTO dto){
            // query whether the current user exists
            SysUser sysUser = super.lambdaQuery()
                                   .eq(SysUser::getId, dto.getId())
                                   .oneOpt()
                                   .orElseThrow(() -> new BusinessException(BusinessMsgState.DATA_NOT_FOUNT));
            // non-mobile phone numbers are not allowed
            if (!ValidateUtil.isPhone(dto.getPhone())) {
                throw new BusinessException(BusinessMsgState.NOT_PHONE);
            }

            sysUser.setPhone(dto.getPhone());
            sysUser.setGender(dto.getGender());
            sysUser.setNickName(dto.getNickName());
            super.savaOrUpdate(sysUser);
            return null;
        }

        /**
         * new users
         *
         * @param dto dto
         * @return /
         */
        @CacheEvict
        @Transactional
        public Void saveOne(UserDTO dto){
            this.validateUsername(dto.getUsername());
            this.validatePhone(dto.getPhone());
            super.lambdaQuery()
                 .select(SysUser::getUsername, SysUser::getEmail, SysUser::getPhone)
                 .and(sysUserLambdaQueryWrapper -> {
                     // verify that the username is unique
                     // verify that the mailbox is unique
                     // verify that the mobile phone number is unique
                     sysUserLambdaQueryWrapper.eq(SysUser::getUsername, dto.getUsername())
                                              .or()
                                              .eq(SysUser::getEmail, dto.getEmail())
                                              .or()
                                              .eq(SysUser::getPhone, dto.getPhone());
                 })
                 .list()
                 .forEach(sysUser -> super.validateFields(sysUser, dto, collection -> {
                     if (!CollectionUtils.isEmpty(collection)) {
                         throw new UserException(UserState.USER_INFO_EXISTING, collection.toString());
                     }
                 }));
            // transfer entity
            SysUser sysUser = this.userConvert.toEntity(dto);
            // set encryption password
            sysUser.setPassword(DigestUtils.md5DigestAsHex(DEFAULT_PASSWORD_BYTE));
            // save user
            super.savaOrUpdate(sysUser);
            // save user position association
            super.baseMapper.savePositionRelationById(sysUser.getId(), dto.getPositionIds());
            // save user role association
            super.baseMapper.savaRoleRelationById(sysUser.getId(), dto.getRoleIds());
            return null;
        }

        /**
         * update user
         *
         * @param dto dto
         * @return /
         */
        @CacheEvict
        @Transactional
        public Void updateOne(UserDTO dto){
            this.validatePhone(dto.getPhone());
            // Query whether the user exists
            SysUser sysUser = super.lambdaQuery()
                                   .eq(SysUser::getId, dto.getId())
                                   .oneOpt()
                                   .orElseThrow(() -> new UserException(UserState.USER_NOT_FOUND));
            // Disable
            if (!dto.getEnabled()) {
                String currentUsername = Context.username();
                // It is forbidden to disable oneself, and it is forbidden to operate the currently logged-in user
                Validator.of(currentUsername)
                         .withValidated(username -> !username.equals(sysUser.getUsername()),
                                 () -> new UserException(UserState.USER_OPT_DISABLE_ONESELF_ERROR));
                // prohibit disabling the administrator
                Validator.of(dto.getUsername())
                         .withValidated(username -> !username.equals(this.securityProperties.getOwner()),
                                 () -> new UserException(UserState.USER_OPT_ERROR));
                // Revoke the authentication of the currently disabled user
                Context.revokeAuthentication(dto.getUsername());
            }
            // Verify username, whether it already exists
            super.lambdaQuery()
                 .select(SysUser::getUsername)
                 .ne(SysUser::getId, dto.getId())
                 .and(queryWrapper -> queryWrapper.eq(SysUser::getUsername, dto.getUsername()))
                 .list()
                 .forEach(entity -> super.validateFields(entity, dto, collection -> {
                     if (!CollectionUtils.isEmpty(collection)) {
                         throw new UserException(UserState.USER_INFO_EXISTING, collection.toString());
                     }
                 }));
            // copy attributes
            this.userConvert.copyProperties(dto, sysUser);
            // renew
            super.savaOrUpdate(sysUser);
            // update dependencies
            this.updateDependents(dto);
            return null;
        }

        /**
         * update user dependencies
         *
         * @param dto dto
         */
        private void updateDependents (UserDTO dto){
            // user id
            final Long userId = dto.getId();
            // user post id collection
            final Set<Long> positionIds = dto.getPositionIds();
            if (!CollectionUtils.isEmpty(positionIds)) {
                super.baseMapper.deletePositionRelationById(userId);
                super.baseMapper.savePositionRelationById(userId, positionIds);
            }
            // user role id collection
            final Set<Long> roleIds = dto.getRoleIds();
            if (!CollectionUtils.isEmpty(roleIds)) {
                super.baseMapper.deleteRoleRelationById(userId);
                super.baseMapper.savaRoleRelationById(dto.getId(), roleIds);
            }
        }

        /**
         * verify mobile phone number
         *
         * @param phone phone
         */
        private void validatePhone (String phone){
            if (ValidateUtil.isNotPhone(phone)) {
                throw new BusinessException(BusinessMsgState.NOT_PHONE);
            }
        }

        /**
         * verify username
         *
         * @date 2021-05-16 12:06:34
         * @param username 用户名
         */
        private void validateUsername (String username){
            if (!ValidateUtil.isUserName(username)) {
                throw new BusinessException(BusinessMsgState.USERNAME_LOW);
            }
        }

        /**
         * delete users
         *
         * @param ids ids
         * @return /
         */
        @CacheEvict
        @Transactional
        public Void deleteByIds(Set <Long> ids) {
            SysUser currentUser = super.lambdaQuery()
                                   .eq(SysUser::getUsername, Context.username())
                                   .oneOpt()
                                   .orElseThrow(() -> new UserException(UserState.USER_NOT_FOUND));
            // prohibit deleting the current operating user
            if (ids.contains(currentUser.getId())) {
                throw new UserException(UserState.USER_PROHIBIT_ERROR);
            }
            final var sysUserList = super.lambdaQuery()
                                  .in(SysUser::getId, ids)
                                  .list();
            // prohibit deleting super administrators
            if (sysUserList.stream()
                           .map(SysUser::getUsername)
                           .anyMatch(Context::isOwner)) {
                throw new UserException(UserState.USER_OPT_ERROR);
            }
            // delete user relationship dependency
            for (SysUser user : sysUserList) {
                super.baseMapper.deleteRoleRelationById(user.getId());
                super.baseMapper.deletePositionRelationById(user.getId());
                try {
                    // revoke user authentication
                    Context.revokeAuthentication(user.getUsername());
                } catch (Exception e) {
                    log.info(e.getMessage(), e.getCause());
                }
            }
            // delete users
            super.deleteByIds(ids);
            return null;
        }
    }


