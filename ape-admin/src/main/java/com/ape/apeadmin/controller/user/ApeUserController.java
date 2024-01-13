package com.ape.apeadmin.controller.user;

import com.alibaba.fastjson2.JSONObject;
import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.enums.ResultCode;
import com.ape.apecommon.utils.PasswordUtils;
import com.ape.apeframework.utils.RedisUtils;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.*;
import com.ape.apesystem.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author shaozhujie
 * @version 1.0
 * @description: 用户controller
 * @date 2023/8/28 9:04
 */
@Controller
@ResponseBody
@RequestMapping("/user")
public class ApeUserController {

    @Autowired
    private ApeUserService apeUserService;
    @Autowired
    private ApeUserRoleService apeUserRoleService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ApeFoodService apeFoodService;
    @Autowired
    private ApeCollectionService apeCollectionService;
    @Autowired
    private ApeOrderService apeOrderService;

    /** 分页查询用户 */
    @Log(name = "分页查询用户", type = BusinessType.OTHER)
    @PostMapping("getUserPage")
    public Result getUserPage(@RequestBody ApeUser apeUser) {
        Page<ApeUser> page = apeUserService.getUserPage(apeUser);
        return Result.success(page);
    }

    /** 根据id查询用户 */
    @Log(name = "根据id查询用户", type = BusinessType.OTHER)
    @GetMapping("getUserById")
    public Result getUserById(@RequestParam("id") String id) {
        ApeUser apeUser = apeUserService.getById(id);
        QueryWrapper<ApeUserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeUserRole::getUserId,apeUser.getId());
        List<ApeUserRole> list = apeUserRoleService.list(queryWrapper);
        List<String> roles = new ArrayList<>();
        for (ApeUserRole apeUserRole : list) {
            roles.add(apeUserRole.getRoleId());
        }
        apeUser.setRoleIds(roles);
        return Result.success(apeUser);
    }

    /** 新增用户 */
    @Log(name = "新增用户", type = BusinessType.INSERT)
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("saveUser")
    public Result saveUser(@RequestBody ApeUser apeUser) {
        //先校验登陆账号是否重复
        boolean account = checkAccount(apeUser);
        if (!account) {
            return Result.fail("登陆账号已存在不可重复！");
        }
        String uuid = IdWorker.get32UUID();
        //密码加盐加密
        String encrypt = PasswordUtils.encrypt(apeUser.getPassword());
        String[] split = encrypt.split("\\$");
        apeUser.setId(uuid);
        apeUser.setPassword(split[0]);
        apeUser.setSalt(split[1]);
        apeUser.setAvatar("/img/avatar.jpeg");
        apeUser.setPwdUpdateDate(new Date());
        //保存用户
        boolean save = apeUserService.save(apeUser);
        //再保存用户角色关系
        List<String> roleIds = apeUser.getRoleIds();
        List<ApeUserRole> apeUserRoles = new ArrayList<>();
        if (roleIds != null && roleIds.size() > 0) {
            for (String roleId : roleIds) {
                ApeUserRole apeUserRole = new ApeUserRole();
                apeUserRole.setUserId(uuid);
                apeUserRole.setRoleId(roleId);
                apeUserRoles.add(apeUserRole);
            }
        }
        apeUserRoleService.saveBatch(apeUserRoles);
        return Result.success();
    }

    /** 编辑用户 */
    @Log(name = "编辑用户", type = BusinessType.UPDATE)
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("editUser")
    public Result editUser(@RequestBody ApeUser apeUser) {
        ApeUser user = apeUserService.getById(apeUser.getId());
        if (!user.getLoginAccount().equals(apeUser.getLoginAccount())) {
            //先校验登陆账号是否重复
            boolean account = checkAccount(apeUser);
            if (!account) {
                return Result.fail("登陆账号已存在不可重复！");
            }
        }
        //更新用户
        boolean edit = apeUserService.updateById(apeUser);
        //先删除用户角色关系
        QueryWrapper<ApeUserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeUserRole::getUserId,apeUser.getId());
        apeUserRoleService.remove(queryWrapper);
        //再次保存最新的关系
        List<String> roleIds = apeUser.getRoleIds();
        List<ApeUserRole> apeUserRoles = new ArrayList<>();
        if (roleIds != null && roleIds.size() > 0) {
            for (String roleId : roleIds) {
                ApeUserRole apeUserRole = new ApeUserRole();
                apeUserRole.setUserId(apeUser.getId());
                apeUserRole.setRoleId(roleId);
                apeUserRoles.add(apeUserRole);
            }
        }
        apeUserRoleService.saveBatch(apeUserRoles);
        return Result.success();
    }

    /** 校验用户 */
    public boolean checkAccount(ApeUser apeUser) {
        QueryWrapper<ApeUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeUser::getLoginAccount,apeUser.getLoginAccount());
        int count = apeUserService.count(queryWrapper);
        return count <= 0;
    }

    /** 删除用户 */
    @Log(name = "删除用户", type = BusinessType.DELETE)
    @Transactional(rollbackFor = Exception.class)
    @GetMapping("removeUser")
    public Result removeUser(@RequestParam("ids")String ids) {
        if (StringUtils.isNotBlank(ids)) {
            String[] asList = ids.split(",");
            for (String id : asList) {
                ApeUser user = apeUserService.getById(id);
                boolean remove = apeUserService.removeById(id);
                QueryWrapper<ApeUserRole> queryWrapper = new QueryWrapper<>();
                queryWrapper.lambda().eq(ApeUserRole::getUserId,id);
                apeUserRoleService.remove(queryWrapper);
                if (user.getUserType() == 1) {
                    //删除美食
                    QueryWrapper<ApeFood> queryWrapper1 = new QueryWrapper<>();
                    queryWrapper1.lambda().eq(ApeFood::getShopId,user.getId());
                    apeFoodService.remove(queryWrapper1);
                    //删除收藏
                    QueryWrapper<ApeCollection> queryWrapper2 = new QueryWrapper<>();
                    queryWrapper2.lambda().eq(ApeCollection::getShopId,user.getId());
                    apeCollectionService.remove(queryWrapper2);
                    //删除订单
                    QueryWrapper<ApeOrder> queryWrapper3 = new QueryWrapper<>();
                    queryWrapper3.lambda().eq(ApeOrder::getShopId,user.getId());
                    apeOrderService.remove(queryWrapper3);
                }
            }
            return Result.success();
        } else {
            return Result.fail("角色id不能为空！");
        }
    }

    /** 重置密码 */
    @Log(name = "重置密码", type = BusinessType.UPDATE)
    @PostMapping("resetPassword")
    public Result resetPassword(@RequestBody JSONObject json) {
        String id = json.getString("id");
        String newPassword = json.getString("newPassword");
        String encrypt = PasswordUtils.encrypt(newPassword);
        String[] split = encrypt.split("\\$");
        ApeUser apeUser = apeUserService.getById(id);
        boolean decrypt = PasswordUtils.decrypt(newPassword, apeUser.getPassword() + "$" + apeUser.getSalt());
        if (decrypt) {
            return Result.fail("新密码不可和旧密码相同！");
        }
        apeUser.setPassword(split[0]);
        apeUser.setSalt(split[1]);
        apeUser.setPwdUpdateDate(new Date());
        boolean update = apeUserService.updateById(apeUser);
        if (update) {
            return Result.success();
        } else {
            return Result.fail(ResultCode.COMMON_DATA_OPTION_ERROR.getMessage());
        }
    }

    /** 获取登陆用户信息 */
    @Log(name = "获取登陆用户信息", type = BusinessType.OTHER)
    @GetMapping("getUserInfo")
    public Result getUserInfo() {
        ApeUser user = ShiroUtils.getUserInfo();
        ApeUser apeUser = apeUserService.getById(user.getId());
        return Result.success(apeUser);
    }

    /** 修改个人信息 */
    @Log(name = "修改个人信息", type = BusinessType.UPDATE)
    @PostMapping("setUserInfo")
    public Result setUserInfo(@RequestBody ApeUser apeUser) {
        ApeUser userInfo = ShiroUtils.getUserInfo();
        apeUser.setId(userInfo.getId());
        apeUserService.updateById(apeUser);
        return Result.success();
    }

    /** 修改个人头像 */
    @Log(name = "修改个人头像", type = BusinessType.UPDATE)
    @PostMapping("setUserAvatar/{id}")
    public Result setUserAvatar(@PathVariable("id") String id,@RequestParam("file") MultipartFile avatar) {
        if(StringUtils.isBlank(id)){
            return Result.fail("用户id为空!");
        }
        ApeUser apeUser = apeUserService.getById(id);
        if(avatar.isEmpty()){
            return Result.fail("上传的头像不能为空!");
        }
        String coverType = avatar.getOriginalFilename().substring(avatar.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase();
        if ("jpeg".equals(coverType)  || "gif".equals(coverType) || "png".equals(coverType) || "bmp".equals(coverType)  || "jpg".equals(coverType)) {
            //文件路径
            String filePath = System.getProperty("user.dir")+System.getProperty("file.separator")+"img";
            //文件名=当前时间到毫秒+原来的文件名
            String fileName = id + "."+ coverType;
            //如果文件路径不存在，新增该路径
            File file1 = new File(filePath);
            if(!file1.exists()){
                boolean mkdir = file1.mkdir();
            }
            //现在的文件地址
            if (StringUtils.isNotBlank(apeUser.getAvatar())) {
                String s = apeUser.getAvatar().split("/")[2];
                File now = new File(filePath + System.getProperty("file.separator") + s);
                boolean delete = now.delete();
            }
            //实际的文件地址
            File dest = new File(filePath + System.getProperty("file.separator") + fileName);
            //存储到数据库里的相对文件地址
            String storeImgPath = "/img/"+fileName;
            try {
                avatar.transferTo(dest);
                //更新头像
                apeUser.setAvatar(storeImgPath);
                apeUserService.updateById(apeUser);
                return Result.success(storeImgPath);
            } catch (IOException e) {
                return Result.fail("上传失败");
            }
        } else {
            return Result.fail("请选择正确的图片格式");
        }
    }

    @GetMapping("/getUserListByType")
    public Result getUserListByType(@RequestParam("type") String type) {
        QueryWrapper<ApeUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeUser::getUserType,type).eq(ApeUser::getStatus,0);
        List<ApeUser> userList = apeUserService.list(queryWrapper);
        return Result.success(userList);
    }

    @GetMapping("checkUser")
    public Result checkUser() {
        ApeUser userInfo = ShiroUtils.getUserInfo();
        ApeUser user = apeUserService.getById(userInfo.getId());
        if (StringUtils.isBlank(user.getProvince()) || StringUtils.isBlank(user.getCity())) {
            return Result.success();
        } else {
            return Result.fail();
        }
    }

    @GetMapping("getShopFour")
    public Result getShopFour() {
        ApeUser user = ShiroUtils.getUserInfo();
        QueryWrapper<ApeUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeUser::getCity,user.getCity()).eq(ApeUser::getUserType,1)
                .eq(ApeUser::getStatus,0).orderByDesc(ApeUser::getCreateTime).last("limit 4");
        List<ApeUser> userList = apeUserService.list(queryWrapper);
        return Result.success(userList);
    }

    @PostMapping("forgetPassword")
    public Result forgetPassword(@RequestBody JSONObject jsonObject) {
        String loginAccount = jsonObject.getString("loginAccount");
        String email = jsonObject.getString("email");
        String password = jsonObject.getString("password");
        String code = jsonObject.getString("code").toLowerCase();
        String s = redisUtils.get(email + "forget");
        if (!code.equals(s)) {
            return Result.fail("验证码错误");
        }
        QueryWrapper<ApeUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeUser::getLoginAccount,loginAccount).last("limit 1");
        ApeUser user = apeUserService.getOne(queryWrapper);
        //密码加盐加密
        String encrypt = PasswordUtils.encrypt(password);
        String[] split = encrypt.split("\\$");
        user.setPassword(split[0]);
        user.setSalt(split[1]);
        boolean update = apeUserService.updateById(user);
        if (update) {
            return Result.success();
        } else {
            return Result.fail("密码修改失败");
        }
    }

    @GetMapping("changeOpen")
    public Result changeOpen(@RequestParam("open")Integer open) {
        ApeUser user = ShiroUtils.getUserInfo();
        ApeUser apeUser = apeUserService.getById(user.getId());
        apeUser.setOpen(open);
        boolean update = apeUserService.updateById(apeUser);
        if (update) {
            return Result.success();
        } else {
            return Result.fail("操作失败");
        }
    }

}
