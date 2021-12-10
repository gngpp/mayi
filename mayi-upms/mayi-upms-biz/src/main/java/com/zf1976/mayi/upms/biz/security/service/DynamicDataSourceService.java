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

package com.zf1976.mayi.upms.biz.security.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;
import com.zf1976.mayi.commom.cache.annotation.CacheConfig;
import com.zf1976.mayi.commom.cache.annotation.CacheEvict;
import com.zf1976.mayi.commom.cache.annotation.CachePut;
import com.zf1976.mayi.commom.cache.constants.KeyConstants;
import com.zf1976.mayi.commom.cache.constants.Namespace;
import com.zf1976.mayi.common.core.util.BooleanUtil;
import com.zf1976.mayi.common.security.access.PermissionAttribute;
import com.zf1976.mayi.common.security.matcher.DynamicRequestMatcher;
import com.zf1976.mayi.common.security.matcher.RequestMatcher;
import com.zf1976.mayi.common.security.matcher.load.LoadDataSource;
import com.zf1976.mayi.common.security.property.SecurityProperties;
import com.zf1976.mayi.upms.biz.dao.SysPermissionDao;
import com.zf1976.mayi.upms.biz.dao.SysResourceDao;
import com.zf1976.mayi.upms.biz.pojo.Permission;
import com.zf1976.mayi.upms.biz.pojo.ResourceLinkBinding;
import com.zf1976.mayi.upms.biz.pojo.ResourceNode;
import com.zf1976.mayi.upms.biz.pojo.po.SysResource;
import com.zf1976.mayi.upms.biz.pojo.query.Query;
import com.zf1976.mayi.upms.biz.security.exception.ResourceException;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


/**
 * 动态资源服务
 *
 * @author mac
 * @date 2020/12/26
 **/
@Service
@CacheConfig(
        namespace = Namespace.RESOURCE,
        postInvoke = {"initialize"}
)
@RefreshScope
@Transactional(readOnly = true, rollbackFor = Throwable.class)
public class DynamicDataSourceService extends ServiceImpl<SysResourceDao, SysResource> implements InitPermission, LoadDataSource {

    private final static byte DEFAULT_CAP = 16;
    private final Map<RequestMatcher, Collection<ConfigAttribute>> requestMap = new ConcurrentHashMap<>(DEFAULT_CAP);
    private final Collection<RequestMatcher> allowRequest = new CopyOnWriteArrayList<>();
    private final Collection<RequestMatcher> blackListRequest = new CopyOnWriteArrayList<>();
    private final SysPermissionDao permissionDao;
    private final SecurityProperties securityProperties;

    public DynamicDataSourceService(SysPermissionDao sysPermissionDao,
            SecurityProperties securityProperties) {
        this.permissionDao = sysPermissionDao;
        this.securityProperties = securityProperties;
    }

    /**
     * 分页查询资源节点
     *
     * @param query 查询对象
     * @return {@link IPage<ResourceNode>}
     */
    @CachePut(key = "#query")
    public IPage<ResourceNode> findByQuery(Query<?> query) {
        // 根据根节点分页查询
        Page<SysResource> sourcePage = super.lambdaQuery()
                                            .isNull(SysResource::getPid)
                                            .page(query.toPage());

        // 查询所有子节点
        List<SysResource> childResourceList = this.permissionDao.selectResourceBindingList()
                                                                .stream()
                                                                .filter(sysResource -> sysResource.getPid() != null)
                                                                .collect(Collectors.toList());
        // 根节点、子节点合并
        @SuppressWarnings("all")
        FluentIterable<SysResource> allResourceList = FluentIterable.concat(childResourceList, sourcePage.getRecords());
        // 根据分页出来的根节点构建资源树
        List<ResourceNode> resourceTreeList = this.generatorResourceTree(allResourceList.toList());
        return new Page<ResourceNode>(sourcePage.getCurrent(),
                sourcePage.getSize(),
                sourcePage.getTotal(),
                sourcePage.isSearchCount()).setRecords(resourceTreeList);
    }

