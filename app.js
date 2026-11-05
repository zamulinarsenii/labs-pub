"use strict";
function number(text){if(text===null||text.trim()==="")throw Error("Введите оба числа");const n=Number(text.replace(",","."));if(!Number.isFinite(n))throw Error("Допускаются только конечные числа");return n;}
function calculate(a,b,op){let value;switch(op){case "+":value=a+b;break;case "-":value=a-b;break;case "*":value=a*b;break;case "/":if(b===0)throw Error("Деление на ноль невозможно");value=a/b;break;default:throw Error("Неизвестная операция");}if(!Number.isFinite(value))throw Error("Результат вне диапазона");return value;}
if(typeof document!=="undefined"){
 const form=document.querySelector("#calc");
 if(form)form.addEventListener("submit",e=>{try{const data=new FormData(form);calculate(number(data.get("a")),number(data.get("b")),data.get("op"));}catch(error){e.preventDefault();document.querySelector("#error").textContent=error.message;}});
 else{const p=new URLSearchParams(location.search);try{const a=number(p.get("a")),b=number(p.get("b")),op=p.get("op");for(const [key,value] of Object.entries({a,b,op,answer:calculate(a,b,op)}))document.getElementById(key).textContent=String(value);}catch(error){document.querySelector("#error").textContent=error.message;}}
}
if(typeof module!=="undefined")module.exports={number,calculate};
