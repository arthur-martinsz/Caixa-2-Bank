package com.ifgoiano.caixa2bank.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;


@Component
public class CheckIsUUID {

    public boolean isUUID(String uuid) {
        String regex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

        Pattern pattern = Pattern.compile(regex);

        return pattern.matcher(uuid).matches();
    }

}
