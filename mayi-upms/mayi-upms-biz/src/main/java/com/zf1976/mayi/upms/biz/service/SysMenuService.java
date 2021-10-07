package com.zf1976.mayi.upms.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.mayi.common.component.cache.annotation.CacheConfig;
import com.zf1976.mayi.common.component.cache.annotation.CacheEvict;
import com.zf1976.mayi.common.component.cache.annotation.CachePut;
import com.zf1976.mayi.common.core.constants.Namespace;
import com.zf1976.mayi.common.security.support.session.manager.SessionManagement;
import com.zf1976.mayi.upms.biz.convert.SysMenuConvert;
import com.zf1976.mayi.upms.biz.dao.SysMenuDao;
import com.zf1976.mayi.upms.biz.dao.SysRoleDao;
import com.zf1976.mayi.upms.biz.pojo.dto.menu.MenuDTO;
import com.zf1976.mayi.upms.biz.pojo.dto.menu.MenuTypeEnum;
import com.zf1976.mayi.upms.biz.pojo.po.SysMenu;
import com.zf1976.mayi.upms.biz.pojo.po.SysRole;
import com.zf1976.mayi.upms.biz.pojo.query.MenuQueryParam;
import com.zf1976.mayi.upms.biz.pojo.query.Query;
import com.zf1976.mayi.upms.biz.pojo.vo.menu.MenuBuildVO;
import com.zf1976.mayi.upms.biz.pojo.vo.menu.MenuVO;
import com.zf1976.mayi.upms.biz.service.base.AbstractService;
import com.zf1976.mayi.upms.biz.service.exception.MenuException;
import com.zf1976.mayi.upms.biz.service.exception.enums.MenuState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

/**
 * 系统菜单(SysMenu)表Service接口
 *
 * @author makejava
 * @since 2020-08-31 11:46:01
 */
@Service
@CacheConfig(namespace = Namespace.MENU, dependsOn = Namespace.ROLE)
public class SysMenuService extends AbstractService<SysMenuDao, SysMenu> {

    private final Logger log = LoggerFactory.getLogger("[SysMenuService]");
    private static final String LAYOUT_NAME = "Layout";
    public static final String NO_REDIRECT = "no_redirect";
    public static final String INDEX = "index";
    public static final String SLASH = "/";
    private final SysRoleDao sysRoleDao;
    private final SysMenuConvert convert;

    public SysMenuService(SysRoleDao sysRoleDao) {
        this.sysRoleDao = sysRoleDao;
        this.convert = SysMenuConvert.INSTANCE;
    }

    /**
     * 获取用户菜单路由
     *
     * @return menu list
     */
    @Transactional(readOnly = true)
    public Collection<MenuBuildVO> generatedMenu() {
        // 菜单
        List<SysMenu> menuList;
        if (SessionManagement.isOwner()) {
            // 管理员获取所有菜单
            menuList = super.selectList();
        } else {
            // 用户id
            final long sessionId = SessionManagement.getSessionId();
            // 获取用户所有角色id
            final Set<Long> roleIds = sysRoleDao.selectBatchByUserId(sessionId)
                                                .stream()
                                                .map(SysRole::getId)
                                                .collect(Collectors.toSet());
            // 获取用户所有菜单
            menuList = this.baseMapper.selectListByRoleIds(roleIds);
        }

        LongAdder maxType = new LongAdder();
        // 获取非叶子
        Optional.ofNullable(baseMapper.selectMaxType())
                .ifPresent(maxType::add);
        final List<SysMenu> targetMenu =  menuList.stream()
                                                  .filter(sysMenu -> sysMenu.getType() < maxType.longValue())
                                                  .sorted(Comparator.comparingInt(SysMenu::getMenuSort))
                                                  .collect(Collectors.toList());
        // 生成菜单树
        for (SysMenu sysMenu : targetMenu) {
            targetMenu.stream()
                     .filter(childrenMenuList ->
                                     !ObjectUtils.nullSafeEquals(sysMenu.getId(), childrenMenuList.getId())
                                             && !ObjectUtils.isEmpty(childrenMenuList.getPid())
                                             && ObjectUtils.nullSafeEquals(sysMenu.getId(), childrenMenuList.getPid()))
                     .forEach(menuVo -> {
                         if (CollectionUtils.isEmpty(sysMenu.getChildren())) {
                             sysMenu.setChildren(new LinkedList<>());
                         }
                         sysMenu.getChildren().add(menuVo);
                     });
        }
        // 获取顶级节点树
        final List<SysMenu> targetTree = targetMenu.stream()
                                                 .filter(sysMenu -> ObjectUtils.isEmpty(sysMenu.getPid()))
                                                 .collect(Collectors.toList());
        return Collections.unmodifiableCollection(this.generatedRoute(targetTree));
    }

