import TextBox from "./TextBox";
import {useState} from "react";
import TopBar from "./TopBar";

function Login(props) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const verify = () => {
    if (username === "ed" && password === "xing") {
      alert("Yay")
      props.history.push('/home');
    }
  }
  return(
    <>
      <TopBar to="/" showOptions={false}/>
      <div className="login">
        <div className="title-text">hungry?</div>
        <br/>
        <TextBox initial="username" change={setUsername} type="text"/>
        <br/>
        <TextBox initial="password" change={setPassword} type="password"/>
        <br/>
        <button onClick={verify}>
          sign in
        </button>
      </div>
    </>
  );
}

export default Login;