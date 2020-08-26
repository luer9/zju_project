pragma solidity ^0.5.0;
pragma experimental ABIEncoderV2;
contract ContractManage{
    struct ContractMessage{
        uint32 id;
        string name;
        string mess;
        string createtime;
        string starttime;
        string endtime;
        string sign1;
        string sign2;
        uint32 state;
    }
    struct StateInfo{
        uint32 name; //0:甲方 1：乙方
        uint32 op; // 0:签署 1:修改 2：生效 3:失效
        uint64 time;
    }
    mapping(uint=>ContractMessage) contracts;
    mapping(uint=>StateInfo[]) stateInfos;
    address owner;
    //合同创建
    function contractStorage(uint32 id, string memory _name,string memory _mess,string memory  _createtime, string memory _starttime,string memory _vaildtime,
        string memory sign1, string memory sign2, uint32  _state)public{
        ContractMessage memory con= ContractMessage(id,_name,_mess, _createtime ,_starttime,_vaildtime, sign1, sign2, _state);
        contracts[id] = con;

    }
    //合同签署信息更新
    function contractUpdate(uint32 id, uint32 name , uint32 state,  uint64 time)public returns (bool){
        contracts[id].state = state;
        StateInfo memory s = StateInfo(name, state, time);
        stateInfos[id].push(s);
    }
    //合同内容更新
    function contractTextUpdate(uint32 id, string memory name, string memory text, string memory starttime, string memory endtime)public {
        contracts[id].name = name;
        contracts[id].mess = text;
        contracts[id].starttime = starttime;
        contracts[id].endtime = endtime;
    }
    //合同查询
    function contractQuery(uint32 id)public view returns(string memory,string memory,string memory , string memory,string memory,
        string memory, string memory,uint32){
        //query
        ContractMessage memory con = contracts[id];
        return (con.name, con.mess, con.createtime, con.starttime, con.endtime, con.sign1, con.sign2, con.state);
    }
    //
    function isContractExist(uint32 id) public view returns(bool){
        ContractMessage memory con = contracts[id];
        if(con.id == 0) return false;
        else return true;
    }
    //
    function queryStateInfo(uint32 id)public view returns(uint[] memory){
        StateInfo[]  memory ss = stateInfos[id];
        uint l = ss.length;
        uint[] memory t = new uint[](l*3);
        uint32 cnt = 0;
        uint32 i;
        for(i = 0; i < l; ++i){
            t[cnt++] = ss[i].name;
            t[cnt++] = ss[i].op;
            t[cnt++] = ss[i].time;
        }
        return t;
    }

}