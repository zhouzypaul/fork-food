import TextBox from "./TextBox";
import {useState} from "react";
import TopBar from "./TopBar";
import {useDispatch} from "react-redux";
import {login} from "../actions";

function NewUser(props) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [confirm, setConfirm] = useState("");
  const [error, setError] = useState("");
  const dispatch = useDispatch();

  const createUser = () => {
    if (password === confirm) {
      dispatch(login(username));
      props.history.push('/home');
    } else {
        setError("Passwords do not match");
    }
  }

  return(
    <>
      <TopBar to="/" showOptions={false}/>
      <div className="login">
        <div className="title-text">join fork</div>
        <div className="login-error">
            {error}
        </div>
        <TextBox initial="username" change={setUsername} type="text"/>
        <TextBox initial="password" change={setPassword} type="password"/>
        <TextBox initial="confirm password" change={setConfirm} type="password"/>
        <button className="primary-button" onClick={createUser}>
          join
        </button>
      </div>
    </>
  );
}

export default NewUser;