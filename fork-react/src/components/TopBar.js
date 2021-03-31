import {Link} from "react-router-dom";
import {useRef, useEffect} from "react";

function TopBar (props) {
  const options = useRef(null);
  const displayOptions = () => {
    if (!props.showOptions) {
      options.current.style.visibility = 'hidden';
    }
  }
  useEffect(displayOptions, [props.showOptions]);
  return (
    <div className="top-bar">
      <span className="fork-home">
        <Link className="links" to={props.to}>
          fork
        </Link>
      </span>
      <span className="options" ref={options}>
        <Link className="links">Profile</Link>
        <Link className="links">Settings</Link>
      </span>
    </div>
  );
}

export default TopBar;