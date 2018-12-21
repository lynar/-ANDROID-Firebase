package com.example.lucas.loginfirebase;

import com.google.firebase.iid.FirebaseInstanceId;

public class User {

    private String m_name;
    private String m_UserName;
    private String m_Email;
    private String m_ID;

    //Getter et Setter

    public String getM_name() {
        return m_name;
    }

    public void setM_name(String m_name) {
        this.m_name = m_name;
    }

    public String getM_UserName() {
        return m_UserName;
    }

    public String getM_Email() {
        return m_Email;
    }
    public String getM_ID() {return m_ID;}

    public void setM_Email(String m_Email) {
        this.m_Email = m_Email;
    }

    public void setM_UserName(String m_UserName) {
        this.m_UserName = m_UserName;
    }

    //CONSTRUCTEUR

    public User(String name,String Username, String Email)
    {
        this.m_name = name;
        this.m_UserName = Username;
        this.m_Email = Email;
    }
    public User(String name, String ID)
    {
        this.m_name = name;
        this.m_ID = ID;
    }

    ///to have the good UserList View
    public String toString()
    {
        return m_name;
    }
}