    /**
     * 构建资源树
     *
     * @param resourceList 资源列表
     * @return {@link List<ResourceNode>}
     * @date 2021-05-07 08:42:41
     */
    public List<ResourceNode> generatorResourceTree(List<SysResource> resourceList) {
        @SuppressWarnings("SimplifyStreamApiCallChains")
        List<ResourceNode> treeList = resourceList.stream()
                                                  .map(ResourceNode::new)
                                                  .collect(Collectors.toList());
        // 遍历所有根节点进行构造树
        for (ResourceNode var1 : treeList) {
            // 循环构造子节点
            for (ResourceNode var2 : treeList) {
                if (var1.getId()
                        .equals(var2.getPid())) {
                    if (var1.getChildren() == null) {
                        var1.setChildren(new ArrayList<>());
                    }
                    // 添加子节点
                    var1.getChildren()
                        .add(var2);
                }
            }
        }
        // 树节点进行递归处理，从根节点到各叶子uri进行链接，并给叶子设置完整路径
        return treeList.stream()
                       .filter(resourceNode -> {
                           // 根据树构建完整uri
                           if (resourceNode.getPid() == null) {
                               this.traverseTree(resourceNode);
                               return true;
                           }
                           return false;
                       })
                       .collect(Collectors.toList());
    }

    /**
     * 根据资源树构建资源链接列表
     *
     * @return {@link List<ResourceLinkBinding>}
     * @date 2021-05-07 23:43:49
     */
    public List<ResourceLinkBinding> generatorResourceLinkBindingList(List<ResourceNode> resourceNodeTree) {
        List<ResourceLinkBinding> resourceLinkBindingList = new LinkedList<>();
        resourceNodeTree.forEach(resourceNode -> {
            this.traverseTree(resourceNode, resourceLinkBindingList);
        });
        return resourceLinkBindingList;
    }

    /**
     * 遍历节点构造完整URI
     *
     * @param parentNode       父节点
     * @param resourceLinkBindingList 资源-权限 链接列表
     * @date 2021-05-07 23:40:54
     */
    private void traverseTree(ResourceNode parentNode, List<ResourceLinkBinding> resourceLinkBindingList) {
        // 递归到叶子节点
        if (parentNode.getChildren() == null) {
            if (resourceLinkBindingList != null) {
                // 构造完整资源链接
                ResourceLinkBinding resourceLink = new ResourceLinkBinding();
                resourceLink.setId(parentNode.getId())
                            .setName(parentNode.getName())
                            .setUri(parentNode.getUri())
                            .setMethod(parentNode.getMethod())
                            .setEnabled(parentNode.getEnabled())
                            .setAllow(parentNode.getAllow())
                            .setBindingPermissions(parentNode.getBindingPermissions());
                if (parentNode.getBindingPermissions() == null) {
                    // 查询资源权限
                    List<Permission> permissionList = this.permissionDao.selectPermissionsByResourceId(parentNode.getId());
                    resourceLink.setBindingPermissions(permissionList);
                }
                resourceLinkBindingList.add(resourceLink);
            }
        } else {
            String parentUri = parentNode.getUri();
            for (ResourceNode childNode : parentNode.getChildren()) {
                // 构造uri
                childNode.setUri(parentUri.concat(childNode.getUri()));
                // 递归
                this.traverseTree(childNode, resourceLinkBindingList);
            }
        }
    }

    /**
     * 遍历节点构造完整URI
     *
     * @param parentNode 父节点
     * @date 2021-05-07 23:40:54
     */
    private void traverseTree(ResourceNode parentNode) {
        // 递归到叶子节点
        if (parentNode.getChildren() != null) {
            String parentUri = this.initResourceNodeFullUri(parentNode);
            for (ResourceNode childNode : parentNode.getChildren()) {
                String childUri = this.initResourceNodeFullUri(childNode);
                // 构造uri
                childNode.setFullUri(parentUri.concat(childUri));
                // 递归
                this.traverseTree(childNode);
            }
        }
    }

