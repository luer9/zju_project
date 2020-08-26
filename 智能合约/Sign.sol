pragma solidity ^0.4.0;
contract Sign{
    struct organization{
        address organizationAddress;
        string organizationId;
        string organizationName;
    }
    struct person{
        address personAddress;
        string personId;
        string personName;
    }
    mapping(string=>organization)organizationMap;
    mapping(string=>person)personMap;
    function newOrganization(string memory _organizationId,string memory _organizationName)public{
        Organization _organizationAddress=new Organization(_organizationId,_organizationName);
        organizationMap[_organizationId]=organization(_organizationAddress,_organizationId,_organizationName);
    }
    function selectOrganization(string _organizationId) returns(address organizationAddress,string organizationName){
        organization _organization=organizationMap[_organizationId];
        return (_organization.organizationAddress,_organization.organizationName);
    }
}
contract Organization{
    address owner;//合约部署者公钥
    string organizationName;//企业名字
    string organizationId;//企业ID

    function Organization(string memory _ID,string memory _name) public{
        owner=msg.sender;
        organizationName=_name;
        organizationId=_ID;
    }
}
