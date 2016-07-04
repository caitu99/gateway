package com.caitu99.gateway.utils;

/**
 *
 * 判断2个字符串是否相等，都为空认为相等
 * Created by Lion on 2015/11/9 0009.
 */
public class StrUtils {

    public static boolean equals(String str1, String str2)
    {
        if(str1 == null && str2 == null)
        {
            return true;
        }
        else {
            if(str1 == null)
            {
                return false;
            }
            else {
                return str1.equals(str2);
            }
        }
    }
}