    /**
     * 初始化节点fullUri属性
     *
     * @param resourceNode 资源节点
     * @return {@link String}
     */
    private String initResourceNodeFullUri(ResourceNode resourceNode) {
        String parentUri = resourceNode.getFullUri();
        if (!StringUtils.hasLength(parentUri)) {
            parentUri = resourceNode.getUri();
        }
        return parentUri;
    }


    /**
     * 加载动态数据源资源
     *
     * @date 2021-05-05 19:53:43
     */
    @CacheEvict
    public void reloadDataSource() {
        //清空缓存
        if (!CollectionUtils.isEmpty(this.requestMap)
                || !CollectionUtils.isEmpty(this.allowRequest)
                || !CollectionUtils.isEmpty(this.blackListRequest)) {
            this.requestMap.clear();
            this.allowRequest.clear();
            this.blackListRequest.clear();
        }
        // 所有资源
        final List<SysResource> resourceList = this.permissionDao.selectResourceBindingList();
        // 构建资源节点树
        final List<ResourceNode> resourceNodeTree = this.generatorResourceTree(resourceList);
        // 绑定权限的资源链接列表
        final List<ResourceLinkBinding> resourceLinkBindingList = this.generatorResourceLinkBindingList(resourceNodeTree);
        resourceLinkBindingList.forEach(resourceLinkBinding -> {
            final Collection<ConfigAttribute> permission = resourceLinkBinding.getBindingPermissions()
                                                                              .stream()
                                                                              .map(Permission::getValue)
                                                                              .map(PermissionAttribute::new)
                                                                              .collect(Collectors.toCollection(ArrayList::new));
            final var pattern = resourceLinkBinding.getUri();
            final var method = resourceLinkBinding.getMethod();
            final var enabled = resourceLinkBinding.getEnabled();
            final var allow = resourceLinkBinding.getAllow();

            /*
             * allow使接口无需权限访问，但仍然需要通过系统认证
             * 1. 如果接口属于关闭状态，不允许allow
             * 2. 如果接口属于开启状态，允许allow
             */
            // enabled
            if (enabled) {
                if (allow) {
                    this.allowRequest.add(new DynamicRequestMatcher(pattern, method));
                } else {
                    this.requestMap.put(new DynamicRequestMatcher(pattern, method), permission);
                }
            } else {
                // blacklist
                this.blackListRequest.add(new DynamicRequestMatcher(pattern, method));
            }
        });
    }

    /**
     * 请求MAP
     *
     * @return {@link Map<RequestMatcher,Collection<ConfigAttribute>>}
     */
    @Override
    @CachePut(key = KeyConstants.RESOURCE_MAP)
    public Map<RequestMatcher, Collection<ConfigAttribute>> loadRequestMap() {
        if (CollectionUtils.isEmpty(this.requestMap)) {
            this.reloadDataSource();
        }
        return this.requestMap;
    }

    /**
     * 加载放行请求
     *
     * @return {@link Collection<RequestMatcher>}
     */
    @Override
    @CachePut(key = KeyConstants.RESOURCE_ALLOW)
    public Collection<RequestMatcher> loadAllowRequest() {
        if (CollectionUtils.isEmpty(this.allowRequest)) {
            this.reloadDataSource();
        }
        // 配置文件白名单
        Set<String> defaultAllow = Sets.newHashSet(this.securityProperties.getIgnoreUri());
        for (String pattern : defaultAllow) {
            this.allowRequest.add(new DynamicRequestMatcher(pattern));
        }
        return this.allowRequest;
    }

    /**
     * 加载黑名单请求
     *
     * @return {@link Collection<RequestMatcher>}
     * @date 2021-05-05 19:53:43
     */
    @CachePut(key = KeyConstants.RESOURCE_BLACK_LIST)
    @Override
    public Collection<RequestMatcher> loadBlackListRequest() {
        if (CollectionUtils.isEmpty(this.blackListRequest)) {
            this.reloadDataSource();
        }
        return this.blackListRequest;
    }

    @Override
    @PostConstruct
    public void initialize() {
        this.reloadDataSource();
    }

