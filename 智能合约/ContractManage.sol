pragma solidity ^0.4.0;

pragma experimental ABIEncoderV2;

import "./sign.sol";

contract ContractManage{
    struct ContractMessage{
        int cnt;
        bytes32[]  ownerid;
        string mess;
        uint starttime;
        uint vaildtime;
        uint signtime;
        string statue;
        address contractAddress;
    }
    ContractMessage [] public database;
    mapping(address=>ContractMessage []) track;
    address owner = msg.sender;
    //合同存储
    function contractStorage(int cnt0, bytes32[]  ownerid0,string _mess,uint _starttime,uint _vaildtime,string _statue)public{
        uint t=now;
        //地址...
        address addr;
        ContractMessage memory con= ContractMessage(cnt0,ownerid0,_mess,_starttime,_vaildtime,t,_statue,addr);
        database.push(con);
        track[addr].push(con);
    }
    //合同更新
    function contractUpdate(int cnt0, bytes32[]  ownerid0,address _contractAddress,string _mess,uint _starttime,uint _vaildtime,string _statue)public returns (bool){
        //确认身份
        if(msg.sender!=owner){
            return false;
        }
        //require(msg.sender == owner);
        uint t=now;
        ContractMessage memory con=getmessFromAddress(_contractAddress);
        //查询不到
        if(con.cnt==-1){
            return false;
        }
        ContractMessage memory con_next= ContractMessage(cnt0,ownerid0,_mess,_starttime,_vaildtime,t,_statue,_contractAddress);
        //历史版本通过哈希数组访问
        track[_contractAddress].push(con_next);
        return true;
    }
    //合同查询
    function contractQuery(address _contractAddress)public returns(ContractMessage){
        //query
        ContractMessage memory con=getmessFromAddress(_contractAddress);
        if(con.cnt==-1){
            return;
        }
        uint len = track[_contractAddress].length;
        //返回最终版本合同
        return track[_contractAddress][len-1];
        //show();
    }
    //通过地址查询
    function getmessFromAddress(address _contractAddress)public returns (ContractMessage){
        for(uint i = 0; i < database.length; i++) {
            if(database[i].contractAddress == _contractAddress){
                return database[i];
            }
        }
        ContractMessage memory tem;
        tem.cnt=-1;
        return tem;
    }
}
