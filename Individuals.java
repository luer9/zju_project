/**
 * @ClassName Individuals
 * @Description
 * @Author baolin chen
 * @Date 2020/8/10 15:05
 * @Version 1.0
 */

class Individuals{
    private String id;        //身份证号
    private String iName;       //姓名
    private String phone;        //手机号码
    private String accountId;   //账户id
    private String publicKey;
    private String privateKey;
    private boolean isPublic,isPrivate;
    Individuals(String iid,String iname,String iphone,String iaid){
        id = iid;
        iName = iname;
        phone = iphone;
        accountId = iaid;
    }

    public String getId() { return id; }

    public String getiName() { return iName; }

    public String getPhone() { return phone; }

    public String getAccountId() { return accountId; }

    public String getPublicKey() { return publicKey; }

    public void setPhone(String phone) { this.phone = phone; }

    public void setPublic(String publicKey) {
        if(!isPublic){
            this.publicKey = publicKey;
            System.out.println("公钥添加成功");
        }
        else
            System.out.println("公钥已经添加，不可修改");
    }

    public void setPrivate(String privateKey) {
        if(!isPrivate){
            this.privateKey = privateKey;
            System.out.println("私钥添加成功");
        }
        else
            System.out.println("私钥已经添加，不可修改");
    }

}

