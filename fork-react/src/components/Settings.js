import TopBar from "./TopBar";
import TextBox from "./TextBox";
import {useState} from "react";
import {useDispatch} from "react-redux";
import {logout} from "../actions";

function Settings() {
  const [old, setOld] = useState("");
  const [newPassword, setNew] = useState("");
  const [confirm, setConfirm] = useState("");
  const [error, setError] = useState("");
  const dispatch = useDispatch();

  const hasWhiteSpace = (s) => {
    return /\s/g.test(s);
  }

  const changePassword = () => {
    let valid = true;
    if (newPassword !== confirm) {
      valid = false;
      setError("passwords do not match");
    } else if (!validPassword(old)) {
      valid = false;
      setError("incorrect password");
    } else if (newPassword.length < 8) {
      valid = false;
      setError("password must be at least 8 characters long");
    } else if (hasWhiteSpace(newPassword)) {
      valid = false;
      setError("password can not contain white spaces");
    }
    if (valid) {
      // change password
    }
  }

  const validPassword = (old) => {
    // check old
    return true;
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
      alert("Account deleted successfully.");
      dispatch(logout());
    }
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
        <button className="primary-button">
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