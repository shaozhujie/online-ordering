package com.ape.apeadmin.controller.login;

import com.alibaba.fastjson2.JSONObject;
import com.ape.apecommon.annotation.Log;
import com.ape.apecommon.constant.Constants;
import com.ape.apecommon.domain.Result;
import com.ape.apecommon.enums.BusinessType;
import com.ape.apecommon.utils.JwtUtil;
import com.ape.apecommon.utils.PasswordUtils;
import com.ape.apecommon.utils.UserAgentUtil;
import com.ape.apeframework.event.LoginLogEvent;
import com.ape.apeframework.utils.RedisUtils;
import com.ape.apeframework.utils.RequestUtils;
import com.ape.apeframework.utils.ShiroUtils;
import com.ape.apesystem.domain.*;
import com.ape.apesystem.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * @author shaozhujie
 * @version 1.0
 * @description: 登陆
 * @date 2023/9/8 9:04
 */
@Controller
@ResponseBody
@RequestMapping("login")
public class LoginController {

    @Autowired
    private ApeUserService apeUserService;

    @Autowired
    private ApeLoginLogService apeLoginLogService;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private ApeMenuService apeMenuService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ApeFoodService apeFoodService;

    @Autowired
    private ApeCommentService apeCommentService;

    @Autowired
    private ApeArticleService apeArticleService;
    
    @Autowired
    private ApeOrderService apeOrderService;

    @PostMapping()
    public Result login(HttpServletRequest request,@RequestBody JSONObject jsonObject) {
        String ipAddr = RequestUtils.getRemoteHost(request);
        String username = jsonObject.getString("username");
        String password = jsonObject.getString("password");
        Integer userType = jsonObject.getInteger("userType");
        QueryWrapper<ApeUser> query = new QueryWrapper<>();
        query.lambda().eq(ApeUser::getLoginAccount,username);
        if (userType == 0) {
            query.lambda().eq(ApeUser::getUserType,2);
        } else {
            query.lambda().in(ApeUser::getUserType,0,1);
        }
        ApeUser apeUser = apeUserService.getOne(query);
        if (apeUser == null) {
            saveLoginLog(request,"用户名不存在",username,ipAddr,1);
            return Result.fail("用户名不存在！");
        }
        //比较加密后得密码
        boolean decrypt = PasswordUtils.decrypt(password, apeUser.getPassword() + "$" + apeUser.getSalt());
        if (!decrypt) {
            saveLoginLog(request,"用户名或密码错误",username,ipAddr,1);
            return Result.fail("用户名或密码错误！");
        }
        if (apeUser.getStatus() == 1) {
            saveLoginLog(request,"用户被禁用",username,ipAddr,1);
            return Result.fail("用户被禁用！");
        }
        //密码正确生成token返回
        String token = JwtUtil.sign(apeUser.getId(), password);
        JSONObject json = new JSONObject();
        json.put("token", token);
        saveLoginLog(request,"登陆成功",username,ipAddr,0);
        return Result.success(json);
    }

    @Log(name = "登出", type = BusinessType.OTHER)
    @GetMapping("logout")
    public Result logout() {
        ApeUser user = ShiroUtils.getUserInfo();
        redisUtils.remove(Constants.PREFIX_USER_TOKEN + user.getId());
        return Result.success();
    }

    @Log(name = "验证密码", type = BusinessType.OTHER)
    @GetMapping("verPassword")
    public Result verPassword(HttpServletRequest request,@RequestParam("password") String password) {
        ApeUser user = ShiroUtils.getUserInfo();
        ApeUser apeUser = apeUserService.getById(user.getId());
        String ipAddr = RequestUtils.getRemoteHost(request);
        if (apeUser.getStatus() == 1) {
            saveLoginLog(request,"用户被禁用",apeUser.getLoginAccount(),ipAddr,1);
            return Result.fail("用户被禁用！");
        }
        boolean decrypt = PasswordUtils.decrypt(password, apeUser.getPassword() + "$" + apeUser.getSalt());
        if (!decrypt) {
            saveLoginLog(request,"验证密码错误",apeUser.getLoginAccount(),ipAddr,1);
            return Result.fail("用户名或密码错误！");
        }
        saveLoginLog(request,"验证成功",apeUser.getLoginAccount(),ipAddr,0);
        return Result.success();
    }


    public void saveLoginLog(HttpServletRequest request,String msg,String username,String ipAddr,Integer state) {
        String agent = request.getHeader("User-Agent");
        String userAgent = UserAgentUtil.getUserAgent(agent);
        String browser = UserAgentUtil.judgeBrowser(userAgent);
        ApeLoginLog apeLoginLog = new ApeLoginLog();
        apeLoginLog.setUserName(username);
        apeLoginLog.setLoginIp(ipAddr);
        apeLoginLog.setBrowser(browser);
        apeLoginLog.setOs(userAgent);
        apeLoginLog.setStatus(state);
        apeLoginLog.setLoginTime(new Date());
        apeLoginLog.setMsg(msg);
        eventPublisher.publishEvent(new LoginLogEvent(apeLoginLog));
    }

