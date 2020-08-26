pragma solidity ^0.4.0;
contract Sign{
    struct organization{
        address organizationAddress;//合约地址
        string organizationCode;//企业代码
        string organizationName;//企业名称
        string regisNumber;//注册号
        string location;//企业所在地
        string businessScope;//经营范围
        string date;//营业期限
    }
    struct person{
        address personAddress;
        string personId;
        string personName;
    }
    mapping(string=>organization)organizationMap;
    mapping(string=>person)personMap;
    //创建企业
    function newOrganization(string memory _organizationCode,string memory _organizationName,string memory _regisNumber,string memory _location,string memory _businessScope,string memory _date)public{
        Organization _organizationAddress=new Organization(_organizationCode,_organizationName,_regisNumber,_location,_businessScope,_date);
        organizationMap[_organizationCode]=organization(_organizationAddress,_organizationCode,_organizationName,_regisNumber,_location,_businessScope,_date);
    }
    //按企业代码(organizationCode)查找企业
    function selectOrganization(string _organizationCode) returns(address organizationAddress,string organizationCode,string organizationName,string regisNumber,string location,string businessScope,string date){
        organization _organization=organizationMap[_organizationCode];
        return (_organization.organizationAddress,_organizationCode,_organization.organizationName,_organization.regisNumber,_organization.location,_organization.businessScope,_organization.date);
    }
    //创建个人
    function newPerson(string memory _personId,string memory _personName)public{
        Person _personAddress=new Person(_personId,_personName);
        personMap[_personId]=person(_personAddress,_personId,_personName);
    }
    //按个人Id(personId)查找个人
    function selectPerson(string _personId) returns(address personAddress,string personId,string personName){
        person _person=personMap[_personId];
        return (_person.personAddress,_personId,_person.personName);
    }
}
contract Organization{
    address owner;//合约部署者公钥
    string organizationName;//企业名字
    string organizationCode;//企业代码
    string regisNumber;//注册号
    string location;//单位所在地
    string businessScope;//经营范围
    string date;//营业期限

    function Organization(string memory _organizationCode,string memory _name,string memory _regisNumber,string memory _location,string memory _businessScope,string memory _date) public{
        owner=msg.sender;
        organizationName=_name;
        organizationCode=_organizationCode;
        regisNumber=_regisNumber;
        location=_location;
        businessScope=_businessScope;
        date=_date;
    }
}
contract Person{
    address owner;//合约部署者公钥
    string personId;
    string personName;

    function Person(string memory _personId,string memory _personName) public{
        owner=msg.sender;
        personId=_personId;
        personName=_personName;
    }
}