    @CacheEvict
    @Transactional
    public Void updateEnabledById(Long id, Boolean enabled) {
        Assert.notNull(id, "id cannot been null");
        Assert.notNull(enabled, "enabled cannot been null");
        SysResource resource = super.lambdaQuery()
                                    .select(SysResource::getId, SysResource::getEnabled, SysResource::getPid)
                                    .eq(SysResource::getId, id)
                                    .oneOpt()
                                    .orElseThrow(() -> new ResourceException("Interface resource does not exist."));
        // enabled
        final var pid = resource.getPid();
        if (pid != null && enabled) {
            final var parentResourceOptional = super.lambdaQuery()
                                                    .select(SysResource::getEnabled)
                                                    .eq(SysResource::getId, pid)
                                                    .oneOpt();
            parentResourceOptional.ifPresent(v -> {
                if (!v.getEnabled()) {
                    throw new ResourceException("Please enabled the parent node resource.");
                }
            });
        }
        // disable
        if (!enabled) {
            List<SysResource> childResourceList = super.lambdaQuery()
                                                       .select(SysResource::getId, SysResource::getEnabled)
                                                       .eq(SysResource::getPid, id)
                                                       .list();
            this.setChildDisableResourceStatus(childResourceList);
            resource.setAllow(false);
        }
        resource.setEnabled(enabled);
        super.updateById(resource);
        return null;
    }

    private void setChildDisableResourceStatus(List<SysResource> resourceList) {
        if (!CollectionUtils.isEmpty(resourceList)) {
            for (SysResource resource : resourceList) {
                resource.setEnabled(false);
                resource.setAllow(false);
            }
            super.updateBatchById(resourceList);
            final var ids = resourceList.stream()
                                        .map(SysResource::getId)
                                        .toList();
            final var childResourceList = super.lambdaQuery()
                                               .select(SysResource::getId, SysResource::getEnabled, SysResource::getAllow)
                                               .in(SysResource::getPid, ids)
                                               .list();
            this.setChildDisableResourceStatus(childResourceList);
        }
    }

    @CacheEvict
    @Transactional
    public Void updateAllowById(Long id, Boolean allow) {
        Assert.notNull(id, "id cannot been null");
        Assert.notNull(allow, "allow cannot been null");
        SysResource resource = super.lambdaQuery()
                                    .select(SysResource::getId, SysResource::getPid,SysResource::getEnabled, SysResource::getAllow)
                                    .eq(SysResource::getId, id)
                                    .oneOpt()
                                    .orElseThrow(() -> new ResourceException("Interface resource does not exist"));
        // enabled
        if (BooleanUtil.isTrue(resource.getEnabled())) {
            final var pid = resource.getPid();
            if (pid != null) {
                final var parentResourceOptional = super.lambdaQuery()
                                                        .select(SysResource::getAllow, SysResource::getEnabled)
                                                        .eq(SysResource::getId, pid)
                                                        .oneOpt();
                parentResourceOptional.ifPresent(v -> {
                    if (!v.getAllow() || !v.getEnabled()) {
                        throw new ResourceException("Please enabled and allow the parent node resource.");
                    }
                });

            } else {
                if (!allow) {
                    List<SysResource> childResourceList = super.lambdaQuery()
                                                               .select(SysResource::getId, SysResource::getAllow)
                                                               .eq(SysResource::getPid, id)
                                                               .list();
                    this.setChildDisabledAllowResourceStatus(childResourceList);
                }
            }
        } else {
            throw new ResourceException("Please enabled the node resource.");
        }
        resource.setAllow(allow);
        super.updateById(resource);
        return null;
    }

    private void setChildDisabledAllowResourceStatus(List<SysResource> resourceList) {
        if (!CollectionUtils.isEmpty(resourceList)) {
            for (SysResource resource : resourceList) {
                resource.setAllow(false);
            }
            super.updateBatchById(resourceList);
            final var ids = resourceList.stream()
                                        .map(SysResource::getId)
                                        .toList();
            final var childResourceList = super.lambdaQuery()
                                               .select(SysResource::getId, SysResource::getAllow)
                                               .in(SysResource::getPid, ids)
                                               .list();
            this.setChildDisabledAllowResourceStatus(childResourceList);
        }
    }

}
