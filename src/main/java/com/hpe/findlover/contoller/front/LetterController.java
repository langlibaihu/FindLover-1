package com.hpe.findlover.contoller.front;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hpe.findlover.model.Letter;
import com.hpe.findlover.model.LetterUser;
import com.hpe.findlover.model.UserAsset;
import com.hpe.findlover.model.UserBasic;
import com.hpe.findlover.service.front.LetterService;
import com.hpe.findlover.service.front.UserAssetService;
import com.hpe.findlover.service.front.UserService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author YYF;
 */
@Controller
@RequestMapping
public class LetterController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private LetterService letterService;
    @Autowired
    private UserAssetService userAssetService;

    @GetMapping("letter")
    public String letter(HttpSession session, Model model) throws Exception {
        UserBasic userBasic = (UserBasic) session.getAttribute("user");
        Map<String,Object> map= letterService.selectOther(userBasic.getId());
        model.addAttribute("map", map);
        return "front/letter";
    }

    @PostMapping("letter")
    @ResponseBody
    public List<Letter> letter(HttpSession session, int currentPage, int lineSize, int otherUserId) throws Exception {
        UserBasic user = (UserBasic) session.getAttribute("user");
        PageHelper.startPage(currentPage, lineSize);
        List<Letter> list = letterService.selectLetter(user.getId(), otherUserId);
        if (user.getVip()) {
            boolean f = letterService.updateVipLetterStatus(list);
        }
        return list;
    }
    @PostMapping("readLetter")
    @ResponseBody
    public String readLetter(HttpSession session, int letterId) {
        UserBasic user = (UserBasic) session.getAttribute("user");
        UserAsset userAsset = userAssetService.selectByPrimaryKey(user.getId());
        if (userAsset.getAsset() >= 5) {
            Letter letter = new Letter();
            letter.setId(letterId);
            letter.setStatus(1);
            userAsset.setAsset(userAsset.getAsset()-5);
            letterService.readLetter(userAsset,letter);
            return "ok";
        }else{
            return "牵手币余额不足";
        }
    }
    @PostMapping("sendLetter")
    @ResponseBody
    public String sendLetter(HttpSession session,int otherUserId, String content){
        UserBasic user = (UserBasic) session.getAttribute("user");
        UserAsset userAsset = userAssetService.selectByPrimaryKey(user.getId());
        logger.debug("开始进入发信息controller,content="+content);
        if (user.getVip()){
            Letter letter=this.returnLetter(user,otherUserId,content);
            logger.debug("letter="+letter.toString());
            if (letterService.insert(letter)){
                return "ok";
            }else{
                return "遇到未知错误";
            }
        }else if (userAsset!=null && userAsset.getAsset()>=5){
            userAsset.setAsset(userAsset.getAsset()-5);
            Letter letter=this.returnLetter(user,otherUserId,content);
            if (letterService.sendLetter(userAsset,letter)){
                return "ok";
            }else{
                return "遇到未知错误";
            }
        }else {
            logger.debug("用户余额不足，返回牵手币余额不足信息");
            return "牵手币余额不足";
        }
    }
    public Letter returnLetter(UserBasic user,int otherUserId, String content){
        Letter letter=new Letter();
        letter.setSendId(user.getId());
        letter.setRecieveId(otherUserId);
        letter.setContent(content);
        letter.setSendTime(new Date());
        letter.setStatus(0);
        return letter;
    }
}