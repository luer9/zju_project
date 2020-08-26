pragma solidity ^0.5.10;

contract Accumulator {
    uint32 sum = 0;

    bytes32 hello = "hello world";

    // increment() 将全局变量 sum + 1
    function increment() public {sum = sum + 1;}

    // getSum() 取得全局变量sum的值
    function getSum() public view returns (uint32){return sum;}

    // getHello() 取得全局变量hello的值
    function getHello() public view returns (bytes32){return hello;}

    // add() 将num1 + num2 加到sum 并返回true
    function add(uint32 num1, uint32 num2) public returns (bool) {
        sum = sum + num1 + num2;
        return (true);
    }
}