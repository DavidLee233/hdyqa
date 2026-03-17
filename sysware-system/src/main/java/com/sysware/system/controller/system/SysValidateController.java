package com.sysware.web.controller.system;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.util.ArrayUtil;
import com.sysware.common.annotation.Log;
import com.sysware.common.core.controller.BaseController;
import com.sysware.common.core.domain.R;
import com.sysware.common.enums.BusinessType;
import com.sysware.system.domain.vo.SysValidateRepetitionVo;
import com.sysware.system.service.ISysValidateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 验证
 */
@Api(tags = "数据验证")
@Validated
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@RestController
@RequestMapping("/system/validate")
public class SysValidateController extends BaseController {

    private final ISysValidateService sysValidateService;


    /**
     * 验证重复
     *
     * @param vo
     */
    @ApiOperation(value = "验证重复")
    @GetMapping("/verifyRepetition")
    public R verifyRepetition(@Validated SysValidateRepetitionVo vo) {


        return R.ok(sysValidateService.verifyRepetition(vo));
    }


    /**
     * 验证用户是否存在角色
     * @param userId
     * @param roleKey
     * @param roleType
     * @return
     */
    @ApiOperation(value = "验证用户是否存在角色")
    @GetMapping("/verifyRoleKey")
    public R verifyRoleKey(String userId,String roleKey,String roleType) {

        return R.ok(sysValidateService.verifyRoleKey(userId,roleKey,roleType));
    }

}
