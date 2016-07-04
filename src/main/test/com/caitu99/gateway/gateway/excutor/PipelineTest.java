package com.caitu99.gateway.gateway.excutor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.caitu99.gateway.exception.CarmenException;
import com.caitu99.gateway.utils.AppUtils;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;


public class PipelineTest {

    @Test
    public void testStart() throws Exception {
        System.out.println(AppUtils.MD5("888888"));
    }
}