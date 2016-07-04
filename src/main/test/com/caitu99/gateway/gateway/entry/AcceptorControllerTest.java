package com.caitu99.gateway.gateway.entry;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by bobo on 10/20/15.
 */
public class AcceptorControllerTest {

    @Test
    public void test() {

        UUID uuid = UUID.randomUUID();
        System.out.print(uuid.toString().replace("-", ""));

    }

}