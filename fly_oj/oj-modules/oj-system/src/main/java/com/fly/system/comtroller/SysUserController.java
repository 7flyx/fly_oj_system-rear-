package com.fly.system.comtroller;

import com.fly.common.core.domain.R;
import com.fly.system.domain.LoginDTO;
import com.fly.system.domain.SysUser;
import com.fly.system.domain.SysUserSaveDTO;
import com.fly.system.domain.SysUserVO;
import com.fly.system.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sysUser")
@Tag(name = "管理员接口")
public class SysUserController {
    @Autowired
    private ISysUserService sysUserService;
    @PostMapping("/login")
    @Operation(summary = "管理员登录", description = "根据账号密码进行管理员登录")
    public R<Void> login(@RequestBody LoginDTO loginDTO) {
        return sysUserService.login(loginDTO.getUserAccount(), loginDTO.getPassword());
    }

    @PostMapping("/add")
    @Operation(summary = "新增管理员", description = "根据提供的信息新增管理员")
    @ApiResponse(responseCode = "1000", description = "操作成功")
    @ApiResponse(responseCode = "2000", description = "服务器繁忙,请稍后重试")
    public R<Void> add(@RequestBody SysUserSaveDTO sysUserSaveDTO) {
        return null;
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户", description = "根据用户id删除用户")
    @Parameters(value = {
            @Parameter(name = "userId", in = ParameterIn.PATH, description = "用户ID")
    })
    public R<Void> delete(@PathVariable Long userId) {
        return null;
    }

    @Operation(summary = "用户详情", description = "根据查询条件 查询用户详情")
    @GetMapping("/detail")
    @Parameters(value = {
            @Parameter(name = "userId", in = ParameterIn.QUERY, description = "用户ID"),
            @Parameter(name = "sex", in = ParameterIn.QUERY, description = "用户性别")
    })
    public R<SysUserVO> detail(@RequestParam(required = true) Long userId, @RequestParam(required = false) String sex) {
        return null;
    }
}






