    /**
     * 初始化菜单路由
     *
     * @param menuTree 菜单树
     */
    private List<MenuBuildVO> generatedRoute(List<SysMenu> menuTree) {
        List<MenuBuildVO> menuBuildList = new LinkedList<>();
        for (SysMenu menu : menuTree) {
            if (Objects.nonNull(menu)) {
                final List<SysMenu> childrenMenu = menu.getChildren();
                final MenuBuildVO var1 = new MenuBuildVO();
                String componentName = StringUtils.isEmpty(menu.getComponentName()) ? menu.getTitle() : menu.getComponentName();
                var1.setName(componentName);
                // 前端一级目录需要添加斜杠
                String path = StringUtils.isEmpty(menu.getPid()) ? SLASH + menu.getRoutePath() : menu.getRoutePath();
                var1.setPath(path);
                var1.setHidden(menu.getHidden());
                var1.intiMeta(menu.getTitle(), menu.getIcon(), !menu.getCache());
                // 非外链
                if (!menu.getIframe()) {
                    // 是否一级目录
                    if (Objects.isNull(menu.getPid())) {
                        String componentPath = StringUtils.isEmpty(menu.getComponentPath()) ? LAYOUT_NAME : menu.getComponentPath();
                        var1.setComponent(componentPath);
                    } else if (!StringUtils.isEmpty(menu.getComponentPath())) {
                        var1.setComponent(menu.getComponentPath());
                    }
                }
                if (!CollectionUtils.isEmpty(childrenMenu)) {
                    var1.setAlwaysShow(true);
                    var1.setRedirect(NO_REDIRECT);
                    var1.setChildren(this.generatedRoute(childrenMenu));
                    // 顶级菜单 无子菜单
                }else if (Objects.isNull(menu.getPid())) {
                    MenuBuildVO var2 = new MenuBuildVO(var1.getMeta());
                    // 是否外链
                    if (!menu.getIframe()) {
                        var2.setPath(INDEX);
                        var2.setName(var1.getName());
                        var2.setComponent(var1.getComponent());
                    }else {
                        var2.setPath(menu.getRoutePath());
                    }
                    List<MenuBuildVO> vars = new LinkedList<>();
                    vars.add(var2);
                    var1.setComponent(LAYOUT_NAME);
                    var1.setName(null);
                    var1.setMeta(null);
                    var1.setChildren(vars);
                }
                menuBuildList.add(var1);
            }
        }
        return menuBuildList;
    }

    /**
     * 菜单查询
     *
     * @param page request page
     * @return page
     */
    @CachePut(key = "#page")
    public IPage<MenuVO> selectMenuPage(Query<MenuQueryParam> page) {
        final IPage<SysMenu> sourcePage = super.queryWrapper()
                                               .chainQuery(page)
                                               .selectPage();
        return this.menuTreeBuilder(sourcePage);
    }

    /**
     * 菜单树构建
     *
     * @param sourcePage source page
     * @return tree page
     */
    private IPage<MenuVO> menuTreeBuilder(IPage<SysMenu> sourcePage) {
        final IPage<MenuVO> targetPage = super.mapPageToTarget(sourcePage, this.convert::toVo);
        // 所有节点
        final List<MenuVO> vertex = new LinkedList<>(targetPage.getRecords());
        vertex.forEach(var1 -> {
            vertex.stream()
                  .filter(var2 -> !ObjectUtils.nullSafeEquals(var2.getId(), var1.getId()))
                  .forEach(var2 -> {
                      if (!ObjectUtils.isEmpty(var2.getPid()) && ObjectUtils.nullSafeEquals(var1.getId(), var2.getPid())) {
                          if (ObjectUtils.isEmpty(var1.getChildren())) {
                              var1.setChildren(new LinkedList<>());
                          }
                          var1.getChildren().add(var2);
                      }
                  });
            // 设置menu tree properties
            this.setMenuTreeProperties(var1);
        });
        // 排除已被添加的节点
        final List<MenuVO> target = vertex.parallelStream()
                                           .filter(menuVO -> ObjectUtils.isEmpty(menuVO.getPid()))
                                           .sorted(Comparator.comparingInt(MenuVO::getMenuSort))
                                           .collect(Collectors.toList());
        return targetPage.setRecords(Collections.unmodifiableList(target));
    }

    /**
     * 设置菜单树属性
     *
     * @param vo vo
     */
    public void setMenuTreeProperties(MenuVO vo) {
        final boolean hasChildrenMenu = CollectionUtils.isEmpty(vo.getChildren());
        vo.setHasChildren(!hasChildrenMenu);
        vo.setLeaf(hasChildrenMenu);
    }

