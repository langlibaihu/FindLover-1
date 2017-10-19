package com.hpe.findlover.service.front.impl;

import com.hpe.findlover.mapper.UserPickMapper;
import com.hpe.findlover.model.UserPick;
import com.hpe.findlover.service.BaseServiceImpl;
import com.hpe.findlover.service.front.UserPickService;
import com.hpe.util.BaseTkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author sinnamm
 * @Date Create in  2017/10/19.
 */
@Service
public class UserPickServiceImpl extends BaseServiceImpl<UserPick> implements UserPickService {
    @Autowired
    private UserPickMapper userPickMapper;

    @Override
    public BaseTkMapper<UserPick> getMapper() {
        return userPickMapper;
    }
}
