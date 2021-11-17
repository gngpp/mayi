package com.zf1976.mayi.upms.biz.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zf1976.mayi.common.core.foundation.DataResult;
import com.zf1976.mayi.upms.biz.pojo.query.Query;
import com.zf1976.mayi.upms.biz.pojo.query.SessionQueryParam;
import com.zf1976.mayi.upms.biz.pojo.vo.SessionVO;
import com.zf1976.mayi.upms.biz.service.SysOnlineService;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * @author mac
 * @date 2021/1/20
 **/
@RestController
@RequestMapping("/api/online")
public class OnlineController {

    private final SysOnlineService service;

    public OnlineController(SysOnlineService sysOnlineService) {
        this.service = sysOnlineService;
    }

    @PostMapping("/page")
    public DataResult<IPage<SessionVO>> selectSessionList(@RequestBody Query<SessionQueryParam> query) {
        return DataResult.success(this.service.selectSessionPage(query));
    }

    @DeleteMapping("/delete")
    public DataResult<Void> deleteSession(@RequestBody Set<Long> ids) {
        return DataResult.success(this.service.forceOffline(ids));
    }
}
