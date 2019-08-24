package com.example.myapplication1;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.*;

public class Details implements Serializable {
    private String username;
    private String password;
    private String sex;
    private String birthdate;
    private String estatus;
    private static ArrayList<Details> ulist=new ArrayList<Details>();
    public Details(String username,String password,String sex,String birthdate,Object estatus) throws RuntimeException
    {
        if(username.equals("")||username.equals(null))
        {
            throw new RuntimeException("username not present");
        }
        if(password.length()<8)
        {
            throw new RuntimeException("password below 8 characters");
        }
        if(password.length()>15)
        {
            throw new RuntimeException("password too long");
        }
        if(password.matches(".*\\d+.*")==false)
        {
            throw new RuntimeException("no number");
        }
        Pattern special = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]~-]");
        Matcher hasSpecial = special.matcher(password);
        if(hasSpecial.find()==false)
        {
            throw new RuntimeException("no special character");
        }
        if(sex.equals("")||sex.equals(null))
        {
            throw new RuntimeException("no sex selected");
        }
        if(birthdate.equals(""))
        {
            throw new RuntimeException("date not set");
        }
        if(estatus.equals(null))
        {
            throw new RuntimeException("employment status not selected");
        }

        this.username=username;
        this.password=password;
        this.sex=sex;
        this.birthdate=birthdate;
        this.estatus=String.valueOf(estatus);

        ulist.add(this);
    }
    public String getUsername()
    {
        return username;
    }
    public String getPassword()
    {
        return password;
    }

    public String getSex() {
        return sex;
    }
    public String getBirthdate() {
        return birthdate;
    }
    public String getEstatus() {
        return estatus;
    }


    public static String detailspresent(String uname, String pass) {
        //initially created the program to compare different objects without using Firebase
        for (int i = 0; i < ulist.size(); i++) {
            if(ulist.equals(null))
            {
                return "not present";
            }
            if(ulist.get(i).getUsername().equals(uname))
            {
                if(ulist.get(i).getPassword().equals(pass))
                {
                    return "true";
                }
                else
                {
                    return "false";
                }
            }

        }
        return "not present";
    }
}
