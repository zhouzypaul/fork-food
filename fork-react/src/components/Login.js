import TextBox from "./TextBox";
import {useState} from "react";
import TopBar from "./TopBar";
import {useDispatch} from "react-redux";
import {login} from "../actions";

function Login(props) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const dispatch = useDispatch();

  const verify = () => {
    if ((username === "ed" && password === "xing") || (username === "sean" && password === "zhan")) {
      dispatch(login(username));
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
        <button onClick={verify} onKeyPress={verify}>
          sign in
        </button>
      </div>
    </>
  );
}

export default Login;