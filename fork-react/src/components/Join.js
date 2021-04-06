import TopBar from "./TopBar";
import {useState} from "react";
import Code from "./Code";

function Join() {
  const [values, setValues] = useState("");
  console.log(values);
  return (
    <>
      <TopBar showOptions={true} to="/home"/>
      <div className="content">
        <Code change={setValues}/>
        <br/>
        <button className="primary-button">join</button>
      </div>
    </>
  );
}

export default Join;