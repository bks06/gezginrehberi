package com.example.alica.gezgin80;

/**
 * Created by alica on 27.04.2018.
 */

public class UserModel {
    private String name;
    private String email;
    private int puan;
    private int check;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }





    public UserModel(String name, String email, int puan, int check){
        this.email = email;
        this.name = name;
        this.puan = puan;
        this.check= check;

    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPuan() {
        return puan;
    }

    public void setPuan(int puan) {
        this.puan = puan;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }



}
