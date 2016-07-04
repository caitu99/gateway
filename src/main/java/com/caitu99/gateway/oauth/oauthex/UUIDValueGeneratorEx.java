package com.caitu99.gateway.oauth.oauthex;

import org.apache.oltu.oauth2.as.issuer.UUIDValueGenerator;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;

import java.util.UUID;

public class UUIDValueGeneratorEx extends UUIDValueGenerator {

    @Override
    public String generateValue(String param) throws OAuthSystemException {
        return UUID.fromString(UUID.nameUUIDFromBytes(param.getBytes()).toString()).toString().replaceAll("-", "");
    }

}
