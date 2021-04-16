import TopBar from "./TopBar";
import TextBox from "./TextBox";
import {useState} from "react";
import {useDispatch, useSelector} from "react-redux";
import {logout} from "../actions";
import axios from "axios";

const SERVER_URL = 'http://localhost:4567';

function Settings(props) {
  const [old, setOld] = useState("");
  const [newPassword, setNew] = useState("");
  const [confirm, setConfirm] = useState("");
  const [error, setError] = useState("");
  const dispatch = useDispatch();
  const user = useSelector(state => state.user);

  const hasWhiteSpace = (s) => {
    return /\s/g.test(s);
  }

  const changePassword = () => {
    let valid = true;
    if (newPassword !== confirm) {
      valid = false;
      setError("passwords do not match");
    } else if (newPassword.length < 8) {
      valid = false;
      setError("password must be at least 8 characters long");
    } else if (hasWhiteSpace(newPassword)) {
      valid = false;
      setError("password can not contain white spaces");
    }
    if (valid) {
      validPassword();
    }
  }

  const validPassword = () => {
    // verify user on backend
    const toSend = {
      // username and password
      username: user,
      password: old
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
        if (response.data["success"]) {
          change();
        } else {
          setError("incorrect password");
        }
      })

      .catch(function (error) {
        console.log(error);
      });
  }

  const change = () => {
    const toSend = {
      // username and password
      username: user,
      password: newPassword
    };
    let config = {
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      },
    };

    axios
      .post(`${SERVER_URL}/updatePwd`, toSend, config)
      .then((response) => {
        if (response.data["success"]) {
          props.history.push('/home');
        } else {
          setError("failed to update password");
        }
      })
      .catch(function (error) {
        console.log(error);
      });
  }

  const submit = (e) => {
    const key = e.key;
    if (key === "Enter") {
      changePassword();
    }
  }

  const deleteAccount = () => {
    const resp = prompt("Type 'DELETE' to delete account permanently.");
    if (resp === "DELETE") {
      dataDelete();
    }
  }

  const dataDelete = () => {
    const toSend = {
      id: user
    };

    let config = {
      headers: {
        'Content-Type': 'application/json',
        'Access-Control-Allow-Origin': '*',
      },
    };

    axios
      .post(`${SERVER_URL}/deleteUser`, toSend, config)
      .then((response) => {
        if (response.data["success"]) {
          alert("Account deleted successfully.");
          dispatch(logout());
        } else {
          alert(response.data["err"].substr(7));
        }

      })
      .catch((error) => {
        console.log(error);
      });
  }

  return (
    <>
      <TopBar to="/home" showOptions={false}/>
      <div className="content">
        <div className="title-text">
          settings
        </div>
        <br/>
        change password
        <div className="login-error">
          {error}
        </div>
        <TextBox initial="old password" change={setOld} onKeyDown={submit} type="password" />
        <TextBox initial="new password" change={setNew} onKeyDown={submit} type="password" />
        <TextBox initial="confirm password" change={setConfirm} onKeyDown={submit} type="password" />
        <button className="primary-button" onClick={changePassword}>
          save
        </button>
      </div>
      <button className="secondary-button" id="delete" onClick={deleteAccount}>
        delete account
      </button>
    </>
  );
}

export default Settings;