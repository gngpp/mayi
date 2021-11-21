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
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.zf1976.mayi.upms.biz.pojo.vo.menu;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * @author mac
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MenuBuildVO {

    public MenuBuildVO() {
    }

    public MenuBuildVO(MenuMetaVo menuMetaVo) {
        this.meta = menuMetaVo;
    }

    private String name;

    private String path;

    private Boolean hidden;

    private String redirect;

    private String component;

    private Boolean alwaysShow;

    private MenuMetaVo meta;

    private List<MenuBuildVO> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public Boolean getAlwaysShow() {
        return alwaysShow;
    }

    public void setAlwaysShow(Boolean alwaysShow) {
        this.alwaysShow = alwaysShow;
    }

    public MenuMetaVo getMeta() {
        return meta;
    }

    public void setMeta(MenuMetaVo meta) {
        this.meta = meta;
    }

    public List<MenuBuildVO> getChildren() {
        return children;
    }

    public void setChildren(List<MenuBuildVO> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "MenuBuildVO{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", hidden=" + hidden +
                ", redirect='" + redirect + '\'' +
                ", component='" + component + '\'' +
                ", alwaysShow=" + alwaysShow +
                ", meta=" + meta +
                ", children=" + children +
                '}';
    }

    public void intiMeta(String title, String icon, boolean noCache) {
        final MenuMetaVo menuMetaVo = new MenuMetaVo();
        menuMetaVo.setIcon(icon);
        menuMetaVo.setTitle(title);
        menuMetaVo.setNoCache(noCache);
        this.meta = menuMetaVo;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class MenuMetaVo {

        private String title;

        private String icon;

        private Boolean noCache;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public Boolean getNoCache() {
            return noCache;
        }

        public void setNoCache(Boolean noCache) {
            this.noCache = noCache;
        }

        @Override
        public String toString() {
            return "MenuMetaVo{" +
                    "title='" + title + '\'' +
                    ", icon='" + icon + '\'' +
                    ", noCache=" + noCache +
                    '}';
        }
    }
}

