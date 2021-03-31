import TextBox from "./TextBox";
import {useDispatch, useSelector} from "react-redux";
import {logout} from "../actions/login";

function Personal(props) {
  const user = useSelector(state => state.user);
  const dispatch = useDispatch();
  const exit = () => {
    dispatch(logout());
    props.history.push('/');
  }
  return (
    <div className="content">
      <div className="title-text">profile</div>
      {user}
      <button>update preferences</button>
      <button onClick={exit}>log out</button>
    </div>
  );
}

export default Personal;