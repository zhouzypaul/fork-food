import TopBar from "./TopBar";
import {useRef, useState} from "react";
import ReactCodeInput from "react-code-input";
import Code from "./Code";

function Join() {
  const [values, setValues] = useState("");
  console.log(values);
  return (
    <>
      <TopBar showOptions={true} to="/home"/>
      <div className="content">
        <Code change={setValues}/>
      </div>
    </>
  );
}

export default Join;