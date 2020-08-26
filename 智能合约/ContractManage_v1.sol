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
    function contractQuery(address _contractAddress, uint version)public returns(ContractMessage){
        //query
        ContractMessage memory con=getmessFromAddress(_contractAddress);
        if(con.cnt==-1){
            return;
        }
        uint len = track[_contractAddress].length;
        if(version==0){
            //version=0：返回最终版本合同
            ContractMessage memory tem=track[_contractAddress][len-1];
            return tem;
            //合同内容 tem.mess
            //合同时间 tem.vaildtime
            //根据接口需求修改返回参数
        
        }
        else if(version<=len){
            //version<=len：返回历史版本
            return track[_contractAddress][version-1];
        }
        else{
            //version超出版本总数：返回空
            return;
        }
    }
    //所有历史版本查询
    function contractHistoryQuery(address _contractAddress)public returns(ContractMessage[]){
        ContractMessage memory con=getmessFromAddress(_contractAddress);
        if(con.cnt==-1){
            return;
        }
        return track[_contractAddress];
        //顺序访问结构体可以得到所有版本的操作方、操作类型、操作时间
    }
    //合同解除
    function contractCancel(address _contractAddress)public returns(bool){
        //寻找该合同
        ContractMessage memory con=getmessFromAddress(_contractAddress);
        if(con.cnt==-1){
            return false;
        }
        //确认身份
        if(msg.sender!=owner){
            return false;
        }
        for(uint i = 0; i < database.length; i++) {
            if(database[i].contractAddress == _contractAddress){
                delete database[i];
                delete track[_contractAddress];
                return true;
            }
        }
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
