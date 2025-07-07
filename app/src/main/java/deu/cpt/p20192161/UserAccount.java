/*
 * 개인프로젝트 - UserAccount.java
 * 개발자 : 컴퓨터공학과 20192161 황도균
 * 20192161@office.deu.ac.kr
 */

package deu.cpt.p20192161;

public class UserAccount {
    private String idToken;
    private String email;
    private String pwd;

    public UserAccount() {
    }
    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
