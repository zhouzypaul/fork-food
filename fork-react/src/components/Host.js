import TopBar from "./TopBar";
import {Link} from "react-router-dom";

function Host() {
  const code = Math.floor(1000 + Math.random() * 9000);
  return (
    <>
      <TopBar to="/home" showOptions={true}/>
      <div className="content">
        <div className="title-text">
          share the code
        </div>
        <div className="code">{code}</div>
        <Link to="/swipe">
          <button className="primary-button">start</button>
        </Link>
      </div>
    </>
  );
}

export default Host;