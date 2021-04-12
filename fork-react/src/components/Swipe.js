import { Link } from "react-router-dom";
import Option from "./Option";

function Swipe() {
  return (
    <>
      <div className="content">
        <div className="choices">
          <button className="ex">&#x2715;</button>
          <Option name="The Capital Grille" star={3} review={100} price="$$" distance="100 miles" />
          <button className="check">&#x2713;</button>
        </div>
      </div>
      <div className="exit-home">
        <Link to="/home">
          <button className="secondary-button">
            leave
          </button>
        </Link>
      </div>
    </>
  );
}

export default Swipe;