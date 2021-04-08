import TextBox from "./TextBox";
import { useState } from "react";
import TopBar from "./TopBar";
import { useDispatch } from "react-redux";
import { login } from "../actions";
import { Link } from "react-router-dom";
import axios from 'axios';

const SERVER_URL = 'http://localhost:4567';

function Login(props) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const dispatch = useDispatch();

  const verify = () => {
    // verify user on backend
    const toSend = {
      // username and password
      username: username,
      password: password
    };
    let config = {
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      },
    };

    axios
      .post(`${SERVER_URL}/login`, toSend, config)
      .then((response) => {
        if (response.data["okay"]) {
          dispatch(login(username));
          props.history.push('/home');
        } else {
          setError("username or password is incorrect")
        }
      })
      .catch(function (error) {
        console.log(error);
      });
  }

  return (
    <>
      <TopBar to="/" showOptions={false} />
      <div className="login">
        <div className="title-text">hungry?</div>
        <div className="login-error">
          {error}
        </div>
        <TextBox initial="username" change={setUsername} type="text" />
        <TextBox initial="password" change={setPassword} type="password" />
        <button className="primary-button" onClick={verify} onKeyPress={verify}>
          sign in
        </button>
        <div className="divider" />
        <Link to="/newuser">
          <button className="secondary-button">
            join
          </button>
        </Link>
      </div>
    </>
  );
}

export default Login;