    @GetMapping("getIndexData")
    public Result getIndexData() {
        QueryWrapper<ApeUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeUser::getUserType,1);
        int shop = apeUserService.count(queryWrapper);
        int food = apeFoodService.count();
        QueryWrapper<ApeUser> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.lambda().eq(ApeUser::getUserType,2);
        int user = apeUserService.count(queryWrapper1);
        int comment = apeCommentService.count();
        JSONObject jsonObject = new JSONObject();
        QueryWrapper<ApeUser> query = new QueryWrapper<>();
        query.lambda().eq(ApeUser::getSex,0);
        int nan = apeUserService.count(query);
        QueryWrapper<ApeUser> query1 = new QueryWrapper<>();
        query1.lambda().eq(ApeUser::getSex,1);
        int nv = apeUserService.count(query1);
        int article = apeArticleService.count();
        int order = apeOrderService.count();
        jsonObject.put("shop",shop);
        jsonObject.put("food",food);
        jsonObject.put("user",user);
        jsonObject.put("comment",comment);
        jsonObject.put("nan",nan);
        jsonObject.put("nv",nv);
        jsonObject.put("article",article);
        jsonObject.put("order",order);
        //店铺销量走势
        List<String> name = new ArrayList<>();
        List<Integer> sale = new ArrayList<>();
        QueryWrapper<ApeUser> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(ApeUser::getUserType,1).orderByDesc(ApeUser::getCreateTime)
                .last("limit 10");
        List<ApeUser> list = apeUserService.list(wrapper);
        for (ApeUser apeUser : list) {
            name.add(apeUser.getName());
            QueryWrapper<ApeFood> foodQuery = new QueryWrapper<>();
            foodQuery.lambda().eq(ApeFood::getShopId,apeUser.getId());
            List<ApeFood> apeFoods = apeFoodService.list(foodQuery);
            Integer saleNum = 0;
            for (ApeFood animal : apeFoods) {
                saleNum += animal.getSaleNum();
            }
            sale.add(saleNum);
        }
        jsonObject.put("name",name);
        jsonObject.put("sale",sale);
        return Result.success(jsonObject);
    }

    @GetMapping("getShopIndexData")
    public Result getShopIndexData() {
        JSONObject jsonObject = new JSONObject();
        ApeUser apeUser = ShiroUtils.getUserInfo();
        QueryWrapper<ApeFood> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ApeFood::getShopId,apeUser.getId());
        int food = apeFoodService.count(queryWrapper);
        QueryWrapper<ApeOrder> orderQueryWrapper = new QueryWrapper<>();
        orderQueryWrapper.lambda().eq(ApeOrder::getShopId,apeUser.getId()).groupBy(ApeOrder::getUserId);
        int user = apeOrderService.count(orderQueryWrapper);
        QueryWrapper<ApeOrder> query = new QueryWrapper<>();
        query.lambda().eq(ApeOrder::getShopId,apeUser.getId());
        int order = apeOrderService.count(query);
        jsonObject.put("food",food);
        jsonObject.put("user",user);
        jsonObject.put("order",order);
        List<String> name = new ArrayList<>();
        List<Integer> sale = new ArrayList<>();
        QueryWrapper<ApeFood> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.lambda().eq(ApeFood::getShopId,apeUser.getId());
        List<ApeFood> foodList = apeFoodService.list(queryWrapper1);
        for (ApeFood item : foodList) {
            name.add(item.getName());
            sale.add(item.getSaleNum());
        }
        jsonObject.put("name",name);
        jsonObject.put("sale",sale);
        return Result.success(jsonObject);
    }

    @GetMapping("getEmailReg")
    public Result getEmailReg(@RequestParam("email") String email) {
        String str="abcdefghigklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789";
        Random r=new Random();
        String arr[]=new String [4];
        String reg="";
        for(int i=0;i<4;i++) {
            int n=r.nextInt(62);
            arr[i]=str.substring(n,n+1);
            reg+=arr[i];
        }
        try {
            redisUtils.set(email + "forget",reg.toLowerCase(),60L);
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setPort(25);
            sender.setHost("smtp.qq.com");
            sender.setUsername("1760272627@qq.com");
            sender.setPassword("nwavnzopbtpibchc");
            sender.setDefaultEncoding("utf-8");
            MimeMessage msg = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setFrom(sender.getUsername());
            helper.setTo(email);
            helper.setSubject("Foodtuck修改密码验证");
            helper.setText("您的邮箱验证码为："+reg,true);
            sender.send(msg);
        }catch (Exception e){
            Result.fail("邮件发送失败");
        }
        return Result.success();
    }

}