    /**
     * 排除本级菜单所在的菜单树
     * @param id id
     * @return 满足前提条件的菜单树
     */
    @CachePut(key = "#id")
    @Transactional(readOnly = true)
    public IPage<MenuVO> selectMenuVertex(Long id) {
        super.lambdaQuery()
             .eq(SysMenu::getId, id)
             .oneOpt().orElseThrow(() -> new MenuException(MenuState.MENU_NOT_FOUND));
        final IPage<SysMenu> sourcePage = super.queryWrapper()
                                               .chainQuery(new Query<>())
                                               .selectPage();
        // 收集本级菜单下所有子菜单id集合
        final Set<Long> nextMenuLowIds = this.collectCurrentChildrenMenuIds(id, new HashSet<>());
        // 排除本级菜单
        nextMenuLowIds.add(id);
        // 过滤下级菜单以及本菜单
        List<SysMenu> collect = sourcePage.getRecords()
                                          .stream()
                                          .filter(sysMenu -> !nextMenuLowIds.contains(sysMenu.getId()))
                                          .sorted(Comparator.comparing(SysMenu::getMenuSort))
                                          .collect(Collectors.toList());
        // 构建菜单树并返回
        return this.menuTreeBuilder(sourcePage.setRecords(collect));
    }

    /**
     * 新增菜单
     *
     * @param dto dtp
     * @return /
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Void saveMenu(MenuDTO dto) {
        // 是否存在匹配菜单类型
        Optional.ofNullable(MenuTypeEnum.match(dto.getType()))
                .orElseThrow(() -> new MenuException(MenuState.MENU_OPT_ERROR));
        // 确认菜单是否已存在
        super.lambdaQuery()
             .select(SysMenu::getTitle, SysMenu::getComponentName, SysMenu::getComponentPath, SysMenu::getRoutePath)
             .and(sysMenuLambdaQueryWrapper -> sysMenuLambdaQueryWrapper.eq(SysMenu::getTitle, dto.getTitle())
                                                                        .or()
                                                                        .eq(SysMenu::getComponentPath, dto.getComponentPath())
                                                                        .or()
                                                                        .eq(SysMenu::getComponentName, dto.getComponentPath())
                                                                        .or()
                                                                        .eq(SysMenu::getRoutePath, dto.getRoutePath()))
             .list()
             .forEach(sysMenu -> super.validateFields(sysMenu, dto, collection -> {
                 if (!CollectionUtils.isEmpty(collection)) {
                     throw new MenuException(MenuState.MENU_INFO_EXISTING, collection.toString());
                 }
             }));

        final Long id = (dto.getPid() != null)? (dto.getPid() > 0 ? dto.getPid() : null) : null;
        dto.setPid(id);
        SysMenu sysMenu = this.convert.toEntity(dto);
        super.savaOrUpdate(sysMenu);
        return null;
    }

    /**
     * 更新菜单
     *
     * @param dto dto
     * @return /
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Void updateMenu(MenuDTO dto) {
        // 是否存在匹配菜单类型
        Optional.ofNullable(MenuTypeEnum.match(dto.getType()))
                .orElseThrow(() -> new MenuException(MenuState.MENU_OPT_ERROR));
        // 校验菜单是否存在
        final SysMenu sysMenu = super.lambdaQuery()
                                     .eq(SysMenu::getId, dto.getId())
                                     .oneOpt().orElseThrow(() -> new MenuException(MenuState.MENU_NOT_FOUND));
        // 校验参数
        this.validateMenu(dto, sysMenu);
        /*
         * 禁止设置本级菜单成为子菜单的菜单
         * 获取所有子菜单id集合
         */
        final Set<Long> childrenMenuIds = this.collectCurrentChildrenMenuIds(sysMenu.getId(), new CopyOnWriteArraySet<>());
        if (!CollectionUtils.isEmpty(childrenMenuIds) && childrenMenuIds.contains(dto.getPid())) {
            throw new MenuException(MenuState.MENU_OPT_ERROR);
        }
        // 禁止设置上级菜单为本级菜单
        if (ObjectUtils.nullSafeEquals(dto.getPid(), sysMenu.getId())) {
            throw new MenuException(MenuState.MENU_OPT_ERROR);
        }
        // 设置节点关系，是子节点还是根节点
        dto.setPid(dto.getPid() != null ? dto.getPid() > 0 ? dto.getPid() : null : null);
        // 复制属性
        this.convert.copyProperties(dto, sysMenu);
        super.savaOrUpdate(sysMenu);
        return null;
    }

    /**
     * 校验
     *
     * @param dto dto
     * @param sysMenu menu
     */
    private void validateMenu(MenuDTO dto, SysMenu sysMenu) {

        // 校验标题是否唯一
        boolean titleEq = ObjectUtils.nullSafeEquals(dto.getTitle(), sysMenu.getTitle());
        // 校验组件路径是否唯一
        boolean pathEq = ObjectUtils.nullSafeEquals(dto.getComponentPath(), sysMenu.getComponentPath());
        // 校验组件名是否唯一
        boolean nameEq = ObjectUtils.nullSafeEquals(dto.getComponentName(), sysMenu.getComponentName());
        // 校验路由是否唯一
        boolean routeEq = ObjectUtils.nullSafeEquals(dto.getRoutePath(), sysMenu.getRoutePath());
        if (titleEq && pathEq && nameEq && routeEq) {
            return;
        }
        super.lambdaQuery()
             .select(SysMenu::getTitle, SysMenu::getComponentName, SysMenu::getComponentPath, SysMenu::getRoutePath)
             .ne(SysMenu::getId, dto.getId())
             .and(sysMenuLambdaQueryWrapper -> sysMenuLambdaQueryWrapper.eq(!titleEq && dto.getTitle() != null, SysMenu::getTitle, dto.getTitle())
                                                                    .or()
                                                                    .eq(!nameEq && dto.getComponentName() != null, SysMenu::getComponentName, dto.getComponentName())
                                                                    .or()
                                                                    .eq(!pathEq && dto.getComponentPath() != null, SysMenu::getComponentPath, dto.getComponentPath())
                                                                    .or()
                                                                    .eq(!routeEq && dto.getRoutePath() != null, SysMenu::getRoutePath, dto.getRoutePath()))
             .list()
             .forEach(var -> super.validateFields(var, dto, collection -> {
                 if (!CollectionUtils.isEmpty(collection)) {
                     throw new MenuException(MenuState.MENU_INFO_EXISTING, collection.toString());
                 }
             }));
    }


    /**
     * 删除菜单包括所有子菜单
     *
     * @param ids id collection
     * @return /
     */
    @CacheEvict
    @Transactional(rollbackFor = Exception.class)
    public Void deleteMenuList(Set<Long> ids){
        final Set<Long> treeIds = this.collectCurrentMenuTreeIds(ids, new CopyOnWriteArraySet<>());
        if (!CollectionUtils.isEmpty(treeIds)) {
            // 删除menu
            super.deleteByIds(treeIds);
            // 删除role-menu
            super.baseMapper.deleteRoleRelationByIds(treeIds);
        }
        return null;
    }

    /**
     * 收集本级菜单tree 所有ids
     *
     * @param ids id collection
     */
    private Set<Long> collectCurrentMenuTreeIds(Set<Long> ids, Set<Long> collectionIds) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptySet();
        }
        Assert.notNull(collectionIds, "collectionIds can not been null");
        collectionIds.addAll(ids);
        ids.forEach(id -> {
            final Set<Long> collectIds = this.collectNextLowChildrenMenuIds(id);
            if (!CollectionUtils.isEmpty(collectIds)) {
                this.collectCurrentMenuTreeIds(collectIds, collectionIds);
            }
        });
        return collectionIds;
    }

    /**
     * 收集本级菜单所有子菜单id集合
     *
     * @param id id
     * @param collectChildrenIds collect children id
     * @return collection
     */
    private Set<Long> collectCurrentChildrenMenuIds(Long id, Set<Long> collectChildrenIds) {

        if (CollectionUtils.isEmpty(collectChildrenIds) || ObjectUtils.isEmpty(id)) {
            // 下级子菜单集合
            final Set<Long> childrenIds = this.collectNextLowChildrenMenuIds(id);
            if (!CollectionUtils.isEmpty(childrenIds)){
                // collect
                collectChildrenIds.addAll(childrenIds);
                // 继续往下子菜单收集
                childrenIds.forEach(childrenId -> this.collectCurrentChildrenMenuIds(childrenId, collectChildrenIds));
            }
        }
        return collectChildrenIds;
    }

    /**
     * 收集下级子菜单id
     *
     * @param id id
     * @return id collection
     */
    private Set<Long> collectNextLowChildrenMenuIds(Long id) {
        if (ObjectUtils.isEmpty(id)) {
            return new HashSet<>();
        }
        return super.lambdaQuery()
                    .eq(SysMenu::getPid, id)
                    .list()
                    .stream()
                    .map(SysMenu::getId)
                    .collect(Collectors.toSet());
    }
}
